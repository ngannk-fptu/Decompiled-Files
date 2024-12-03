/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.hwpf.model.Colorref;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class SHDAbstractType {
    protected Colorref field_1_cvFore;
    protected Colorref field_2_cvBack;
    protected int field_3_ipat;

    protected SHDAbstractType() {
        this.field_1_cvFore = new Colorref();
        this.field_2_cvBack = new Colorref();
    }

    protected SHDAbstractType(SHDAbstractType other) {
        this.field_1_cvFore = other.field_1_cvFore == null ? null : other.field_1_cvFore.copy();
        this.field_2_cvBack = other.field_2_cvBack == null ? null : other.field_2_cvBack.copy();
        this.field_3_ipat = other.field_3_ipat;
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_cvFore = new Colorref(data, 0 + offset);
        this.field_2_cvBack = new Colorref(data, 4 + offset);
        this.field_3_ipat = LittleEndian.getShort(data, 8 + offset);
    }

    public void serialize(byte[] data, int offset) {
        this.field_1_cvFore.serialize(data, 0 + offset);
        this.field_2_cvBack.serialize(data, 4 + offset);
        LittleEndian.putUShort(data, 8 + offset, this.field_3_ipat);
    }

    public byte[] serialize() {
        byte[] result = new byte[SHDAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 10;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SHDAbstractType other = (SHDAbstractType)obj;
        if (this.field_1_cvFore != other.field_1_cvFore) {
            return false;
        }
        if (this.field_2_cvBack != other.field_2_cvBack) {
            return false;
        }
        return this.field_3_ipat == other.field_3_ipat;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_cvFore, this.field_2_cvBack, this.field_3_ipat);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[SHD]\n");
        builder.append("    .cvFore               = ");
        builder.append(" (").append(this.getCvFore()).append(" )\n");
        builder.append("    .cvBack               = ");
        builder.append(" (").append(this.getCvBack()).append(" )\n");
        builder.append("    .ipat                 = ");
        builder.append(" (").append(this.getIpat()).append(" )\n");
        builder.append("[/SHD]\n");
        return builder.toString();
    }

    @Internal
    public Colorref getCvFore() {
        return this.field_1_cvFore;
    }

    @Internal
    public void setCvFore(Colorref field_1_cvFore) {
        this.field_1_cvFore = field_1_cvFore;
    }

    @Internal
    public Colorref getCvBack() {
        return this.field_2_cvBack;
    }

    @Internal
    public void setCvBack(Colorref field_2_cvBack) {
        this.field_2_cvBack = field_2_cvBack;
    }

    @Internal
    public int getIpat() {
        return this.field_3_ipat;
    }

    @Internal
    public void setIpat(int field_3_ipat) {
        this.field_3_ipat = field_3_ipat;
    }
}

