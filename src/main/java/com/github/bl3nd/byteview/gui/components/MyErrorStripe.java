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

package com.github.bl3nd.byteview.gui.components;

import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is based on {@link ErrorStrip}, but with our own implementations to work with how occurrences are marked on
 * the text area.
 * <p>
 * Created by Bl3nd.
 * Date: 6/12/2024
 */
public class MyErrorStripe extends JPanel {
	private final RSyntaxTextArea textArea;
	private final transient Listener listener;

	public MyErrorStripe(RSyntaxTextArea textArea) {
		this.textArea = textArea;
		setLayout(null);
		listener = new Listener();
		addMouseListener(listener);
	}

	private int lineToY(int line, Rectangle r) {
		if (r == null) {
			r = new Rectangle();
		}

		textArea.computeVisibleRect(r);
		int h = r.height;
		float lineCount = textArea.getLineCount();
		int lineHeight = textArea.getLineHeight();
		int linesPerVisibleRect = h / lineHeight;
		return Math.round((h - 1) * line / Math.max(lineCount, linesPerVisibleRect));
	}

	private int yToLine(int y) {
		int line = -1;
		int h = textArea.getVisibleRect().height;
		int lineHeight = textArea.getLineHeight();
		int linesPerVisibleRect = h / lineHeight;
		int lineCount = textArea.getLineCount();
		if (y < h) {
			float at = y / (float) h;
			line = Math.round((Math.max(lineCount, linesPerVisibleRect) - 1) * at);
		}

		return line;
	}

	private void paintParserNoticeMarker(Graphics2D g, @NotNull ParserNotice notice, int width, int height) {
		Color borderColor = notice.getColor();
		if (borderColor == null) {
			borderColor = Color.BLACK;
		}

		Color fillColor = borderColor.brighter();
		g.setColor(fillColor);
		g.fillRect(0, 0, width, height);

		g.setColor(borderColor);
		g.drawRect(0, 0, width - 1, height - 1);
	}

	public void refreshMarkers() {
		removeAll();
		Map<Integer, Marker> markerMap = new HashMap<>();
		List<DocumentRange> occurrences = textArea.getMarkedOccurrences();
		addMarkersForRanges(occurrences, markerMap, textArea.getMarkOccurrencesColor());
		revalidate();
		repaint();
	}

	private void addMarkersForRanges(@NotNull List<DocumentRange> occurrences, Map<Integer, Marker> markerMap,
									 Color color) {
		for (DocumentRange range : occurrences) {
			int line;
			try {
				line = textArea.getLineOfOffset(range.getStartOffset());
			} catch (BadLocationException e) {
				continue;
			}

			ParserNotice notice = new MarkedOccurrenceNotice(range, color);
			Integer key = line;
			Marker m = markerMap.get(key);
			if (m == null) {
				m = new Marker(notice);
				m.addMouseListener(listener);
				markerMap.put(key, m);
				add(m);
			} else {
				if (!m.containsMarkedOccurrence()) {
					m.addNotice(notice);
				}
			}
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(14, textArea.getPreferredScrollableViewportSize().height);
	}

	@Override
	public void doLayout() {
		for (int i = 0; i < getComponentCount(); i++) {
			Marker m = (Marker) getComponent(i);
			m.updateLocation();
		}
	}

	@Override
	public void addNotify() {
		super.addNotify();
		refreshMarkers();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
	}

	private class Listener extends MouseAdapter {
		private final Rectangle r = new Rectangle();

		@Override
		public void mouseClicked(@NotNull MouseEvent e) {
			Component source = (Component) e.getSource();
			if (source instanceof MyErrorStripe.Marker marker) {
				marker.mouseClicked(e);
				return;
			}

			int line = yToLine(e.getY());
			if (line > -1) {
				try {
					int offset = textArea.getLineOfOffset(line);
					textArea.setCaretPosition(offset);
					RSyntaxUtilities.selectAndPossiblyCenter(textArea, new DocumentRange(offset, offset), false);
				} catch (BadLocationException exception) {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
			}
		}
	}

	private class MarkedOccurrenceNotice implements ParserNotice {
		private final DocumentRange range;
		private final Color color;

		MarkedOccurrenceNotice(DocumentRange range, Color color) {
			this.range = range;
			this.color = color;
		}

		@Override
		public boolean containsPosition(int pos) {
			return pos >= range.getStartOffset() && pos < range.getEndOffset();
		}

		@Override
		public Color getColor() {
			return color;
		}

		@Override
		public int getLength() {
			return range.getEndOffset() - range.getStartOffset();
		}

		@Override
		public Level getLevel() {
			return Level.INFO;
		}

		@Override
		public int getLine() {
			try {
				return textArea.getLineOfOffset(range.getStartOffset()) + 1;
			} catch (BadLocationException e) {
				return 0;
			}
		}

		@Override
		public boolean getKnowsOffsetAndLength() {
			return false;
		}

		@Contract(pure = true)
		@Override
		public @NotNull String getMessage() {
			return "";
		}

		@Override
		public int getOffset() {
			return range.getStartOffset();
		}

		@Override
		public Parser getParser() {
			return null;
		}

		@Override
		public boolean getShowInEditor() {
			return false;
		}

		@Override
		public String getToolTipText() {
			return null;
		}

		@Override
		public int compareTo(@NotNull ParserNotice o) {
			return 0;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

	private static final int MARKER_HEIGHT = 3;

	private class Marker extends JComponent {
		private final java.util.List<ParserNotice> notices;

		Marker(ParserNotice notice) {
			notices = new ArrayList<>();
			addNotice(notice);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setSize(getPreferredSize());
		}

		private void addNotice(ParserNotice notice) {
			notices.add(notice);
		}

		@Contract(value = " -> new", pure = true)
		@Override
		public @NotNull Dimension getPreferredSize() {
			return new Dimension(12, MARKER_HEIGHT);
		}

		@Override
		protected void paintComponent(Graphics g) {
			final ParserNotice notice = getHighestPriorityNotice();
			if (notice != null) {
				paintParserNoticeMarker((Graphics2D) g, notice, getWidth(), getHeight());
			}
		}

		protected void mouseClicked(MouseEvent e) {
			ParserNotice pn = notices.getFirst();
			int offs = pn.getOffset();
			int len = pn.getLength();
			if (offs > -1 && len > -1) { // These values are optional
				DocumentRange range = new DocumentRange(offs, offs + len);
				RSyntaxUtilities.selectAndPossiblyCenter(textArea, range, true);
			} else {
				int line = pn.getLine();
				try {
					offs = textArea.getLineStartOffset(line);
					textArea.getFoldManager().ensureOffsetNotInClosedFold(offs);
					textArea.setCaretPosition(offs);
				} catch (BadLocationException ble) { // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
			}
		}

		public boolean containsMarkedOccurrence() {
			boolean result = false;
			for (ParserNotice notice : notices) {
				if (notice instanceof MarkedOccurrenceNotice) {
					result = true;
					break;
				}
			}
			return result;
		}

		public ParserNotice getHighestPriorityNotice() {
			ParserNotice selectedNotice = null;
			int lowestLevel = Integer.MAX_VALUE;
			for (ParserNotice notice : notices) {
				if (notice.getLevel().getNumericValue() < lowestLevel) {
					lowestLevel = notice.getLevel().getNumericValue();
					selectedNotice = notice;
				}
			}

			return selectedNotice;
		}

		public void updateLocation() {
			int line = notices.getFirst().getLine();
			int y = lineToY(line - 1, null);
			setLocation(2, y);
		}
	}
}
