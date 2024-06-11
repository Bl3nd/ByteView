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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bl3nd.
 * Date: 5/22/2024
 */
public class Constants {
	public static final String FOLDER_LOCATION = System.getProperty("user.home") + "/Byte View";
	public static final String TEMP_LOCATION = FOLDER_LOCATION + "/temp";
	public static final String CONFIG_LOCATION = FOLDER_LOCATION + "/configuration.json";
	public static final List<String> decompilers;

	static {
		decompilers = new ArrayList<>();
		decompilers.add("Vine Flower");
	}
}
