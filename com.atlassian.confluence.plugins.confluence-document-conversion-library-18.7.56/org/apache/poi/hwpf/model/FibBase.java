/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.hwpf.model.types.FibBaseAbstractType;
import org.apache.poi.util.Internal;

@Internal
public class FibBase
extends FibBaseAbstractType {
    public FibBase() {
    }

    public FibBase(byte[] std, int offset) {
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
        FibBase other = (FibBase)obj;
        if (this.field_10_flags2 != other.field_10_flags2) {
            return false;
        }
        if (this.field_11_Chs != other.field_11_Chs) {
            return false;
        }
        if (this.field_12_chsTables != other.field_12_chsTables) {
            return false;
        }
        if (this.field_13_fcMin != other.field_13_fcMin) {
            return false;
        }
        if (this.field_14_fcMac != other.field_14_fcMac) {
            return false;
        }
        if (this.field_1_wIdent != other.field_1_wIdent) {
            return false;
        }
        if (this.field_2_nFib != other.field_2_nFib) {
            return false;
        }
        if (this.field_3_unused != other.field_3_unused) {
            return false;
        }
        if (this.field_4_lid != other.field_4_lid) {
            return false;
        }
        if (this.field_5_pnNext != other.field_5_pnNext) {
            return false;
        }
        if (this.field_6_flags1 != other.field_6_flags1) {
            return false;
        }
        if (this.field_7_nFibBack != other.field_7_nFibBack) {
            return false;
        }
        if (this.field_8_lKey != other.field_8_lKey) {
            return false;
        }
        return this.field_9_envr == other.field_9_envr;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_wIdent, this.field_2_nFib, this.field_3_unused, this.field_4_lid, this.field_5_pnNext, this.field_6_flags1, this.field_7_nFibBack, this.field_8_lKey, this.field_9_envr, this.field_10_flags2, this.field_11_Chs, this.field_12_chsTables, this.field_13_fcMin, this.field_14_fcMac);
    }
}

