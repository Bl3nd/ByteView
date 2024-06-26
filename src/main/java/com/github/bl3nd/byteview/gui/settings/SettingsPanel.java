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

package com.github.bl3nd.byteview.gui.settings;

import com.github.bl3nd.byteview.gui.components.MyTree;
import com.github.bl3nd.byteview.gui.components.MyTreeNode;
import com.github.bl3nd.byteview.gui.settings.panels.GeneralDecompilerPanel;
import com.github.bl3nd.byteview.gui.settings.panels.GeneralPanel;
import com.github.bl3nd.byteview.gui.settings.panels.VineFlowerSettingPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

/**
 * Settings panel (Tree & Individual setting page)
 * <p>
 * Created by Bl3nd.
 * Date: 5/26/2024
 */
public class SettingsPanel extends JPanel {
	public static JSplitPane splitPane;
	public static MyTree tree;
	private TreePath currentPath = new TreePath(new TreeNode[]{new MyTreeNode("Settings"), new MyTreeNode("General")});

	private static boolean decompileEntireChecked = false;

	public SettingsPanel() {
		super(new BorderLayout());

		MyTreeNode top = new MyTreeNode("Settings");
		createTree(top);
		tree = new MyTree(top);
		tree.setBackground(UIManager.getColor("Component.background"));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setSelectionPath(currentPath);

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(200, this.getHeight()));
		scrollPane.setMinimumSize(new Dimension(115, this.getHeight()));
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, new JPanel());
		add(splitPane, BorderLayout.CENTER);

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
						currentPath = path;
						setSettingPanelForPath(path.getLastPathComponent().toString(),
								path.getParentPath().getLastPathComponent().toString());
					} else { // Click outside valid item bounds
						tree.setSelectionPath(null);
						splitPane.setRightComponent(new JPanel());
					}
				}
			}
		});

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				TreePath path = tree.getSelectionPath();
				if (path == null) {
					return;
				}

				String settingPath = path.getLastPathComponent().toString();
				String settingParent = path.getParentPath().getLastPathComponent().toString();
				currentPath = path;
				setSettingPanelForPath(settingPath, settingParent);
			}
		});

		setVisible(true);
	}

	public void selectLastPath() {
		if (currentPath != null) {
			setSettingPanelForPath(currentPath.getLastPathComponent().toString(),
					currentPath.getParentPath().getLastPathComponent().toString());
		}
	}

	private void setSettingPanelForPath(@NotNull String path, String parentPath) {
		SwingUtilities.invokeLater(() -> {
			if (path.equalsIgnoreCase("General") && parentPath.equalsIgnoreCase("Settings")) {
				splitPane.setRightComponent(new GeneralPanel());
			} else if (path.equalsIgnoreCase("Decompilers")) {
				splitPane.setRightComponent(new JPanel());
			} else if (path.equalsIgnoreCase("General") && parentPath.equalsIgnoreCase("Decompilers")) {
				splitPane.setRightComponent(new GeneralDecompilerPanel());
			} else if (path.equalsIgnoreCase("VineFlower")) {
				JScrollPane scrollPane = new JScrollPane(new VineFlowerSettingPanel());
				scrollPane.getVerticalScrollBar().setUnitIncrement(16);
				splitPane.setRightComponent(scrollPane);
			}
		});
	}

	private void createTree(@NotNull MyTreeNode top) {
		MyTreeNode category = null;
		MyTreeNode setting = null;

		category = new MyTreeNode("General");
		top.add(category);

//		setting = new MyTreeNode("Test");
//		category.add(setting);

		category = new MyTreeNode("Decompilers");

		setting = new MyTreeNode("General");
		category.add(setting);

		setting = new MyTreeNode("VineFlower");
		category.add(setting);

		top.add(category);
	}

	public MyTree getTree() {
		return tree;
	}
}
