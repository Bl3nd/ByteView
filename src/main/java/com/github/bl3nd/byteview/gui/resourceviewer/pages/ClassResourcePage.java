/*
 * MIT License
 *
 * Copyright (c) 2024 Cody March
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.bl3nd.byteview.gui.resourceviewer.pages;

import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.actions.GoToAction;
import com.github.bl3nd.byteview.files.ClassFileContainer;
import com.github.bl3nd.byteview.gui.components.MyErrorStripe;
import com.github.bl3nd.byteview.gui.components.RequestFocustListener;
import com.github.bl3nd.byteview.gui.resourceviewer.component.RSyntaxTextAreaHighlighterEx;
import com.github.bl3nd.byteview.tokens.TokenUtil;
import com.github.bl3nd.byteview.tokens.location.ClassFieldLocation;
import com.github.bl3nd.byteview.tokens.location.ClassLocalVariableLocation;
import com.github.bl3nd.byteview.tokens.location.ClassMethodLocation;
import com.github.bl3nd.byteview.tokens.location.ClassParameterLocation;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SmartHighlightPainter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

/**
 * A page that contains a decompiled classes text.
 * <p>
 * Created by Bl3nd.
 * Date: 5/30/2024
 */
public class ClassResourcePage extends Page {
	private final MyErrorStripe errorStripe;

	public ClassResourcePage(@NotNull ClassFileContainer classFileContainer) {
		super(classFileContainer);

		textArea = new RSyntaxTextArea(classFileContainer.getContent());
		textArea.setCaretPosition(0);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		textArea.setEditable(false);
		textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		textArea.setHighlighter(new RSyntaxTextAreaHighlighterEx());
		textArea.setMarkOccurrencesColor(Color.ORANGE);
		textArea.setBackground(UIManager.getColor("Panel.background"));
		textArea.setForeground(UIManager.getColor("Label.foreground"));
		textArea.setCurrentLineHighlightColor(UIManager.getColor("ScrollBar.thumb"));
		textArea.setAntiAliasingEnabled(true);

		textArea.addAncestorListener(new RequestFocustListener(false));

		/*textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (ByteView.mainFrame.fileStructurePane.getOpenedContainer() == null) {
					ByteView.mainFrame.fileStructurePane.showContainerStructure(classFileContainer);
				}

				RSyntaxTextAreaHighlighterEx highlighterEx = (RSyntaxTextAreaHighlighterEx) textArea.getHighlighter();
				highlighterEx.clearMarkOccurrencesHighlights();

				RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
//				markOccurrences(textArea, classFileContainer);
			}
		});*/

		textArea.addCaretListener(e -> {
			RSyntaxTextAreaHighlighterEx highlighterEx = (RSyntaxTextAreaHighlighterEx) textArea.getHighlighter();
			highlighterEx.clearMarkOccurrencesHighlights();

			RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
			markOccurrences(textArea, classFileContainer);
		});

		errorStripe = new MyErrorStripe(textArea);
		add(errorStripe, BorderLayout.LINE_END);

		/*
		This action goes to a members declaration.
		 */
		textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "goToAction");
		textArea.getActionMap().put("goToAction", new GoToAction(classFileContainer));

		RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);

		setVisible(true);
	}

	/**
	 * Mark the occurrences of a particular class member.
	 *
	 * @param textArea           The text area which to search the tokens
	 * @param classFileContainer The pages container
	 */
	public void markOccurrences(
			@NotNull RSyntaxTextArea textArea,
			@NotNull ClassFileContainer classFileContainer
	) {
		RSyntaxTextAreaHighlighterEx highlighterEx = (RSyntaxTextAreaHighlighterEx) textArea.getHighlighter();
		Token token = textArea.modelToToken(textArea.getCaretPosition() - 1);
		if (token == null || token.getLexeme().equals(";")) {
			highlighterEx.clearMarkOccurrencesHighlights();
			errorStripe.refreshMarkers();
			return;
		}

		token = TokenUtil.getToken(textArea, token);
		if (token == null) {
			highlighterEx.clearMarkOccurrencesHighlights();
			errorStripe.refreshMarkers();
			return;
		}

		highlighterEx.clearMarkOccurrencesHighlights();

		int line = textArea.getCaretLineNumber() + 1;
		int column = textArea.getCaretOffsetFromLineStart();
		Token finalToken = token;

		/*
		Fields
		 */
		markField(textArea, classFileContainer, line, column, finalToken, highlighterEx);

		/*
		Method parameters
		 */
		markMethodParameter(textArea, classFileContainer, line, column, finalToken, highlighterEx);

		/*
		Method local variables
		 */
		markMethodLocalVariable(textArea, classFileContainer, line, column, finalToken, highlighterEx);

		/*
		Methods
		 */
		markMethod(textArea, classFileContainer, line, column, finalToken, highlighterEx);

		errorStripe.refreshMarkers();
	}

	/**
	 * Search through the text area and mark all occurrences that match the selected token.
	 *
	 * @param textArea           the text area
	 * @param classFileContainer the container
	 * @param line               the caret line
	 * @param column             the caret column
	 * @param finalToken         the token
	 * @param highlighterEx      the highlighter
	 */
	private static void markMethod(
			@NotNull RSyntaxTextArea textArea,
			@NotNull ClassFileContainer classFileContainer,
			int line,
			int column,
			Token finalToken,
			RSyntaxTextAreaHighlighterEx highlighterEx
	) {
		classFileContainer.methodMembers.values().forEach(methods -> methods.forEach(method -> {
			String owner;
			String parameterTypes;
			if (method.line() == line && method.columnStart() - 1 <= column && method.columnEnd() >= column) {
				owner = method.owner();
				parameterTypes = method.methodParameterTypes();
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (
							ClassMethodLocation location :
							classFileContainer.getMethodLocationsFor(finalToken.getLexeme())
					) {
						if (Objects.equals(owner, location.owner())
								&& Objects.equals(parameterTypes, location.methodParameterTypes())
						) {
							int startOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnStart() - 1);
							int endOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnEnd() - 1);
							highlighterEx.addMarkedOccurrenceHighlight(
									startOffset, endOffset, new SmartHighlightPainter()
							);
						}
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));
	}

	/**
	 * Search through the text area and mark all occurrences that match the selected token.
	 *
	 * @param textArea           the text area
	 * @param classFileContainer the container
	 * @param line               the caret line
	 * @param column             the caret column
	 * @param finalToken         the token
	 * @param highlighterEx      the highlighter
	 */
	private static void markMethodLocalVariable(
			@NotNull RSyntaxTextArea textArea,
			@NotNull ClassFileContainer classFileContainer,
			int line,
			int column,
			Token finalToken,
			RSyntaxTextAreaHighlighterEx highlighterEx
	) {
		classFileContainer.methodLocalMembers.values().forEach(localVariables -> localVariables.forEach(localVariable -> {
			String method;
			if (localVariable.line() == line && localVariable.columnStart() - 1 <= column && localVariable.columnEnd() >= column) {
				method = localVariable.method();
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (
							ClassLocalVariableLocation location :
							classFileContainer.getLocalLocationsFor(finalToken.getLexeme())
					) {
						if (Objects.equals(method, location.method())) {
							int startOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnStart() - 1);
							int endOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnEnd() - 1);

							highlighterEx.addMarkedOccurrenceHighlight(
									startOffset, endOffset, new SmartHighlightPainter()
							);
						}
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));
	}

	/**
	 * Search through the text area and mark all occurrences that match the selected token.
	 *
	 * @param textArea           the text area
	 * @param classFileContainer the container
	 * @param line               the caret line
	 * @param column             the caret column
	 * @param finalToken         the token
	 * @param highlighterEx      the highlighter
	 */
	private static void markMethodParameter(
			@NotNull RSyntaxTextArea textArea,
			@NotNull ClassFileContainer classFileContainer,
			int line,
			int column,
			Token finalToken,
			RSyntaxTextAreaHighlighterEx highlighterEx
	) {
		classFileContainer.methodParameterMembers.values().forEach(parameters -> parameters.forEach(parameter -> {
			String method;
			if (parameter.line() == line && parameter.columnStart() - 1 <= column && parameter.columnEnd() >= column) {
				method = parameter.method();
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (
							ClassParameterLocation location :
							classFileContainer.getParameterLocationsFor(finalToken.getLexeme())
					) {
						if (Objects.equals(method, location.method())) {
							int startOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnStart() - 1);
							int endOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnEnd() - 1);

							highlighterEx.addMarkedOccurrenceHighlight(
									startOffset, endOffset, new SmartHighlightPainter()
							);
						}
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));
	}

	/**
	 * Search through the text area and mark all occurrences that match the selected token.
	 *
	 * @param textArea           the text area
	 * @param classFileContainer the container
	 * @param line               the caret line
	 * @param column             the caret column
	 * @param finalToken         the token
	 * @param highlighterEx      the highlighter
	 */
	private static void markField(
			@NotNull RSyntaxTextArea textArea,
			@NotNull ClassFileContainer classFileContainer,
			int line,
			int column,
			Token finalToken,
			RSyntaxTextAreaHighlighterEx highlighterEx
	) {
		classFileContainer.fieldMembers.values().forEach(fields -> fields.forEach(field -> {
			String owner;
			if (field.line() == line && field.columnStart() - 1 <= column && field.columnEnd() >= column) {
				owner = field.owner();
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (
							ClassFieldLocation location :
							classFileContainer.getMemberLocationsFor(finalToken.getLexeme())
					) {
						if (Objects.equals(owner, location.owner())) {
							int startOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnStart() - 1);
							int endOffset = root
									.getElement(location.line() - 1)
									.getStartOffset() + (location.columnEnd() - 1);
							highlighterEx.addMarkedOccurrenceHighlight(
									startOffset, endOffset, new SmartHighlightPainter()
							);
						}
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));
	}
}
