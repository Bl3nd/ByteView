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

import javax.swing.*;
import java.awt.*;

/**
 * Created by Bl3nd.
 * Date: 5/12/2024
 */
public class HeaderPanel extends JPanel {
	public JPanel header;
	public JPanel content;

	private final String title;
	private final Icon icon;
	private final Insets insets;

	public HeaderPanel(String title, Icon icon, Insets insets) {
		super(new BorderLayout());
		this.title = title;
		this.icon = icon;
		this.insets = insets;

		createHeader();
		createContent();

		add(header, BorderLayout.NORTH);
		add(content, BorderLayout.CENTER);
	}

	private void createHeader() {
		if (title == null) {
			throw new IllegalStateException("Header panel cannot be created without a title");
		}

		header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
		JLabel iconLabel = new JLabel(icon);
		header.add(Box.createRigidArea(new Dimension(5, 0)));
		header.add(iconLabel);

		JLabel titleLabel = new JLabel(title);
		header.add(Box.createRigidArea(new Dimension(3, 0)));
		header.add(titleLabel);
		header.setMinimumSize(new Dimension(50, 10));
		header.setPreferredSize(new Dimension(50, 32));
		header.setBorder(new MutableLineBorder(UIManager.getColor("Component.borderColor"), insets.top, insets.left, insets.bottom, insets.right));
	}

	private void createContent() {
		content = new JPanel(new BorderLayout());
	}

	public void setContent(JComponent component) {
		this.content.add(component);
		JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);
	}

	public void resetBorders() {
		header.setBorder(new MutableLineBorder(UIManager.getColor("Component.borderColor"), insets.top, insets.left, insets.bottom, insets.right));
	}

	@Override
	public Insets getInsets() {
		return insets;
	}
}
