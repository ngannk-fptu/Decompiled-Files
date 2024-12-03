/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.poi.hwpf.model.types.PICFAbstractType;
import org.apache.poi.util.Internal;

@Internal
public class PICF
extends PICFAbstractType {
    public PICF() {
    }

    public PICF(byte[] std, int offset) {
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
        PICF other = (PICF)obj;
        if (this.field_10_padding2 != other.field_10_padding2) {
            return false;
        }
        if (this.field_11_dxaGoal != other.field_11_dxaGoal) {
            return false;
        }
        if (this.field_12_dyaGoal != other.field_12_dyaGoal) {
            return false;
        }
        if (this.field_13_mx != other.field_13_mx) {
            return false;
        }
        if (this.field_14_my != other.field_14_my) {
            return false;
        }
        if (this.field_15_dxaReserved1 != other.field_15_dxaReserved1) {
            return false;
        }
        if (this.field_16_dyaReserved1 != other.field_16_dyaReserved1) {
            return false;
        }
        if (this.field_17_dxaReserved2 != other.field_17_dxaReserved2) {
            return false;
        }
        if (this.field_18_dyaReserved2 != other.field_18_dyaReserved2) {
            return false;
        }
        if (this.field_19_fReserved != other.field_19_fReserved) {
            return false;
        }
        if (this.field_1_lcb != other.field_1_lcb) {
            return false;
        }
        if (this.field_20_bpp != other.field_20_bpp) {
            return false;
        }
        if (!Arrays.equals(this.field_21_brcTop80, other.field_21_brcTop80)) {
            return false;
        }
        if (!Arrays.equals(this.field_22_brcLeft80, other.field_22_brcLeft80)) {
            return false;
        }
        if (!Arrays.equals(this.field_23_brcBottom80, other.field_23_brcBottom80)) {
            return false;
        }
        if (!Arrays.equals(this.field_24_brcRight80, other.field_24_brcRight80)) {
            return false;
        }
        if (this.field_25_dxaReserved3 != other.field_25_dxaReserved3) {
            return false;
        }
        if (this.field_26_dyaReserved3 != other.field_26_dyaReserved3) {
            return false;
        }
        if (this.field_27_cProps != other.field_27_cProps) {
            return false;
        }
        if (this.field_2_cbHeader != other.field_2_cbHeader) {
            return false;
        }
        if (this.field_3_mm != other.field_3_mm) {
            return false;
        }
        if (this.field_4_xExt != other.field_4_xExt) {
            return false;
        }
        if (this.field_5_yExt != other.field_5_yExt) {
            return false;
        }
        if (this.field_6_swHMF != other.field_6_swHMF) {
            return false;
        }
        if (this.field_7_grf != other.field_7_grf) {
            return false;
        }
        if (this.field_8_padding != other.field_8_padding) {
            return false;
        }
        return this.field_9_mmPM == other.field_9_mmPM;
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this.field_1_lcb, this.field_2_cbHeader, this.field_3_mm, this.field_4_xExt, this.field_5_yExt, this.field_6_swHMF, this.field_7_grf, this.field_8_padding, this.field_9_mmPM, this.field_10_padding2, this.field_11_dxaGoal, this.field_12_dyaGoal, this.field_13_mx, this.field_14_my, this.field_15_dxaReserved1, this.field_16_dyaReserved1, this.field_17_dxaReserved2, this.field_18_dyaReserved2, this.field_19_fReserved, this.field_20_bpp, this.field_21_brcTop80, this.field_22_brcLeft80, this.field_23_brcBottom80, this.field_24_brcRight80, this.field_25_dxaReserved3, this.field_26_dyaReserved3, this.field_27_cProps});
    }
}

