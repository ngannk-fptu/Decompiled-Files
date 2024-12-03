/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.hwpf.model.types.FibRgW97AbstractType;
import org.apache.poi.util.Internal;

@Internal
public class FibRgW97
extends FibRgW97AbstractType {
    public FibRgW97() {
    }

    public FibRgW97(byte[] std, int offset) {
        this.fillFields(std, offset);
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
        FibRgW97 other = (FibRgW97)obj;
        if (this.field_10_reserved10 != other.field_10_reserved10) {
            return false;
        }
        if (this.field_11_reserved11 != other.field_11_reserved11) {
            return false;
        }
        if (this.field_12_reserved12 != other.field_12_reserved12) {
            return false;
        }
        if (this.field_13_reserved13 != other.field_13_reserved13) {
            return false;
        }
        if (this.field_14_lidFE != other.field_14_lidFE) {
            return false;
        }
        if (this.field_1_reserved1 != other.field_1_reserved1) {
            return false;
        }
        if (this.field_2_reserved2 != other.field_2_reserved2) {
            return false;
        }
        if (this.field_3_reserved3 != other.field_3_reserved3) {
            return false;
        }
        if (this.field_4_reserved4 != other.field_4_reserved4) {
            return false;
        }
        if (this.field_5_reserved5 != other.field_5_reserved5) {
            return false;
        }
        if (this.field_6_reserved6 != other.field_6_reserved6) {
            return false;
        }
        if (this.field_7_reserved7 != other.field_7_reserved7) {
            return false;
        }
        if (this.field_8_reserved8 != other.field_8_reserved8) {
            return false;
        }
        return this.field_9_reserved9 == other.field_9_reserved9;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_reserved1, this.field_2_reserved2, this.field_3_reserved3, this.field_4_reserved4, this.field_5_reserved5, this.field_6_reserved6, this.field_7_reserved7, this.field_8_reserved8, this.field_9_reserved9, this.field_10_reserved10, this.field_11_reserved11, this.field_12_reserved12, this.field_13_reserved13, this.field_14_lidFE);
    }
}

