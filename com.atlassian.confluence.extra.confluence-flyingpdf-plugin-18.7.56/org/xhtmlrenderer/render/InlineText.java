/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Rectangle;
import org.w3c.dom.Text;
import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.layout.FunctionData;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.CharCounts;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.Uu;

public class InlineText {
    private InlineLayoutBox _parent;
    private int _x;
    private String _masterText;
    private int _start;
    private int _end;
    private int _width;
    private FunctionData _functionData;
    private boolean _containedLF = false;
    private short _selectionStart;
    private short _selectionEnd;
    private float[] _glyphPositions;
    private boolean _trimmedLeadingSpace;
    private boolean _trimmedTrailingSpace;
    private Text _textNode;

    public void trimTrailingSpace(LayoutContext c) {
        if (!this.isEmpty() && this._masterText.charAt(this._end - 1) == ' ') {
            --this._end;
            this.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), this.getParent().getStyle().getFSFont(c), this.getSubstring()));
            this.setTrimmedTrailingSpace(true);
        }
    }

    public boolean isEmpty() {
        return this._start == this._end && !this._containedLF;
    }

    public String getSubstring() {
        if (this.getMasterText() != null) {
            if (this._start == -1 || this._end == -1) {
                throw new RuntimeException("negative index in InlineBox");
            }
            if (this._end < this._start) {
                throw new RuntimeException("end is less than setStartStyle");
            }
            return this.getMasterText().substring(this._start, this._end);
        }
        throw new RuntimeException("No master text set!");
    }

    public void setSubstring(int start, int end) {
        if (end < start) {
            Uu.p("setting substring to: " + start + " " + end);
            throw new RuntimeException("set substring length too long: " + this);
        }
        if (end < 0 || start < 0) {
            throw new RuntimeException("Trying to set negative index to inline box");
        }
        this._start = start;
        this._end = end;
        if (this._end > 0 && this._masterText.charAt(this._end - 1) == '\n') {
            this._containedLF = true;
            --this._end;
        }
    }

    public String getMasterText() {
        return this._masterText;
    }

    public void setMasterText(String masterText) {
        this._masterText = masterText;
    }

    public int getX() {
        return this._x;
    }

    public void setX(int x) {
        this._x = x;
    }

    public int getWidth() {
        return this._width;
    }

    public void setWidth(int width) {
        this._width = width;
    }

    public void paint(RenderingContext c) {
        c.getOutputDevice().drawText(c, this);
    }

    public void paintSelection(RenderingContext c) {
        c.getOutputDevice().drawSelection(c, this);
    }

    public InlineLayoutBox getParent() {
        return this._parent;
    }

    public void setParent(InlineLayoutBox parent) {
        this._parent = parent;
    }

    public boolean isDynamicFunction() {
        return this._functionData != null;
    }

    public FunctionData getFunctionData() {
        return this._functionData;
    }

    public void setFunctionData(FunctionData functionData) {
        this._functionData = functionData;
    }

    public void updateDynamicValue(RenderingContext c) {
        String value = this._functionData.getContentFunction().calculate(c, this._functionData.getFunction(), this);
        this._start = 0;
        this._end = value.length();
        this._masterText = value;
        this._width = c.getTextRenderer().getWidth(c.getFontContext(), this.getParent().getStyle().getFSFont(c), value);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("InlineText: ");
        if (this._containedLF || this.isDynamicFunction()) {
            result.append("(");
            if (this._containedLF) {
                result.append('L');
            }
            if (this.isDynamicFunction()) {
                result.append('F');
            }
            result.append(") ");
        }
        result.append('(');
        result.append(this.getSubstring());
        result.append(')');
        return result.toString();
    }

    public boolean updateSelection(RenderingContext c, Rectangle selection) {
        this.ensureGlyphPositions(c);
        float[] positions = this._glyphPositions;
        int y = this.getParent().getAbsY();
        int offset = this.getParent().getAbsX() + this.getX();
        short prevSelectionStart = this._selectionStart;
        short prevSelectionEnd = this._selectionEnd;
        boolean found = false;
        this._selectionStart = 0;
        this._selectionEnd = 0;
        for (int i = 0; i < positions.length - 2; i += 2) {
            Rectangle target = new Rectangle((int)((float)offset + (positions[i] + positions[i + 2]) / 2.0f), y, 1, this.getParent().getHeight());
            if (!selection.intersects(target)) continue;
            if (!found) {
                found = true;
                this._selectionStart = (short)(i / 2);
                this._selectionEnd = (short)(i / 2 + 1);
                continue;
            }
            this._selectionEnd = (short)(this._selectionEnd + 1);
        }
        return prevSelectionStart != this._selectionStart || prevSelectionEnd != this._selectionEnd;
    }

    private void ensureGlyphPositions(RenderingContext c) {
        if (this._glyphPositions == null) {
            FSGlyphVector glyphVector = c.getTextRenderer().getGlyphVector(c.getOutputDevice(), this.getParent().getStyle().getFSFont(c), this.getSubstring());
            this._glyphPositions = c.getTextRenderer().getGlyphPositions(c.getOutputDevice(), this.getParent().getStyle().getFSFont(c), glyphVector);
        }
    }

    public boolean clearSelection() {
        boolean result = this._selectionStart != 0 || this._selectionEnd != 0;
        this._selectionStart = 0;
        this._selectionEnd = 0;
        return result;
    }

    public boolean isSelected() {
        return this._selectionStart != this._selectionEnd;
    }

    public short getSelectionEnd() {
        return this._selectionEnd;
    }

    public short getSelectionStart() {
        return this._selectionStart;
    }

    public String getSelection() {
        return this.getSubstring().substring(this._selectionStart, this._selectionEnd);
    }

    public void selectAll() {
        this._selectionStart = 0;
        this._selectionEnd = (short)this.getSubstring().length();
    }

    public String getTextExportText() {
        char[] ch = this.getSubstring().toCharArray();
        StringBuffer result = new StringBuffer();
        if (this.isTrimmedLeadingSpace()) {
            result.append(' ');
        }
        for (int i = 0; i < ch.length; ++i) {
            char c = ch[i];
            if (c == '\n') continue;
            result.append(c);
        }
        if (this.isTrimmedTrailingSpace()) {
            result.append(' ');
        }
        return result.toString();
    }

    public boolean isTrimmedLeadingSpace() {
        return this._trimmedLeadingSpace;
    }

    public void setTrimmedLeadingSpace(boolean trimmedLeadingSpace) {
        this._trimmedLeadingSpace = trimmedLeadingSpace;
    }

    private void setTrimmedTrailingSpace(boolean trimmedTrailingSpace) {
        this._trimmedTrailingSpace = trimmedTrailingSpace;
    }

    private boolean isTrimmedTrailingSpace() {
        return this._trimmedTrailingSpace;
    }

    public void countJustifiableChars(CharCounts counts) {
        String s = this.getSubstring();
        int len = s.length();
        int spaces = 0;
        int other = 0;
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (c == ' ' || c == '\u00a0' || c == '\u3000') {
                ++spaces;
                continue;
            }
            ++other;
        }
        counts.setSpaceCount(counts.getSpaceCount() + spaces);
        counts.setNonSpaceCount(counts.getNonSpaceCount() + other);
    }

    public float calcTotalAdjustment(JustificationInfo info) {
        String s = this.getSubstring();
        int len = s.length();
        float result = 0.0f;
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (c == ' ' || c == '\u00a0' || c == '\u3000') {
                result += info.getSpaceAdjust();
                continue;
            }
            result += info.getNonSpaceAdjust();
        }
        return result;
    }

    public int getStart() {
        return this._start;
    }

    public int getEnd() {
        return this._end;
    }

    public void setSelectionStart(short s) {
        this._selectionStart = s;
    }

    public void setSelectionEnd(short s) {
        this._selectionEnd = s;
    }

    public Text getTextNode() {
        return this._textNode;
    }

    public void setTextNode(Text node) {
        this._textNode = node;
    }
}

