/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FRDAbstractType {
    protected short field_1_nAuto;

    protected FRDAbstractType() {
    }

    protected FRDAbstractType(FRDAbstractType other) {
        this.field_1_nAuto = other.field_1_nAuto;
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_nAuto = LittleEndian.getShort(data, 0 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_nAuto);
    }

    public static int getSize() {
        return 2;
    }

    public String toString() {
        return "[FRD]\n    .nAuto                =  (" + this.getNAuto() + " )\n[/FRD]\n";
    }

    public short getNAuto() {
        return this.field_1_nAuto;
    }

    public void setNAuto(short field_1_nAuto) {
        this.field_1_nAuto = field_1_nAuto;
    }
}

