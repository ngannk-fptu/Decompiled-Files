/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.HRESIAbstractType;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class Hyphenation
extends HRESIAbstractType
implements Duplicatable {
    public Hyphenation() {
    }

    public Hyphenation(Hyphenation other) {
        super(other);
    }

    public Hyphenation(short hres) {
        byte[] data = new byte[2];
        LittleEndian.putShort(data, 0, hres);
        this.fillFields(data, 0);
    }

    @Override
    public Hyphenation copy() {
        return new Hyphenation(this);
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
        Hyphenation other = (Hyphenation)obj;
        if (this.field_1_hres != other.field_1_hres) {
            return false;
        }
        return this.field_2_chHres == other.field_2_chHres;
    }

    public short getValue() {
        byte[] data = new byte[2];
        this.serialize(data, 0);
        return LittleEndian.getShort(data);
    }

    public int hashCode() {
        return Objects.hash(this.field_1_hres, this.field_2_chHres);
    }

    public boolean isEmpty() {
        return this.field_1_hres == 0 && this.field_2_chHres == 0;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[HRESI] EMPTY";
        }
        return super.toString();
    }
}

