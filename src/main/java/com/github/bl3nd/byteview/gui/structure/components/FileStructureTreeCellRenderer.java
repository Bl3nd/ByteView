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

package com.github.bl3nd.byteview.gui.structure.components;

import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.files.ClassFileContainer;
import com.github.bl3nd.byteview.files.FileContainer;
import com.github.bl3nd.byteview.gui.components.MyTreeNode;
import com.github.bl3nd.byteview.misc.FileMisc;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bl3nd.
 * Date: 5/14/2024
 */
public class FileStructureTreeCellRenderer extends DefaultTreeCellRenderer {
	private final Map<MyTreeNode, Icon> iconMap = new HashMap<>();

	@Override
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus
	) {
		Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof MyTreeNode node) {
			if (iconMap.containsKey(value)) {
				setIcon(iconMap.get(value));
				return comp;
			}

			if (node.getParent() == null) {
				return comp;
			}

			String nameWithExtension = node.toString();
			String name = FileMisc.removeExtension(nameWithExtension);
			FileContainer container = ByteView.mainFrame.fileStructurePane.openedContainer;
			if (container instanceof ClassFileContainer) {
				if (nameWithExtension.endsWith(".class")) {
					setText(name);
					return comp;
				}
			}

			if (!container.memberMap.isEmpty()) {
				Icon icon = container.memberMap.get(name);
				if (icon != null) {
					setIcon(icon);
				}
			}
		}

		return comp;
	}
}
