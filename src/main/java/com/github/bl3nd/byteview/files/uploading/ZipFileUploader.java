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
import com.github.bl3nd.byteview.files.FileContainer;
import com.github.bl3nd.byteview.files.ZipFileContainer;
import com.github.bl3nd.byteview.gui.components.MyTreeNode;
import com.github.bl3nd.byteview.misc.Constants;
import com.github.bl3nd.byteview.misc.FileMisc;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Bl3nd.
 * Date: 6/1/2024
 */
public class ZipFileUploader implements FileUploader {
	private final File file;

	public ZipFileUploader(final File file) {
		this.file = file;
	}

	@Override
	public void upload() throws IOException {
		if (!ByteView.mainFrame.resourcePane.files.contains(file)) {
			ZipFileContainer container = new ZipFileContainer(file);
			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.isDirectory()) {
						continue;
					}

					String entryName = entry.getName();
					if (entryName.endsWith(".class")) {
						StringBuilder directory = new StringBuilder();
						byte[] bytes = FileMisc.readBytes(zis);
						String[] split = entryName.split("/");
						String fileName = split[split.length - 1];
						for (int i = 0; i < split.length - 1; i++) {
							directory.append(split[i]).append(File.separator);
						}

						ClassFileContainer classFileContainer = new ClassFileContainer(bytes, fileName);
						classFileContainer.rootNode = new MyTreeNode(FileMisc.removeExtension(container.fileName) + File.separator + directory);
						container.fileEntries.put(FileMisc.removeExtension(entryName), classFileContainer);
					} else if (entryName.endsWith(".MF")) {
						byte[] bytes = FileMisc.readBytes(zis);
						String[] split = entryName.split("/");
						FileContainer fileContainer = new FileContainer(bytes, split[1]);
						fileContainer.rootNode = new MyTreeNode(split[0]);
						container.fileEntries.put(FileMisc.removeExtension(entryName), fileContainer);
					}

					zis.closeEntry();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			ByteView.mainFrame.resourcePane.files.add(file);

			SwingUtilities.invokeLater(() -> ByteView.mainFrame.resourcePane.addResource(container));
		}
	}
}
