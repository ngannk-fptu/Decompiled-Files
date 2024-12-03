/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfFill;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusImage;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.hemf.record.emfplus.HemfPlusPath;
import org.apache.poi.hwmf.record.HwmfBrushStyle;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPlusBrush {
    private static int readPositions(LittleEndianInputStream leis, Consumer<float[]> pos) {
        int count = leis.readInt();
        int size = 4;
        float[] positions = new float[count];
        for (int i = 0; i < count; ++i) {
            positions[i] = leis.readFloat();
            size += 4;
        }
        pos.accept(positions);
        return size;
    }

    private static int readColors(LittleEndianInputStream leis, Consumer<float[]> pos, Consumer<Color[]> cols) {
        int[] count = new int[]{0};
        int size = HemfPlusBrush.readPositions(leis, p -> {
            count[0] = ((float[])p).length;
            pos.accept((float[])p);
        });
        Color[] colors = new Color[count[0]];
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = HemfPlusDraw.readARGB(leis.readInt());
        }
        cols.accept(colors);
        return size + colors.length * 4;
    }

    private static int readFactors(LittleEndianInputStream leis, Consumer<float[]> pos, Consumer<float[]> facs) {
        int[] count = new int[]{0};
        int size = HemfPlusBrush.readPositions(leis, p -> {
            count[0] = ((float[])p).length;
            pos.accept((float[])p);
        });
        float[] factors = new float[count[0]];
        for (int i = 0; i < factors.length; ++i) {
            factors[i] = leis.readFloat();
        }
        facs.accept(factors);
        return size + factors.length * 4;
    }

    public static class EmfPlusTextureBrushData
    implements EmfPlusBrushData {
        private int dataFlags;
        private HemfPlusImage.EmfPlusWrapMode wrapMode;
        private AffineTransform brushTransform;
        private HemfPlusImage.EmfPlusImage image;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            this.dataFlags = leis.readInt();
            this.wrapMode = HemfPlusImage.EmfPlusWrapMode.valueOf(leis.readInt());
            long size = 8L;
            if (TRANSFORM.isSet(this.dataFlags)) {
                this.brushTransform = new AffineTransform();
                size += (long)HemfFill.readXForm(leis, this.brushTransform);
            }
            if (dataSize > size) {
                this.image = new HemfPlusImage.EmfPlusImage();
                size += this.image.init(leis, dataSize - size, HemfPlusObject.EmfPlusObjectType.IMAGE, 0);
            }
            return Math.toIntExact(size);
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            this.image.applyObject(ctx, null);
            prop.setBrushBitmap(prop.getEmfPlusImage());
            prop.setBrushStyle(HwmfBrushStyle.BS_PATTERN);
            prop.setBrushTransform(this.brushTransform);
        }

        @Override
        public void applyPen(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public EmfPlusBrushType getGenericRecordType() {
            return EmfPlusBrushType.TEXTURE_FILL;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("dataFlags", () -> this.dataFlags, "wrapMode", () -> this.wrapMode, "brushTransform", () -> this.brushTransform, "image", () -> this.image);
        }
    }

    public static class EmfPlusPathGradientBrushData
    implements EmfPlusBrushData {
        private int dataFlags;
        private HemfPlusImage.EmfPlusWrapMode wrapMode;
        private Color centerColor;
        private final Point2D centerPoint = new Point2D.Double();
        private Color[] surroundingColor;
        private HemfPlusPath.EmfPlusPath boundaryPath;
        private Point2D[] boundaryPoints;
        private AffineTransform blendTransform;
        private float[] positions;
        private Color[] blendColors;
        private float[] blendFactorsH;
        private Double focusScaleX;
        private Double focusScaleY;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            this.dataFlags = leis.readInt();
            this.wrapMode = HemfPlusImage.EmfPlusWrapMode.valueOf(leis.readInt());
            this.centerColor = HemfPlusDraw.readARGB(leis.readInt());
            long size = 12L;
            if (this.wrapMode == null) {
                return size;
            }
            size += (long)HemfPlusDraw.readPointF(leis, this.centerPoint);
            int colorCount = leis.readInt();
            this.surroundingColor = new Color[colorCount];
            for (int i = 0; i < colorCount; ++i) {
                this.surroundingColor[i] = HemfPlusDraw.readARGB(leis.readInt());
            }
            size += ((long)colorCount + 1L) * 4L;
            if (PATH.isSet(this.dataFlags)) {
                int pathDataSize = leis.readInt();
                size += 4L;
                this.boundaryPath = new HemfPlusPath.EmfPlusPath();
                size += this.boundaryPath.init(leis, pathDataSize, HemfPlusObject.EmfPlusObjectType.PATH, 0);
            } else {
                int pointCount = leis.readInt();
                size += 4L;
                this.boundaryPoints = new Point2D[pointCount];
                for (int i = 0; i < pointCount; ++i) {
                    this.boundaryPoints[i] = new Point2D.Double();
                    size += (long)HemfPlusDraw.readPointF(leis, this.boundaryPoints[i]);
                }
            }
            if (TRANSFORM.isSet(this.dataFlags)) {
                this.blendTransform = new AffineTransform();
                size += (long)HemfFill.readXForm(leis, this.blendTransform);
            }
            boolean isPreset = PRESET_COLORS.isSet(this.dataFlags);
            boolean blendH = BLEND_FACTORS_H.isSet(this.dataFlags);
            if (isPreset && blendH) {
                throw new RuntimeException("invalid combination of preset colors and blend factors h");
            }
            size += isPreset ? (long)HemfPlusBrush.readColors(leis, d -> {
                this.positions = d;
            }, c -> {
                this.blendColors = c;
            }) : 0L;
            size += blendH ? (long)HemfPlusBrush.readFactors(leis, d -> {
                this.positions = d;
            }, f -> {
                this.blendFactorsH = f;
            }) : 0L;
            if (FOCUS_SCALES.isSet(this.dataFlags)) {
                int focusScaleCount = leis.readInt();
                if (focusScaleCount != 2) {
                    throw new RuntimeException("invalid focus scale count");
                }
                this.focusScaleX = leis.readFloat();
                this.focusScaleY = leis.readFloat();
                size += 12L;
            }
            return size;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
        }

        @Override
        public void applyPen(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public EmfPlusBrushType getGenericRecordType() {
            return EmfPlusBrushType.PATH_GRADIENT;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("flags", () -> this.dataFlags);
            m.put("wrapMode", () -> this.wrapMode);
            m.put("centerColor", () -> this.centerColor);
            m.put("centerPoint", () -> this.centerPoint);
            m.put("surroundingColor", () -> this.surroundingColor);
            m.put("boundaryPath", () -> this.boundaryPath);
            m.put("boundaryPoints", () -> this.boundaryPoints);
            m.put("blendTransform", () -> this.blendTransform);
            m.put("positions", () -> this.positions);
            m.put("blendColors", () -> this.blendColors);
            m.put("blendFactorsH", () -> this.blendFactorsH);
            m.put("focusScaleX", () -> this.focusScaleX);
            m.put("focusScaleY", () -> this.focusScaleY);
            return Collections.unmodifiableMap(m);
        }
    }

    public static class EmfPlusLinearGradientBrushData
    implements EmfPlusBrushData {
        private int dataFlags;
        private HemfPlusImage.EmfPlusWrapMode wrapMode;
        private final Rectangle2D rect = new Rectangle2D.Double();
        private Color startColor;
        private Color endColor;
        private AffineTransform blendTransform;
        private float[] positions;
        private Color[] blendColors;
        private float[] positionsV;
        private float[] blendFactorsV;
        private float[] positionsH;
        private float[] blendFactorsH;
        private static final int[] FLAG_MASKS = new int[]{2, 4, 8, 16, 128};
        private static final String[] FLAG_NAMES = new String[]{"TRANSFORM", "PRESET_COLORS", "BLEND_FACTORS_H", "BLEND_FACTORS_V", "BRUSH_DATA_IS_GAMMA_CORRECTED"};

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            this.dataFlags = leis.readInt();
            this.wrapMode = HemfPlusImage.EmfPlusWrapMode.valueOf(leis.readInt());
            long size = 8L;
            size += (long)HemfPlusDraw.readRectF(leis, this.rect);
            this.startColor = HemfPlusDraw.readARGB(leis.readInt());
            this.endColor = HemfPlusDraw.readARGB(leis.readInt());
            leis.skipFully(8);
            size += 16L;
            if (TRANSFORM.isSet(this.dataFlags)) {
                this.blendTransform = new AffineTransform();
                size += (long)HemfFill.readXForm(leis, this.blendTransform);
            }
            if (this.isPreset() && (this.isBlendH() || this.isBlendV())) {
                throw new RuntimeException("invalid combination of preset colors and blend factors v/h");
            }
            size += this.isPreset() ? (long)HemfPlusBrush.readColors(leis, d -> {
                this.positions = d;
            }, c -> {
                this.blendColors = c;
            }) : 0L;
            size += this.isBlendV() ? (long)HemfPlusBrush.readFactors(leis, d -> {
                this.positionsV = d;
            }, f -> {
                this.blendFactorsV = f;
            }) : 0L;
            return size += this.isBlendH() ? (long)HemfPlusBrush.readFactors(leis, d -> {
                this.positionsH = d;
            }, f -> {
                this.blendFactorsH = f;
            }) : 0L;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setBrushStyle(HwmfBrushStyle.BS_LINEAR_GRADIENT);
            prop.setBrushRect(this.rect);
            prop.setBrushTransform(this.blendTransform);
            if (this.isPreset()) {
                EmfPlusLinearGradientBrushData.setColorProps(prop::setBrushColorsH, this.positions, this::getBlendColorAt);
            } else {
                EmfPlusLinearGradientBrushData.setColorProps(prop::setBrushColorsH, this.positionsH, this::getBlendHColorAt);
            }
            EmfPlusLinearGradientBrushData.setColorProps(prop::setBrushColorsV, this.positionsV, this::getBlendVColorAt);
            if (!(this.isPreset() || this.isBlendH() || this.isBlendV())) {
                prop.setBrushColorsH(Arrays.asList(EmfPlusLinearGradientBrushData.kv(Float.valueOf(0.0f), this.startColor), EmfPlusLinearGradientBrushData.kv(Float.valueOf(1.0f), this.endColor)));
            }
        }

        @Override
        public void applyPen(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public EmfPlusBrushType getGenericRecordType() {
            return EmfPlusBrushType.LINEAR_GRADIENT;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("flags", GenericRecordUtil.getBitsAsString(() -> this.dataFlags, FLAG_MASKS, FLAG_NAMES));
            m.put("wrapMode", () -> this.wrapMode);
            m.put("rect", () -> this.rect);
            m.put("startColor", () -> this.startColor);
            m.put("endColor", () -> this.endColor);
            m.put("blendTransform", () -> this.blendTransform);
            m.put("positions", () -> this.positions);
            m.put("blendColors", () -> this.blendColors);
            m.put("positionsV", () -> this.positionsV);
            m.put("blendFactorsV", () -> this.blendFactorsV);
            m.put("positionsH", () -> this.positionsH);
            m.put("blendFactorsH", () -> this.blendFactorsH);
            return Collections.unmodifiableMap(m);
        }

        private boolean isPreset() {
            return PRESET_COLORS.isSet(this.dataFlags);
        }

        private boolean isBlendH() {
            return BLEND_FACTORS_H.isSet(this.dataFlags);
        }

        private boolean isBlendV() {
            return BLEND_FACTORS_V.isSet(this.dataFlags);
        }

        private Map.Entry<Float, Color> getBlendColorAt(int index) {
            return EmfPlusLinearGradientBrushData.kv(Float.valueOf(this.positions[index]), this.blendColors[index]);
        }

        private Map.Entry<Float, Color> getBlendHColorAt(int index) {
            return EmfPlusLinearGradientBrushData.kv(Float.valueOf(this.positionsH[index]), this.interpolateColors(this.blendFactorsH[index]));
        }

        private Map.Entry<Float, Color> getBlendVColorAt(int index) {
            return EmfPlusLinearGradientBrushData.kv(Float.valueOf(this.positionsV[index]), this.interpolateColors(this.blendFactorsV[index]));
        }

        private static Map.Entry<Float, Color> kv(Float position, Color color) {
            return new AbstractMap.SimpleEntry<Float, Color>(position, color);
        }

        private static void setColorProps(Consumer<List<? extends Map.Entry<Float, Color>>> setter, float[] positions, Function<Integer, ? extends Map.Entry<Float, Color>> sup) {
            if (positions == null) {
                setter.accept(null);
            } else {
                setter.accept(IntStream.range(0, positions.length).boxed().map(sup).collect(Collectors.toList()));
            }
        }

        private Color interpolateColors(double factor) {
            return this.interpolateColorsRGB(factor);
        }

        private Color interpolateColorsRGB(double factor) {
            double[] start = DrawPaint.RGB2SCRGB(this.startColor);
            double[] end = DrawPaint.RGB2SCRGB(this.endColor);
            int a = (int)Math.round((double)this.startColor.getAlpha() + factor * (double)(this.endColor.getAlpha() - this.startColor.getAlpha()));
            double r = start[0] + factor * (end[0] - start[0]);
            double g = start[1] + factor * (end[1] - start[1]);
            double b = start[2] + factor * (end[2] - start[2]);
            Color inter = DrawPaint.SCRGB2RGB(r, g, b);
            return new Color(inter.getRed(), inter.getGreen(), inter.getBlue(), a);
        }
    }

    public static class EmfPlusHatchBrushData
    implements EmfPlusBrushData {
        private EmfPlusHatchStyle style;
        private Color foreColor;
        private Color backColor;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) {
            this.style = EmfPlusHatchStyle.valueOf(leis.readInt());
            this.foreColor = HemfPlusDraw.readARGB(leis.readInt());
            this.backColor = HemfPlusDraw.readARGB(leis.readInt());
            return 12L;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setBrushColor(new HwmfColorRef(this.foreColor));
            prop.setBackgroundColor(new HwmfColorRef(this.backColor));
            prop.setEmfPlusBrushHatch(this.style);
        }

        @Override
        public void applyPen(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setPenColor(new HwmfColorRef(this.foreColor));
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public EmfPlusBrushType getGenericRecordType() {
            return EmfPlusBrushType.HATCH_FILL;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("style", () -> this.style, "foreColor", () -> this.foreColor, "backColor", () -> this.backColor);
        }
    }

    public static class EmfPlusSolidBrushData
    implements EmfPlusBrushData {
        private Color solidColor;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize) throws IOException {
            this.solidColor = HemfPlusDraw.readARGB(leis.readInt());
            return 4L;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setBrushColor(new HwmfColorRef(this.solidColor));
            prop.setBrushTransform(null);
            prop.setBrushStyle(HwmfBrushStyle.BS_SOLID);
        }

        @Override
        public void applyPen(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setPenColor(new HwmfColorRef(this.solidColor));
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public EmfPlusBrushType getGenericRecordType() {
            return EmfPlusBrushType.SOLID_COLOR;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("solidColor", () -> this.solidColor);
        }
    }

    public static class EmfPlusBrush
    implements HemfPlusObject.EmfPlusObjectData {
        private static final int MAX_OBJECT_SIZE = 1000000;
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private EmfPlusBrushType brushType;
        private byte[] brushBytes;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            leis.mark(4);
            long size = this.graphicsVersion.init(leis);
            if (this.isContinuedRecord()) {
                leis.reset();
                size = 0L;
            } else {
                int brushInt = leis.readInt();
                this.brushType = EmfPlusBrushType.valueOf(brushInt);
                assert (this.brushType != null);
                size += 4L;
            }
            this.brushBytes = IOUtils.toByteArray(leis, Math.toIntExact(dataSize - size), 1000000);
            return dataSize;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            EmfPlusBrushData brushData = this.getBrushData(continuedObjectData);
            brushData.applyObject(ctx, continuedObjectData);
        }

        public void applyPen(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            EmfPlusBrushData brushData = this.getBrushData(continuedObjectData);
            brushData.applyPen(ctx, continuedObjectData);
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public byte[] getBrushBytes() {
            return this.brushBytes;
        }

        public EmfPlusBrushData getBrushData(List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            EmfPlusBrushData brushData = this.brushType.constructor.get();
            byte[] buf = this.getRawData(continuedObjectData);
            try {
                brushData.init(new LittleEndianInputStream((InputStream)new UnsynchronizedByteArrayInputStream(buf)), buf.length);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return brushData;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public byte[] getRawData(List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
                bos.write(this.getBrushBytes());
                if (continuedObjectData != null) {
                    for (HemfPlusObject.EmfPlusObjectData emfPlusObjectData : continuedObjectData) {
                        bos.write(((EmfPlusBrush)emfPlusObjectData).getBrushBytes());
                    }
                }
                Object object = bos.toByteArray();
                return object;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public EmfPlusBrushType getGenericRecordType() {
            return this.brushType;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("graphicsVersion", this::getGraphicsVersion, "brushData", () -> this.getBrushData(null));
        }
    }

    public static interface EmfPlusBrushData
    extends GenericRecord {
        public static final BitField PATH = BitFieldFactory.getInstance(1);
        public static final BitField TRANSFORM = BitFieldFactory.getInstance(2);
        public static final BitField PRESET_COLORS = BitFieldFactory.getInstance(4);
        public static final BitField BLEND_FACTORS_H = BitFieldFactory.getInstance(8);
        public static final BitField BLEND_FACTORS_V = BitFieldFactory.getInstance(16);
        public static final BitField FOCUS_SCALES = BitFieldFactory.getInstance(64);
        public static final BitField IS_GAMMA_CORRECTED = BitFieldFactory.getInstance(128);
        public static final BitField DO_NOT_TRANSFORM = BitFieldFactory.getInstance(256);

        public long init(LittleEndianInputStream var1, long var2) throws IOException;

        public void applyObject(HemfGraphics var1, List<? extends HemfPlusObject.EmfPlusObjectData> var2);

        public void applyPen(HemfGraphics var1, List<? extends HemfPlusObject.EmfPlusObjectData> var2);
    }

    public static enum EmfPlusHatchStyle {
        HORIZONTAL(0),
        VERTICAL(1),
        FORWARD_DIAGONAL(2),
        BACKWARD_DIAGONAL(3),
        LARGE_GRID(4),
        DIAGONAL_CROSS(5),
        PERCENT_05(6),
        PERCENT_10(7),
        PERCENT_20(8),
        PERCENT_25(9),
        PERCENT_30(10),
        PERCENT_40(11),
        PERCENT_50(12),
        PERCENT_60(13),
        PERCENT_70(14),
        PERCENT_75(15),
        PERCENT_80(16),
        PERCENT_90(17),
        LIGHT_DOWNWARD_DIAGONAL(18),
        LIGHT_UPWARD_DIAGONAL(19),
        DARK_DOWNWARD_DIAGONAL(20),
        DARK_UPWARD_DIAGONAL(21),
        WIDE_DOWNWARD_DIAGONAL(22),
        WIDE_UPWARD_DIAGONAL(23),
        LIGHT_VERTICAL(24),
        LIGHT_HORIZONTAL(25),
        NARROW_VERTICAL(26),
        NARROW_HORIZONTAL(27),
        DARK_VERTICAL(28),
        DARK_HORIZONTAL(29),
        DASHED_DOWNWARD_DIAGONAL(30),
        DASHED_UPWARD_DIAGONAL(31),
        DASHED_HORIZONTAL(32),
        DASHED_VERTICAL(33),
        SMALL_CONFETTI(34),
        LARGE_CONFETTI(35),
        ZIGZAG(36),
        WAVE(37),
        DIAGONAL_BRICK(38),
        HORIZONTAL_BRICK(39),
        WEAVE(40),
        PLAID(41),
        DIVOT(42),
        DOTTED_GRID(43),
        DOTTED_DIAMOND(44),
        SHINGLE(45),
        TRELLIS(46),
        SPHERE(47),
        SMALL_GRID(48),
        SMALL_CHECKER_BOARD(49),
        LARGE_CHECKER_BOARD(50),
        OUTLINED_DIAMOND(51),
        SOLID_DIAMOND(52);

        public final int id;

        private EmfPlusHatchStyle(int id) {
            this.id = id;
        }

        public static EmfPlusHatchStyle valueOf(int id) {
            for (EmfPlusHatchStyle wrt : EmfPlusHatchStyle.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusBrushType {
        SOLID_COLOR(0, EmfPlusSolidBrushData::new),
        HATCH_FILL(1, EmfPlusHatchBrushData::new),
        TEXTURE_FILL(2, EmfPlusTextureBrushData::new),
        PATH_GRADIENT(3, EmfPlusPathGradientBrushData::new),
        LINEAR_GRADIENT(4, EmfPlusLinearGradientBrushData::new);

        public final int id;
        public final Supplier<? extends EmfPlusBrushData> constructor;

        private EmfPlusBrushType(int id, Supplier<? extends EmfPlusBrushData> constructor) {
            this.id = id;
            this.constructor = constructor;
        }

        public static EmfPlusBrushType valueOf(int id) {
            for (EmfPlusBrushType wrt : EmfPlusBrushType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

