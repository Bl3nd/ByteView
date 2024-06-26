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

package com.github.bl3nd.byteview.files;

import com.github.bl3nd.byteview.gui.components.MyTreeNode;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Bl3nd.
 * Date: 5/14/2024
 */
public class FileContainer {
	public HashMap<String, Icon> memberMap = new LinkedHashMap<>();

	public MyTreeNode rootNode;
	public final byte[] bytes;
	public final String fileName;
	public String content;

	private String decompilerUsed;

	public FileContainer(byte[] bytes, String fileName) {
		this.bytes = bytes;
		this.fileName = fileName;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDecompilerUsed() {
		return decompilerUsed;
	}

	public void setDecompilerUsed(String decompilerUsed) {
		this.decompilerUsed = decompilerUsed;
	}
}
