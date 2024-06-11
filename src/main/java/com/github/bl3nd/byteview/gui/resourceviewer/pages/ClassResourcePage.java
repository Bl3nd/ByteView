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
import com.github.bl3nd.byteview.files.ClassFileContainer;
import com.github.bl3nd.byteview.gui.resourceviewer.component.RSyntaxTextAreaHighlighterEx;
import com.github.bl3nd.byteview.misc.ClassMemberLocation;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

/**
 * Created by Bl3nd.
 * Date: 5/30/2024
 */
public class ClassResourcePage extends Page {
	public ClassResourcePage(@NotNull ClassFileContainer classFileContainer) {
		super(classFileContainer);

		textArea = new RSyntaxTextArea(classFileContainer.getContent());
		textArea.setCaretPosition(0);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		textArea.setEditable(false);
		textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		textArea.setHighlighter(new RSyntaxTextAreaHighlighterEx());
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (ByteView.mainFrame.fileStructurePane.getOpenedContainer() == null) {
					ByteView.mainFrame.fileStructurePane.showContainerStructure(classFileContainer);
				}

				RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
				markOccurrences(textArea, classFileContainer);
			}
		});

//		ErrorStrip errorStrip = new ErrorStrip(textArea);
//		errorStrip.setShowMarkedOccurrences(true);
//		add(errorStrip, BorderLayout.LINE_END);

		textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "goToAction");
		textArea.getActionMap().put("goToAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
				Token token = textArea.modelToToken(textArea.getCaretPosition());
				/*if (token == null || !classFileContainer.getTokenRanges().containsKey(token.getLexeme())) {
					return;
				}

				Element root = textArea.getDocument().getDefaultRootElement();
				ClassMemberLocation first = classFileContainer.getTokenRanges().get(token.getLexeme()).getFirst();
				int startOffset = root.getElement(first.line() - 1).getStartOffset() + (first.columnStart() - 1);
				textArea.setCaretPosition(startOffset);*/

				if (token == null) {
					return;
				}

				int line = textArea.getCaretLineNumber() + 1;
				int column = textArea.getCaretOffsetFromLineStart();

				classFileContainer.fieldMembers.values().forEach(fields -> fields.forEach(field -> {
					if (field.line() == line && field.columnStart() - 1 <= column && field.columnEnd() >= column) {
						Element root = textArea.getDocument().getDefaultRootElement();
						ClassMemberLocation first = fields.getFirst();
						int startOffset =
								root.getElement(first.line() - 1).getStartOffset() + (first.columnStart() - 1);
						textArea.setCaretPosition(startOffset);
					}
				}));

				classFileContainer.methodMembers.values().forEach(methods -> methods.forEach(method -> {
					if (method.line() == line && method.columnStart() - 1 <= column && method.columnEnd() >= column) {
						Element root = textArea.getDocument().getDefaultRootElement();
						ClassMemberLocation first = methods.getFirst();
						if (!Objects.equals(method.owner(), first.owner())) {
							return;
						}

						if (!first.decRef().equalsIgnoreCase("declaration")) {
							return;
						}

						int startOffset =
								root.getElement(first.line() - 1).getStartOffset() + (first.columnStart() - 1);
						textArea.setCaretPosition(startOffset);
					}
				}));

				classFileContainer.methodParameterMembers.values().forEach(parameters -> parameters.forEach(parameter -> {
					if (parameter.line() == line && parameter.columnStart() - 1 <= column && parameter.columnEnd() >= column) {
						Element root = textArea.getDocument().getDefaultRootElement();
						ClassMemberLocation first = parameters.getFirst();
						if (!Objects.equals(parameter.methodOwner(), first.methodOwner())) {
							return;
						}

						int startOffset = root.getElement(first.line() - 1).getStartOffset() + (first.columnStart() - 1);
						textArea.setCaretPosition(startOffset);
					}
				}));

				classFileContainer.methodLocalMembers.values().forEach(localMembers -> localMembers.forEach(localMember -> {
					if (localMember.line() == line && localMember.columnStart() - 1 <= column && localMember.columnEnd() >= column) {
						Element root = textArea.getDocument().getDefaultRootElement();
						ClassMemberLocation first = localMembers.getFirst();
						if (!Objects.equals(localMember.methodOwner(), first.methodOwner())) {
							return;
						}

						int startOffset = root.getElement(first.line() - 1).getStartOffset() + (first.columnStart() - 1);
						textArea.setCaretPosition(startOffset);
					}
				}));
			}
		});

		RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);

		setVisible(true);
	}

	public void markOccurrences(@NotNull RSyntaxTextArea textArea,
										@NotNull ClassFileContainer classFileContainer) {
		Token token = textArea.modelToToken(textArea.getCaretPosition() - 1);
		if (token == null || token.getLexeme().equals(";")) {
			return;
		}

		token = Objects.equals(token.getLexeme(), " ") || Objects.equals(token.getLexeme(), ".") ?
				textArea.modelToToken(textArea.getCaretPosition() + 1) : token;
		if (token == null) {
			return;
		}

		RSyntaxTextAreaHighlighterEx highlighterEx = (RSyntaxTextAreaHighlighterEx) textArea.getHighlighter();

		highlighterEx.clearMarkOccurrencesHighlights();

		/*int line = textArea.getCaretLineNumber();
		int column = textArea.getCaretOffsetFromLineStart();
		Collection<ArrayList<ClassMemberLocation>> values = classFileContainer.tokenRanges.values();
		for (int i = 0; i < values.size(); i++) {
			for (ArrayList<ClassMemberLocation> value : values) {
				ClassMemberLocation location = value.get(i);
				if (location.line() == line && location.columnStart() <= column && location.columnEnd() >= column) {
					System.err.println();
				}
			}
		}*/
		int line = textArea.getCaretLineNumber() + 1;
		int column = textArea.getCaretOffsetFromLineStart();
		Token finalToken = token;

		classFileContainer.fieldMembers.values().forEach(field -> field.forEach(member -> {
			if (member.line() == line && member.columnStart() - 1 <= column && member.columnEnd() >= column) {
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (ClassMemberLocation location : classFileContainer.getMemberLocationsFor(finalToken.getLexeme())) {
						int startOffset =
								root.getElement(location.line() - 1).getStartOffset() + (location.columnStart() - 1);
						int endOffset = root.getElement(location.line() - 1).getStartOffset() + (location.columnEnd() - 1);
//					if (container.getTokenRanges().get(pathName).getFirst().equals(location)) {
//						textArea.setCaretPosition(startOffset);
//					}

						highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset,
								new SmartHighlightPainter());
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));

		classFileContainer.methodParameterMembers.values().forEach(field -> field.forEach(member -> {
			String methodOwner;
			if (member.line() == line && member.columnStart() - 1 <= column && member.columnEnd() >= column) {
				methodOwner = member.methodOwner();
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (ClassMemberLocation location : classFileContainer.getParameterLocationsFor(finalToken.getLexeme())) {
						if (Objects.equals(methodOwner, location.methodOwner())) {
							int startOffset =
									root.getElement(location.line() - 1).getStartOffset() + (location.columnStart() - 1);
							int endOffset = root.getElement(location.line() - 1).getStartOffset() + (location.columnEnd() - 1);

							highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset,
									new SmartHighlightPainter());
						}
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));

		classFileContainer.methodLocalMembers.values().forEach(field -> field.forEach(member -> {
			String methodOwner;
			if (member.line() == line && member.columnStart() - 1 <= column && member.columnEnd() >= column) {
				methodOwner = member.methodOwner();
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (ClassMemberLocation location : classFileContainer.getLocalLocationsFor(finalToken.getLexeme())) {
						if (Objects.equals(methodOwner, location.methodOwner())) {
							int startOffset =
									root.getElement(location.line() - 1).getStartOffset() + (location.columnStart() - 1);
							int endOffset = root.getElement(location.line() - 1).getStartOffset() + (location.columnEnd() - 1);

							highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset,
									new SmartHighlightPainter());
						}
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));

		classFileContainer.methodMembers.values().forEach(field -> field.forEach(member -> {
			String owner;
			if (member.line() == line && member.columnStart() - 1 <= column && member.columnEnd() >= column) {
				owner = member.owner();
				try {
					Element root = textArea.getDocument().getDefaultRootElement();
					for (ClassMemberLocation location : classFileContainer.getMethodLocationsFor(finalToken.getLexeme())) {
						if (Objects.equals(owner, location.owner())) {
							int startOffset =
									root.getElement(location.line() - 1).getStartOffset() + (location.columnStart() - 1);
							int endOffset = root.getElement(location.line() - 1).getStartOffset() + (location.columnEnd() - 1);

							highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset,
									new SmartHighlightPainter());
						}
					}
				} catch (BadLocationException ex) {
					throw new RuntimeException(ex);
				}
			}
		}));
	}
}
