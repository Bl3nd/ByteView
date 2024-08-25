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

package com.github.bl3nd.byteview.gui.fileviewer;

import com.formdev.flatlaf.FlatLaf;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.decompiler.Decompiler;
import com.github.bl3nd.byteview.decompiler.dialog.DecompilerDialog;
import com.github.bl3nd.byteview.files.ClassFileContainer;
import com.github.bl3nd.byteview.files.FileContainer;
import com.github.bl3nd.byteview.files.ZipFileContainer;
import com.github.bl3nd.byteview.gui.components.HeaderPanel;
import com.github.bl3nd.byteview.gui.components.MyTree;
import com.github.bl3nd.byteview.gui.components.MyTreeNode;
import com.github.bl3nd.byteview.gui.fileviewer.components.FileResourceTreeCellRenderer;
import com.github.bl3nd.byteview.gui.resourceviewer.pages.ClassResourcePage;
import com.github.bl3nd.byteview.misc.Icons;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bl3nd.
 * Date: 5/12/2024
 */
public class FileResourcePane extends JPanel {
	public final Map<String, FileContainer> uploadedFiles = new HashMap<>();
	public final List<File> files = new ArrayList<>();

	public final MyTreeNode root = new MyTreeNode("Uploaded files:");
	public final MyTree tree = new MyTree(root);
	public final HeaderPanel headerPanel;
	public final JCheckBox decompileEntireCheckbox = new JCheckBox();

