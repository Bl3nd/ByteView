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

package com.github.bl3nd.byteview.gui;

import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.gui.components.ThemeMenuItem;
import com.github.bl3nd.byteview.gui.fileviewer.FileResourcePane;
import com.github.bl3nd.byteview.gui.fileviewer.actions.FileActions;
import com.github.bl3nd.byteview.gui.resourceviewer.ResourceViewerPane;
import com.github.bl3nd.byteview.gui.settings.Settings;
import com.github.bl3nd.byteview.gui.structure.FileStructurePane;
import com.github.bl3nd.byteview.misc.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Bl3nd.
 * Date: 5/12/2024
 */
public class ByteViewFrame extends JFrame {
	public FileResourcePane resourcePane = new FileResourcePane();
	public ResourceViewerPane resourceViewerPane = new ResourceViewerPane();
	public FileStructurePane fileStructurePane = new FileStructurePane();
	public JMenu openRecentMenu;
	public JSplitPane splitPane;
	public JSplitPane splitPane1;

	public ByteViewFrame() {
		super("ByteView");
		setSize(1200, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setIconImages(FlatSVGUtils.createWindowIconImages("/icons/ByteView.svg"));

		JMenuBar menuBar = setupMenuBar();

		setJMenuBar(menuBar);

		getContentPane().setLayout(new BorderLayout());
		resourcePane.setPreferredSize(new Dimension(225, 800));
		resourcePane.setMinimumSize(new Dimension(80, 800));

		resourceViewerPane.setPreferredSize(new Dimension(700, 800));
		resourceViewerPane.setMinimumSize(new Dimension(20, 800));

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, resourcePane, resourceViewerPane);
//		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, _ -> {
//			int width = resourcePane.getWidth() + resourceViewerPane.getMinimumSize().width + 6;
//			splitPane.setMinimumSize(new Dimension(width, this.getHeight()));
//		});

		fileStructurePane.setPreferredSize(new Dimension(225, 800));
		fileStructurePane.setMinimumSize(new Dimension(80, 800));
		if (!ByteView.configuration.getAlwaysShowHierarchy()) {
			splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, null);
		} else {
			splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, fileStructurePane);
		}

		addWindowStateListener(e -> {
			int newState = e.getNewState();
			if (newState == MAXIMIZED_BOTH) {
				splitPane1.setResizeWeight(1);
				splitPane.setResizeWeight(0);
			}
		});

		add(splitPane1, BorderLayout.CENTER);

		setVisible(true);
	}

	private @NotNull JMenuBar setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open", Icons.uploadFileIcon);
		openItem.setMnemonic('O');
		openItem.addActionListener(_ -> FileActions.openFileExplorer());
		fileMenu.add(openItem);

		openRecentMenu = new JMenu("Open Recent");
		openRecentMenu.setMnemonic('R');
		fileMenu.add(openRecentMenu);
		menuBar.add(fileMenu);

		JMenu viewMenu = new JMenu("View");
		JMenuItem settingsItem = new JMenuItem("Settings", Icons.settingsIcon);
		settingsItem.setMnemonic('S');
		settingsItem.addActionListener(_ -> Settings.openSettingsDialog());
		viewMenu.add(settingsItem);
		menuBar.add(viewMenu);

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem settingsPopupItem = new JMenuItem("Settings...", Icons.settingsIcon);
		settingsPopupItem.addActionListener(_ -> Settings.openSettingsDialog());

		ThemeMenuItem themeMenuItem = new ThemeMenuItem();

		popupMenu.add(settingsPopupItem);
		popupMenu.add(themeMenuItem);

		FlatButton button = new FlatButton();
		button.setIcon(Icons.settingsIcon);
		button.setButtonType(FlatButton.ButtonType.toolBarButton);
		button.setFocusable(false);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				JButton button = (JButton) e.getSource();
				JMenuBar parent = (JMenuBar) button.getParent();
				int height = parent.getHeight();
				popupMenu.show(e.getComponent(), e.getX(), height - 4);
			}
		});

		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(button);

		return menuBar;
	}
}
