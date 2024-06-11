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

package com.github.bl3nd.byteview.gui.fileviewer.actions;

import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.files.uploading.ClassFileUploader;
import com.github.bl3nd.byteview.files.uploading.ZipFileUploader;
import com.github.bl3nd.byteview.gui.fileviewer.util.FileExtensionFilter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bl3nd.
 * Date: 5/13/2024
 */
public class FileActions {
	private static final String[] FILE_EXTENSIONS = new String[]{"jar", "zip", "war", "class"};

	public static void openFileExplorer() {
		List<String> extensions = new ArrayList<>(Arrays.stream(FILE_EXTENSIONS).toList());
		Collections.sort(extensions);
		StringBuilder builder = new StringBuilder();
		for (String extension : extensions) {
			builder.append("*.").append(extension).append(", ");
		}

		builder.setLength(builder.length() - 2);
		String description = builder.toString();
		String[] extArray = extensions.toArray(new String[0]);
		JFileChooser chooser = new JFileChooser();
		chooser.removeChoosableFileFilter(chooser.getFileFilter());
		chooser.setFileFilter(new FileExtensionFilter("Accepted Files (" + description + ")", extArray));

		chooser.setCurrentDirectory(ByteView.configuration.getRecentUploadedDirectory());

		if (chooser.showOpenDialog(ByteView.mainFrame) == JFileChooser.APPROVE_OPTION) {
			ByteView.configuration.setRecentUploadedDirectory(chooser.getSelectedFile());
			ByteView.configuration.addToRecentFiles(chooser.getSelectedFile().getAbsolutePath());
			try {
				openFile(chooser.getSelectedFile());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void openFile(@NotNull File selectedFile) throws IOException {
		String fileExtension =
				selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf(".") + 1);
		if (fileExtension.equals(FILE_EXTENSIONS[0])) {// JAR
			ZipFileUploader archiveUploader = new ZipFileUploader(selectedFile);
			archiveUploader.upload();
		} else if (fileExtension.equals(FILE_EXTENSIONS[1])) { // ZIP
			System.err.println("Zip");
		} else if (fileExtension.equals(FILE_EXTENSIONS[2])) { // WAR
			System.err.println("war");
		} else if (fileExtension.equals(FILE_EXTENSIONS[3])) {// Class
			ClassFileUploader classFileUploader = new ClassFileUploader(selectedFile);
			classFileUploader.upload();
		}
	}
}
