/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.FFDataBase;
import org.apache.poi.hwpf.model.Sttb;
import org.apache.poi.hwpf.model.Xstz;
import org.apache.poi.hwpf.model.types.FFDataBaseAbstractType;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class FFData {
    private FFDataBase _base;
    private Sttb _hsttbDropList;
    private Integer _wDef;
    private Xstz _xstzEntryMcr;
    private Xstz _xstzExitMcr;
    private Xstz _xstzHelpText;
    private Xstz _xstzName;
    private Xstz _xstzStatText;
    private Xstz _xstzTextDef;
    private Xstz _xstzTextFormat;

    public FFData(byte[] std, int offset) {
        this.fillFields(std, offset);
    }

    public void fillFields(byte[] std, int startOffset) {
        int offset = startOffset;
        this._base = new FFDataBase(std, offset);
        this._xstzName = new Xstz(std, offset += FFDataBaseAbstractType.getSize());
        offset += this._xstzName.getSize();
        if (this._base.getIType() == 0) {
            this._xstzTextDef = new Xstz(std, offset);
            offset += this._xstzTextDef.getSize();
        } else {
            this._xstzTextDef = null;
        }
        if (this._base.getIType() == 1 || this._base.getIType() == 2) {
            this._wDef = LittleEndian.getUShort(std, offset);
            offset += 2;
        } else {
            this._wDef = null;
        }
        this._xstzTextFormat = new Xstz(std, offset);
        this._xstzHelpText = new Xstz(std, offset += this._xstzTextFormat.getSize());
        this._xstzStatText = new Xstz(std, offset += this._xstzHelpText.getSize());
        this._xstzEntryMcr = new Xstz(std, offset += this._xstzStatText.getSize());
        this._xstzExitMcr = new Xstz(std, offset += this._xstzEntryMcr.getSize());
        offset += this._xstzExitMcr.getSize();
        if (this._base.getIType() == 2) {
            this._hsttbDropList = new Sttb(std, offset);
        }
    }

    public int getDefaultDropDownItemIndex() {
        return this._wDef;
    }

    public String[] getDropList() {
        return this._hsttbDropList.getData();
    }

    public int getSize() {
        int size = FFDataBaseAbstractType.getSize();
        size += this._xstzName.getSize();
        if (this._base.getIType() == 0) {
            size += this._xstzTextDef.getSize();
        }
        if (this._base.getIType() == 1 || this._base.getIType() == 2) {
            size += 2;
        }
        size += this._xstzTextFormat.getSize();
        size += this._xstzHelpText.getSize();
        size += this._xstzStatText.getSize();
        size += this._xstzEntryMcr.getSize();
        size += this._xstzExitMcr.getSize();
        if (this._base.getIType() == 2) {
            size += this._hsttbDropList.getSize();
        }
        return size;
    }

    public String getTextDef() {
        return this._xstzTextDef.getAsJavaString();
    }

    public byte[] serialize() {
        byte[] buffer = new byte[this.getSize()];
        int offset = 0;
        this._base.serialize(buffer, offset);
        offset += FFDataBaseAbstractType.getSize();
        offset += this._xstzName.serialize(buffer, offset);
        if (this._base.getIType() == 0) {
            offset += this._xstzTextDef.serialize(buffer, offset);
        }
        if (this._base.getIType() == 1 || this._base.getIType() == 2) {
            LittleEndian.putUShort(buffer, offset, this._wDef);
            offset += 2;
        }
        offset += this._xstzTextFormat.serialize(buffer, offset);
        offset += this._xstzHelpText.serialize(buffer, offset);
        offset += this._xstzStatText.serialize(buffer, offset);
        offset += this._xstzEntryMcr.serialize(buffer, offset);
        offset += this._xstzExitMcr.serialize(buffer, offset);
        if (this._base.getIType() == 2) {
            this._hsttbDropList.serialize(buffer, offset);
        }
        return buffer;
    }
}

