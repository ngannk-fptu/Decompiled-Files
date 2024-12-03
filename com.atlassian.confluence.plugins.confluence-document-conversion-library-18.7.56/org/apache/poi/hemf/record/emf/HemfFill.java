/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hemf.record.emf;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfDraw;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfBitmapDib;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfDraw;
import org.apache.poi.hwmf.record.HwmfFill;
import org.apache.poi.hwmf.record.HwmfRegionMode;
import org.apache.poi.hwmf.record.HwmfTernaryRasterOp;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;

public final class HemfFill {
    private HemfFill() {
    }

    static long readBitmap(LittleEndianInputStream leis, HwmfBitmapDib bitmap, int startIdx, int offBmi, int cbBmi, int offBits, int cbBits) throws IOException {
        if (offBmi == 0) {
            return 0L;
        }
        int offCurr = leis.getReadIndex() - (startIdx - 8);
        int undefinedSpace1 = offBmi - offCurr;
        if (undefinedSpace1 < 0) {
            return 0L;
        }
        int undefinedSpace2 = offBits - offCurr - cbBmi - undefinedSpace1;
        assert (undefinedSpace2 >= 0);
        leis.skipFully(undefinedSpace1);
        if (cbBmi == 0 || cbBits == 0) {
            return undefinedSpace1;
        }
        int dibSize = cbBmi + cbBits;
        if (undefinedSpace2 == 0) {
            return (long)undefinedSpace1 + (long)bitmap.init(leis, dibSize);
        }
        UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream(cbBmi + cbBits);
        long cbBmiSrcAct = IOUtils.copy(leis, (OutputStream)bos, cbBmi);
        assert (cbBmiSrcAct == (long)cbBmi);
        leis.skipFully(undefinedSpace2);
        long cbBitsSrcAct = IOUtils.copy(leis, (OutputStream)bos, cbBits);
        assert (cbBitsSrcAct == (long)cbBits);
        LittleEndianInputStream leisDib = new LittleEndianInputStream(bos.toInputStream());
        int dibSizeAct = bitmap.init(leisDib, dibSize);
        assert (dibSizeAct <= dibSize);
        return (long)undefinedSpace1 + (long)cbBmi + (long)undefinedSpace2 + (long)cbBits;
    }

    static long readRgnData(LittleEndianInputStream leis, List<Rectangle2D> rgnRects) {
        long rgnHdrSize = leis.readUInt();
        assert (rgnHdrSize == 32L);
        long rgnHdrType = leis.readUInt();
        assert (rgnHdrType == 1L);
        long rgnCntRect = leis.readUInt();
        long rgnCntBytes = leis.readUInt();
        long size = 16L;
        Rectangle2D.Double rgnBounds = new Rectangle2D.Double();
        size += HemfDraw.readRectL(leis, rgnBounds);
        int i = 0;
        while ((long)i < rgnCntRect) {
            Rectangle2D.Double rgnRct = new Rectangle2D.Double();
            size += HemfDraw.readRectL(leis, rgnRct);
            rgnRects.add(rgnRct);
            ++i;
        }
        return size;
    }

    static int readBounds2(LittleEndianInputStream leis, Rectangle2D bounds) {
        int x = leis.readInt();
        int y = leis.readInt();
        int w = leis.readInt();
        int h = leis.readInt();
        bounds.setRect(x, y, w, h);
        return 16;
    }

    public static int readXForm(LittleEndianInputStream leis, AffineTransform xform) {
        double m00 = leis.readFloat();
        double m01 = leis.readFloat();
        double m10 = leis.readFloat();
        double m11 = leis.readFloat();
        double m02 = leis.readFloat();
        double m12 = leis.readFloat();
        xform.setTransform(m00, -m10, -m01, m11, m02, m12);
        if (xform.isIdentity()) {
            xform.setToIdentity();
        }
        return 24;
    }

    static Shape getRgnShape(List<Rectangle2D> rgnRects) {
        if (rgnRects.size() == 1) {
            return rgnRects.get(0);
        }
        Area frame = new Area();
        rgnRects.forEach(rct -> frame.add(new Area((Shape)rct)));
        return frame;
    }

