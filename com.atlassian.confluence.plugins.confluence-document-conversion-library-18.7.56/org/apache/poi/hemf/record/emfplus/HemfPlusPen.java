/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfFill;
import org.apache.poi.hemf.record.emf.HemfPenStyle;
import org.apache.poi.hemf.record.emfplus.HemfPlusBrush;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.hemf.record.emfplus.HemfPlusPath;
import org.apache.poi.hwmf.record.HwmfPenStyle;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPlusPen {

    public static class EmfPlusAdjustableArrowCap
    extends EmfPlusCustomLineCap {
        private double width;
        private double height;
        private double middleInset;
        private boolean isFilled;

        @Override
        public long init(LittleEndianInputStream leis) throws IOException {
            this.width = leis.readFloat();
            this.height = leis.readFloat();
            this.middleInset = leis.readFloat();
            this.isFilled = leis.readInt() != 0;
            return 16L + super.init(leis);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("width", () -> this.width, "height", () -> this.height, "middleInset", () -> this.middleInset, "isFilled", () -> this.isFilled, "base", () -> super.getGenericProperties());
        }
    }

    public static class EmfPlusPathArrowCap
    extends EmfPlusCustomLineCap {
        private static final BitField FILL_PATH = BitFieldFactory.getInstance(1);
        private static final BitField LINE_PATH = BitFieldFactory.getInstance(2);
        private static final int[] FLAGS_MASKS = new int[]{1, 2};
        private static final String[] FLAGS_NAMES = new String[]{"FILL_PATH", "LINE_PATH"};
        private int dataFlags;
        private EmfPlusLineCapType baseCap;
        private double baseInset;
        private HemfPlusPath.EmfPlusPath fillPath;
        private HemfPlusPath.EmfPlusPath outlinePath;

        @Override
        public long init(LittleEndianInputStream leis) throws IOException {
            this.dataFlags = leis.readInt();
            this.baseCap = EmfPlusLineCapType.valueOf(leis.readInt());
            this.baseInset = leis.readFloat();
            long size = 12L;
            size += super.init(leis);
            size += this.initPath(leis, FILL_PATH, p -> {
                this.fillPath = p;
            });
            return size += this.initPath(leis, LINE_PATH, p -> {
                this.outlinePath = p;
            });
        }

        private long initPath(LittleEndianInputStream leis, BitField bitField, Consumer<HemfPlusPath.EmfPlusPath> setter) throws IOException {
            if (!bitField.isSet(this.dataFlags)) {
                return 0L;
            }
            int pathSize = leis.readInt();
            HemfPlusPath.EmfPlusPath path = new HemfPlusPath.EmfPlusPath();
            setter.accept(path);
            return 4L + path.init(leis, pathSize, HemfPlusObject.EmfPlusObjectType.PATH, -1);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getBitsAsString(() -> this.dataFlags, FLAGS_MASKS, FLAGS_NAMES), "baseCap", () -> this.baseCap, "baseInset", () -> this.baseInset, "base", () -> super.getGenericProperties(), "fillPath", () -> this.fillPath, "outlinePath", () -> this.outlinePath);
        }
    }

    public static class EmfPlusPen
    implements HemfPlusObject.EmfPlusObjectData {
        private static final BitField TRANSFORM = BitFieldFactory.getInstance(1);
        private static final BitField START_CAP = BitFieldFactory.getInstance(2);
        private static final BitField END_CAP = BitFieldFactory.getInstance(4);
        private static final BitField JOIN = BitFieldFactory.getInstance(8);
        private static final BitField MITER_LIMIT = BitFieldFactory.getInstance(16);
        private static final BitField LINE_STYLE = BitFieldFactory.getInstance(32);
        private static final BitField DASHED_LINE_CAP = BitFieldFactory.getInstance(64);
        private static final BitField DASHED_LINE_OFFSET = BitFieldFactory.getInstance(128);
        private static final BitField DASHED_LINE = BitFieldFactory.getInstance(256);
        private static final BitField NON_CENTER = BitFieldFactory.getInstance(512);
        private static final BitField COMPOUND_LINE = BitFieldFactory.getInstance(1024);
        private static final BitField CUSTOM_START_CAP = BitFieldFactory.getInstance(2048);
        private static final BitField CUSTOM_END_CAP = BitFieldFactory.getInstance(4096);
        private static final int[] FLAGS_MASKS = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096};
        private static final String[] FLAGS_NAMES = new String[]{"TRANSFORM", "START_CAP", "END_CAP", "JOIN", "MITER_LIMIT", "LINE_STYLE", "DASHED_LINE_CAP", "DASHED_LINE_OFFSET", "DASHED_LINE", "NON_CENTER", "COMPOUND_LINE", "CUSTOM_START_CAP", "CUSTOM_END_CAP"};
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private int type;
        private int penDataFlags;
        private HemfPlusDraw.EmfPlusUnitType unitType;
        private double penWidth;
        private final AffineTransform trans = new AffineTransform();
        private EmfPlusLineCapType startCap;
        private EmfPlusLineCapType endCap = this.startCap = EmfPlusLineCapType.FLAT;
        private EmfPlusLineJoin lineJoin = EmfPlusLineJoin.ROUND;
        private Double miterLimit = 1.0;
        private EmfPlusLineStyle style = EmfPlusLineStyle.SOLID;
        private EmfPlusDashedLineCapType dashedLineCapType;
        private Double dashOffset;
        private float[] dashedLineData;
        private EmfPlusPenAlignment penAlignment;
        private double[] compoundLineData;
        private EmfPlusCustomLineCap customStartCap;
        private EmfPlusCustomLineCap customEndCap;
        private final HemfPlusBrush.EmfPlusBrush brush = new HemfPlusBrush.EmfPlusBrush();

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            int i;
            long size = this.graphicsVersion.init(leis);
            this.type = leis.readInt();
            this.penDataFlags = leis.readInt();
            this.unitType = HemfPlusDraw.EmfPlusUnitType.valueOf(leis.readInt());
            this.penWidth = leis.readFloat();
            size += 16L;
            if (TRANSFORM.isSet(this.penDataFlags)) {
                size += (long)HemfFill.readXForm(leis, this.trans);
            }
            if (START_CAP.isSet(this.penDataFlags)) {
                this.startCap = EmfPlusLineCapType.valueOf(leis.readInt());
                size += 4L;
            }
            if (END_CAP.isSet(this.penDataFlags)) {
                this.endCap = EmfPlusLineCapType.valueOf(leis.readInt());
                size += 4L;
            }
            if (JOIN.isSet(this.penDataFlags)) {
                this.lineJoin = EmfPlusLineJoin.valueOf(leis.readInt());
                size += 4L;
            }
            if (MITER_LIMIT.isSet(this.penDataFlags)) {
                this.miterLimit = leis.readFloat();
                size += 4L;
            }
            if (LINE_STYLE.isSet(this.penDataFlags)) {
                this.style = EmfPlusLineStyle.valueOf(leis.readInt());
                size += 4L;
            }
            if (DASHED_LINE_CAP.isSet(this.penDataFlags)) {
                this.dashedLineCapType = EmfPlusDashedLineCapType.valueOf(leis.readInt());
                size += 4L;
            }
            if (DASHED_LINE_OFFSET.isSet(this.penDataFlags)) {
                this.dashOffset = leis.readFloat();
                size += 4L;
            }
            if (DASHED_LINE.isSet(this.penDataFlags)) {
                int dashesSize = leis.readInt();
                if (dashesSize < 0 || dashesSize > 1000) {
                    throw new RuntimeException("Invalid dash data size");
                }
                this.dashedLineData = new float[dashesSize];
                for (i = 0; i < dashesSize; ++i) {
                    this.dashedLineData[i] = leis.readFloat();
                }
                size += (long)(4 * (dashesSize + 1));
            }
            if (NON_CENTER.isSet(this.penDataFlags)) {
                this.penAlignment = EmfPlusPenAlignment.valueOf(leis.readInt());
                size += 4L;
            }
            if (COMPOUND_LINE.isSet(this.penDataFlags)) {
                int compoundSize = leis.readInt();
                if (compoundSize < 0 || compoundSize > 1000) {
                    throw new RuntimeException("Invalid compound line data size");
                }
                this.compoundLineData = new double[compoundSize];
                for (i = 0; i < compoundSize; ++i) {
                    this.compoundLineData[i] = leis.readFloat();
                }
                size += (long)(4 * (compoundSize + 1));
            }
            if (CUSTOM_START_CAP.isSet(this.penDataFlags)) {
                size += this.initCustomCap(c -> {
                    this.customStartCap = c;
                }, leis);
            }
            if (CUSTOM_END_CAP.isSet(this.penDataFlags)) {
                size += this.initCustomCap(c -> {
                    this.customEndCap = c;
                }, leis);
            }
            size += this.brush.init(leis, dataSize - size, HemfPlusObject.EmfPlusObjectType.BRUSH, 0);
            return size;
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        private long initCustomCap(Consumer<EmfPlusCustomLineCap> setter, LittleEndianInputStream leis) throws IOException {
            int CustomStartCapSize = leis.readInt();
            long size = 4L;
            HemfPlusHeader.EmfPlusGraphicsVersion version = new HemfPlusHeader.EmfPlusGraphicsVersion();
            size += version.init(leis);
            assert (version.getGraphicsVersion() != null);
            boolean adjustableArrow = leis.readInt() != 0;
            size += 4L;
            EmfPlusCustomLineCap cap = adjustableArrow ? new EmfPlusAdjustableArrowCap() : new EmfPlusPathArrowCap();
            setter.accept(cap);
            return Math.toIntExact(size += cap.init(leis));
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HwmfPenStyle.HwmfLineJoin lineJoin;
            HwmfPenStyle.HwmfLineCap cap;
            HemfDrawProperties prop = ctx.getProperties();
            this.brush.applyPen(ctx, continuedObjectData);
            prop.setPenWidth(this.penWidth);
            switch (this.startCap) {
                default: {
                    cap = HwmfPenStyle.HwmfLineCap.FLAT;
                    break;
                }
                case ROUND: {
                    cap = HwmfPenStyle.HwmfLineCap.ROUND;
                    break;
                }
                case SQUARE: {
                    cap = HwmfPenStyle.HwmfLineCap.SQUARE;
                }
            }
            switch (this.lineJoin) {
                default: {
                    lineJoin = HwmfPenStyle.HwmfLineJoin.BEVEL;
                    break;
                }
                case ROUND: {
                    lineJoin = HwmfPenStyle.HwmfLineJoin.ROUND;
                    break;
                }
                case MITER_CLIPPED: 
                case MITER: {
                    lineJoin = HwmfPenStyle.HwmfLineJoin.MITER;
                }
            }
            HwmfPenStyle.HwmfLineDash lineDash = this.dashedLineData == null ? HwmfPenStyle.HwmfLineDash.SOLID : HwmfPenStyle.HwmfLineDash.USERSTYLE;
            boolean isAlternate = lineDash != HwmfPenStyle.HwmfLineDash.SOLID && this.dashOffset != null && this.dashOffset == 0.0;
            boolean isGeometric = this.unitType == HemfPlusDraw.EmfPlusUnitType.World || this.unitType == HemfPlusDraw.EmfPlusUnitType.Display;
            HemfPenStyle penStyle = HemfPenStyle.valueOf(cap, lineJoin, lineDash, isAlternate, isGeometric);
            penStyle.setLineDashes(this.dashedLineData);
            prop.setPenStyle(penStyle);
        }

        public HemfPlusObject.EmfPlusObjectType getGenericRecordType() {
            return HemfPlusObject.EmfPlusObjectType.PEN;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("type", () -> this.type);
            m.put("flags", GenericRecordUtil.getBitsAsString(() -> this.penDataFlags, FLAGS_MASKS, FLAGS_NAMES));
            m.put("unitType", () -> this.unitType);
            m.put("penWidth", () -> this.penWidth);
            m.put("trans", () -> this.trans);
            m.put("startCap", () -> this.startCap);
            m.put("endCap", () -> this.endCap);
            m.put("join", () -> this.lineJoin);
            m.put("miterLimit", () -> this.miterLimit);
            m.put("style", () -> this.style);
            m.put("dashedLineCapType", () -> this.dashedLineCapType);
            m.put("dashOffset", () -> this.dashOffset);
            m.put("dashedLineData", () -> this.dashedLineData);
            m.put("penAlignment", () -> this.penAlignment);
            m.put("compoundLineData", () -> this.compoundLineData);
            m.put("customStartCap", () -> this.customStartCap);
            m.put("customEndCap", () -> this.customEndCap);
            m.put("brush", () -> this.brush);
            return Collections.unmodifiableMap(m);
        }
    }

    @Internal
    public static abstract class EmfPlusCustomLineCap
    implements GenericRecord {
        private EmfPlusLineCapType startCap;
        private EmfPlusLineCapType endCap;
        private EmfPlusLineJoin join;
        private double miterLimit;
        private double widthScale;
        private final Point2D fillHotSpot = new Point2D.Double();
        private final Point2D lineHotSpot = new Point2D.Double();

        protected long init(LittleEndianInputStream leis) throws IOException {
            this.startCap = EmfPlusLineCapType.valueOf(leis.readInt());
            this.endCap = EmfPlusLineCapType.valueOf(leis.readInt());
            this.join = EmfPlusLineJoin.valueOf(leis.readInt());
            this.miterLimit = leis.readFloat();
            this.widthScale = leis.readFloat();
            long size = 20L;
            size += (long)HemfPlusDraw.readPointF(leis, this.fillHotSpot);
            return size += (long)HemfPlusDraw.readPointF(leis, this.lineHotSpot);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap m = new LinkedHashMap();
            m.put("startCap", () -> this.startCap);
            m.put("endCap", () -> this.endCap);
            m.put("join", () -> this.join);
            m.put("miterLimit", () -> this.miterLimit);
            m.put("widthScale", () -> this.widthScale);
            m.put("fillHotSpot", () -> this.fillHotSpot);
            m.put("lineHotSpot", () -> this.lineHotSpot);
            return m;
        }

        public final HemfPlusObject.EmfPlusObjectType getGenericRecordType() {
            return HemfPlusObject.EmfPlusObjectType.CUSTOM_LINE_CAP;
        }
    }

    public static enum EmfPlusPenAlignment {
        CENTER(0),
        INSET(1),
        LEFT(2),
        OUTSET(3),
        RIGHT(4);

        public final int id;

        private EmfPlusPenAlignment(int id) {
            this.id = id;
        }

        public static EmfPlusPenAlignment valueOf(int id) {
            for (EmfPlusPenAlignment wrt : EmfPlusPenAlignment.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusDashedLineCapType {
        FLAT(0),
        ROUND(2),
        TRIANGLE(3);

        public final int id;

        private EmfPlusDashedLineCapType(int id) {
            this.id = id;
        }

        public static EmfPlusDashedLineCapType valueOf(int id) {
            for (EmfPlusDashedLineCapType wrt : EmfPlusDashedLineCapType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusLineStyle {
        SOLID(0),
        DASH(1),
        DOT(2),
        DASH_DOT(3),
        DASH_DOT_DOT(4),
        CUSTOM(5);

        public final int id;

        private EmfPlusLineStyle(int id) {
            this.id = id;
        }

        public static EmfPlusLineStyle valueOf(int id) {
            for (EmfPlusLineStyle wrt : EmfPlusLineStyle.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusLineJoin {
        MITER(0),
        BEVEL(1),
        ROUND(2),
        MITER_CLIPPED(3);

        public final int id;

        private EmfPlusLineJoin(int id) {
            this.id = id;
        }

        public static EmfPlusLineJoin valueOf(int id) {
            for (EmfPlusLineJoin wrt : EmfPlusLineJoin.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusLineCapType {
        FLAT(0),
        SQUARE(1),
        ROUND(2),
        TRIANGLE(3),
        NO_ANCHOR(16),
        SQUARE_ANCHOR(17),
        ROUND_ANCHOR(18),
        DIAMOND_ANCHOR(19),
        ARROW_ANCHOR(20),
        ANCHOR_MASK(240),
        CUSTOM(255);

        public final int id;

        private EmfPlusLineCapType(int id) {
            this.id = id;
        }

        public static EmfPlusLineCapType valueOf(int id) {
            for (EmfPlusLineCapType wrt : EmfPlusLineCapType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

