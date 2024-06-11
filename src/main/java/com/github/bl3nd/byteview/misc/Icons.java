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

import com.github.weisj.darklaf.properties.icons.IconLoader;
import com.github.weisj.darklaf.properties.icons.IconResolver;

import javax.swing.*;

/**
 * Created by Bl3nd.
 * Date: 5/12/2024
 */
public class Icons {

	public static final Icon filesIcon;
	public static final Icon folderIcon;
	public static final Icon settingsIcon;
	public static final Icon uploadFileIcon;
	public static final Icon javaFileIcon;
	public static final Icon classFileIcon;
	public static final Icon fieldIcon;
	public static final Icon fieldPrivateIcon;
	public static final Icon fieldStaticIcon;
	public static final Icon fieldPrivateStaticIcon;
	public static final Icon methodIcon;
	public static final Icon methodPrivateIcon;
	public static final Icon methodStaticIcon;
	public static final Icon methodPrivateStaticIcon;
	public static final Icon closeIcon;

	static {
		IconResolver iconResolver = IconLoader.get();
		filesIcon = iconResolver.getIcon("icons/file_list.svg", false);
		uploadFileIcon = iconResolver.getIcon("icons/import_file.svg", false);
		folderIcon = iconResolver.getIcon("icons/folder.svg", false);
		settingsIcon = iconResolver.getIcon("icons/settings.svg", false);
		javaFileIcon = iconResolver.getIcon("icons/java_file.svg", false);
		classFileIcon = iconResolver.getIcon("icons/class_file.svg", false);
		fieldIcon = iconResolver.getIcon("icons/field.svg", false);
		fieldPrivateIcon = iconResolver.getIcon("icons/field_private.svg", false);
		fieldStaticIcon = iconResolver.getIcon("icons/field_static.svg", false);
		fieldPrivateStaticIcon = iconResolver.getIcon("icons/field_private_static.svg", false);
		methodIcon = iconResolver.getIcon("icons/method.svg", false);
		methodPrivateIcon = iconResolver.getIcon("icons/method_private.svg", false);
		methodStaticIcon = iconResolver.getIcon("icons/method_static.svg", false);
		methodPrivateStaticIcon = iconResolver.getIcon("icons/method_private_static.svg", false);
		closeIcon = iconResolver.getIcon("icons/close.svg", false);
	}
}