    public static class EmfSetDiBitsToDevice
    implements HemfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();
        protected final Point2D dest = new Point2D.Double();
        protected final Rectangle2D src = new Rectangle2D.Double();
        protected HwmfFill.ColorUsage usageSrc;
        protected final HwmfBitmapDib bitmap = new HwmfBitmapDib();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setDiBitsToDevice;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            long size = HemfDraw.readRectL(leis, this.bounds);
            size += HemfDraw.readPointL(leis, this.dest);
            size += (long)HemfFill.readBounds2(leis, this.src);
            int offBmiSrc = (int)leis.readUInt();
            int cbBmiSrc = (int)leis.readUInt();
            int offBitsSrc = (int)leis.readUInt();
            int cbBitsSrc = (int)leis.readUInt();
            this.usageSrc = HwmfFill.ColorUsage.valueOf((int)leis.readUInt());
            int iStartScan = (int)leis.readUInt();
            int cScans = (int)leis.readUInt();
            size += 28L;
            return size += HemfFill.readBitmap(leis, this.bitmap, startIdx, offBmiSrc, cbBmiSrc, offBitsSrc, cbBitsSrc);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        public Point2D getDest() {
            return this.dest;
        }

        public Rectangle2D getSrc() {
            return this.src;
        }

        public HwmfFill.ColorUsage getUsageSrc() {
            return this.usageSrc;
        }

