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

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * Created by Bl3nd.
 * Date: 5/12/2024
 */
public class MutableLineBorder extends AbstractBorder implements UIResource {
	private final Color color;
	protected int top;
	protected int left;
	protected int bottom;
	protected int right;

	/**
	 * Creates a border where the desired line is drawn.
	 *
	 * @param color  The border color
	 * @param top    If a line needs to be drawn on the top, this should be a value of 1 or higher
	 * @param left   If a line needs to be drawn on the left, this should be a value of 1 or higher
	 * @param bottom If a line needs to be drawn on the bottom, this should be a value of 1 or higher
	 * @param right  If a line needs to be drawn on the right, this should be a value of 1 or higher
	 */
	public MutableLineBorder(Color color, int top, int left, int bottom, int right) {
		this.color = color;
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public MutableLineBorder(Color color, Insets insets) {
		this(color, insets.top, insets.left, insets.bottom, insets.right);
	}

	/**
	 * This implementation returns a new {@link Insets} object
	 * that is initialized by the {@link #getBorderInsets(Component, Insets)}
	 * method.
	 *
	 * @param c the component for which this border insets value applies
	 * @return a new {@link Insets} object
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(top, left, bottom, right);
	}

	/**
	 * Re-initializes the insets parameter with this Border's current Insets.
	 *
	 * @param c      the component for which this border insets value applies
	 * @param insets the object to be reinitialized
	 * @return the {@code insets} object
	 * @throws NullPointerException if the specified {@code insets}
	 *                              is {@code null}
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.top = top;
		insets.left = left;
		insets.bottom = bottom;
		insets.right = right;
		return insets;
	}

	/**
	 * Paint the border.
	 *
	 * @param c      the component for which this border is being painted
	 * @param g      the paint graphics
	 * @param x      the x position of the painted border
	 * @param y      the y position of the painted border
	 * @param width  the width of the painted border
	 * @param height the height of the painted border
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(color);
		g.fillRect(x, y, width - right, top); // Top
		g.fillRect(x, y + top, left, height - top); // Left
		g.fillRect(x + left, y + height - bottom, width - left, bottom); // Bottom
		g.fillRect(x + width - right, y, right, height - bottom); // Right
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}
}
