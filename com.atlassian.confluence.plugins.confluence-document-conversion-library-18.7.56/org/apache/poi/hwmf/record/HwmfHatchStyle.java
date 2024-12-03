/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

public enum HwmfHatchStyle {
    HS_HORIZONTAL(0, 0xFF000000FFL),
    HS_VERTICAL(1, -8608480567731124088L),
    HS_FDIAGONAL(2, 577588857680175120L),
    HS_BDIAGONAL(3, 1161999622378488840L),
    HS_CROSS(4, 0x111111FF111111FFL),
    HS_DIAGCROSS(5, 1739588480058663960L),
    HS_SOLIDCLR(6, -1L),
    HS_DITHEREDCLR(7, -6172840429334713771L),
    HS_SOLIDTEXTCLR(8, -1L),
    HS_DITHEREDTEXTCLR(9, -6172840429334713771L),
    HS_SOLIDBKCLR(10, 0L),
    HS_DITHEREDBKCLR(11, -6172840429334713771L);

    private final int flag;
    private final long pattern;

    private HwmfHatchStyle(int flag, long pattern) {
        this.flag = flag;
        this.pattern = pattern;
    }

    public int getFlag() {
        return this.flag;
    }

    public long getPattern() {
        return this.pattern;
    }

    public static HwmfHatchStyle valueOf(int flag) {
        for (HwmfHatchStyle hs : HwmfHatchStyle.values()) {
            if (hs.flag != flag) continue;
            return hs;
        }
        return null;
    }
}

