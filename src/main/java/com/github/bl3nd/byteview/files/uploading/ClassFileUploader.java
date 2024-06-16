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

package com.github.bl3nd.byteview.files.uploading;

import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.files.ClassFileContainer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Bl3nd.
 * Date: 5/21/2024
 */
public class ClassFileUploader implements FileUploader {
	private final File file;

	public ClassFileUploader(final File file) {
		this.file = file;
	}

	public void upload() {
		try {
			if (!ByteView.mainFrame.resourcePane.files.contains(file)) {
				ClassFileContainer container = new ClassFileContainer(file);
				ByteView.mainFrame.resourcePane.files.add(file);
				SwingUtilities.invokeLater(() -> ByteView.mainFrame.resourcePane.addResource(container));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
