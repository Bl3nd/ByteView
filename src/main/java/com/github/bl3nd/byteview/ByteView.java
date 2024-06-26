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

package com.github.bl3nd.byteview;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.github.bl3nd.byteview.gui.ByteViewFrame;
import com.github.bl3nd.byteview.misc.Configuration;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.github.bl3nd.byteview.misc.Constants.CONFIG_LOCATION;
import static com.github.bl3nd.byteview.misc.Constants.TEMP_LOCATION;

/**
 * Created by Bl3nd.
 * Date: 5/12/2024
 */
public class ByteView {
	public static ByteViewFrame mainFrame;
	public static Configuration configuration;

	public static void main(String[] args) {
		configuration = new Configuration();
		FlatLaf.registerCustomDefaultsSource("themes");

		if (new File(CONFIG_LOCATION).exists()) {
			String currentTheme = configuration.getTheme();
			if (currentTheme.equalsIgnoreCase("light")) {
				FlatLightLaf.setup();
			} else {
				for (FlatAllIJThemes.FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
					if (info.getName().equals(currentTheme)) {
						try {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
								 UnsupportedLookAndFeelException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}

		setupUISettings();

		if (new File(CONFIG_LOCATION).exists()) {
			configuration.read();
		} else {
			configuration.buildDocument();
			configuration.read();
		}


		File temp = new File(TEMP_LOCATION);
		if (!temp.exists()) {
			temp.mkdirs();
		}

		temp.deleteOnExit();

		mainFrame = new ByteViewFrame();
		configuration.resetRecentFilesMenu();
	}

	/**
	 * Setup FlatLaf UI specific customizations
	 */
	private static void setupUISettings() {
		UIManager.put("TitlePane.unifiedBackground", false);
		UIManager.put("Component.hideMnemonics", false);
		UIManager.put("MenuItem.selectionArc", 10);
		UIManager.put("MenuItem.selectionInsets", new Insets(0, 3, 0, 3));
		UIManager.put("SplitPaneDivider.gripDotCount", 0);
		UIManager.put("Tree.selectionArc", 5);
		UIManager.put("Tree.selectionInsets", new Insets(0, 2, 0, 2));
	}

	public static void updateUIComponents() {
		for (Window w : Window.getWindows()) {
			SwingUtilities.updateComponentTreeUI(w);
		}
	}
}
