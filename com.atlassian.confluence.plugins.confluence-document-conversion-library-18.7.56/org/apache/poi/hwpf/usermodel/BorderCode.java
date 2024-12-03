/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;

public final class BorderCode
implements Duplicatable {
    public static final int SIZE = 4;
    private static final BitField _dptLineWidth = BitFieldFactory.getInstance(255);
    private static final BitField _brcType = BitFieldFactory.getInstance(65280);
    private static final BitField _ico = BitFieldFactory.getInstance(255);
    private static final BitField _dptSpace = BitFieldFactory.getInstance(7936);
    private static final BitField _fShadow = BitFieldFactory.getInstance(8192);
    private static final BitField _fFrame = BitFieldFactory.getInstance(16384);
    private short _info;
    private short _info2;

    public BorderCode() {
    }

    public BorderCode(BorderCode other) {
        this._info = other._info;
        this._info2 = other._info2;
    }

    public BorderCode(byte[] buf, int offset) {
        this._info = LittleEndian.getShort(buf, offset);
        this._info2 = LittleEndian.getShort(buf, offset + 2);
    }

    public void serialize(byte[] buf, int offset) {
        LittleEndian.putShort(buf, offset, this._info);
        LittleEndian.putShort(buf, offset + 2, this._info2);
    }

    public int toInt() {
        byte[] buf = new byte[4];
        this.serialize(buf, 0);
        return LittleEndian.getInt(buf);
    }

    public boolean isEmpty() {
        return this._info == 0 && this._info2 == 0 || this._info == -1;
    }

    public boolean equals(Object o) {
        if (!(o instanceof BorderCode)) {
            return false;
        }
        BorderCode brc = (BorderCode)o;
        return this._info == brc._info && this._info2 == brc._info2;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    @Override
    public BorderCode copy() {
        return new BorderCode(this);
    }

    public int getLineWidth() {
        return _dptLineWidth.getShortValue(this._info);
    }

    public void setLineWidth(int lineWidth) {
        this._info = _dptLineWidth.setShortValue(this._info, (short)lineWidth);
    }

    public int getBorderType() {
        return _brcType.getShortValue(this._info);
    }

    public void setBorderType(int borderType) {
        this._info = _brcType.setShortValue(this._info, (short)borderType);
    }

    public short getColor() {
        return _ico.getShortValue(this._info2);
    }

    public void setColor(short color) {
        this._info2 = _ico.setShortValue(this._info2, color);
    }

    public int getSpace() {
        return _dptSpace.getShortValue(this._info2);
    }

    public void setSpace(int space) {
        this._info2 = (short)_dptSpace.setValue(this._info2, space);
    }

    public boolean isShadow() {
        return _fShadow.getValue(this._info2) != 0;
    }

    public void setShadow(boolean shadow) {
        this._info2 = (short)_fShadow.setValue(this._info2, shadow ? 1 : 0);
    }

    public boolean isFrame() {
        return _fFrame.getValue(this._info2) != 0;
    }

    public void setFrame(boolean frame) {
        this._info2 = (short)_fFrame.setValue(this._info2, frame ? 1 : 0);
    }

    public String toString() {
        return this.isEmpty() ? "[BRC] EMPTY" : "[BRC]\n        .dptLineWidth         =  (" + this.getLineWidth() + " )\n        .brcType              =  (" + this.getBorderType() + " )\n        .ico                  =  (" + this.getColor() + " )\n        .dptSpace             =  (" + this.getSpace() + " )\n        .fShadow              =  (" + this.isShadow() + " )\n        .fFrame               =  (" + this.isFrame() + " )\n";
    }
}

