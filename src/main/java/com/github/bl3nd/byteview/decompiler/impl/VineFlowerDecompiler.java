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
import com.github.bl3nd.byteview.files.ZipFileContainer;
import com.github.bl3nd.byteview.gui.components.MyTreeNode;
import com.github.bl3nd.byteview.tokens.location.*;
import com.github.bl3nd.byteview.misc.Constants;
import com.github.bl3nd.byteview.misc.FileMisc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Manifest;

/**
 * Created by Bl3nd.
 * Date: 6/17/2024
 */
public class VineFlowerDecompiler extends Decompiler implements IResultSaver, AutoCloseable {
	private static final Path TEMP_LOCATION = new File(Constants.TEMP_LOCATION).toPath();
	private final Fernflower engine;

	public VineFlowerDecompiler(FileContainer container) {
		this(container, container.getFileName(), getDefaults(), new PrintStreamLogger(System.out));
	}

	private VineFlowerDecompiler(FileContainer container, String fileName, Map<String, Object> options, IFernflowerLogger logger) {
		super(container, fileName);
		engine = new Fernflower(this, options, logger);
	}

	private static @NotNull @UnmodifiableView Map<String, Object> getDefaults() {
		return Map.ofEntries(
				Map.entry("ascii-strings", getSetting("--ascii-strings")),
				Map.entry("boolean-as-int", getSetting("--boolean-as-int")),
				Map.entry("bytecode-source-mapping", getSetting("--bytecode-source-mapping")),
				Map.entry("decompile-assert", getSetting("--decompile-assert")),
				Map.entry("decompile-complex-constant-dynamic", getSetting("--decompile-complex-constant-dynamic")),
				Map.entry("decompile-enums", getSetting("--decompile-enums")),
				Map.entry("decompile-generics", getSetting("--decompile-generics")),
				Map.entry("decompile-inner", getSetting("--decompile-inner")),
				Map.entry("decompile-java4", getSetting("--decompile-java4")),
				Map.entry("decompile-preview", getSetting("--decompile-preview")),
				Map.entry("decompile-switch-expressions", getSetting("--decompile-switch-expressions")),
				Map.entry("decompiler-comments", getSetting("--decompiler-comments")),
				Map.entry("dump-bytecode-on-error", getSetting("--dump-bytecode-on-error")),
				Map.entry("dump-code-lines", getSetting("--dump-code-lines")),
				Map.entry("dump-exception-on-error", getSetting("--dump-exception-on-error")),
				Map.entry("dump-text-tokens", "1"),
				Map.entry("ensure-synchronized-monitors", "1"),
				Map.entry("explicit-generics", getSetting("--explicit-generics")),
				Map.entry("force-jsr-inline", getSetting("--force-jsr-inline")),
				Map.entry("hide-default-constructor", getSetting("--hide-default-constructor")),
				Map.entry("hide-empty-super", getSetting("--hide-empty-super")),
				Map.entry("ignore-invalid-bytecode", getSetting("--ignore-invalid-bytecode")),
				Map.entry("include-classpath", getSetting("--include-classpath")),
				Map.entry("incorporate-returns", getSetting("--incorporate-returns")),
				Map.entry("inline-simple-lambdas", getSetting("--inline-simple-lambdas")),
				Map.entry("keep-literals", getSetting("--keep-literals")),
				Map.entry("lambda-to-anonymous-class", getSetting("--lambda-to-anonymous-class")),
				Map.entry("mark-corresponding-synthetics", getSetting("--mark-corresponding-synthetics")),
				Map.entry("override-annotation", getSetting("--override-annotation")),
				Map.entry("pattern-matching", getSetting("--pattern-matching")),
				Map.entry("remove-bridge", getSetting("--remove-bridge")),
				Map.entry("remove-empty-try-catch", getSetting("--remove-empty-try-catch")),
				Map.entry("remove-getclass", getSetting("--remove-getclass")),
				Map.entry("remove-imports", "0"),
				Map.entry("remove-synthetic", getSetting("--remove-synthetic")),
				Map.entry("rename-members", "1"),
				Map.entry("show-hidden-statements", getSetting("--show-hidden-statements")),
				Map.entry("simplify-stack", "1"),
				Map.entry("skip-extra-files", getSetting("--skip-extra-files")),
				Map.entry("sourcefile-comments", "1"),
				Map.entry("synthetic-not-set", getSetting("--synthetic-not-set")),
				Map.entry("ternary-constant-simplification", getSetting("--ternary-constant-simplification")),
				Map.entry("ternary-in-if", getSetting("--ternary-in-if")),
				Map.entry("try-loop-fix", getSetting("--try-loop-fix")),
				Map.entry("undefined-as-object", getSetting("--undefined-as-object")),
				Map.entry("use-lvt-names", getSetting("--use-lvt-names")),
				Map.entry("use-method-parameters", "1"),
				Map.entry("verify-anonymous-classes", getSetting("--verify-anonymous-classes")),
				Map.entry("verify-merges", getSetting("--verify-merges")),
				Map.entry("warn-inconsistent-inner-attributes", "1")
		);
	}

