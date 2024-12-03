/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.TableCellDescriptor;
import org.apache.poi.hwpf.usermodel.TableRow;

public final class TableCell
extends Range {
    private int _levelNum;
    private TableCellDescriptor _tcd;
    private int _leftEdge;
    private int _width;

    public TableCell(int startIdxInclusive, int endIdxExclusive, TableRow parent, int levelNum, TableCellDescriptor tcd, int leftEdge, int width) {
        super(startIdxInclusive, endIdxExclusive, parent);
        this._tcd = tcd;
        this._leftEdge = leftEdge;
        this._width = width;
        this._levelNum = levelNum;
    }

    public boolean isFirstMerged() {
        if (this._tcd == null) {
            return false;
        }
        return this._tcd.isFFirstMerged();
    }

    public boolean isMerged() {
        if (this._tcd == null) {
            return false;
        }
        return this._tcd.isFMerged();
    }

    public boolean isVertical() {
        if (this._tcd == null) {
            return false;
        }
        return this._tcd.isFVertical();
    }

    public boolean isBackward() {
        if (this._tcd == null) {
            return false;
        }
        return this._tcd.isFBackward();
    }

    public boolean isRotateFont() {
        if (this._tcd == null) {
            return false;
        }
        return this._tcd.isFRotateFont();
    }

    public boolean isVerticallyMerged() {
        if (this._tcd == null) {
            return false;
        }
        return this._tcd.isFVertMerge();
    }

    public boolean isFirstVerticallyMerged() {
        if (this._tcd == null) {
            return false;
        }
        return this._tcd.isFVertRestart();
    }

    public byte getVertAlign() {
        if (this._tcd == null) {
            return 0;
        }
        return this._tcd.getVertAlign();
    }

    public BorderCode getBrcTop() {
        if (this._tcd == null) {
            return new BorderCode();
        }
        return this._tcd.getBrcTop();
    }

    public BorderCode getBrcBottom() {
        if (this._tcd == null) {
            return new BorderCode();
        }
        return this._tcd.getBrcBottom();
    }

    public BorderCode getBrcLeft() {
        if (this._tcd == null) {
            return new BorderCode();
        }
        return this._tcd.getBrcLeft();
    }

    public BorderCode getBrcRight() {
        if (this._tcd == null) {
            return new BorderCode();
        }
        return this._tcd.getBrcRight();
    }

    public int getLeftEdge() {
        return this._leftEdge;
    }

    public int getWidth() {
        return this._width;
    }

    public TableCellDescriptor getDescriptor() {
        return this._tcd;
    }
}

