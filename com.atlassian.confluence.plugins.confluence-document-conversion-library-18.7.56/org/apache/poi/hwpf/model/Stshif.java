/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.hwpf.model.types.StshifAbstractType;
import org.apache.poi.util.Internal;

@Internal
class Stshif
extends StshifAbstractType {
    public Stshif() {
    }

    public Stshif(byte[] std, int offset) {
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
        Stshif other = (Stshif)obj;
        if (this.field_1_cstd != other.field_1_cstd) {
            return false;
        }
        if (this.field_2_cbSTDBaseInFile != other.field_2_cbSTDBaseInFile) {
            return false;
        }
        if (this.field_3_info3 != other.field_3_info3) {
            return false;
        }
        if (this.field_4_stiMaxWhenSaved != other.field_4_stiMaxWhenSaved) {
            return false;
        }
        if (this.field_5_istdMaxFixedWhenSaved != other.field_5_istdMaxFixedWhenSaved) {
            return false;
        }
        if (this.field_6_nVerBuiltInNamesWhenSaved != other.field_6_nVerBuiltInNamesWhenSaved) {
            return false;
        }
        if (this.field_7_ftcAsci != other.field_7_ftcAsci) {
            return false;
        }
        if (this.field_8_ftcFE != other.field_8_ftcFE) {
            return false;
        }
        return this.field_9_ftcOther == other.field_9_ftcOther;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_cstd, this.field_2_cbSTDBaseInFile, this.field_3_info3, this.field_4_stiMaxWhenSaved, this.field_5_istdMaxFixedWhenSaved, this.field_6_nVerBuiltInNamesWhenSaved, this.field_7_ftcAsci, this.field_8_ftcFE, this.field_9_ftcOther);
    }

    public byte[] serialize() {
        byte[] result = new byte[Stshif.getSize()];
        this.serialize(result, 0);
        return result;
    }
}

