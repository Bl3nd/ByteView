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

package com.github.bl3nd.byteview.actions;

import com.github.bl3nd.byteview.files.ClassFileContainer;
import com.github.bl3nd.byteview.tokens.TokenUtil;
import com.github.bl3nd.byteview.tokens.location.ClassFieldLocation;
import com.github.bl3nd.byteview.tokens.location.ClassLocalVariableLocation;
import com.github.bl3nd.byteview.tokens.location.ClassMethodLocation;
import com.github.bl3nd.byteview.tokens.location.ClassParameterLocation;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.event.ActionEvent;
import java.util.Objects;

/**
 * When a user enters "Ctrl + 'b'", go to the member's declaration.
 * <p>
 * Created by Bl3nd.
 * Date: 6/17/2024
 */
public class GoToAction extends AbstractAction {
	private final ClassFileContainer container;

	public GoToAction(final ClassFileContainer container) {
		this.container = container;
	}

	@Override
	public void actionPerformed(@NotNull ActionEvent e) {
		RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
		Token token = textArea.modelToToken(textArea.getCaretPosition() - 1);
		if (token == null) {
			return;
		}

		token = TokenUtil.getToken(textArea, token);
		int line = textArea.getCaretLineNumber() + 1;
		int column = textArea.getCaretOffsetFromLineStart();

		// Fields
		container.fieldMembers.values().forEach(fields -> fields.forEach(field -> {
			if (field.line() == line && field.columnStart() - 1 <= column && field.columnEnd() >= column) {
				Element root = textArea.getDocument().getDefaultRootElement();
				ClassFieldLocation first = fields.getFirst();
				int startOffset = root.getElement(first.line() - 1).getStartOffset() + (first.columnStart() - 1);
				textArea.setCaretPosition(startOffset);
			}
		}));

		// Methods
		Token finalToken = token;
		container.methodMembers.values().forEach(methods -> methods.forEach(method -> {
			if (method.line() == line && method.columnStart() - 1 <= column && method.columnEnd() >= column) {
				Element root = textArea.getDocument().getDefaultRootElement();
				for (ClassMethodLocation location : container.getMethodLocationsFor(finalToken.getLexeme())) {
					if (!Objects.equals(method.owner(), location.owner())) {
						continue;
					}

					if (!Objects.equals(method.methodParameterTypes(), location.methodParameterTypes())) {
						continue;
					}

					if (location.decRef().equalsIgnoreCase("declaration")) {
						int startOffset = root.getElement(location.line() - 1).getStartOffset() + (location.columnStart() - 1);
						textArea.setCaretPosition(startOffset);
					}
				}
			}
		}));

		// Method parameters
		container.methodParameterMembers.values().forEach(parameters -> parameters.forEach(parameter -> {
			if (parameter.line() == line && parameter.columnStart() - 1 <= column && parameter.columnEnd() >= column) {
				Element root = textArea.getDocument().getDefaultRootElement();
				if (parameter.decRef().equalsIgnoreCase("declaration")) {
					int startOffset = root.getElement(parameter.line() - 1).getStartOffset() + (parameter.columnStart() - 1);
					textArea.setCaretPosition(startOffset);
				} else {
					String method = parameter.method();
					parameters.stream().filter(classParameterLocation -> classParameterLocation.method().equals(method)).forEach(classParameterLocation -> {
						if (classParameterLocation.decRef().equalsIgnoreCase("declaration")) {
							int startOffset = root.getElement(classParameterLocation.line() - 1).getStartOffset() + (classParameterLocation.columnStart() - 1);
							textArea.setCaretPosition(startOffset);
						}
					});
				}
			}
		}));

		// Method local variables
		container.methodLocalMembers.values().forEach(localMembers -> localMembers.forEach(localMember -> {
			if (localMember.line() == line && localMember.columnStart() - 1 <= column && localMember.columnEnd() >= column) {
				Element root = textArea.getDocument().getDefaultRootElement();
				if (localMember.decRef().equals("declaration")) {
					int startOffset = root.getElement(localMember.line() - 1).getStartOffset() + (localMember.columnStart() - 1);
					textArea.setCaretPosition(startOffset);
				} else {
					String method = localMember.method();
					localMembers.stream().filter(classLocalVariableLocation -> classLocalVariableLocation.method().equals(method)).forEach(classLocalVariableLocation -> {
						if (classLocalVariableLocation.decRef().equalsIgnoreCase("declaration")) {
							int startOffset = root.getElement(classLocalVariableLocation.line() - 1).getStartOffset() + (classLocalVariableLocation.columnStart() - 1);
							textArea.setCaretPosition(startOffset);
						}
					});
				}
			}
		}));
	}
}
