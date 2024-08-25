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

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.FontUtils;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.gui.settings.panels.GeneralDecompilerPanel;
import com.github.bl3nd.byteview.gui.settings.panels.VineFlowerSettingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by Bl3nd.
 * Date: 5/18/2024
 */
public class Settings {

	public static boolean changeHierarchySetting = false;
	public static boolean changeDecompilerSetting = false;

	private static final SettingsPanel settingsPanel = new SettingsPanel();

	public static void openSettingsDialog() {
		changeDecompilerSetting = ByteView.configuration.getDecompileEntireArchive();
		settingsPanel.setPreferredSize(new Dimension(600, 400));
		settingsPanel.selectLastPath();

		int i = JOptionPane.showOptionDialog(ByteView.mainFrame, settingsPanel, "Settings",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		checkFontChange(i);
		checkHierarchyChange(i);
		updateDecompiler(i);
		updateDecompileEntireArchive(i);

		if (i == JOptionPane.OK_OPTION) {
			updateVineFlowerSettings();
			ByteView.configuration.read();
		}
	}

	private static void updateDecompileEntireArchive(int option) {
		if (option == JOptionPane.OK_OPTION) {
			if (ByteView.configuration.getDecompileEntireArchive() != changeDecompilerSetting) {
				ByteView.configuration.setDecompileEntireArchive(changeDecompilerSetting);
				ByteView.mainFrame.resourcePane.decompileEntireCheckbox.setSelected(changeDecompilerSetting);
			}
		}
	}

	private static void updateVineFlowerSettings() {
		if (VineFlowerSettingPanel.settingChanges.isEmpty()) {
			return;
		}

		for (String settings : VineFlowerSettingPanel.settingChanges.keySet()) {
			if (ByteView.configuration.getVineFlowerSettings().get(settings).equals(VineFlowerSettingPanel.settingChanges.get(settings))) {
				VineFlowerSettingPanel.settingChanges.remove(settings);
				return;
			}
		}

		if (VineFlowerSettingPanel.settingChanges.isEmpty()) {
			return;
		}

		try {
			ByteView.configuration.updateVineFlowerSettings(VineFlowerSettingPanel.settingChanges);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void updateDecompiler(int optionClick) {
		if (optionClick == JOptionPane.OK_OPTION) {
			if (changeDecompilerSetting) {
				ByteView.configuration.setCurrentDecompiler((String) GeneralDecompilerPanel.availableDecompilers.getSelectedItem());
			}
		}
	}

	private static void checkFontChange(int optionClick) {
		if (optionClick == JOptionPane.CANCEL_OPTION || optionClick == JOptionPane.CLOSED_OPTION) { // Cancel/Exit
			if (!Objects.equals(ByteView.configuration.getCurrentFontFamily(),
					UIManager.getFont("defaultFont").getFamily())) {
				Font font = UIManager.getFont("Label.font");
				Font oldFont = FontUtils.getCompositeFont(ByteView.configuration.getCurrentFontFamily(), font.getStyle(),
						font.getSize());
				SwingUtilities.invokeLater(() -> {
					UIManager.put("defaultFont", oldFont);
					settingsPanel.getTree().setFont(UIManager.getFont("defaultFont"));
					updateUI();
				});

			}
		} else if (optionClick == JOptionPane.OK_OPTION) { // OK
			if (!Objects.equals(ByteView.configuration.getCurrentFontFamily(),
					UIManager.getFont("defaultFont").getFamily())) {
				ByteView.configuration.setCurrentFontFamily(UIManager.getFont("defaultFont").getFamily());
			}
		}
	}

	private static void checkHierarchyChange(int optionClick) {
		if (optionClick == JOptionPane.OK_OPTION) {
			if (changeHierarchySetting) {
				changeHierarchySetting = false;
				ByteView.configuration.setAlwaysShowHierarchy(!ByteView.configuration.getAlwaysShowHierarchy());
				SwingUtilities.invokeLater(() -> {
					if (ByteView.configuration.getAlwaysShowHierarchy()) {
						ByteView.mainFrame.splitPane1.setRightComponent(ByteView.mainFrame.fileStructurePane);
					} else {
						ByteView.mainFrame.fileStructurePane.hideContainerStructure();
						ByteView.mainFrame.splitPane1.setRightComponent(null);
					}
				});
			}
		}
	}

	private static void updateUI() {
		SwingUtilities.invokeLater(() -> {
			FlatLaf.updateUI();
			ByteView.mainFrame.resourcePane.headerPanel.resetBorders();
			if (ByteView.mainFrame.fileStructurePane.headerPanel.isVisible()) {
				ByteView.mainFrame.fileStructurePane.headerPanel.resetBorders();
			}
		});
	}
}
