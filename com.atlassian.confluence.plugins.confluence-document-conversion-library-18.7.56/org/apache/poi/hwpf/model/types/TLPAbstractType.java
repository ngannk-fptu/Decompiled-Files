/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class TLPAbstractType {
    private static final BitField fBorders = new BitField(1);
    private static final BitField fShading = new BitField(2);
    private static final BitField fFont = new BitField(4);
    private static final BitField fColor = new BitField(8);
    private static final BitField fBestFit = new BitField(16);
    private static final BitField fHdrRows = new BitField(32);
    private static final BitField fLastRow = new BitField(64);
    protected short field_1_itl;
    protected byte field_2_tlp_flags;

    public TLPAbstractType() {
    }

    public TLPAbstractType(TLPAbstractType other) {
        this.field_1_itl = other.field_1_itl;
        this.field_2_tlp_flags = other.field_2_tlp_flags;
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_itl = LittleEndian.getShort(data, 0 + offset);
        this.field_2_tlp_flags = data[2 + offset];
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_itl);
        data[2 + offset] = this.field_2_tlp_flags;
    }

    public String toString() {
        return "[TLP]\n    .itl                  = (" + this.getItl() + " )\n    .tlp_flags            = (" + this.getTlp_flags() + " )\n         .fBorders                 = " + this.isFBorders() + "\n         .fShading                 = " + this.isFShading() + "\n         .fFont                    = " + this.isFFont() + "\n         .fColor                   = " + this.isFColor() + "\n         .fBestFit                 = " + this.isFBestFit() + "\n         .fHdrRows                 = " + this.isFHdrRows() + "\n         .fLastRow                 = " + this.isFLastRow() + "\n[/TLP]\n";
    }

    public int getSize() {
        return 7;
    }

    public short getItl() {
        return this.field_1_itl;
    }

    public void setItl(short field_1_itl) {
        this.field_1_itl = field_1_itl;
    }

    public byte getTlp_flags() {
        return this.field_2_tlp_flags;
    }

    public void setTlp_flags(byte field_2_tlp_flags) {
        this.field_2_tlp_flags = field_2_tlp_flags;
    }

    public void setFBorders(boolean value) {
        this.field_2_tlp_flags = (byte)fBorders.setBoolean(this.field_2_tlp_flags, value);
    }

    public boolean isFBorders() {
        return fBorders.isSet(this.field_2_tlp_flags);
    }

    public void setFShading(boolean value) {
        this.field_2_tlp_flags = (byte)fShading.setBoolean(this.field_2_tlp_flags, value);
    }

    public boolean isFShading() {
        return fShading.isSet(this.field_2_tlp_flags);
    }

    public void setFFont(boolean value) {
        this.field_2_tlp_flags = (byte)fFont.setBoolean(this.field_2_tlp_flags, value);
    }

    public boolean isFFont() {
        return fFont.isSet(this.field_2_tlp_flags);
    }

    public void setFColor(boolean value) {
        this.field_2_tlp_flags = (byte)fColor.setBoolean(this.field_2_tlp_flags, value);
    }

    public boolean isFColor() {
        return fColor.isSet(this.field_2_tlp_flags);
    }

    public void setFBestFit(boolean value) {
        this.field_2_tlp_flags = (byte)fBestFit.setBoolean(this.field_2_tlp_flags, value);
    }

    public boolean isFBestFit() {
        return fBestFit.isSet(this.field_2_tlp_flags);
    }

    public void setFHdrRows(boolean value) {
        this.field_2_tlp_flags = (byte)fHdrRows.setBoolean(this.field_2_tlp_flags, value);
    }

    public boolean isFHdrRows() {
        return fHdrRows.isSet(this.field_2_tlp_flags);
    }

    public void setFLastRow(boolean value) {
        this.field_2_tlp_flags = (byte)fLastRow.setBoolean(this.field_2_tlp_flags, value);
    }

    public boolean isFLastRow() {
        return fLastRow.isSet(this.field_2_tlp_flags);
    }
}

