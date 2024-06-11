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

package com.github.bl3nd.byteview.decompiler.dialog;

import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.decompiler.Decompiler;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bl3nd.
 * Date: 6/3/2024
 */
public class DecompilerDialog {

	private DecompilerDialog() {
	}

	public static int open() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel("A decompiler is not set. Please set one to continue."));
		List<String> list = new ArrayList<>();
		for (Decompiler.Types s : Decompiler.Types.values()) {
			list.add(s.name());
		}

		JComboBox<Object> availableCompilers = new JComboBox<>(list.toArray());
		availableCompilers.setSelectedItem(ByteView.configuration.getCurrentDecompiler());
		availableCompilers.addActionListener(DecompilerDialog::changeCompiler);
		panel.add(availableCompilers);

		return JOptionPane.showConfirmDialog(ByteView.mainFrame, panel,
				"Set a decompiler", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	private static void changeCompiler(@NotNull ActionEvent event) {
		JComboBox<?> comboBox = (JComboBox<?>) event.getSource();
		String selectedDecompiler = (String) comboBox.getSelectedItem();
		ByteView.configuration.setCurrentDecompiler(selectedDecompiler);
	}

}