	private static @NotNull String getSetting(String setting) {
		boolean flag = ByteView.configuration.getVineFlowerSettings().get(setting);
		if (flag) {
			return "1";
		} else {
			return "0";
		}
	}

	private File root;
	private ZipFileContainer archiveContainer;

	@Override
	public void decompile(byte[] bytes) {
		final File tempFile;
		if (this.fileContainer.rootNode.getUserObject().toString().contains(File.separator)) {
			root = new File(TEMP_LOCATION + File.separator + this.fileContainer.rootNode.getUserObject().toString());
			if (!root.exists()) {
				//noinspection ResultOfMethodCallIgnored
				root.mkdirs();
			}

			tempFile = new File(root + File.separator + fileName);
		} else {
			tempFile = new File(TEMP_LOCATION + File.separator + fileName);
		}

		if (!tempFile.exists()) {
			try {
				//noinspection ResultOfMethodCallIgnored
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

		if (ByteView.configuration.getDecompileEntireArchive()) {
			String root = this.fileContainer.rootNode.getUserObject().toString();
			if (!root.contains(".class")) {
				String archiveName = root.substring(0, root.lastIndexOf(File.separator));
				archiveContainer = (ZipFileContainer) ByteView.mainFrame.resourcePane.uploadedFiles.get(archiveName + ".jar");
				engine.addSource(archiveContainer.file);
			}
		}

		engine.addSource(tempFile);

		this.decompileContext();

		//noinspection ResultOfMethodCallIgnored
		tempFile.delete();

		String fileName = this.qualifiedName;
		String zipFile = this.fileContainer.rootNode.getUserObject().toString();
		final File output;
		if (zipFile.contains(File.separator)) {
			output =
					new File(TEMP_LOCATION + File.separator + zipFile.substring(0, zipFile.indexOf(File.separator)) + File.separator + fileName);
		} else {
			output = new File(TEMP_LOCATION + File.separator + fileName);
		}

		if (output.exists()) {
			String content;
			try {
				content = FileMisc.read(output);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			//noinspection ResultOfMethodCallIgnored
			output.delete();

			if (root != null) {
				deleteDirectory(TEMP_LOCATION.toFile());
			}

			content = readTokens(content, (ClassFileContainer) fileContainer);

			this.fileContainer.setContent(content);
			((ClassFileContainer) this.fileContainer).setHasBeenDecompiled(true);
			this.fileContainer.setDecompilerUsed("VineFlower");

			if (ByteView.configuration.getDecompileEntireArchive()) {
				MyTreeNode root = ByteView.mainFrame.resourcePane.root.getChildByUserObject(zipFile.substring(0, zipFile.length() - 1) +
						".jar");
				MyTreeNode node = root.getChildByUserObject(this.fileContainer.getFileName());
				node.setUserObject(fileName);
				SwingUtilities.invokeLater(() -> ByteView.mainFrame.resourcePane.tree.updateUI());
			}
		}
	}

	private @NotNull String readTokens(@NotNull String content, ClassFileContainer container) {
		int index = content.indexOf("Tokens:") - 3;
		String tokenString = content.substring(index);
		tokenString = tokenString.replace("/*\nTokens:", "").replace("*/", "").trim();
		BufferedReader reader = new BufferedReader(new StringReader(tokenString));
		reader.lines().forEach(s -> handleToken(s, container));
		return content.substring(0, index - 2);
	}

	/**
	 * Handle each token given from VineFlower and add it to our maps
	 *
	 * @param s the token string
	 */
	private void handleToken(@NotNull String s, ClassFileContainer container) {
		String methodString = s;
		s = replaceUnnecessary(s);
		String[] strings = s.split(" ");
		/*if (strings[2].equalsIgnoreCase("field")) {
			handleField(strings, container);
		}

		if (strings[2].equalsIgnoreCase("parameter")) {
			handleParameter(strings, container);
		}

		if (strings[2].equalsIgnoreCase("local")) {
			handleLocal(strings, container);
		}

		if (strings[2].equalsIgnoreCase("method")) {
			handleMethod(methodString, container);
		}

		// TODO: Once all token types are handled, remove this
		if (!strings[2].equalsIgnoreCase("field")) {
			if (!strings[2].equalsIgnoreCase("parameter")) {
				if (!strings[2].equalsIgnoreCase("local")) {
					if (!strings[2].equalsIgnoreCase("method")) {
						System.err.println(Arrays.toString(strings));
					}
				}
			}
		}*/
	}

	/**
	 * Get rid of unnecessary stuff from the token string
	 *
	 * @param s the token
	 * @return the cleaned token
	 */
	private @NotNull String replaceUnnecessary(@NotNull String s) {
		s = s.replace("(", "").replace(",", "").replace(")", "").replace("[", "").replace("]", "");
		return s;
	}

	/**
	 * Handle a field token
	 *
	 * @param tokenArray the token as an array for easier use
	 */
	private void handleField(String @NotNull [] tokenArray, ClassFileContainer container) {
		int[] position = getMemberPosition(tokenArray[0], tokenArray[1]);
		String owner = tokenArray[4].substring(0, tokenArray[4].indexOf("#"));
		String fieldName = tokenArray[4].substring(tokenArray[4].indexOf("#") + 1, tokenArray[4].indexOf(":"));
		putField(fieldName, new ClassFieldLocation(owner, tokenArray[3], position[0], position[1], position[2]), container);
	}

	/**
	 * Handle a parameter token
	 *
	 * @param tokenArray the token as an array for easier use
	 */
	@SuppressWarnings("DuplicatedCode")
	private void handleParameter(String @NotNull [] tokenArray, ClassFileContainer container) {
		int[] position = getMemberPosition(tokenArray[0], tokenArray[1]);
		String owner = tokenArray[4].substring(0, tokenArray[4].indexOf("#"));
		String method = tokenArray[4].substring(tokenArray[4].indexOf("#") + 1, tokenArray[4].indexOf(":"));
		String parameterName = tokenArray[4].substring(tokenArray[4].indexOf(":") + 1);
		putParameter(parameterName, new ClassParameterLocation(owner, method, tokenArray[3], position[0], position[1], position[2]),
				container);
	}

	/**
	 * Handle a local variable token
	 *
	 * @param tokenArray the token as an array for easier use
	 */
	@SuppressWarnings("DuplicatedCode")
	private void handleLocal(String @NotNull [] tokenArray, ClassFileContainer container) {
		int[] position = getMemberPosition(tokenArray[0], tokenArray[1]);
		String owner = tokenArray[4].substring(0, tokenArray[4].indexOf("#"));
		String method = tokenArray[4].substring(tokenArray[4].indexOf("#") + 1, tokenArray[4].indexOf(":"));
		String localName = tokenArray[4].substring(tokenArray[4].indexOf(":") + 1);
		putLocal(localName, new ClassLocalVariableLocation(owner, method, tokenArray[3], position[0], position[1], position[2]),
				container);
	}

	/**
	 * Handle a method token
	 *
	 * @param methodToken the method token
	 */
	private void handleMethod(@NotNull String methodToken, ClassFileContainer container) {
		String[] split = methodToken.split(" ");
		int[] position = getMemberPosition(split[0].replace("(", "").replace(",", ""), split[1].replace(")", "").replace(",", ""));
		String owner = split[4].substring(0, split[4].indexOf("#"));
		String ownerPackage = owner;
		if (owner.contains("/")) {
			owner = owner.substring(owner.lastIndexOf("/") + 1);
		}

		String methodName = split[4].substring(split[4].indexOf("#") + 1, split[4].indexOf("("));
		String methodParameterTypes = split[4].substring(split[4].indexOf("(") + 1, split[4].indexOf(")"));
		putMethod(methodName, new ClassMethodLocation(owner, methodParameterTypes, split[3].replace("[", "").replace("]", ""),
				position[0], position[1], position[2]), container);
	}

	/**
	 * Get the position of the token
	 *
	 * @param start the starting line and column
	 * @param end   the ending line and column
	 * @return the position
	 */
	private int @NotNull [] getMemberPosition(@NotNull String start, @NotNull String end) {
		// We assume the token will be on the same line from start to finish
		int line = Integer.parseInt(start.split(":")[0]);
		int startColumn = Integer.parseInt(start.split(":")[1]);
		int endColumn = Integer.parseInt(end.split(":")[1]);
		return new int[]{line, startColumn, endColumn};
	}

	private void putField(String key, ClassFieldLocation value, ClassFileContainer fileContainer) {
		fileContainer.fieldMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	private void putParameter(String key, ClassParameterLocation value, ClassFileContainer fileContainer) {
		fileContainer.methodParameterMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	private void putLocal(String key, ClassLocalVariableLocation value, ClassFileContainer fileContainer) {
		fileContainer.methodLocalMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	private void putMethod(String key, ClassMethodLocation value, ClassFileContainer fileContainer) {
		fileContainer.methodMembers.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void deleteDirectory(@NotNull File root) {
		File[] files = root.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}

		if (!root.getName().equals("temp")) {
			root.delete();
		}
	}

	private void decompileContext() {
		try {
			engine.decompileContext();
		} finally {
			engine.clearContext();
		}
	}

	/**
	 * Save files into this path. TODO: When I implement saving...
	 *
	 * @param path The path of the folder
	 */
	@Override
	public void saveFolder(String path) {
		System.err.println("Save folder: " + path);
	}

	@Override
	public void copyFile(String source, String path, String entryName) {
		System.err.println("Copy file: " + source + " path: " + path + " entryName: " + entryName);
	}

	private String qualifiedName;

	@Override
	public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
		String zipFilePath = this.fileContainer.rootNode.getUserObject().toString();
		ZipFileContainer zipFileContainer = null;
		if (zipFilePath.contains(File.separator)) {
			path = zipFilePath.substring(0, zipFilePath.indexOf(File.separator));
			entryName = entryName.replace(".java", "");
			zipFileContainer = (ZipFileContainer) ByteView.mainFrame.resourcePane.uploadedFiles.get(zipFilePath.replace(File.separator,
					"") + ".jar");
		}

		if (zipFileContainer != null) {
			ClassFileContainer clazz = (ClassFileContainer) zipFileContainer.fileEntries.get(entryName);
		}

		this.qualifiedName = qualifiedName + ".java";
		Path entryPath = TEMP_LOCATION.resolve(path).resolve(this.qualifiedName);
		try (BufferedWriter writer = Files.newBufferedWriter(entryPath)) {
			if (content != null) {
				writer.write(content);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to save class", e);
		}
	}

	/**
	 * TODO: When implementing saving of the decompiled output.
	 *
	 * @param path        The path of the archive
	 * @param archiveName The name of the saved archive
	 * @param manifest    The manifest
	 */
	@Override
	public void createArchive(String path, String archiveName, Manifest manifest) {
		System.err.println("Create archive: " + path + " archiveName: " + archiveName + " manifest: " + manifest);
	}

	/**
	 * TODO: When implementing saving of the decompiled output.
	 *
	 * @param path        The path of the archive
	 * @param archiveName The name of the saved archive
	 * @param entryName   The entry name (e.g 'META-INF' & 'META-INF/'
	 */
	@Override
	public void saveDirEntry(String path, String archiveName, String entryName) {
		System.err.println("Save dir entry: " + path + " archiveName: " + archiveName + " entryName: " + entryName);
	}

	/**
	 * TODO: When implementing saving of the decompiled output.
	 *
	 * @param source      The source path of the archive file
	 * @param path        The desired destination path
	 * @param archiveName The archive name
	 * @param entry       Any entries to copy
	 */
	@Override
	public void copyEntry(String source, String path, String archiveName, String entry) {
		System.err.println("Copy entry: " + source + " path: " + path + " archiveName: " + archiveName + " entryName: " + entry);
	}

	@Override
	public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
		System.err.println("Save class entry: " + path + " archiveName: " + archiveName + " qualifiedName: " + qualifiedName + " entryName"
				+ ": " + entryName + " content: " + content);
		this.saveClassEntry(path, archiveName, qualifiedName, entryName, content, null);
	}

	@Override
	public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, @NotNull String content,
							   int[] mapping) {
		String start = "renamed from: ";
		int startIndex = content.indexOf(start);
		int endIndex = content.indexOf("\n", startIndex + 1);
		if (startIndex != -1 && endIndex != -1) {
			String s = content.substring(startIndex, endIndex);
			String decompiledName = s.substring(s.indexOf(":") + 1).trim();
			ClassFileContainer clazz = (ClassFileContainer) archiveContainer.fileEntries.get(decompiledName);
			if (clazz != null && !clazz.equals(this.fileContainer)) {
				ZipFileContainer zipContainer = (ZipFileContainer) ByteView.mainFrame.resourcePane.uploadedFiles.get(archiveName);
				zipContainer.fileEntries.values().remove(clazz);
				MyTreeNode root = ByteView.mainFrame.resourcePane.root.getChildByUserObject(archiveName);
				MyTreeNode node = root.getChildByUserObject(decompiledName + ".class");
				node.setUserObject(qualifiedName + ".class");
				SwingUtilities.invokeLater(() -> ByteView.mainFrame.resourcePane.tree.updateUI());
				clazz = new ClassFileContainer(clazz.bytes, qualifiedName + ".class");
				zipContainer.fileEntries.put(qualifiedName, clazz);
				clazz.setDecompilerUsed("VineFlower");
				clazz.setHasBeenDecompiled(true);
				content = readTokens(content, clazz);
				clazz.setContent(content);
			}
		}
	}

	@Override
	public void closeArchive(String path, String archiveName) {
		System.err.println("Close archive: " + path + " archiveName: " + archiveName);
	}

	@Override
	public void close() throws IOException {
		System.err.println("Close");
	}
}
