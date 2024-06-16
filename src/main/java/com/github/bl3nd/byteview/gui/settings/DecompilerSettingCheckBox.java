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

import com.github.bl3nd.byteview.gui.settings.panels.VineFlowerSettingPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Bl3nd.
 * Date: 6/6/2024
 */
public class DecompilerSettingCheckBox extends JCheckBox {
	private final String setting;

	public DecompilerSettingCheckBox(final String setting) {
		super();
		this.setting = setting;

		addActionListener(this::updateCheckbox);
	}

	private void updateCheckbox(@NotNull ActionEvent event) {
		JCheckBox checkBox = (JCheckBox) event.getSource();
		// TODO: Either find a way to make this check box more generalized for any decompiler or create individual
		//  decompiler checkboxes
		VineFlowerSettingPanel.settingChanges.put(setting, checkBox.isSelected());
	}
}
