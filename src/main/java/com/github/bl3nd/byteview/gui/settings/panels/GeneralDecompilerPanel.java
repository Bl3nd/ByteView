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
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.decompiler.Decompiler;
import com.github.bl3nd.byteview.gui.settings.Settings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bl3nd.
 * Date: 6/17/2024
 */
public class GeneralDecompilerPanel extends JPanel {
	public static JComboBox<Object> availableDecompilers;

	public GeneralDecompilerPanel() {
		create();
	}

	private void create() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);

		JLabel label = new JLabel("General:");
		label.putClientProperty(FlatClientProperties.STYLE, "font:bold");

		JLabel activeDecompiler = new JLabel("Decompiler to use:");
		List<String> list = new ArrayList<>();
		for (Decompiler.Types s : Decompiler.Types.values()) {
			list.add(s.name());
		}

		availableDecompilers = new JComboBox<>(list.toArray());
		availableDecompilers.setSelectedItem(ByteView.configuration.getCurrentDecompiler());
		availableDecompilers.addActionListener(GeneralDecompilerPanel::notifyUpdateDecompiler);

		JLabel decompileAll = new JLabel("Decompile entire archive");
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(Settings.changeDecompilerSetting);
		checkBox.addActionListener(GeneralDecompilerPanel::decompileAllChanged);

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup().addComponent(label))
				.addGroup(layout.createSequentialGroup()
						.addPreferredGap(
								label,
								activeDecompiler,
								LayoutStyle.ComponentPlacement.INDENT,
								15,
								15
						)
						.addComponent(activeDecompiler)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
						.addComponent(
								availableDecompilers,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE
						)
				)
				.addGroup(layout.createSequentialGroup()
						.addPreferredGap(
								label,
								checkBox,
								LayoutStyle.ComponentPlacement.INDENT,
								15,
								15
						)
						.addComponent(checkBox)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 20)
						.addComponent(decompileAll)
				)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup().addComponent(label))
				.addGroup(layout.createParallelGroup()
						.addComponent(activeDecompiler)
						.addComponent(
								availableDecompilers,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE
						)
				)
				.addGroup(layout.createParallelGroup()
						.addComponent(checkBox)
						.addComponent(decompileAll)
				)
		);
	}

	private static void decompileAllChanged(@NotNull ActionEvent event) {
		JCheckBox checkBox = (JCheckBox) event.getSource();
		Settings.changeDecompilerSetting = checkBox.isSelected() && !ByteView.configuration.getDecompileEntireArchive();
	}

	private static void notifyUpdateDecompiler(@NotNull ActionEvent event) {
		JComboBox<?> checkBox = (JComboBox<?>) event.getSource();
		String selectedDecompiler = (String) checkBox.getSelectedItem();
		Settings.changeDecompilerSetting = !ByteView.configuration.getCurrentDecompiler().equals(selectedDecompiler);
	}
}
