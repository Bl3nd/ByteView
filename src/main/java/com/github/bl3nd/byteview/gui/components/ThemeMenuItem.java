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

package com.github.bl3nd.byteview.gui.components;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.github.bl3nd.byteview.ByteView;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bl3nd.
 * Date: 6/28/2024
 */
public class ThemeMenuItem extends JMenuItem implements ActionListener {
	public ThemeMenuItem() {
		super("Themes...");
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JPanel panel = new JPanel();

		List<String> themes = new ArrayList<>();
		for (FlatAllIJThemes.FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
			themes.add(info.getName());
		}

		JList<String> themeList = new JList<>();
		for (FlatAllIJThemes.FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
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

		themeList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				themeList.grabFocus();
				themeList.ensureIndexIsVisible(themeList.getSelectedIndex());
			}
		});

		themeList.addListSelectionListener(e1 -> {
			JList<String> list = (JList<String>) e1.getSource();
			String selectedTheme = list.getSelectedValue();
			SwingUtilities.invokeLater(() -> setTheme(selectedTheme));
		});

		themeList.setSelectedValue(ByteView.configuration.getTheme(), true);

		JScrollPane scrollPane = new JScrollPane(themeList);
		scrollPane.requestFocusInWindow();
		panel.add(scrollPane);
		JOptionPane.showOptionDialog(ByteView.mainFrame, panel, "Themes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);
	}

	private void setTheme(String selectedTheme) {
		for (FlatAllIJThemes.FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
			if (info.getName().equals(selectedTheme)) {
				FlatAnimatedLafChange.showSnapshot();

				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
					throw new RuntimeException(e);
				}

				FlatLaf.updateUI();
				FlatAnimatedLafChange.hideSnapshotWithAnimation();
				ByteView.mainFrame.resourceViewerPane.getPages().forEach((s, page) -> page.getTextArea().setCurrentLineHighlightColor(UIManager.getColor("textHighlight")));

				ByteView.configuration.updateCurrentTheme(selectedTheme);
				break;
			}
		}
	}
}
