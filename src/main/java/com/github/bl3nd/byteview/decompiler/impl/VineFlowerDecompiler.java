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

package com.github.bl3nd.byteview.decompiler.impl;

import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.decompiler.Decompiler;
import com.github.bl3nd.byteview.files.ClassFileContainer;
import com.github.bl3nd.byteview.files.FileContainer;
import com.github.bl3nd.byteview.misc.ClassMemberLocation;
import com.github.bl3nd.byteview.misc.FileMisc;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.github.bl3nd.byteview.misc.Constants.TEMP_LOCATION;

/**
 * Created by Bl3nd.
 * Date: 5/22/2024
 */
public class VineFlowerDecompiler extends Decompiler {
	public VineFlowerDecompiler(FileContainer container) {
		super(container, container.getFileName());
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public String decompile(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		final File tempFile = new File(TEMP_LOCATION + File.separator + fileName);
		if (!tempFile.exists()) {
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			out.write(bytes);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write bytes to temp file! " + e);
		}

		String exception = "";
		try {
			ConsoleDecompiler.main(generateMainMethod(tempFile.getAbsolutePath(),
					new File(TEMP_LOCATION).getAbsolutePath()));
		} catch (Throwable t) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			t.printStackTrace();
			exception = t.getMessage();
		}

		tempFile.delete();

		String fileName = FileMisc.removeExtension(tempFile.getName());
		final File output = new File(TEMP_LOCATION + File.separator + fileName + ".java");
		if (output.exists()) {
			String content;
			try {
				content = FileMisc.read(output);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			output.delete();
			int index = content.indexOf("Tokens:") - 3;
			String tokenString = content.substring(index);
			tokenString = tokenString.replace("/*\nTokens:", "").replace("*/", "").trim();
			BufferedReader reader = new BufferedReader(new StringReader(tokenString));
			reader.lines().forEach(this::parse);

			content = content.substring(0, index - 2);
			return content;
		}

		return exception;
	}

	private HashMap<String, ArrayList<ClassMemberLocation>> locations = new HashMap<>();

	private void parse(@NotNull String s) {
		String methodString = s;
		s = s.replace("(", "").replace(",", "").replace(")", "").replace("[", "").replace("]", "");
		String[] strings = s.split(" ");
		if (strings[2].equalsIgnoreCase("field")) {
			handleField(strings);
		}

		if (strings[2].equalsIgnoreCase("parameter")) {
			handleParameter(strings);
		}

		if (strings[2].equalsIgnoreCase("local")) {
			handleLocal(strings);
		}

		if (strings[2].equalsIgnoreCase("method")) {
			handleMethod(methodString);
		}

		if (!strings[2].equalsIgnoreCase("field")) {
			if (!strings[2].equalsIgnoreCase("parameter")) {
				if (!strings[2].equalsIgnoreCase("local")) {
					if (!strings[2].equalsIgnoreCase("method")) {
						System.err.println(Arrays.toString(strings));
					}
				}
			}
		}
	}

	private void handleField(String @NotNull [] strings) {
		int[] position = getMemberPosition(strings[0], strings[1]);
		String owner = strings[4].substring(0, strings[4].indexOf("#"));
		String fieldName = strings[4].substring(strings[4].indexOf("#") + 1, strings[4].indexOf(":"));
		ClassMemberLocation location = new ClassMemberLocation(owner, "", strings[3], position[0], position[1],
				position[2]);
		putField(fieldName, location);
	}

	private void handleParameter(String @NotNull [] strings) {
		int[] position = getMemberPosition(strings[0], strings[1]);
		String owner = strings[4].substring(0, strings[4].indexOf("#"));
		String method = strings[4].substring(strings[4].indexOf("#") + 1, strings[4].indexOf(":"));
		String parameterName = strings[4].substring(strings[4].indexOf(":") + 1);
		ClassMemberLocation location = new ClassMemberLocation(owner, method, strings[3], position[0], position[1],
				position[2]);
		putParameter(parameterName, location);
	}

	private void handleLocal(String @NotNull [] strings) {
		int[] position = getMemberPosition(strings[0], strings[1]);
		String owner = strings[4].substring(0, strings[4].indexOf("#"));
		String method = strings[4].substring(strings[4].indexOf("#") + 1, strings[4].indexOf(":"));
		String localName = strings[4].substring(strings[4].indexOf(":") + 1);
		ClassMemberLocation location = new ClassMemberLocation(owner, method, strings[3], position[0], position[1],
				position[2]);
		putLocal(localName, location);
	}

	private void handleMethod(@NotNull String text) {
		String[] s = text.split(" ");
		int[] position = getMemberPosition(s[0].replace("(", "").replace(",", ""), s[1].replace(")", "").replace(",",
				""));
		String owner = s[4].substring(0, s[4].indexOf("#"));
		String ownerPackage = owner;
		if (owner.contains("/")) {
			owner = owner.substring(owner.lastIndexOf("/") + 1);
		}

		String methodName = s[4].substring(s[4].indexOf("#") + 1, s[4].indexOf("("));
		putMethod(methodName, new ClassMemberLocation(owner, "", s[3].replace("[", "").replace("]", ""), position[0],
				position[1], position[2]));
	}

	@Contract(pure = true)
	private int @NotNull [] getMemberPosition(@NotNull String start, @NotNull String end) {
		int line = Integer.parseInt(start.split(":")[0]);
		int startColumn = Integer.parseInt(start.split(":")[1]);
		int endColumn = Integer.parseInt(end.split(":")[1]);
		return new int[]{line, startColumn, endColumn};
	}

	private void putField(String key, ClassMemberLocation value) {
		((ClassFileContainer) fileContainer).fieldMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	private void putParameter(String key, ClassMemberLocation value) {
		((ClassFileContainer) fileContainer).methodParameterMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	private void putLocal(String key, ClassMemberLocation value) {
		((ClassFileContainer) fileContainer).methodLocalMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	private void putMethod(String key, ClassMemberLocation value) {
		((ClassFileContainer) fileContainer).methodMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	@Contract(value = "_, _ -> new", pure = true)
	private String @NotNull [] generateMainMethod(String className, String folder) {
		return new String[]{
//				"--add-external=" + ByteView.configuration.getRecentUploadedDirectory(),
				getSetting("--ascii-strings"),
				getSetting("--boolean-as-int"),
				getSetting("--bytecode-source-mapping"),
				getSetting("--decompile-assert"),
				getSetting("--decompile-complex-constant-dynamic"),
				getSetting("--decompile-enums"),
				getSetting("--decompile-finally"),
				getSetting("--decompile-generics"),
				getSetting("--decompile-inner"),
				getSetting("--decompile-java4"),
				getSetting("--decompile-preview"),
				getSetting("--decompile-switch-expressions"),
				getSetting("--decompiler-comments"),
				getSetting("--dump-bytecode-on-error"),
				getSetting("--dump-code-lines"),
				getSetting("--dump-exception-on-error"),
				"--dump-text-tokens=true",
				"--ensure-synchronized-monitors=true",
//				"--error-message=\"\"",
				getSetting("--explicit-generics"),
				getSetting("--force-jsr-inline"),
				getSetting("--hide-default-constructor"),
				getSetting("--hide-empty-super"),
				getSetting("--ignore-invalid-bytecode"),
				getSetting("--include-classpath"),
//				"--include-runtime=",
				getSetting("--incorporate-returns"),
//				"--indent-string=",
				getSetting("--inline-simple-lambdas"),
				getSetting("--keep-literals"),
				getSetting("--lambda-to-anonymous-class"),
				getSetting("--mark-corresponding-synthetics"),
				getSetting("--override-annotation"),
				getSetting("--pattern-matching"),
				getSetting("--remove-bridge"),
				getSetting("--remove-empty-try-catch"),
				getSetting("--remove-getclass"),
				"--remove-imports=false",
				getSetting("--remove-synthetic"),
				"--rename-members=true",
				getSetting("--show-hidden-statements"),
				"--simplify-stack=true",
				getSetting("--skip-extra-files"),
				"--sourcefile-comments=true",
				getSetting("--synthetic-not-set"),
				getSetting("--ternary-constant-simplification"),
				getSetting("--ternary-in-if"),
				getSetting("--try-loop-fix"),
				getSetting("--undefined-as-object"),
				getSetting("--use-lvt-names"),
				"--use-method-parameters=true",
				getSetting("--verify-anonymous-classes"),
				getSetting("--verify-merges"),
				"-warn-inconsistent-inner-attributes=true",
				className,
//				ByteView.configuration.getRecentUploadedDirectory().getAbsolutePath(),
				folder
		};
	}

	private @NotNull String getSetting(String setting) {
		return setting + "=" + ByteView.configuration.getVineFlowerSettings().get(setting);
	}
}
