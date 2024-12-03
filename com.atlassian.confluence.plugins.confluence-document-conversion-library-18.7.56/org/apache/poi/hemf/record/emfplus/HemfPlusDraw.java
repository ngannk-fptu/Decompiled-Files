/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.math3.linear.LUDecomposition
 *  org.apache.commons.math3.linear.MatrixUtils
 *  org.apache.commons.math3.linear.RealMatrix
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfFill;
import org.apache.poi.hemf.record.emfplus.HemfPlusMisc;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordType;
import org.apache.poi.hwmf.record.HwmfBrushStyle;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfMisc;
import org.apache.poi.hwmf.record.HwmfPenStyle;
import org.apache.poi.hwmf.record.HwmfTernaryRasterOp;
import org.apache.poi.hwmf.record.HwmfText;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.StringUtil;

public final class HemfPlusDraw {
    private static final int MAX_OBJECT_SIZE = 1000000;

    private HemfPlusDraw() {
    }

    static double round10(double d) {
        return BigDecimal.valueOf(d).setScale(10, RoundingMode.HALF_UP).doubleValue();
    }

    static int readRectS(LittleEndianInputStream leis, Rectangle2D bounds) {
        short x = leis.readShort();
        short y = leis.readShort();
        short width = leis.readShort();
        short height = leis.readShort();
        bounds.setRect(x, y, width, height);
        return 8;
    }

    static int readRectF(LittleEndianInputStream leis, Rectangle2D bounds) {
        double x = leis.readFloat();
        double y = leis.readFloat();
        double width = leis.readFloat();
        double height = leis.readFloat();
        bounds.setRect(x, y, width, height);
        return 16;
    }

    static int readPointS(LittleEndianInputStream leis, Point2D point) {
        double x = leis.readShort();
        double y = leis.readShort();
        point.setLocation(x, y);
        return 4;
    }

    static int readPointF(LittleEndianInputStream leis, Point2D point) {
        double x = leis.readFloat();
        double y = leis.readFloat();
        point.setLocation(x, y);
        return 8;
    }

    static int readPointR(LittleEndianInputStream leis, Point2D point) {
        int[] p = new int[]{0};
        int size = HemfPlusDraw.readEmfPlusInteger(leis, p);
        double x = p[0];
        double y = p[0];
        point.setLocation(x, y);
        return size += HemfPlusDraw.readEmfPlusInteger(leis, p);
    }

    private static int readEmfPlusInteger(LittleEndianInputStream leis, int[] value) {
        value[0] = leis.readByte();
        if ((value[0] & 0x80) == 0) {
            return 1;
        }
        value[0] = (value[0] << 8 | leis.readByte() & 0xFF) & Short.MAX_VALUE;
        return 2;
    }

    static Color readARGB(int argb) {
        return new Color(argb >>> 16 & 0xFF, argb >>> 8 & 0xFF, argb & 0xFF, argb >>> 24 & 0xFF);
    }

