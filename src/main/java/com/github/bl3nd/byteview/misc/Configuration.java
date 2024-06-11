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

import com.eclipsesource.json.*;
import com.formdev.flatlaf.util.FontUtils;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.files.uploading.ClassFileUploader;
import com.github.bl3nd.byteview.files.uploading.ZipFileUploader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.bl3nd.byteview.misc.Constants.CONFIG_LOCATION;

/**
 * Created by Bl3nd.
 * Date: 5/18/2024
 */
public class Configuration {

	private static final int MAX_RECENT_FILES = 20;
	private final List<String> recentFiles = new ArrayList<>();
	private File recentUploadedDirectory;
	private String currentFontFamily;
	private boolean alwaysShowHierarchy = true;
	private String currentDecompiler;
	private final HashMap<String, Boolean> vineFlowerSettings = new HashMap<>();

	public void buildDocument() {
		try {
			JsonObject rootObject = Json.object();

			String fontFamily = UIManager.getFont("Label.font").getFamily();
			rootObject.add("fontFamily", currentFontFamily != null ? currentFontFamily : fontFamily);
			rootObject.add("recentUploadedDirectory", recentUploadedDirectory != null ?
					recentUploadedDirectory.getAbsolutePath() : System.getProperty("user.home"));
			rootObject.add("alwaysShowHierarchy", alwaysShowHierarchy);
			rootObject.add("currentDecompiler", "none");

			JsonObject vineFlowerSettings = Json.object();
			vineFlowerSettings.add("--ascii-strings", false);
			vineFlowerSettings.add("--boolean-as-int", true);
			vineFlowerSettings.add("--bytecode-source-mapping", false);
			vineFlowerSettings.add("--decompile-assert", true);
			vineFlowerSettings.add("--decompile-complex-constant-dynamic", false);
			vineFlowerSettings.add("--decompile-enums", true);
			vineFlowerSettings.add("--decompile-finally", true);
			vineFlowerSettings.add("--decompile-generics", true);
			vineFlowerSettings.add("--decompile-inner", true);
			vineFlowerSettings.add("--decompile-java4", true);
			vineFlowerSettings.add("--decompile-preview", true);
			vineFlowerSettings.add("--decompile-switch-expressions", true);
			vineFlowerSettings.add("--decompiler-comments", true);
			vineFlowerSettings.add("--dump-bytecode-on-error", true);
			vineFlowerSettings.add("--dump-code-lines", false);
			vineFlowerSettings.add("--dump-exception-on-error", true);
			vineFlowerSettings.add("--explicit-generics", false);
			vineFlowerSettings.add("--force-jsr-inline", false);
			vineFlowerSettings.add("--hide-default-constructor", true);
			vineFlowerSettings.add("--hide-empty-super", true);
			vineFlowerSettings.add("--ignore-invalid-bytecode", false);
			vineFlowerSettings.add("--include-classpath", false);
			vineFlowerSettings.add("--incorporate-returns", true);
			vineFlowerSettings.add("--inline-simple-lambdas", true);
			vineFlowerSettings.add("--keep-literals", false);
			vineFlowerSettings.add("--lambda-to-anonymous-class", false);
			vineFlowerSettings.add("--mark-corresponding-synthetics", false);
			vineFlowerSettings.add("--override-annotation", true);
			vineFlowerSettings.add("--pattern-matching", true);
			vineFlowerSettings.add("--remove-bridge", true);
			vineFlowerSettings.add("--remove-empty-try-catch", true);
			vineFlowerSettings.add("--remove-getclass", true);
			vineFlowerSettings.add("--remove-synthetic", true);
			vineFlowerSettings.add("--show-hidden-statements", false);
			vineFlowerSettings.add("--skip-extra-files", false);
			vineFlowerSettings.add("--synthetic-not-set", false);
			vineFlowerSettings.add("--ternary-constant-simplification", false);
			vineFlowerSettings.add("--ternary-in-if", true);
			vineFlowerSettings.add("--try-loop-fix", true);
			vineFlowerSettings.add("--undefined-as-object", true);
			vineFlowerSettings.add("--use-lvt-names", true);
			vineFlowerSettings.add("--verify-anonymous-classes", false);
			vineFlowerSettings.add("--verify-merges", false);

			rootObject.add("VineFlowerSettings", vineFlowerSettings);

			FileWriter writer = new FileWriter(CONFIG_LOCATION);
			rootObject.writeTo(writer, WriterConfig.PRETTY_PRINT);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void read() {
		try {
			FileReader fileReader = new FileReader(CONFIG_LOCATION);
			JsonObject rootObject = (JsonObject) Json.parse(fileReader);

			// Font
			JsonValue value = rootObject.get("fontFamily");
			currentFontFamily = value.asString();
			Font font = UIManager.getFont("Label.font");
			Font loadedFont = FontUtils.getCompositeFont(currentFontFamily, font.getStyle(), font.getSize());
			UIManager.put("defaultFont", loadedFont);

			// Recent directory
			value = rootObject.get("recentUploadedDirectory");
			recentUploadedDirectory = new File(value.asString());

			// Recent files
			value = rootObject.get("recentFiles");
			if (value != null) {
				JsonArray filesArray = value.asArray();
				filesArray.forEach(file -> {
					// This is not my favorite hack around getting each JsonArray string
					String[] files = file.asString().split(", ");
					for (String s : files) {
						if (s.startsWith("[")) {
							s = s.substring(1);
						}

						if (s.endsWith("]")) {
							s = s.substring(0, s.length() - 1);
						}

						if (!recentFiles.contains(s)) {
							recentFiles.add(s);
						}
					}
				});
			}

			// Hierarchy pane
			value = rootObject.get("alwaysShowHierarchy");
			alwaysShowHierarchy = value.asBoolean();

			value = rootObject.get("currentDecompiler");
			currentDecompiler = value.asString();

			value = rootObject.get("VineFlowerSettings");
			for (String setting : value.asObject().names()) {
				vineFlowerSettings.put(setting, value.asObject().getValue(setting).orElseThrow().asBoolean());
			}

			fileReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void updateJson(String name, Object value) {
		try {
			FileReader fileReader = new FileReader(CONFIG_LOCATION);
			JsonObject rootObject = (JsonObject) Json.parse(fileReader);
			if (value instanceof String) {
				rootObject.set(name, (String) value);
			} else if (value instanceof ArrayList<?>) {
				rootObject.set(name, Json.array(String.valueOf(value)));
			} else if (value instanceof Boolean) {
				rootObject.set(name, (Boolean) value);
			}

			fileReader.close();

			FileWriter writer = new FileWriter(CONFIG_LOCATION);
			rootObject.writeTo(writer, WriterConfig.PRETTY_PRINT);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setRecentUploadedDirectory(File file) {
		recentUploadedDirectory = file;
		updateJson("recentUploadedDirectory", recentUploadedDirectory.getAbsolutePath());
	}

	public File getRecentUploadedDirectory() {
		return recentUploadedDirectory;
	}

	public void setCurrentFontFamily(String currentFontFamily) {
		this.currentFontFamily = currentFontFamily;
		updateJson("fontFamily", currentFontFamily);
	}

	public String getCurrentFontFamily() {
		return currentFontFamily;
	}

	public void addToRecentFiles(String file) {
		recentFiles.remove(file);

		if (recentFiles.size() == MAX_RECENT_FILES) {
			recentFiles.removeLast();
		}

		recentFiles.addFirst(file);
		resetRecentFilesMenu();
		updateJson("recentFiles", recentFiles);
	}

	public List<String> getRecentFiles() {
		return recentFiles;
	}

	public void resetRecentFilesMenu() {
		ByteView.mainFrame.openRecentMenu.removeAll();
		for (String recentFile : recentFiles) {
			if (!recentFile.isEmpty()) {
				JMenuItem menuItem = new JMenuItem(recentFile);
				menuItem.addActionListener(e -> {
					JMenuItem source = (JMenuItem) e.getSource();
					File file = new File(source.getText());
					if (!ByteView.mainFrame.resourcePane.uploadedFiles.containsKey(file.getName())) {
						String extension =
								file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
						if (extension.equalsIgnoreCase("class")) {
							ClassFileUploader uploader = new ClassFileUploader(new File(source.getText()));
							uploader.upload();
							addToRecentFiles(file.getAbsolutePath());
						} else if (extension.equalsIgnoreCase("jar")) {
							ZipFileUploader uploader = new ZipFileUploader(new File(source.getText()));
							try {
								uploader.upload();
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}

							addToRecentFiles(file.getAbsolutePath());
						}
					}
				});

				ByteView.mainFrame.openRecentMenu.add(menuItem);
			}
		}
	}

	public void setAlwaysShowHierarchy(boolean alwaysShowHierarchy) {
		this.alwaysShowHierarchy = alwaysShowHierarchy;
		updateJson("alwaysShowHierarchy", alwaysShowHierarchy);
	}

	public boolean getAlwaysShowHierarchy() {
		return alwaysShowHierarchy;
	}

	public void setCurrentDecompiler(String currentDecompiler) {
		this.currentDecompiler = currentDecompiler;
		updateJson("currentDecompiler", currentDecompiler);
	}

	public String getCurrentDecompiler() {
		return currentDecompiler;
	}

	public boolean hasCurrentDecompiler() {
		return !currentDecompiler.equals("none");
	}

	public HashMap<String, Boolean> getVineFlowerSettings() {
		return vineFlowerSettings;
	}

	public void updateVineFlowerSettings(@NotNull HashMap<String, Boolean> settings) throws IOException {
		FileReader fileReader = new FileReader(CONFIG_LOCATION);
		JsonObject rootObject = (JsonObject) Json.parse(fileReader);
		JsonObject settingsObject = (JsonObject) rootObject.get("VineFlowerSettings");
		for (String setting : settings.keySet()) {
			settingsObject.set(setting, settings.get(setting));
		}

		settings.clear();

		fileReader.close();

		FileWriter writer = new FileWriter(CONFIG_LOCATION);
		rootObject.writeTo(writer, WriterConfig.PRETTY_PRINT);
		writer.close();
	}
}
