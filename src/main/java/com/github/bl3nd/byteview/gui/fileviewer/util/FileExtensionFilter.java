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

package com.github.bl3nd.byteview.gui.fileviewer.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Locale;

/**
 * Created by Bl3nd.
 * Date: 5/13/2024
 */
public class FileExtensionFilter extends FileFilter {
	private final String description;
	private final String[] lcExtensions;

	public FileExtensionFilter(final String description, final String... extensions) {
		if (extensions == null || extensions.length == 0) {
			throw new IllegalArgumentException("Extensions cannot be null or empty");
		}

		this.description = description;
		this.lcExtensions = new String[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			if (extensions[i] == null || extensions[i].isEmpty()) {
				throw new IllegalArgumentException("Any extension cannot be null or empty");
			}

			this.lcExtensions[i] = extensions[i].toLowerCase();
		}
	}

	@Override
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}

			String fileName = f.getName();
			int i = fileName.lastIndexOf('.');
			if (i > 0 && i < fileName.length() - 1) {
				String extension = fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
				for (String ext : lcExtensions) {
					if (extension.equals(ext)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
