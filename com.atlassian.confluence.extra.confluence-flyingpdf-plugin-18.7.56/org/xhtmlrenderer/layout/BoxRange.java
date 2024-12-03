/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

public class BoxRange {
    private final int _start;
    private final int _end;

    public BoxRange(int start, int end) {
        this._start = start;
        this._end = end;
    }

    public int getStart() {
        return this._start;
    }

    public int getEnd() {
        return this._end;
    }

    public String toString() {
        return "[start=" + this._start + ", end=" + this._end + "]";
    }
}

