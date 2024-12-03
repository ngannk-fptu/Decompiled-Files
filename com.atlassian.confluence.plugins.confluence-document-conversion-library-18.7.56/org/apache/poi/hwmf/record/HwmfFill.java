/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfBitmap16;
import org.apache.poi.hwmf.record.HwmfBitmapDib;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfDraw;
import org.apache.poi.hwmf.record.HwmfMisc;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.hwmf.record.HwmfTernaryRasterOp;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfFill {
    static int readBounds2(LittleEndianInputStream leis, Rectangle2D bounds) {
        short h = leis.readShort();
        short w = leis.readShort();
        short y = leis.readShort();
        short x = leis.readShort();
        bounds.setRect(x, y, w, h);
        return 8;
    }

    private static boolean hasBitmap(long recordSize, int recordFunction) {
        return recordSize > (long)((recordFunction >> 8) + 3);
    }

    private static HwmfTernaryRasterOp readRasterOperation(LittleEndianInputStream leis) {
        int rasterOpCode = leis.readUShort();
        int rasterOpIndex = leis.readUShort();
        HwmfTernaryRasterOp rasterOperation = HwmfTernaryRasterOp.valueOf(rasterOpIndex);
        assert (rasterOperation != null && rasterOpCode == rasterOperation.getOpCode());
        return rasterOperation;
    }

    public static class WmfDibStretchBlt
    implements HwmfRecord,
    HwmfImageRecord {
        protected HwmfTernaryRasterOp rasterOperation;
        protected final Rectangle2D srcBounds = new Rectangle2D.Double();
        protected final Rectangle2D dstBounds = new Rectangle2D.Double();
        protected HwmfBitmapDib target;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.dibStretchBlt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            boolean hasBitmap = HwmfFill.hasBitmap(recordSize, recordFunction);
            this.rasterOperation = HwmfFill.readRasterOperation(leis);
            int size = 4;
            size += HwmfFill.readBounds2(leis, this.srcBounds);
            if (!hasBitmap) {
                leis.readShort();
                size += 2;
            }
            size += HwmfFill.readBounds2(leis, this.dstBounds);
            if (hasBitmap) {
                this.target = new HwmfBitmapDib();
                size += this.target.init(leis, (int)(recordSize - 6L - (long)size));
            }
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            prop.setRasterOp3(this.rasterOperation);
            if (this.target != null) {
                HwmfMisc.WmfSetBkMode.HwmfBkMode oldMode = prop.getBkMode();
                prop.setBkMode(HwmfMisc.WmfSetBkMode.HwmfBkMode.TRANSPARENT);
                Color fgColor = prop.getPenColor().getColor();
                Color bgColor = prop.getBackgroundColor().getColor();
                BufferedImage bi = this.target.getImage(fgColor, bgColor, true);
                ctx.drawImage(bi, this.srcBounds, this.dstBounds);
                prop.setBkMode(oldMode);
            }
        }

        @Override
        public BufferedImage getImage(Color foreground, Color background, boolean hasAlpha) {
            return this.target != null && this.target.isValid() ? this.target.getImage(foreground, background, hasAlpha) : null;
        }

        @Override
        public byte[] getBMPData() {
            return this.target != null && this.target.isValid() ? this.target.getBMPData() : null;
        }

        public HwmfTernaryRasterOp getRasterOperation() {
            return this.rasterOperation;
        }

        public Rectangle2D getSrcBounds() {
            return this.srcBounds;
        }

        public Rectangle2D getDstBounds() {
            return this.dstBounds;
        }

        public HwmfBitmapDib getTarget() {
            return this.target;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("rasterOperation", this::getRasterOperation, "srcBounds", this::getSrcBounds, "dstBounds", this::getDstBounds, "target", this::getTarget);
        }
    }

    public static class WmfDibBitBlt
    extends WmfDibStretchBlt {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.dibBitBlt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            boolean hasBitmap = HwmfFill.hasBitmap(recordSize / 2L, recordFunction);
            this.rasterOperation = HwmfFill.readRasterOperation(leis);
            int size = 4;
            Point2D.Double srcPnt = new Point2D.Double();
            size += HwmfDraw.readPointS(leis, srcPnt);
            if (!hasBitmap) {
                leis.readShort();
                size += 2;
            }
            size += HwmfFill.readBounds2(leis, this.dstBounds);
            if (hasBitmap) {
                this.target = new HwmfBitmapDib();
                size += this.target.init(leis, (int)(recordSize - 6L - (long)size));
            }
            this.srcBounds.setRect(((Point2D)srcPnt).getX(), ((Point2D)srcPnt).getY(), this.dstBounds.getWidth(), this.dstBounds.getHeight());
            return size;
        }
    }

    public static class WmfSetDibToDev
    implements HwmfRecord,
    HwmfImageRecord,
    HwmfObjectTableEntry {
        private ColorUsage colorUsage;
        private int scanCount;
        private int startScan;
        protected final Rectangle2D srcBounds = new Rectangle2D.Double();
        protected final Rectangle2D dstBounds = new Rectangle2D.Double();
        private HwmfBitmapDib dib;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setDibToDev;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.colorUsage = ColorUsage.valueOf(leis.readUShort());
            this.scanCount = leis.readUShort();
            this.startScan = leis.readUShort();
            int size = 6;
            Point2D.Double srcPnt = new Point2D.Double();
            size += HwmfDraw.readPointS(leis, srcPnt);
            size += HwmfFill.readBounds2(leis, this.dstBounds);
            this.dib = new HwmfBitmapDib();
            size += this.dib.init(leis, (int)(recordSize - 6L - (long)size));
            this.srcBounds.setRect(((Point2D)srcPnt).getX(), ((Point2D)srcPnt).getY(), this.dstBounds.getWidth(), this.dstBounds.getHeight());
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
        }

        @Override
        public BufferedImage getImage(Color foreground, Color background, boolean hasAlpha) {
            return this.dib.getImage(foreground, background, hasAlpha);
        }

        @Override
        public byte[] getBMPData() {
            return this.dib.getBMPData();
        }

        public ColorUsage getColorUsage() {
            return this.colorUsage;
        }

        public int getScanCount() {
            return this.scanCount;
        }

        public int getStartScan() {
            return this.startScan;
        }

        public Rectangle2D getSrcBounds() {
            return this.srcBounds;
        }

        public Rectangle2D getDstBounds() {
            return this.dstBounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("colorUsage", this::getColorUsage, "scanCount", this::getScanCount, "startScan", this::getStartScan, "srcBounds", this::getSrcBounds, "dstBounds", this::getDstBounds, "dib", () -> this.dib);
        }
    }

    public static class WmfBitBlt
    extends WmfStretchBlt {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.bitBlt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            boolean hasBitmap = HwmfFill.hasBitmap(recordSize / 2L, recordFunction);
            this.rasterOperation = HwmfFill.readRasterOperation(leis);
            int size = 4;
            Point2D.Double srcPnt = new Point2D.Double();
            size += HwmfDraw.readPointS(leis, srcPnt);
            if (!hasBitmap) {
                leis.readShort();
                size += 2;
            }
            size += HwmfFill.readBounds2(leis, this.dstBounds);
            if (hasBitmap) {
                this.target = new HwmfBitmap16();
                size += this.target.init(leis);
            }
            this.srcBounds.setRect(((Point2D)srcPnt).getX(), ((Point2D)srcPnt).getY(), this.dstBounds.getWidth(), this.dstBounds.getHeight());
            return size;
        }
    }

    public static class WmfStretchDib
    implements HwmfRecord,
    HwmfImageRecord {
        protected HwmfTernaryRasterOp rasterOperation;
        protected ColorUsage colorUsage;
        protected final Rectangle2D srcBounds = new Rectangle2D.Double();
        protected final Rectangle2D dstBounds = new Rectangle2D.Double();
        protected final HwmfBitmapDib bitmap = new HwmfBitmapDib();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.stretchDib;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.rasterOperation = HwmfFill.readRasterOperation(leis);
            this.colorUsage = ColorUsage.valueOf(leis.readUShort());
            int size = 6;
            size += HwmfFill.readBounds2(leis, this.srcBounds);
            size += HwmfFill.readBounds2(leis, this.dstBounds);
            size += this.bitmap.init(leis, (int)(recordSize - 6L - (long)size));
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            prop.setRasterOp3(this.rasterOperation);
            if (this.bitmap.isValid()) {
                BufferedImage bi = this.bitmap.getImage(prop.getPenColor().getColor(), prop.getBackgroundColor().getColor(), prop.getBkMode() == HwmfMisc.WmfSetBkMode.HwmfBkMode.TRANSPARENT);
                ctx.drawImage(bi, this.srcBounds, this.dstBounds);
            } else if (!this.dstBounds.isEmpty()) {
                ctx.drawImage((ImageRenderer)null, (Rectangle2D)new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0), this.dstBounds);
            }
        }

        @Override
        public BufferedImage getImage(Color foreground, Color background, boolean hasAlpha) {
            return this.bitmap.getImage(foreground, background, hasAlpha);
        }

        public HwmfBitmapDib getBitmap() {
            return this.bitmap;
        }

        @Override
        public byte[] getBMPData() {
            return this.bitmap.getBMPData();
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfTernaryRasterOp getRasterOperation() {
            return this.rasterOperation;
        }

        public ColorUsage getColorUsage() {
            return this.colorUsage;
        }

        public Rectangle2D getSrcBounds() {
            return this.srcBounds;
        }

        public Rectangle2D getDstBounds() {
            return this.dstBounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("rasterOperation", this::getRasterOperation, "colorUsage", this::getColorUsage, "srcBounds", this::getSrcBounds, "dstBounds", this::getDstBounds);
        }
    }

    public static class WmfStretchBlt
    implements HwmfRecord {
        protected HwmfTernaryRasterOp rasterOperation;
        protected final Rectangle2D srcBounds = new Rectangle2D.Double();
        protected final Rectangle2D dstBounds = new Rectangle2D.Double();
        protected HwmfBitmap16 target;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.stretchBlt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            boolean hasBitmap = HwmfFill.hasBitmap(recordSize, recordFunction);
            this.rasterOperation = HwmfFill.readRasterOperation(leis);
            int size = 4;
            size += HwmfFill.readBounds2(leis, this.srcBounds);
            if (!hasBitmap) {
                leis.readShort();
                size += 2;
            }
            size += HwmfFill.readBounds2(leis, this.dstBounds);
            if (hasBitmap) {
                this.target = new HwmfBitmap16();
                size += this.target.init(leis);
            }
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfTernaryRasterOp getRasterOperation() {
            return this.rasterOperation;
        }

        public Rectangle2D getSrcBounds() {
            return this.srcBounds;
        }

        public Rectangle2D getDstBounds() {
            return this.dstBounds;
        }

        public HwmfBitmap16 getTarget() {
            return this.target;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("rasterOperation", this::getRasterOperation, "srcBounds", this::getSrcBounds, "dstBounds", this::getDstBounds, "target", this::getTarget);
        }
    }

    public static class WmfPatBlt
    implements HwmfRecord {
        private HwmfTernaryRasterOp rasterOperation;
        private final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.patBlt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.rasterOperation = HwmfFill.readRasterOperation(leis);
            return HwmfFill.readBounds2(leis, this.bounds) + 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public HwmfTernaryRasterOp getRasterOperation() {
            return this.rasterOperation;
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("rasterOperation", this::getRasterOperation, "bounds", this::getBounds);
        }
    }

    public static class WmfInvertRegion
    implements HwmfRecord {
        private int regionIndex;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.invertRegion;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.regionIndex = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public int getRegionIndex() {
            return this.regionIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("regionIndex", this::getRegionIndex);
        }
    }

    public static class WmfExtFloodFill
    extends WmfFloodFill {
        protected HwmfFloodFillMode mode;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.extFloodFill;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.mode = HwmfFloodFillMode.values()[leis.readUShort()];
            return super.init(leis, recordSize, recordFunction) + 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public HwmfFloodFillMode getMode() {
            return this.mode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("mode", this::getMode);
        }

        public static enum HwmfFloodFillMode {
            FLOOD_FILL_BORDER,
            FLOOD_FILL_SURFACE;

        }
    }

    public static class WmfSetPolyfillMode
    implements HwmfRecord {
        protected HwmfPolyfillMode polyFillMode;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setPolyFillMode;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.polyFillMode = HwmfPolyfillMode.valueOf(leis.readUShort() & 3);
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.getProperties().setPolyfillMode(this.polyFillMode);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfPolyfillMode getPolyFillMode() {
            return this.polyFillMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("polyFillMode", this::getPolyFillMode);
        }

        public static enum HwmfPolyfillMode {
            ALTERNATE(1, 0),
            WINDING(2, 1);

            public final int wmfFlag;
            public final int awtFlag;

            private HwmfPolyfillMode(int wmfFlag, int awtFlag) {
                this.wmfFlag = wmfFlag;
                this.awtFlag = awtFlag;
            }

            public static HwmfPolyfillMode valueOf(int wmfFlag) {
                for (HwmfPolyfillMode pm : HwmfPolyfillMode.values()) {
                    if (pm.wmfFlag != wmfFlag) continue;
                    return pm;
                }
                return null;
            }
        }
    }

    public static class WmfFloodFill
    implements HwmfRecord {
        protected final HwmfColorRef colorRef = new HwmfColorRef();
        protected final Point2D start = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.floodFill;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            int size = this.colorRef.init(leis);
            return size += HwmfDraw.readPointS(leis, this.start);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public HwmfColorRef getColorRef() {
            return this.colorRef;
        }

        public Point2D getStart() {
            return this.start;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("colorRef", this::getColorRef, "start", this::getStart);
        }
    }

    public static class WmfPaintRegion
    implements HwmfRecord {
        int regionIndex;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.paintRegion;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.regionIndex = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.applyObjectTableEntry(this.regionIndex);
            Shape region = ctx.getProperties().getRegion();
            if (region != null) {
                ctx.fill(region);
            }
        }

        public int getRegionIndex() {
            return this.regionIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("regionIndex", this::getRegionIndex);
        }
    }

    public static class WmfFillRegion
    implements HwmfRecord {
        protected int regionIndex;
        protected int brushIndex;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.fillRegion;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.regionIndex = leis.readUShort();
            this.brushIndex = leis.readUShort();
            return 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.applyObjectTableEntry(this.regionIndex);
            ctx.applyObjectTableEntry(this.brushIndex);
            Shape region = ctx.getProperties().getRegion();
            if (region != null) {
                ctx.fill(region);
            }
        }

        public int getRegionIndex() {
            return this.regionIndex;
        }

        public int getBrushIndex() {
            return this.brushIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("regionIndex", this::getRegionIndex, "brushIndex", this::getBrushIndex);
        }
    }

    public static enum ColorUsage {
        DIB_RGB_COLORS(0),
        DIB_PAL_COLORS(1),
        DIB_PAL_INDICES(2);

        public final int flag;

        private ColorUsage(int flag) {
            this.flag = flag;
        }

        public static ColorUsage valueOf(int flag) {
            for (ColorUsage bs : ColorUsage.values()) {
                if (bs.flag != flag) continue;
                return bs;
            }
            return null;
        }
    }

    public static interface HwmfImageRecord {
        default public BufferedImage getImage() {
            return this.getImage(Color.BLACK, new Color(0xFFFFFF, true), true);
        }

        public BufferedImage getImage(Color var1, Color var2, boolean var3);

        public byte[] getBMPData();
    }
}

