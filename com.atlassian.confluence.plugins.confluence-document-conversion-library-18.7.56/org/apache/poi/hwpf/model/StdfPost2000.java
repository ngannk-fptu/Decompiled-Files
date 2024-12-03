/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.hwpf.model.types.StdfPost2000AbstractType;
import org.apache.poi.util.Internal;

@Internal
class StdfPost2000
extends StdfPost2000AbstractType {
    public StdfPost2000() {
    }

    public StdfPost2000(byte[] std, int offset) {
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
        StdfPost2000 other = (StdfPost2000)obj;
        if (this.field_1_info1 != other.field_1_info1) {
            return false;
        }
        if (this.field_2_rsid != other.field_2_rsid) {
            return false;
        }
        return this.field_3_info3 == other.field_3_info3;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_info1, this.field_2_rsid, this.field_3_info3);
    }

    public byte[] serialize() {
        byte[] result = new byte[StdfPost2000.getSize()];
        this.serialize(result, 0);
        return result;
    }
}

