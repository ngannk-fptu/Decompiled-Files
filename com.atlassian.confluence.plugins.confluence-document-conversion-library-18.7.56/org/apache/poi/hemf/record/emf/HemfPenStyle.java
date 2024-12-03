/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import org.apache.poi.hwmf.record.HwmfPenStyle;

public class HemfPenStyle
extends HwmfPenStyle {
    private float[] dashPattern;

    public HemfPenStyle(int flag) {
        super(flag);
    }

    public HemfPenStyle(HemfPenStyle other) {
        super(other);
        this.dashPattern = other.dashPattern == null ? null : (float[])other.dashPattern.clone();
    }

    public static HemfPenStyle valueOf(HwmfPenStyle.HwmfLineCap cap, HwmfPenStyle.HwmfLineJoin join, HwmfPenStyle.HwmfLineDash dash, boolean isAlternateDash, boolean isGeometric) {
        int flag = 0;
        flag = SUBSECTION_DASH.setValue(flag, dash.wmfFlag);
        flag = SUBSECTION_ENDCAP.setValue(flag, cap.wmfFlag);
        flag = SUBSECTION_JOIN.setValue(flag, join.wmfFlag);
        flag = SUBSECTION_ALTERNATE.setBoolean(flag, isAlternateDash);
        flag = SUBSECTION_GEOMETRIC.setBoolean(flag, isGeometric);
        return new HemfPenStyle(flag);
    }

    public static HemfPenStyle valueOf(int flag) {
        return new HemfPenStyle(flag);
    }

    @Override
    public float[] getLineDashes() {
        return this.getLineDash() == HwmfPenStyle.HwmfLineDash.USERSTYLE ? this.dashPattern : super.getLineDashes();
    }

    public void setLineDashes(float[] dashPattern) {
        this.dashPattern = dashPattern == null ? null : (float[])dashPattern.clone();
    }

    @Override
    public HemfPenStyle copy() {
        return new HemfPenStyle(this);
    }
}

