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

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.gui.fileviewer.FileResourcePane;
import com.github.bl3nd.byteview.gui.fileviewer.actions.FileActions;
import com.github.bl3nd.byteview.gui.resourceviewer.ResourceViewerPane;
import com.github.bl3nd.byteview.gui.settings.Settings;
import com.github.bl3nd.byteview.gui.structure.FileStructurePane;
import com.github.bl3nd.byteview.misc.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		setIconImages(FlatSVGUtils.createWindowIconImages("/icons/byte.svg"));

		JMenuBar menuBar = setupMenuBar();

		setJMenuBar(menuBar);

		getContentPane().setLayout(new BorderLayout());
		resourcePane.setPreferredSize(new Dimension(225, 800));
		resourcePane.setMinimumSize(new Dimension(80, 800));

		resourceViewerPane.setPreferredSize(new Dimension(700, 800));
		resourceViewerPane.setMinimumSize(new Dimension(20, 800));

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, resourcePane, resourceViewerPane);
		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, _ -> {
			int width = resourcePane.getWidth() + resourceViewerPane.getMinimumSize().width + 6;
			splitPane.setMinimumSize(new Dimension(width, this.getHeight()));
		});

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

		JMenuItem themePopupItem = new JMenuItem("Theme...");
		themePopupItem.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel();

				List<String> themes = new ArrayList<>();
				for (FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
					themes.add(info.getName());
				}

				JList<String> themeList = new JList<>();
				for (FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
					themeList.add(new JLabel(info.getName()));
				}

				themeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				themeList.setLayoutOrientation(JList.VERTICAL);
				themeList.setModel(new AbstractListModel<>() {
					@Override
					public int getSize() {
						return FlatAllIJThemes.INFOS.length;
					}

					@Override
					public String getElementAt(int index) {
						return themes.get(index);
					}
				});
				themeList.addListSelectionListener(e1 -> {
					JList<String> list = (JList<String>) e1.getSource();
					String selectedTheme = list.getSelectedValue();
					SwingUtilities.invokeLater(() -> setTheme(selectedTheme));
				});

				JScrollPane scrollPane = new JScrollPane(themeList);
				panel.add(scrollPane);
				JOptionPane.showOptionDialog(ByteView.mainFrame, panel, "Themes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
						null, null);
			}
		});

		popupMenu.add(settingsPopupItem);
		popupMenu.add(themePopupItem);

		JButton button = new JButton(Icons.settingsIcon);
		button.putClientProperty("JButton.buttonType", "toolBarButton");
		button.putClientProperty(FlatClientProperties.STYLE, "background:$background; toolbar.focusColor:#ffffff;");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.putClientProperty(FlatClientProperties.STYLE, "background:#fdfdfd; toolbar.focusColor:#d8d8d8;");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.putClientProperty(FlatClientProperties.STYLE, "background:$background; toolbar.focusColor:#ffffff");
			}

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

	private void setTheme(String selectedTheme) {
		for (FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
			if (info.getName().equals(selectedTheme)) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
					throw new RuntimeException(e);
				}

				FlatLaf.updateUI();
				ByteView.mainFrame.resourceViewerPane.getPages().forEach((s, page) -> page.getTextArea().setCurrentLineHighlightColor(UIManager.getColor("textHighlight")));

				ByteView.configuration.updateCurrentTheme(selectedTheme);
				break;
			}
		}

	}
}