        public HwmfBitmapDib getBitmap() {
            return this.bitmap;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds, "dest", this::getDest, "src", this::getSrc, "usageSrc", this::getUsageSrc, "bitmap", this::getBitmap);
        }
    }

    public static class EmfAlphaBlend
    implements HemfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();
        protected final Rectangle2D destRect = new Rectangle2D.Double();
        protected final Rectangle2D srcRect = new Rectangle2D.Double();
        protected byte blendOperation;
        protected byte blendFlags;
        protected int srcConstantAlpha;
        protected byte alphaFormat;
        protected final AffineTransform xFormSrc = new AffineTransform();
        protected final HwmfColorRef bkColorSrc = new HwmfColorRef();
        protected HwmfFill.ColorUsage usageSrc;
        protected final HwmfBitmapDib bitmap = new HwmfBitmapDib();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.alphaBlend;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            long size = HemfDraw.readRectL(leis, this.bounds);
            size += (long)HemfFill.readBounds2(leis, this.destRect);
            this.blendOperation = leis.readByte();
            assert (this.blendOperation == 0);
            this.blendFlags = leis.readByte();
            assert (this.blendOperation == 0);
            this.srcConstantAlpha = leis.readUByte();
            this.alphaFormat = leis.readByte();
            int xSrc = leis.readInt();
            int ySrc = leis.readInt();
            size += 12L;
            size += (long)HemfFill.readXForm(leis, this.xFormSrc);
            size += (long)this.bkColorSrc.init(leis);
            this.usageSrc = HwmfFill.ColorUsage.valueOf((int)leis.readUInt());
            int offBmiSrc = (int)leis.readUInt();
            int cbBmiSrc = (int)leis.readUInt();
            int offBitsSrc = (int)leis.readUInt();
            int cbBitsSrc = (int)leis.readUInt();
            int cxSrc = leis.readInt();
            int cySrc = leis.readInt();
            this.srcRect.setRect(xSrc, ySrc, cxSrc, cySrc);
            size += 28L;
            return size += HemfFill.readBitmap(leis, this.bitmap, startIdx, offBmiSrc, cbBmiSrc, offBitsSrc, cbBitsSrc);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("bounds", () -> this.bounds);
            m.put("destRect", () -> this.destRect);
            m.put("srcRect", () -> this.srcRect);
            m.put("blendOperation", () -> this.blendOperation);
            m.put("blendFlags", () -> this.blendFlags);
            m.put("srcConstantAlpha", () -> this.srcConstantAlpha);
            m.put("alphaFormat", () -> this.alphaFormat);
            m.put("xFormSrc", () -> this.xFormSrc);
            m.put("bkColorSrc", () -> this.bkColorSrc);
            m.put("usageSrc", () -> this.usageSrc);
            m.put("bitmap", () -> this.bitmap);
            return Collections.unmodifiableMap(m);
        }
    }

    public static class EmfExtSelectClipRgn
    implements HemfRecord {
        protected HwmfRegionMode regionMode;
        protected final List<Rectangle2D> rgnRects = new ArrayList<Rectangle2D>();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.extSelectClipRgn;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long rgnDataSize = leis.readUInt();
            this.regionMode = HwmfRegionMode.valueOf((int)leis.readUInt());
            long size = 8L;
            if (this.regionMode != HwmfRegionMode.RGN_COPY) {
                size += HemfFill.readRgnData(leis, this.rgnRects);
            }
            return size;
        }

        protected Shape getShape() {
            return HemfFill.getRgnShape(this.rgnRects);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.setClip(this.getShape(), this.regionMode, true);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfRegionMode getRegionMode() {
            return this.regionMode;
        }

        public List<Rectangle2D> getRgnRects() {
            return this.rgnRects;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("regionMode", this::getRegionMode, "rgnRects", this::getRgnRects);
        }
    }

    public static class EmfFillRgn
    extends HwmfFill.WmfFillRegion
    implements HemfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();
        protected final List<Rectangle2D> rgnRects = new ArrayList<Rectangle2D>();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.fillRgn;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            long rgnDataSize = leis.readUInt();
            this.brushIndex = (int)leis.readUInt();
            size += 8L;
            return size += HemfFill.readRgnData(leis, this.rgnRects);
        }

        protected Shape getShape() {
            return HemfFill.getRgnShape(this.rgnRects);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        public List<Rectangle2D> getRgnRects() {
            return this.rgnRects;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "bounds", this::getBounds, "rgnRects", this::getRgnRects);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfPaintRgn
    extends EmfInvertRgn {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.paintRgn;
        }
    }

    public static class EmfInvertRgn
    implements HemfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();
        protected final List<Rectangle2D> rgnRects = new ArrayList<Rectangle2D>();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.invertRgn;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            long rgnDataSize = leis.readUInt();
            size += 4L;
            return size += HemfFill.readRgnData(leis, this.rgnRects);
        }

        protected Shape getShape() {
            return HemfFill.getRgnShape(this.rgnRects);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        public List<Rectangle2D> getRgnRects() {
            return this.rgnRects;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds, "rgnRects", this::getRgnRects);
        }
    }

    public static class EmfFrameRgn
    extends HwmfDraw.WmfFrameRegion
    implements HemfRecord {
        private final Rectangle2D bounds = new Rectangle2D.Double();
        private final List<Rectangle2D> rgnRects = new ArrayList<Rectangle2D>();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.frameRgn;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            long rgnDataSize = leis.readUInt();
            this.brushIndex = (int)leis.readUInt();
            int width = leis.readInt();
            int height = leis.readInt();
            this.frame.setSize(width, height);
            size += 16L;
            return size += HemfFill.readRgnData(leis, this.rgnRects);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.applyObjectTableEntry(this.brushIndex);
            ctx.fill(this.getShape());
        }

        protected Shape getShape() {
            return HemfFill.getRgnShape(this.rgnRects);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        public List<Rectangle2D> getRgnRects() {
            return this.rgnRects;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "bounds", this::getBounds, "rgnRects", this::getRgnRects);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfBitBlt
    extends EmfStretchBlt {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.bitBlt;
        }

        @Override
        protected boolean srcEqualsDstDimension() {
            return false;
        }
    }

    public static class EmfStretchDiBits
    extends HwmfFill.WmfStretchDib
    implements HemfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.stretchDiBits;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            long size = HemfDraw.readRectL(leis, this.bounds);
            int xDest = leis.readInt();
            int yDest = leis.readInt();
            size += 8L;
            size += (long)HemfFill.readBounds2(leis, this.srcBounds);
            int offBmiSrc = (int)leis.readUInt();
            int cbBmiSrc = (int)leis.readUInt();
            int offBitsSrc = (int)leis.readUInt();
            int cbBitsSrc = (int)leis.readUInt();
            this.colorUsage = HwmfFill.ColorUsage.valueOf(leis.readInt());
            int rasterOpIndex = (int)leis.readUInt();
            this.rasterOperation = HwmfTernaryRasterOp.valueOf(rasterOpIndex >>> 16);
            int cxDest = leis.readInt();
            int cyDest = leis.readInt();
            this.dstBounds.setRect(xDest, yDest, cxDest, cyDest);
            size += 32L;
            return size += HemfFill.readBitmap(leis, this.bitmap, startIdx, offBmiSrc, cbBmiSrc, offBitsSrc, cbBitsSrc);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "bounds", this::getBounds);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfStretchBlt
    extends HwmfFill.WmfStretchDib
    implements HemfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();
        protected final AffineTransform xFormSrc = new AffineTransform();
        protected final HwmfColorRef bkColorSrc = new HwmfColorRef();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.stretchBlt;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int startIdx = leis.getReadIndex();
            long size = HemfDraw.readRectL(leis, this.bounds);
            size += (long)HemfFill.readBounds2(leis, this.dstBounds);
            int rasterOpIndex = (int)leis.readUInt();
            this.rasterOperation = HwmfTernaryRasterOp.valueOf(rasterOpIndex >>> 16);
            size += 4L;
            Point2D.Double srcPnt = new Point2D.Double();
            size += HemfDraw.readPointL(leis, srcPnt);
            size += (long)HemfFill.readXForm(leis, this.xFormSrc);
            size += (long)this.bkColorSrc.init(leis);
            this.colorUsage = HwmfFill.ColorUsage.valueOf((int)leis.readUInt());
            int offBmiSrc = (int)leis.readUInt();
            int cbBmiSrc = (int)leis.readUInt();
            if ((size += 12L) >= recordSize) {
                return size;
            }
            int offBitsSrc = (int)leis.readUInt();
            int cbBitsSrc = (int)leis.readUInt();
            if ((size += 8L) >= recordSize) {
                return size;
            }
            if (this.srcEqualsDstDimension()) {
                this.srcBounds.setRect(((Point2D)srcPnt).getX(), ((Point2D)srcPnt).getY(), this.dstBounds.getWidth(), this.dstBounds.getHeight());
            } else {
                int srcWidth = leis.readInt();
                int srcHeight = leis.readInt();
                size += 8L;
                this.srcBounds.setRect(((Point2D)srcPnt).getX(), ((Point2D)srcPnt).getY(), srcWidth, srcHeight);
            }
            return size += HemfFill.readBitmap(leis, this.bitmap, startIdx, offBmiSrc, cbBmiSrc, offBitsSrc, cbBitsSrc);
        }

        protected boolean srcEqualsDstDimension() {
            return false;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setBackgroundColor(this.bkColorSrc);
            super.draw(ctx);
        }

        @Override
        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        public AffineTransform getXFormSrc() {
            return this.xFormSrc;
        }

        public HwmfColorRef getBkColorSrc() {
            return this.bkColorSrc;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "bounds", this::getBounds, "xFormSrc", this::getXFormSrc, "bkColorSrc", this::getBkColorSrc);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfExtFloodFill
    extends HwmfFill.WmfExtFloodFill
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.extFloodFill;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readPointL(leis, this.start);
            this.mode = HwmfFill.WmfExtFloodFill.HwmfFloodFillMode.values()[(int)leis.readUInt()];
            return (size += (long)this.colorRef.init(leis)) + 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetPolyfillMode
    extends HwmfFill.WmfSetPolyfillMode
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setPolyfillMode;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.polyFillMode = HwmfFill.WmfSetPolyfillMode.HwmfPolyfillMode.valueOf((int)leis.readUInt());
            return 4L;
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }
}

