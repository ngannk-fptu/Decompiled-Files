/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;

public class HwmfPenStyle
implements Duplicatable,
GenericRecord {
    protected static final BitField SUBSECTION_DASH = BitFieldFactory.getInstance(7);
    protected static final BitField SUBSECTION_ALTERNATE = BitFieldFactory.getInstance(8);
    protected static final BitField SUBSECTION_ENDCAP = BitFieldFactory.getInstance(768);
    protected static final BitField SUBSECTION_JOIN = BitFieldFactory.getInstance(12288);
    protected static final BitField SUBSECTION_GEOMETRIC = BitFieldFactory.getInstance(65536);
    protected int flag;

    public HwmfPenStyle(int flag) {
        this.flag = flag;
    }

    public HwmfPenStyle(HwmfPenStyle other) {
        this.flag = other.flag;
    }

    public static HwmfPenStyle valueOf(int flag) {
        return new HwmfPenStyle(flag);
    }

    public HwmfLineCap getLineCap() {
        return HwmfLineCap.valueOf(SUBSECTION_ENDCAP.getValue(this.flag));
    }

    public HwmfLineJoin getLineJoin() {
        return HwmfLineJoin.valueOf(SUBSECTION_JOIN.getValue(this.flag));
    }

    public HwmfLineDash getLineDash() {
        return HwmfLineDash.valueOf(SUBSECTION_DASH.getValue(this.flag));
    }

    public float[] getLineDashes() {
        return this.getLineDash().dashes;
    }

    public boolean isAlternateDash() {
        return SUBSECTION_ALTERNATE.isSet(this.flag);
    }

    public boolean isGeometric() {
        return SUBSECTION_GEOMETRIC.isSet(this.flag);
    }

    @Override
    public HwmfPenStyle copy() {
        return new HwmfPenStyle(this);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("lineCap", this::getLineCap, "lineJoin", this::getLineJoin, "lineDash", this::getLineDash, "lineDashes", this::getLineDashes, "alternateDash", this::isAlternateDash, "geometric", this::isGeometric);
    }

    public static enum HwmfLineDash {
        SOLID(0, null),
        DASH(1, 10.0f, 8.0f),
        DOT(2, 2.0f, 4.0f),
        DASHDOT(3, 10.0f, 8.0f, 2.0f, 8.0f),
        DASHDOTDOT(4, 10.0f, 4.0f, 2.0f, 4.0f, 2.0f, 4.0f),
        NULL(5, null),
        INSIDEFRAME(6, null),
        USERSTYLE(7, null);

        public final int wmfFlag;
        public final float[] dashes;

        private HwmfLineDash(int wmfFlag, float ... dashes) {
            this.wmfFlag = wmfFlag;
            this.dashes = dashes;
        }

        static HwmfLineDash valueOf(int wmfFlag) {
            for (HwmfLineDash hs : HwmfLineDash.values()) {
                if (hs.wmfFlag != wmfFlag) continue;
                return hs;
            }
            return null;
        }
    }

    public static enum HwmfLineJoin {
        ROUND(0, 1),
        BEVEL(1, 2),
        MITER(2, 0);

        public final int wmfFlag;
        public final int awtFlag;

        private HwmfLineJoin(int wmfFlag, int awtFlag) {
            this.wmfFlag = wmfFlag;
            this.awtFlag = awtFlag;
        }

        static HwmfLineJoin valueOf(int wmfFlag) {
            for (HwmfLineJoin hs : HwmfLineJoin.values()) {
                if (hs.wmfFlag != wmfFlag) continue;
                return hs;
            }
            return null;
        }
    }

    public static enum HwmfLineCap {
        ROUND(0, 1),
        SQUARE(1, 2),
        FLAT(2, 0);

        public final int wmfFlag;
        public final int awtFlag;

        private HwmfLineCap(int wmfFlag, int awtFlag) {
            this.wmfFlag = wmfFlag;
            this.awtFlag = awtFlag;
        }

        static HwmfLineCap valueOf(int wmfFlag) {
            for (HwmfLineCap hs : HwmfLineCap.values()) {
                if (hs.wmfFlag != wmfFlag) continue;
                return hs;
            }
            return null;
        }
    }
}