    public static class EmfPlusDrawRects
    implements HemfPlusRecord,
    HemfPlusMisc.EmfPlusObjectId,
    EmfPlusCompressed {
        private int flags;
        private final List<Rectangle2D> rectData = new ArrayList<Rectangle2D>();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.drawRects;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            int count = leis.readInt();
            int size = 4;
            BiFunction<LittleEndianInputStream, Rectangle2D, Integer> readRect = this.getReadRect();
            for (int i = 0; i < count; ++i) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                size += readRect.apply(leis, rect).intValue();
            }
            return size;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "rectData", () -> this.rectData);
        }
    }

    public static class EmfPlusDrawDriverString
    implements HemfPlusRecord,
    HemfPlusMisc.EmfPlusObjectId,
    EmfPlusSolidColor {
        private static final BitField CMAP_LOOKUP = BitFieldFactory.getInstance(1);
        private static final BitField VERTICAL = BitFieldFactory.getInstance(2);
        private static final BitField REALIZED_ADVANCE = BitFieldFactory.getInstance(4);
        private static final BitField LIMIT_SUBPIXEL = BitFieldFactory.getInstance(8);
        private static final int[] OPTIONS_MASK = new int[]{1, 2, 4, 8};
        private static final String[] OPTIONS_NAMES = new String[]{"CMAP_LOOKUP", "VERTICAL", "REALIZED_ADVANCE", "LIMIT_SUBPIXEL"};
        private int flags;
        private int brushId;
        private int optionsFlags;
        private String glyphs;
        private final List<Point2D> glyphPos = new ArrayList<Point2D>();
        private final AffineTransform transformMatrix = new AffineTransform();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.drawDriverString;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public int getBrushIdValue() {
            return this.brushId;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.brushId = leis.readInt();
            this.optionsFlags = leis.readInt();
            int matrixPresent = leis.readInt();
            int glyphCount = leis.readInt();
            int size = 16;
            byte[] glyphBuf = IOUtils.toByteArray(leis, glyphCount * 2, 1000000);
            this.glyphs = StringUtil.getFromUnicodeLE(glyphBuf);
            size += glyphBuf.length;
            int glyphPosCnt = REALIZED_ADVANCE.isSet(this.optionsFlags) ? 1 : glyphCount;
            for (int i = 0; i < glyphCount; ++i) {
                Point2D.Double p = new Point2D.Double();
                size += HemfPlusDraw.readPointF(leis, p);
                this.glyphPos.add(p);
            }
            if (matrixPresent != 0) {
                size += HemfFill.readXForm(leis, this.transformMatrix);
            }
            return size;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setTextAlignLatin(HwmfText.HwmfTextAlignment.LEFT);
            prop.setTextVAlignLatin(HwmfText.HwmfTextVerticalAlignment.BASELINE);
            ctx.applyPlusObjectTableEntry(this.getObjectId());
            if (this.isSolidColor()) {
                prop.setTextColor(new HwmfColorRef(this.getSolidColor()));
            } else {
                ctx.applyPlusObjectTableEntry(this.getBrushId());
            }
            if (REALIZED_ADVANCE.isSet(this.optionsFlags)) {
                byte[] buf = this.glyphs.getBytes(StandardCharsets.UTF_16LE);
                ctx.drawString(buf, buf.length, this.glyphPos.get(0), null, null, null, null, true);
            } else {
                PrimitiveIterator.OfInt glyphIter = this.glyphs.codePoints().iterator();
                this.glyphPos.forEach(p -> {
                    byte[] buf = new String(new int[]{glyphIter.next()}, 0, 1).getBytes(StandardCharsets.UTF_16LE);
                    ctx.drawString(buf, buf.length, (Point2D)p, null, null, null, null, true);
                });
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "brushId", this::getBrushId, "optionsFlags", GenericRecordUtil.getBitsAsString(() -> this.optionsFlags, OPTIONS_MASK, OPTIONS_NAMES), "glyphs", () -> this.glyphs, "glyphPos", () -> this.glyphPos, "transform", () -> this.transformMatrix);
        }
    }

    public static class EmfPlusFillPath
    extends EmfPlusFillRegion {
        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.fillPath;
        }
    }

    public static class EmfPlusFillRegion
    implements HemfPlusRecord,
    EmfPlusSolidColor,
    HemfPlusMisc.EmfPlusObjectId {
        private int flags;
        private int brushId;

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.fillRegion;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public int getBrushIdValue() {
            return this.brushId;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.brushId = leis.readInt();
            return 4L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            this.applyColor(ctx);
            ctx.applyPlusObjectTableEntry(this.getObjectId());
            HemfDrawProperties prop = ctx.getProperties();
            ctx.fill(prop.getPath());
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "brushId", () -> this.brushId);
        }
    }

    public static class EmfPlusDrawImage
    implements HemfPlusRecord,
    HemfPlusMisc.EmfPlusObjectId,
    EmfPlusCompressed {
        private int flags;
        private int imageAttributesID;
        private EmfPlusUnitType srcUnit;
        private final Rectangle2D srcRect = new Rectangle2D.Double();
        private final Rectangle2D rectData = new Rectangle2D.Double();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.drawImage;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.imageAttributesID = leis.readInt();
            this.srcUnit = EmfPlusUnitType.valueOf(leis.readInt());
            assert (this.srcUnit == EmfPlusUnitType.Pixel);
            int size = 8;
            size += HemfPlusDraw.readRectF(leis, this.srcRect);
            return size += this.getReadRect().apply(leis, this.rectData).intValue();
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.applyPlusObjectTableEntry(this.imageAttributesID);
            ctx.applyPlusObjectTableEntry(this.getObjectId());
            HemfDrawProperties prop = ctx.getProperties();
            prop.setRasterOp3(HwmfTernaryRasterOp.SRCCOPY);
            prop.setBkMode(HwmfMisc.WmfSetBkMode.HwmfBkMode.TRANSPARENT);
            ctx.drawImage(prop.getEmfPlusImage(), this.srcRect, this.rectData);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "imageAttributesID", () -> this.imageAttributesID, "srcUnit", () -> this.srcUnit, "srcRect", () -> this.srcRect, "rectData", () -> this.rectData);
        }
    }

    public static class EmfPlusDrawImagePoints
    implements HemfPlusRecord,
    HemfPlusMisc.EmfPlusObjectId,
    EmfPlusCompressed,
    EmfPlusRelativePosition {
        private static final BitField EFFECT = BitFieldFactory.getInstance(8192);
        private int flags;
        private int imageAttributesID;
        private EmfPlusUnitType srcUnit;
        private final Rectangle2D srcRect = new Rectangle2D.Double();
        private final Point2D upperLeft = new Point2D.Double();
        private final Point2D lowerRight = new Point2D.Double();
        private final Point2D lowerLeft = new Point2D.Double();
        private final AffineTransform trans = new AffineTransform();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.drawImagePoints;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.imageAttributesID = leis.readInt();
            this.srcUnit = EmfPlusUnitType.valueOf(leis.readInt());
            assert (this.srcUnit == EmfPlusUnitType.Pixel);
            int size = 8;
            size += HemfPlusDraw.readRectF(leis, this.srcRect);
            int count = leis.readInt();
            assert (count == 3);
            size += 4;
            BiFunction<LittleEndianInputStream, Point2D, Integer> readPoint = this.isRelativePosition() ? HemfPlusDraw::readPointR : (this.isCompressed() ? HemfPlusDraw::readPointS : HemfPlusDraw::readPointF);
            size += readPoint.apply(leis, this.lowerLeft).intValue();
            size += readPoint.apply(leis, this.lowerRight).intValue();
            size += readPoint.apply(leis, this.upperLeft).intValue();
            RealMatrix para2normal = MatrixUtils.createRealMatrix((double[][])new double[][]{{this.lowerLeft.getX(), this.lowerRight.getX(), this.upperLeft.getX()}, {this.lowerLeft.getY(), this.lowerRight.getY(), this.upperLeft.getY()}, {1.0, 1.0, 1.0}});
            RealMatrix rect2normal = MatrixUtils.createRealMatrix((double[][])new double[][]{{this.srcRect.getMinX(), this.srcRect.getMaxX(), this.srcRect.getMinX()}, {this.srcRect.getMinY(), this.srcRect.getMinY(), this.srcRect.getMaxY()}, {1.0, 1.0, 1.0}});
            RealMatrix normal2rect = new LUDecomposition(rect2normal).getSolver().getInverse();
            double[][] m = para2normal.multiply(normal2rect).getData();
            this.trans.setTransform(HemfPlusDraw.round10(m[0][0]), HemfPlusDraw.round10(m[1][0]), HemfPlusDraw.round10(m[0][1]), HemfPlusDraw.round10(m[1][1]), HemfPlusDraw.round10(m[0][2]), HemfPlusDraw.round10(m[1][2]));
            return size;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            ctx.applyPlusObjectTableEntry(this.imageAttributesID);
            ctx.applyPlusObjectTableEntry(this.getObjectId());
            ImageRenderer ir = prop.getEmfPlusImage();
            if (ir == null) {
                return;
            }
            AffineTransform txSaved = ctx.getTransform();
            AffineTransform tx = (AffineTransform)txSaved.clone();
            HwmfTernaryRasterOp oldOp = prop.getRasterOp3();
            HwmfMisc.WmfSetBkMode.HwmfBkMode oldBk = prop.getBkMode();
            try {
                tx.concatenate(this.trans);
                ctx.setTransform(tx);
                prop.setRasterOp3(HwmfTernaryRasterOp.SRCCOPY);
                prop.setBkMode(HwmfMisc.WmfSetBkMode.HwmfBkMode.TRANSPARENT);
                ctx.drawImage(ir, this.srcRect, this.srcRect);
            }
            finally {
                prop.setBkMode(oldBk);
                prop.setRasterOp3(oldOp);
                ctx.setTransform(txSaved);
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("flags", this::getFlags);
            m.put("imageAttributesID", () -> this.imageAttributesID);
            m.put("srcUnit", () -> this.srcUnit);
            m.put("srcRect", () -> this.srcRect);
            m.put("upperLeft", () -> this.upperLeft);
            m.put("lowerLeft", () -> this.lowerLeft);
            m.put("lowerRight", () -> this.lowerRight);
            m.put("transform", () -> this.trans);
            return Collections.unmodifiableMap(m);
        }
    }

    public static class EmfPlusFillRects
    implements HemfPlusRecord,
    EmfPlusCompressed,
    EmfPlusSolidColor {
        private int flags;
        private int brushId;
        private final ArrayList<Rectangle2D> rectData = new ArrayList();

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.fillRects;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.brushId = leis.readInt();
            int count = leis.readInt();
            BiFunction<LittleEndianInputStream, Rectangle2D, Integer> readRect = this.getReadRect();
            this.rectData.ensureCapacity(count);
            int size = 8;
            for (int i = 0; i < count; ++i) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                size += readRect.apply(leis, rect).intValue();
                this.rectData.add(rect);
            }
            return size;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            this.applyColor(ctx);
            Area area = new Area();
            this.rectData.stream().map(Area::new).forEach(area::add);
            HwmfPenStyle ps = prop.getPenStyle();
            try {
                prop.setPenStyle(null);
                ctx.fill(area);
            }
            finally {
                prop.setPenStyle(ps);
            }
        }

        @Override
        public int getBrushIdValue() {
            return this.brushId;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public HemfPlusRecordType getGenericRecordType() {
            return this.getEmfPlusRecordType();
        }

        public List<Rectangle2D> getRectData() {
            return this.rectData;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "brushId", this::getBrushId, "brushColor", this::getSolidColor, "rectData", this::getRectData);
        }
    }

    public static class EmfPlusDrawPath
    implements HemfPlusRecord,
    HemfPlusMisc.EmfPlusObjectId {
        private int flags;
        private int penId;

        @Override
        public HemfPlusRecordType getEmfPlusRecordType() {
            return HemfPlusRecordType.drawPath;
        }

        @Override
        public int getFlags() {
            return this.flags;
        }

        public int getPenId() {
            return this.penId;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
            this.flags = flags;
            this.penId = leis.readInt();
            assert (0 <= this.penId && this.penId <= 63);
            assert (dataSize == 4L);
            return 4L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.applyPlusObjectTableEntry(this.penId);
            ctx.applyPlusObjectTableEntry(this.getObjectId());
            HemfDrawProperties prop = ctx.getProperties();
            Path2D path = prop.getPath();
            if (path != null) {
                ctx.draw(path);
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public HemfPlusRecordType getGenericRecordType() {
            return this.getEmfPlusRecordType();
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("flags", this::getFlags, "penId", this::getPenId);
        }
    }

    public static interface EmfPlusSolidColor {
        public static final BitField SOLID_COLOR = BitFieldFactory.getInstance(32768);

        public int getFlags();

        public int getBrushIdValue();

        default public boolean isSolidColor() {
            return SOLID_COLOR.isSet(this.getFlags());
        }

        default public int getBrushId() {
            return this.isSolidColor() ? -1 : this.getBrushIdValue();
        }

        default public Color getSolidColor() {
            return this.isSolidColor() ? HemfPlusDraw.readARGB(this.getBrushIdValue()) : null;
        }

        default public void applyColor(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            if (this.isSolidColor()) {
                prop.setBrushStyle(HwmfBrushStyle.BS_SOLID);
                prop.setBrushColor(new HwmfColorRef(this.getSolidColor()));
            } else {
                ctx.applyPlusObjectTableEntry(this.getBrushId());
            }
        }
    }

    public static interface EmfPlusRelativePosition {
        public static final BitField POSITION = BitFieldFactory.getInstance(2048);

        public int getFlags();

        default public boolean isRelativePosition() {
            return POSITION.isSet(this.getFlags());
        }
    }

    public static interface EmfPlusCompressed {
        public static final BitField COMPRESSED = BitFieldFactory.getInstance(16384);

        public int getFlags();

        default public boolean isCompressed() {
            return COMPRESSED.isSet(this.getFlags());
        }

        default public BiFunction<LittleEndianInputStream, Rectangle2D, Integer> getReadRect() {
            return this.isCompressed() ? HemfPlusDraw::readRectS : HemfPlusDraw::readRectF;
        }
    }

    public static enum EmfPlusUnitType {
        World(0),
        Display(1),
        Pixel(2),
        Point(3),
        Inch(4),
        Document(5),
        Millimeter(6);

        public final int id;

        private EmfPlusUnitType(int id) {
            this.id = id;
        }

        public static EmfPlusUnitType valueOf(int id) {
            for (EmfPlusUnitType wrt : EmfPlusUnitType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

