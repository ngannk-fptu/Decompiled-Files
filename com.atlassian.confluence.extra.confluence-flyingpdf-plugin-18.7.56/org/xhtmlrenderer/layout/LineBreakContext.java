/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.w3c.dom.Text;

public class LineBreakContext {
    private String _master;
    private int _start;
    private int _end;
    private int _savedEnd;
    private boolean _unbreakable;
    private boolean _needsNewLine;
    private int _width;
    private boolean _endsOnNL;
    private Text _textNode;

    public int getLast() {
        return this._master.length();
    }

    public void reset() {
        this._width = 0;
        this._unbreakable = false;
        this._needsNewLine = false;
    }

    public int getEnd() {
        return this._end;
    }

    public void setEnd(int end) {
        this._end = end;
    }

    public String getMaster() {
        return this._master;
    }

    public void setMaster(String master) {
        this._master = master;
    }

    public int getStart() {
        return this._start;
    }

    public void setStart(int start) {
        this._start = start;
    }

    public String getStartSubstring() {
        return this._master.substring(this._start);
    }

    public String getCalculatedSubstring() {
        if (this._end > 0 && this._master.charAt(this._end - 1) == '\n') {
            return this._master.substring(this._start, this._end - 1);
        }
        return this._master.substring(this._start, this._end);
    }

    public boolean isUnbreakable() {
        return this._unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this._unbreakable = unbreakable;
    }

    public boolean isNeedsNewLine() {
        return this._needsNewLine;
    }

    public void setNeedsNewLine(boolean needsLineBreak) {
        this._needsNewLine = needsLineBreak;
    }

    public int getWidth() {
        return this._width;
    }

    public void setWidth(int width) {
        this._width = width;
    }

    public boolean isFinished() {
        return this._end == this.getMaster().length();
    }

    public void resetEnd() {
        this._end = this._savedEnd;
    }

    public void saveEnd() {
        this._savedEnd = this._end;
    }

    public boolean isEndsOnNL() {
        return this._endsOnNL;
    }

    public void setEndsOnNL(boolean b) {
        this._endsOnNL = b;
    }

    public Text getTextNode() {
        return this._textNode;
    }

    public void setTextNode(Text _text) {
        this._textNode = _text;
    }
}

