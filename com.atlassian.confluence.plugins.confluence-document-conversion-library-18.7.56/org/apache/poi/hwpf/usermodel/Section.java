/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.HWPFOldDocument;
import org.apache.poi.hwpf.model.SEPX;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.SectionProperties;

public final class Section
extends Range
implements Duplicatable {
    private final SectionProperties _props;

    public Section(Section other) {
        super(other);
        this._props = other._props.copy();
    }

    public Section(SEPX sepx, Range parent) {
        super(Math.max(parent._start, sepx.getStart()), Math.min(parent._end, sepx.getEnd()), parent);
        this._props = parent.getDocument() instanceof HWPFOldDocument ? new SectionProperties() : sepx.getSectionProperties();
    }

    @Override
    public Section copy() {
        return new Section(this);
    }

    public int getDistanceBetweenColumns() {
        return this._props.getDxaColumns();
    }

    public int getMarginBottom() {
        return this._props.getDyaBottom();
    }

    public int getMarginLeft() {
        return this._props.getDxaLeft();
    }

    public int getMarginRight() {
        return this._props.getDxaRight();
    }

    public int getMarginTop() {
        return this._props.getDyaTop();
    }

    public int getNumColumns() {
        return this._props.getCcolM1() + 1;
    }

    public int getPageHeight() {
        return this._props.getYaPage();
    }

    public int getPageWidth() {
        return this._props.getXaPage();
    }

    public void setMarginBottom(int marginWidth) {
        this._props.setDyaBottom(marginWidth);
    }

    public void setMarginLeft(int marginWidth) {
        this._props.setDxaLeft(marginWidth);
    }

    public void setMarginRight(int marginWidth) {
        this._props.setDxaRight(marginWidth);
    }

    public void setMarginTop(int marginWidth) {
        this._props.setDyaTop(marginWidth);
    }

    public boolean isColumnsEvenlySpaced() {
        return this._props.getFEvenlySpaced();
    }

    public short getFootnoteRestartQualifier() {
        return this._props.getRncFtn();
    }

    public int getFootnoteNumberingOffset() {
        return this._props.getNFtn();
    }

    public int getFootnoteNumberingFormat() {
        return this._props.getNfcFtnRef();
    }

    public short getEndnoteRestartQualifier() {
        return this._props.getRncEdn();
    }

    public int getEndnoteNumberingOffset() {
        return this._props.getNEdn();
    }

    public int getEndnoteNumberingFormat() {
        return this._props.getNfcEdnRef();
    }

    @Override
    public String toString() {
        return "Section [" + this.getStartOffset() + "; " + this.getEndOffset() + ")";
    }

    public int type() {
        return 2;
    }
}

