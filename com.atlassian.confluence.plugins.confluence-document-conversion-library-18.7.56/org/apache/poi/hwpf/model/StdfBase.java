/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.hwpf.model.types.StdfBaseAbstractType;
import org.apache.poi.util.Internal;

@Internal
class StdfBase
extends StdfBaseAbstractType {
    public StdfBase() {
    }

    public StdfBase(byte[] std, int offset) {
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
        StdfBase other = (StdfBase)obj;
        if (this.field_1_info1 != other.field_1_info1) {
            return false;
        }
        if (this.field_2_info2 != other.field_2_info2) {
            return false;
        }
        if (this.field_3_info3 != other.field_3_info3) {
            return false;
        }
        if (this.field_4_bchUpe != other.field_4_bchUpe) {
            return false;
        }
        return this.field_5_grfstd == other.field_5_grfstd;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_info1, this.field_2_info2, this.field_3_info3, this.field_4_bchUpe, this.field_5_grfstd);
    }

    public byte[] serialize() {
        byte[] result = new byte[StdfBase.getSize()];
        this.serialize(result, 0);
        return result;
    }
}