	public FileResourcePane() {
		super(new BorderLayout());
		decompileEntireCheckbox.setSelected(ByteView.configuration.getDecompileEntireArchive());
		decompileEntireCheckbox.setToolTipText("Decompile entire archive. This helps with naming but at a cost of CPU and memory usage!");
		decompileEntireCheckbox.addActionListener(e -> {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			ByteView.configuration.setDecompileEntireArchive(checkBox.isSelected());
		});
		headerPanel = new HeaderPanel("Files", Icons.folderIcon, new Insets(0, 0, 0, 1), decompileEntireCheckbox);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		headerPanel.setContent(tree);
		add(headerPanel, BorderLayout.CENTER);

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
					if (bounds.contains(e.getX(), e.getY()) || e.getY() >= bounds.y && e.getY() < bounds.y + bounds.height && (e.getX() < bounds.x || e.getX() > bounds.x + bounds.width)) {
						FileContainer container;
						String pathName = path.getPathComponent(1).toString();
						if (pathName.endsWith(".jar")) {
							ZipFileContainer zipFileContainer = (ZipFileContainer) ByteView.mainFrame.resourcePane.uploadedFiles.get(pathName);
							pathName = path.getLastPathComponent().toString();
							if (pathName.endsWith(".class")) {
								int count = path.getPathCount();
								StringBuilder s = new StringBuilder();
								for (int i = 2; i < count - 1; i++) {
									s.append(path.getPathComponent(i).toString()).append("/");
								}

								pathName = s + pathName.substring(0, pathName.indexOf('.'));
							} else if (pathName.equalsIgnoreCase("manifest")) {
								pathName = path.getPathComponent(path.getPathCount() - 2).toString() + "/" + pathName;
							}

							container = zipFileContainer.fileEntries.get(pathName);
							if (container == null) {
								return;
							}
						} else {
							container = ByteView.mainFrame.resourcePane.uploadedFiles.get(pathName);
						}

						if (e.getClickCount() == 2) {
							if (container instanceof ClassFileContainer c) {
								if (!ByteView.configuration.hasCurrentDecompiler()) {
									int i = DecompilerDialog.open();
									if (i != JOptionPane.OK_OPTION) {
										return;
									}
								}

								if (!c.hasBeenDecompiled || !c.getDecompilerUsed().equalsIgnoreCase(ByteView.configuration.getCurrentDecompiler())) {
									Decompiler decompiler = Decompiler.getDecompiler(
											ByteView.configuration.getCurrentDecompiler(), c
									);
									byte[] bytes = c.getBytes();
									decompiler.decompile(bytes);
									c.parse();
								}

								if (!ByteView.configuration.getAlwaysShowHierarchy()) {
									ByteView.mainFrame.splitPane1.setRightComponent(ByteView.mainFrame.fileStructurePane);
								}

								ByteView.mainFrame.fileStructurePane.showContainerStructure(c);
								ByteView.mainFrame.resourceViewerPane.addPage(new ClassResourcePage(c));
							}
						}
					} else { // Clicked outside a valid item bounds
						if (ByteView.configuration.getAlwaysShowHierarchy()) {
							ByteView.mainFrame.fileStructurePane.hideContainerStructure();
						} else {
							ByteView.mainFrame.splitPane1.setRightComponent(null);
						}

						tree.setSelectionPath(null);
					}
				}
			}
		});

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				TreePath path = tree.getSelectionPath();
				if (path == null) {
					return;
				}

				String pathName = path.getPathComponent(1).toString();
				int keyCode = KeyEvent.getExtendedKeyCodeForChar(e.getKeyChar());
				if (pathName.endsWith(".jar")) {
					if (keyCode == 127) { // Delete key
						root.remove(uploadedFiles.get(pathName).rootNode);
						ByteView.mainFrame.resourceViewerPane.removeAllTabsRelatedToArchive(uploadedFiles.get(pathName));
						uploadedFiles.remove(pathName);
						files.removeIf(file -> file.getName().equals(pathName));
						SwingUtilities.invokeLater(() -> {
							FlatLaf.updateUI();
							headerPanel.resetBorders();
							ByteView.mainFrame.fileStructurePane.headerPanel.resetBorders();
						});
					} else if (keyCode == 10) { // Enter key
						//TODO: Expand this node
					}
				} else if (pathName.endsWith(".class")) {
					/*if (keyCode == 127) { // Delete key
						root.remove(uploadedFiles.get(pathName).rootNode);
						uploadedFiles.remove(pathName);
						files.removeIf(file -> file.getName().equals(pathName));
						SwingUtilities.invokeLater(() -> {
							FlatLaf.updateUI();
							headerPanel.resetBorders();
							ByteView.mainFrame.fileStructurePane.headerPanel.resetBorders();
						});
					} else*/
					if (keyCode == 10) { // Enter key
						ClassFileContainer container =
								(ClassFileContainer) ByteView.mainFrame.resourcePane.uploadedFiles.get(pathName);
						if (!ByteView.configuration.hasCurrentDecompiler()) {
							int i = DecompilerDialog.open();
							if (i != JOptionPane.OK_OPTION) {
								return;
							}
						}

						if (!container.hasBeenDecompiled) {
							Decompiler decompiler = Decompiler.getDecompiler(
									ByteView.configuration.getCurrentDecompiler(), container
							);
							byte[] bytes = container.getBytes();
							decompiler.decompile(bytes);
							container.parse();
						}

						if (!ByteView.configuration.getAlwaysShowHierarchy()) {
							ByteView.mainFrame.splitPane1.setRightComponent(ByteView.mainFrame.fileStructurePane);
						}

						ByteView.mainFrame.fileStructurePane.showContainerStructure(container);
						ByteView.mainFrame.resourceViewerPane.addPage(new ClassResourcePage(container));
					}
				}
			}
		});

		setVisible(true);
	}

	/**
	 * Adds this container to the resource tree.
	 *
	 * @param container the container to add
	 */
	public void addResource(FileContainer container) {
		uploadedFiles.put(container.fileName, container);
		MyTreeNode rootNode = container.rootNode = new MyTreeNode(container.getFileName());
		root.add(rootNode);
		tree.setCellRenderer(new FileResourceTreeCellRenderer());
		createTree(rootNode, container);
		tree.expandPath(new TreePath(tree.getModel().getRoot()));
		tree.updateUI();
	}

	private void createTree(MyTreeNode rootNode, FileContainer container) {
		if (container instanceof ZipFileContainer zfc) {
			if (!zfc.fileEntries.isEmpty()) {
				for (String entry : ((ZipFileContainer) container).fileEntries.keySet()) {
					final String[] split = entry.split("/");
					int splitLength = split.length;
					if (splitLength < 2) {
						rootNode.add(new MyTreeNode(entry + ".class"));
					} else {
						MyTreeNode parent = rootNode;
						for (int i = 0; i < splitLength; i++) {
							String s = split[i];
							if (i == splitLength - 1 && !s.equalsIgnoreCase("manifest")) {
								s += ".class";
							}

							MyTreeNode child = parent.getChildByUserObject(s);
							if (child == null) {
								child = new MyTreeNode(s);
								parent.add(child);
							}

							parent = child;
						}
					}
				}
			}
		}
	}
}
