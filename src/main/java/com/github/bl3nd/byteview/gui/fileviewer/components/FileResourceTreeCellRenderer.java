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

package com.github.bl3nd.byteview.gui.fileviewer.components;

import com.github.bl3nd.byteview.gui.components.MyTreeNode;
import com.github.bl3nd.byteview.misc.FileMisc;
import com.github.bl3nd.byteview.misc.Icons;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by Bl3nd.
 * Date: 5/14/2024
 */
public class FileResourceTreeCellRenderer extends DefaultTreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof MyTreeNode node) {
			if (node.getParent() == null) {
				return comp;
			}

			String nameWithExtension = node.toString();
			String name = FileMisc.removeExtension(nameWithExtension);
			if (nameWithExtension.endsWith(".java")) {
				setNodeIcon(node, Icons.javaFileIcon);
				setText(name);
			} else if (nameWithExtension.endsWith(".class")) {
				setNodeIcon(node, Icons.classFileIcon);
				setText(name);
			}
		}

		return comp;
	}

	private void setNodeIcon(MyTreeNode node, Icon icon) {
		setIcon(icon);
	}
}
