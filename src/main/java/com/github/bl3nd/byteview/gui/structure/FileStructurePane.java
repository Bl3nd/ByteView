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

package com.github.bl3nd.byteview.gui.structure;

import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.files.ClassFileContainer;
import com.github.bl3nd.byteview.files.FileContainer;
import com.github.bl3nd.byteview.gui.components.HeaderPanel;
import com.github.bl3nd.byteview.gui.components.MyTree;
import com.github.bl3nd.byteview.gui.components.MyTreeNode;
import com.github.bl3nd.byteview.gui.resourceviewer.component.RSyntaxTextAreaHighlighterEx;
import com.github.bl3nd.byteview.gui.resourceviewer.pages.ClassResourcePage;
import com.github.bl3nd.byteview.gui.resourceviewer.pages.Page;
import com.github.bl3nd.byteview.gui.structure.components.FileStructureTreeCellRenderer;
import com.github.bl3nd.byteview.misc.ClassMemberLocation;
import org.fife.ui.rtextarea.SmartHighlightPainter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Bl3nd.
 * Date: 5/14/2024
 */
public class FileStructurePane extends JPanel {

	public FileContainer openedContainer;
	public final HeaderPanel headerPanel;

	private final MyTreeNode root = new MyTreeNode(null);
	private final MyTree tree = new MyTree(root);

	public FileStructurePane() {
		super(new BorderLayout());
		headerPanel = new HeaderPanel("File Structure", null, new Insets(0, 1, 0, 0));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		headerPanel.setContent(tree);

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
					if (path == null || path.getPathCount() == 1) {
						return;
					}

					Rectangle bounds = tree.getPathBounds(path);
					if (bounds == null) {
						return;
					}

					// Clicked on valid item bounds
					ClassResourcePage page = (ClassResourcePage) ByteView.mainFrame.resourceViewerPane.getTabbedPane().getSelectedComponent();
					RSyntaxTextAreaHighlighterEx highlighterEx = (RSyntaxTextAreaHighlighterEx) page.getTextArea().getHighlighter();
					if (bounds.contains(e.getX(), e.getY()) || e.getY() >= bounds.y && e.getY() < bounds.y + bounds.height && (e.getX() < bounds.x || e.getX() > bounds.x + bounds.width)) {
						String pathName = path.getLastPathComponent().toString();
						pathName = pathName.substring(0, pathName.indexOf(":")).trim();
						ClassFileContainer container = (ClassFileContainer) openedContainer;
						page.getTextArea().getHighlighter().removeAllHighlights();
						Element root = page.getTextArea().getDocument().getDefaultRootElement();
						String finalPathName = pathName;
						container.fieldMembers.values().forEach(fields -> fields.forEach(_ -> {
							for (ClassMemberLocation location : container.getMemberLocationsFor(finalPathName)) {
								if (location.decRef().equalsIgnoreCase("declaration")) {
									int startOffset =
											root.getElement(location.line() - 1).getStartOffset() + (location.columnStart() - 1);
									int endOffset =
											root.getElement(location.line() - 1).getStartOffset() + (location.columnEnd() - 1);

									try {
										highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset,
												new SmartHighlightPainter());
										page.getTextArea().setCaretPosition(startOffset);
										page.markOccurrences(page.getTextArea(), container);
									} catch (BadLocationException ex) {
										throw new RuntimeException(ex);
									}
								}
							}
						}));
						/*try {
							for (ClassMemberLocation location : container.getTokenRanges().get(pathName)) {
								int startOffset =
										root.getElement(location.line() - 1).getStartOffset() + (location.columnStart() - 1);
								int endOffset = root.getElement(location.line() - 1).getStartOffset() + (location.columnEnd());
								if (container.getTokenRanges().get(pathName).getFirst().equals(location)) {
									page.getTextArea().setCaretPosition(startOffset);
								}

								page.getTextArea().getHighlighter().addHighlight(startOffset, endOffset,
										RSyntaxTextAreaHighlighter.DefaultPainter);
							}
						} catch (BadLocationException ex) {
							throw new RuntimeException(ex);
						}*/
					} else {
						page.getTextArea().getHighlighter().removeAllHighlights();
					}
				}
			}
		});

		add(headerPanel, BorderLayout.CENTER);
	}

	public void showContainerStructure(FileContainer container) {
		if (container == openedContainer) {
			return;
		}

		root.removeAllChildren();
		root.removeFromParent();
		tree.removeAll();

		this.openedContainer = container;

		setupTree(container);
	}

	public void hideContainerStructure() {
		root.removeAllChildren();
		root.removeFromParent();
		tree.removeAll();
		Component component = ByteView.mainFrame.resourceViewerPane.getTabbedPane().getSelectedComponent();
		this.openedContainer = !ByteView.mainFrame.resourceViewerPane.getPages().isEmpty() ? ((Page) component).getFileContainer() : null;
		tree.updateUI();
	}

	private void createTree(MyTreeNode node, @NotNull FileContainer container) {
		for (String field : container.memberMap.keySet()) {
			final String[] spl = field.split("/");
			if (spl.length < 2) {
				node.add(new MyTreeNode(field));
			}
		}
	}

	private void setupTree(@NotNull FileContainer container) {
		MyTreeNode node = container.rootNode = new MyTreeNode(container.getFileName());
		root.setUserObject(container.getFileName());
		root.add(node);
		createTree(node, container);
		tree.setCellRenderer(new FileStructureTreeCellRenderer());
		TreePath expandPath = new TreePath(node.getPath());
		tree.expandPath(expandPath);
		tree.updateUI();
	}

	public FileContainer getOpenedContainer() {
		return openedContainer;
	}
}
