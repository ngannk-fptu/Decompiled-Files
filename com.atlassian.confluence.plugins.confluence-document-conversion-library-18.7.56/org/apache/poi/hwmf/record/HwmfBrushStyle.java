/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

public enum HwmfBrushStyle {
    BS_SOLID(0),
    BS_NULL(1),
    BS_HATCHED(2),
    BS_PATTERN(3),
    BS_INDEXED(4),
    BS_DIBPATTERN(5),
    BS_DIBPATTERNPT(6),
    BS_PATTERN8X8(7),
    BS_DIBPATTERN8X8(8),
    BS_MONOPATTERN(9),
    BS_LINEAR_GRADIENT(256);

    int flag;

    private HwmfBrushStyle(int flag) {
        this.flag = flag;
    }

    public static HwmfBrushStyle valueOf(int flag) {
        for (HwmfBrushStyle bs : HwmfBrushStyle.values()) {
            if (bs.flag != flag) continue;
            return bs;
        }
        return null;
    }
}

