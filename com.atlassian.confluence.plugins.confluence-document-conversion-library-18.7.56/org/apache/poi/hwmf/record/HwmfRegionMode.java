/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.function.BiFunction;

public final class HwmfRegionMode
extends Enum<HwmfRegionMode> {
    public static final /* enum */ HwmfRegionMode RGN_AND = new HwmfRegionMode(1, HwmfRegionMode::andOp);
    public static final /* enum */ HwmfRegionMode RGN_OR = new HwmfRegionMode(2, HwmfRegionMode::orOp);
    public static final /* enum */ HwmfRegionMode RGN_XOR = new HwmfRegionMode(3, HwmfRegionMode::xorOp);
    public static final /* enum */ HwmfRegionMode RGN_DIFF = new HwmfRegionMode(4, HwmfRegionMode::diffOp);
    public static final /* enum */ HwmfRegionMode RGN_COPY = new HwmfRegionMode(5, HwmfRegionMode::copyOp);
    public static final /* enum */ HwmfRegionMode RGN_COMPLEMENT = new HwmfRegionMode(-1, HwmfRegionMode::complementOp);
    private final int flag;
    private final BiFunction<Shape, Shape, Shape> op;
    private static final /* synthetic */ HwmfRegionMode[] $VALUES;

    public static HwmfRegionMode[] values() {
        return (HwmfRegionMode[])$VALUES.clone();
    }

    public static HwmfRegionMode valueOf(String name) {
        return Enum.valueOf(HwmfRegionMode.class, name);
    }

    private HwmfRegionMode(int flag, BiFunction<Shape, Shape, Shape> op) {
        this.flag = flag;
        this.op = op;
    }

    public static HwmfRegionMode valueOf(int flag) {
        for (HwmfRegionMode rm : HwmfRegionMode.values()) {
            if (rm.flag != flag) continue;
            return rm;
        }
        return null;
    }

    public int getFlag() {
        return this.flag;
    }

    public Shape applyOp(Shape oldClip, Shape newClip) {
        return this.op.apply(oldClip, newClip);
    }

    private static Shape andOp(Shape oldClip, Shape newClip) {
        assert (newClip != null);
        if (newClip.getBounds2D().isEmpty()) {
            return oldClip;
        }
        if (oldClip == null) {
            return newClip;
        }
        Area newArea = new Area(oldClip);
        newArea.intersect(new Area(newClip));
        return newArea.getBounds2D().isEmpty() ? newClip : newArea;
    }

    private static Shape orOp(Shape oldClip, Shape newClip) {
        assert (newClip != null);
        if (newClip.getBounds2D().isEmpty()) {
            return oldClip;
        }
        if (oldClip == null) {
            return newClip;
        }
        Area newArea = new Area(oldClip);
        newArea.add(new Area(newClip));
        return newArea;
    }

    private static Shape xorOp(Shape oldClip, Shape newClip) {
        assert (newClip != null);
        if (newClip.getBounds2D().isEmpty()) {
            return oldClip;
        }
        if (oldClip == null) {
            return newClip;
        }
        Area newArea = new Area(oldClip);
        newArea.exclusiveOr(new Area(newClip));
        return newArea;
    }

    private static Shape diffOp(Shape oldClip, Shape newClip) {
        assert (newClip != null);
        if (newClip.getBounds2D().isEmpty()) {
            return oldClip;
        }
        if (oldClip == null) {
            return newClip;
        }
        Area newArea = new Area(oldClip);
        newArea.subtract(new Area(newClip));
        return newArea;
    }

    private static Shape copyOp(Shape oldClip, Shape newClip) {
        return newClip == null || newClip.getBounds2D().isEmpty() ? null : newClip;
    }

    private static Shape complementOp(Shape oldClip, Shape newClip) {
        assert (newClip != null);
        if (newClip.getBounds2D().isEmpty()) {
            return oldClip;
        }
        if (oldClip == null) {
            return newClip;
        }
        Area newArea = new Area(newClip);
        newArea.subtract(new Area(oldClip));
        return newArea;
    }

    static {
        $VALUES = new HwmfRegionMode[]{RGN_AND, RGN_OR, RGN_XOR, RGN_DIFF, RGN_COPY, RGN_COMPLEMENT};
    }
}

