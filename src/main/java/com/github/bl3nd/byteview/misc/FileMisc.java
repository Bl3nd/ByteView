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

package com.github.bl3nd.byteview.misc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.io.*;

/**
 * Created by Bl3nd.
 * Date: 5/14/2024
 */
public class FileMisc {
	public static @NotNull String removeExtension(@NotNull String fileName) {
		int lastPeriod = fileName.lastIndexOf('.');
		if (lastPeriod > 0) {
			return fileName.substring(0, lastPeriod);
		}

		return fileName;
	}

	@Contract(pure = true)
	public synchronized static @NotNull String read(File output) throws IOException {
		FileInputStream fis = new FileInputStream(output);
		StringBuilder result = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
		}

		return result.toString();
	}

	public static byte[] readBytes(@NotNull File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			return InterpreterUtil.readBytes(fis, (int) file.length());
		}
	}
}
