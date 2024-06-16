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

package com.github.bl3nd.byteview.gui.settings.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.util.FontUtils;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.gui.settings.Settings;
import com.github.bl3nd.byteview.gui.settings.SettingsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * General settings page
 * <p>
 * Created by Bl3nd.
 * Date: 5/26/2024
 */
public class GeneralPanel extends JPanel {

	public GeneralPanel() {
		create();
	}

	private void create() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);

		// ------------ General Title --------------
		JLabel label = new JLabel("General:");
		label.putClientProperty(FlatClientProperties.STYLE, "font:bold");

		// ------------ Font Settings --------------
		JLabel fontLabel = new JLabel("General font:");
		String[] availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
		Arrays.sort(availableFontFamilyNames);
		ArrayList<String> families = new ArrayList<>(Arrays.asList("Arial", "Cantarell", "Comic Sans MS",
				"DejaVu Sans", "Dialog", "Inter", "Liberation Sans", "Noto Sans", "Open Sans", "Roboto",
				"SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"));
		ArrayList<?> clonedFamilies = (ArrayList<?>) families.clone();
		Font currentFont = UIManager.getFont("Label.font");
		String currentFamily = currentFont.getFamily();
		if (!families.contains(currentFamily)) {
			families.add(currentFamily);
		}

		families.sort(String.CASE_INSENSITIVE_ORDER);
		for (String family : families) {
			if (Arrays.binarySearch(availableFontFamilyNames, family) >= 0) {
				continue;
			}

			clonedFamilies.remove(family);
		}
		JComboBox<?> fontComboBox = new JComboBox<>(clonedFamilies.toArray());
		fontComboBox.setSelectedItem(currentFamily);
		fontComboBox.addActionListener(GeneralPanel::updateFont);

		// ------------ Hierarchy Setting --------------
		JLabel alwaysShowHierarchyLabel = new JLabel("Always show hierarchy:");
		JCheckBox hierarchyCheckbox = new JCheckBox();
		if (ByteView.configuration.getAlwaysShowHierarchy()) {
			hierarchyCheckbox.setSelected(true);
		}

		hierarchyCheckbox.addActionListener(GeneralPanel::updateAlwaysShowHierarchy);

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(label)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(label, fontLabel, LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(fontLabel)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(fontComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(
						layout.createSequentialGroup()
								.addPreferredGap(label, alwaysShowHierarchyLabel,
										LayoutStyle.ComponentPlacement.INDENT, 15, 15)
								.addComponent(alwaysShowHierarchyLabel)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
								.addComponent(hierarchyCheckbox)
				)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
								.addComponent(label)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(fontLabel)
								.addComponent(fontComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(alwaysShowHierarchyLabel)
								.addComponent(hierarchyCheckbox)
				)
		);
	}

	private static void updateFont(@NotNull ActionEvent event) {
		JComboBox<?> comboBox = (JComboBox<?>) event.getSource();
		String fontFamily = (String) comboBox.getSelectedItem();
		FlatAnimatedLafChange.showSnapshot();
		Font font = UIManager.getFont("defaultFont");
		Font newFont = FontUtils.getCompositeFont(fontFamily, font.getStyle(), font.getSize());
		UIManager.put("defaultFont", newFont);
		SwingUtilities.invokeLater(() -> SettingsPanel.tree.setFont(newFont));
		resetAndUpdateUI();
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
	}

	private static void updateAlwaysShowHierarchy(@NotNull ActionEvent event) {
		JCheckBox checkBox = (JCheckBox) event.getSource();
		Settings.changeHierarchySetting = !checkBox.isSelected() || !ByteView.configuration.getAlwaysShowHierarchy();
	}

	private static void resetAndUpdateUI() {
		SwingUtilities.invokeLater(() -> {
			FlatLaf.updateUI();
			ByteView.mainFrame.resourcePane.headerPanel.resetBorders();
			if (ByteView.mainFrame.fileStructurePane.headerPanel.isVisible()) {
				ByteView.mainFrame.fileStructurePane.headerPanel.resetBorders();
			}
			SettingsPanel.tree.setBackground(UIManager.getColor("Component.background"));
		});
	}
}
