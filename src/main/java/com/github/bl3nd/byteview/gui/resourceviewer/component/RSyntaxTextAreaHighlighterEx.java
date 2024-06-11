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

package com.github.bl3nd.byteview.gui.resourceviewer.component;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rtextarea.SmartHighlightPainter;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bl3nd.
 * Date: 6/10/2024
 */
public class RSyntaxTextAreaHighlighterEx extends RSyntaxTextAreaHighlighter {
	private List<SyntaxLayeredHighlightInfoImpl> markedOccurrences = new ArrayList<>();
	private static final Color DEFAULT_PARSER_NOTICE_COLOR	= Color.RED;

	public Object addMarkedOccurrenceHighlight(int start, int end,
										SmartHighlightPainter p) throws BadLocationException {
		Document doc = textArea.getDocument();
		TextUI mapper = textArea.getUI();
		// Always layered highlights for marked occurrences.
		SyntaxLayeredHighlightInfoImpl i = new SyntaxLayeredHighlightInfoImpl();
		i.setPainter(p);
		i.setStartOffset(doc.createPosition(start));
		// HACK: Use "end-1" to prevent chars the user types at the "end" of
		// the highlight to be absorbed into the highlight (default Highlight
		// behavior).
		i.setEndOffset(doc.createPosition(end-1));
		markedOccurrences.add(i);
		mapper.damageRange(textArea, start, end);
		return i;
	}

	public void clearMarkOccurrencesHighlights() {
		// Don't remove via an iterator; since our List is an ArrayList, this
		// implies tons of System.arrayCopy()s
		for (HighlightInfo info : markedOccurrences) {
			repaintListHighlight(info);
		}
		markedOccurrences.clear();
	}

	@Override
	public void paintLayeredHighlights(Graphics g, int lineStart, int lineEnd,
									   Shape viewBounds, JTextComponent editor, View view) {
		paintListLayered(g, lineStart,lineEnd, viewBounds, editor, view, markedOccurrences);
		super.paintLayeredHighlights(g, lineStart, lineEnd, viewBounds, editor, view);
	}

	private static class SyntaxLayeredHighlightInfoImpl extends
			LayeredHighlightInfoImpl {

		private ParserNotice notice;

		@Override
		public Color getColor() {
			Color color = null;
			if (notice!=null) {
				color = notice.getColor();
				if (color==null) {
					color = DEFAULT_PARSER_NOTICE_COLOR;
				}
			}
			return color;
		}

		@Override
		public String toString() {
			return "[SyntaxLayeredHighlightInfoImpl: " +
					"startOffs=" + getStartOffset() +
					", endOffs=" + getEndOffset() +
					", color=" + getColor() +
					"]";
		}

	}
}
