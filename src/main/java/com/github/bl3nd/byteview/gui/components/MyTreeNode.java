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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.HashMap;

/**
 * Created by Bl3nd.
 * Date: 5/14/2024
 */
public class MyTreeNode extends DefaultMutableTreeNode {
	private HashMap<Object, MyTreeNode> map = null;

	public MyTreeNode(final Object userObject) {
		super(userObject);
	}

	@Override
	public void insert(MutableTreeNode newChild, int childIndex) {
		super.insert(newChild, childIndex);
	}

	@Override
	public void add(MutableTreeNode newChild) {
		super.add(newChild);
		addToMap((MyTreeNode) newChild);
	}

	@Override
	public void remove(int childIndex) {
		if (map != null) {
			TreeNode node = getChildAt(childIndex);
			map.remove(((MyTreeNode) node).getUserObject());
		}

		super.remove(childIndex);
	}

	@Override
	public void remove(MutableTreeNode aChild) {
		if (map != null && aChild != null) {
			map.remove(((MyTreeNode) aChild).getUserObject());
		}

		/*assert aChild != null;
		if (!aChild.getParent().equals(this)) {
			aChild = this.getChildByUserObject(((MyTreeNode) aChild.getParent()).getUserObject().toString());
		}*/

		super.remove(aChild);
	}

	@Override
	public void removeAllChildren() {
		if (map != null) {
			map.clear();
		}

		super.removeAllChildren();
	}

	public MyTreeNode getChildByUserObject(Object userObject) {
		if (map != null) {
			return map.get(userObject);
		}

		for (int i = 0; i < getChildCount(); i++) {
			MyTreeNode child = (MyTreeNode) getChildAt(i);
			if (child.getUserObject().equals(userObject)) {
				return child;
			}
		}

		return null;
	}

	private void addToMap(MyTreeNode child) {
		if (map != null) {
			map.put(child.getUserObject(), child);
		} else if (getChildCount() == 3) {
			buildMap();
		}
	}

	private void buildMap() {
		map = new HashMap<>();
		for (int i = 0; i < getChildCount(); i++) {
			MyTreeNode child = (MyTreeNode) getChildAt(i);
			map.put(child.getUserObject(), child);
		}
	}
}
