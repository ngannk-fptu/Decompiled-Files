/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

public enum HwmfMapMode {
    MM_TEXT(1, 0),
    MM_LOMETRIC(2, 254),
    MM_HIMETRIC(3, 2540),
    MM_LOENGLISH(4, 100),
    MM_HIENGLISH(5, 1000),
    MM_TWIPS(6, 1440),
    MM_ISOTROPIC(7, -1),
    MM_ANISOTROPIC(8, -1);

    public final int flag;
    public final int scale;

    private HwmfMapMode(int flag, int scale) {
        this.flag = flag;
        this.scale = scale;
    }

    public static HwmfMapMode valueOf(int flag) {
        for (HwmfMapMode mm : HwmfMapMode.values()) {
            if (mm.flag != flag) continue;
            return mm;
        }
        return MM_ISOTROPIC;
    }
}

