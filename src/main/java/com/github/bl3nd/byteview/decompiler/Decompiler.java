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

package com.github.bl3nd.byteview.decompiler;

import com.github.bl3nd.byteview.decompiler.impl.VineFlowerDecompiler;
import com.github.bl3nd.byteview.files.FileContainer;

/**
 * Created by Bl3nd.
 * Date: 5/22/2024
 */
public abstract class Decompiler {
	public FileContainer fileContainer;
	public final String fileName;

	/**
	 * A decompiler
	 *
	 * @param container the container to decompile
	 * @param fileName  the container's file name
	 */
	public Decompiler(final FileContainer container, final String fileName) {
		this.fileName = fileName;
		this.fileContainer = container;
	}

	/**
	 * Get the decompiler from the user's configuration
	 *
	 * @param currentDecompiler the user's current decompiler
	 * @param container the container to decompile
	 * @return the decompiler
	 */
	public static Decompiler getDecompiler(String currentDecompiler, FileContainer container) {
		if (currentDecompiler == null || currentDecompiler.equalsIgnoreCase("none")) {
			return null;
		}

		if (currentDecompiler.equalsIgnoreCase("VineFlower")) {
			return new VineFlowerDecompiler(container);
		}

		return null;
	}

	public abstract void decompile(byte[] bytes);

	/**
	 * The type of decompilers implemented. Used mainly for the decompiler dialog.
	 */
	public enum Types {
		None("none"),
		VineFlower("VineFlower"),
		;

		final String name;

		Types(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
