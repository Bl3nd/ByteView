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

package com.github.bl3nd.byteview.gui.resourceviewer.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.bl3nd.byteview.misc.Icons;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Bl3nd.
 * Date: 6/6/2024
 */
public class TabComponentLabel extends JPanel {
	public TabComponentLabel(String name, Icon icon) {
		super();
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel(icon));
		panel.add(Box.createRigidArea(new Dimension(6, this.getHeight())));
		panel.add(new JLabel(name));
		panel.add(Box.createRigidArea(new Dimension(6, this.getHeight())));
		JButton close = new JButton(Icons.closeIcon);
		close.putClientProperty(FlatClientProperties.STYLE, "margin:1,1,1,1");
		panel.add(close);
		add(panel);
		setOpaque(false);
	}
}
