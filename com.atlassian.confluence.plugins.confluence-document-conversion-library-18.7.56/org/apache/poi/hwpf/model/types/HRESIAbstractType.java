/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.Internal;

@Internal
public abstract class HRESIAbstractType {
    public static final byte HRES_NO = 0;
    public static final byte HRES_NORMAL = 1;
    public static final byte HRES_ADD_LETTER_BEFORE = 2;
    public static final byte HRES_CHANGE_LETTER_BEFORE = 3;
    public static final byte HRES_DELETE_LETTER_BEFORE = 4;
    public static final byte HRES_CHANGE_LETTER_AFTER = 5;
    public static final byte HRES_DELETE_BEFORE_CHANGE_BEFORE = 6;
    protected byte field_1_hres;
    protected byte field_2_chHres;

    protected HRESIAbstractType() {
    }

    protected HRESIAbstractType(HRESIAbstractType other) {
        this.field_1_hres = other.field_1_hres;
        this.field_2_chHres = other.field_2_chHres;
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_hres = data[0 + offset];
        this.field_2_chHres = data[1 + offset];
    }

    public void serialize(byte[] data, int offset) {
        data[0 + offset] = this.field_1_hres;
        data[1 + offset] = this.field_2_chHres;
    }

    public static int getSize() {
        return 6;
    }

    public String toString() {
        return "[HRESI]\n    .hres                 =  (" + this.getHres() + " )\n    .chHres               =  (" + this.getChHres() + " )\n[/HRESI]\n";
    }

    public byte getHres() {
        return this.field_1_hres;
    }

    public void setHres(byte field_1_hres) {
        this.field_1_hres = field_1_hres;
    }

    public byte getChHres() {
        return this.field_2_chHres;
    }

    public void setChHres(byte field_2_chHres) {
        this.field_2_chHres = field_2_chHres;
    }
}

