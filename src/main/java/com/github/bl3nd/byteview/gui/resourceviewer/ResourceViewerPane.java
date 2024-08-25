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

package com.github.bl3nd.byteview.gui.resourceviewer;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.bl3nd.byteview.ByteView;
import com.github.bl3nd.byteview.files.FileContainer;
import com.github.bl3nd.byteview.files.ZipFileContainer;
import com.github.bl3nd.byteview.gui.resourceviewer.pages.Page;
import com.github.bl3nd.byteview.misc.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

/**
 * Created by Bl3nd.
 * Date: 5/12/2024
 */
public class ResourceViewerPane extends JPanel {
	private final JTabbedPane tabbedPane;
	private final HashMap<String, Page> pages = new LinkedHashMap<>();

	public ResourceViewerPane() {
		super(new BorderLayout());
		tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addChangeListener(e -> {
			JTabbedPane pane = (JTabbedPane) e.getSource();
			Page page = (Page) pane.getSelectedComponent();
			if (page == null) {
				return;
			}

			if (ByteView.mainFrame.fileStructurePane.openedContainer.equals(page.getFileContainer())) {
				return;
			}

			ByteView.mainFrame.fileStructurePane.showContainerStructure(page.getFileContainer());
		});

		tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
		tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
		tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK,
				(BiConsumer<JTabbedPane, Integer>) this::removeTab
		);

		add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);
	}

	private void removeTab(@NotNull JTabbedPane tabPane, Integer tabIndex) {
		pages.remove(tabPane.getTitleAt(tabIndex));
		ByteView.mainFrame.fileStructurePane.removeContainerStructure();
		tabPane.removeTabAt(tabIndex);
	}

	public void removeAllTabsRelatedToArchive(FileContainer container) {
		@SuppressWarnings("unchecked") HashMap<String, Page> copy = (HashMap<String, Page>) pages.clone();
		copy.forEach((String title, Page page) -> {
			if (container instanceof ZipFileContainer zip) {
				zip.fileEntries.forEach((String _, FileContainer fileContainer) -> {
					if (page.getFileContainer().equals(fileContainer)) {
						removeTab(tabbedPane, tabbedPane.indexOfTab(title));
					}
				});
			}
		});
	}

	public void addPage(@NotNull Page page) {
		if (pages.containsKey(page.getName())) {
			tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(page.getName()));
			page.getTextArea().requestFocusInWindow();
			return;
		}

		Icon icon = null;
		if (page.name.endsWith(".java")) {
			icon = Icons.classFileIcon;
		}

		tabbedPane.addTab(page.getName(), icon, page);
		final int tabIndex = tabbedPane.indexOfTab(page.name);
		tabbedPane.setSelectedIndex(tabIndex);

		page.getTextArea().requestFocusInWindow();
		pages.put(page.getName(), page);
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public HashMap<String, Page> getPages() {
		return pages;
	}
}
