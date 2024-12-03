/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfDraw;
import org.apache.poi.hwmf.record.HwmfObjectTableEntry;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.hwmf.record.HwmfRegionMode;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfWindowing {

    public static class WmfCreateRegion
    implements HwmfRecord,
    HwmfObjectTableEntry {
        private int nextInChain;
        private int objectType;
        private int objectCount;
        private int regionSize;
        private int scanCount;
        private int maxScan;
        private final Rectangle2D bounds = new Rectangle2D.Double();
        private WmfScanObject[] scanObjects;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.createRegion;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.nextInChain = leis.readShort();
            this.objectType = leis.readShort();
            this.objectCount = leis.readInt();
            this.regionSize = leis.readShort();
            this.scanCount = leis.readShort();
            this.maxScan = leis.readShort();
            double left = leis.readShort();
            double top = leis.readShort();
            double right = leis.readShort();
            double bottom = leis.readShort();
            this.bounds.setRect(left, top, right - left, bottom - top);
            int size = 22;
            this.scanObjects = new WmfScanObject[this.scanCount];
            for (int i = 0; i < this.scanCount; ++i) {
                this.scanObjects[i] = new WmfScanObject();
                size += this.scanObjects[i].init(leis);
            }
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.addObjectTableEntry(this);
        }

        @Override
        public void applyObject(HwmfGraphics ctx) {
            Shape lastRect = null;
            Area scanLines = new Area();
            int count = 0;
            for (WmfScanObject so : this.scanObjects) {
                int y = Math.min(so.top, so.bottom);
                int h = Math.abs(so.top - so.bottom - 1);
                for (int i = 0; i < so.count / 2; ++i) {
                    int x = Math.min(so.left_scanline[i], so.right_scanline[i]);
                    int w = Math.abs(so.right_scanline[i] - so.left_scanline[i] - 1);
                    lastRect = new Rectangle2D.Double(x, y, w, h);
                    scanLines.add(new Area(lastRect));
                    ++count;
                }
            }
            Area region = null;
            if (count > 0) {
                region = count == 1 ? lastRect : scanLines;
            }
            ctx.getProperties().setRegion(region);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("nextInChain", () -> this.nextInChain);
            m.put("objectType", () -> this.objectType);
            m.put("objectCount", () -> this.objectCount);
            m.put("regionSize", () -> this.regionSize);
            m.put("scanCount", () -> this.scanCount);
            m.put("maxScan", () -> this.maxScan);
            m.put("bounds", () -> this.bounds);
            m.put("scanObjects", () -> Arrays.asList(this.scanObjects));
            return Collections.unmodifiableMap(m);
        }
    }

    public static class WmfScanObject
    implements GenericRecord {
        private int count;
        private int top;
        private int bottom;
        private int[] left_scanline;
        private int[] right_scanline;
        private int count2;

        public int init(LittleEndianInputStream leis) {
            this.count = leis.readUShort();
            this.top = leis.readUShort();
            this.bottom = leis.readUShort();
            int size = 6;
            this.left_scanline = new int[this.count / 2];
            this.right_scanline = new int[this.count / 2];
            for (int i = 0; i < this.count / 2; ++i) {
                this.left_scanline[i] = leis.readUShort();
                this.right_scanline[i] = leis.readUShort();
                size += 4;
            }
            this.count2 = leis.readUShort();
            return size += 2;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("count", () -> this.count, "top", () -> this.top, "bottom", () -> this.bottom, "left_scanline", () -> Arrays.asList(new int[][]{this.left_scanline}), "right_scanline", () -> Arrays.asList(new int[][]{this.right_scanline}), "count2", () -> this.count2);
        }
    }

    public static class WmfSelectClipRegion
    implements HwmfRecord {
        private int region;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.selectClipRegion;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.region = leis.readShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
        }

        public int getRegion() {
            return this.region;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("region", this::getRegion);
        }
    }

    public static class WmfIntersectClipRect
    implements HwmfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.intersectClipRect;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readBounds(leis, this.bounds);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.setClip(this.bounds, HwmfRegionMode.RGN_AND, false);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds);
        }
    }

    public static class WmfExcludeClipRect
    implements HwmfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.excludeClipRect;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readBounds(leis, this.bounds);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.setClip(HwmfDraw.normalizeBounds(this.bounds), HwmfRegionMode.RGN_DIFF, false);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds);
        }
    }

    public static class WmfOffsetClipRgn
    implements HwmfRecord {
        protected final Point2D offset = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.offsetClipRgn;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readPointS(leis, this.offset);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            Shape oldClip = ctx.getProperties().getClip();
            if (oldClip == null) {
                return;
            }
            AffineTransform at = new AffineTransform();
            at.translate(this.offset.getX(), this.offset.getY());
            Shape newClip = at.createTransformedShape(oldClip);
            ctx.setClip(newClip, HwmfRegionMode.RGN_COPY, false);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getOffset() {
            return this.offset;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("offset", this::getOffset);
        }
    }

    public static class WmfScaleViewportExt
    implements HwmfRecord {
        protected final Dimension2D scale = new Dimension2DDouble();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.scaleViewportExt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            double yDenom = leis.readShort();
            double yNum = leis.readShort();
            double xDenom = leis.readShort();
            double xNum = leis.readShort();
            this.scale.setSize(xNum / xDenom, yNum / yDenom);
            return 8;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            Rectangle2D old;
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D rectangle2D = old = prop.getViewport() == null ? prop.getWindow() : prop.getViewport();
            if (this.scale.getWidth() != 1.0 || this.scale.getHeight() != 1.0) {
                double width = old.getWidth() * this.scale.getWidth();
                double height = old.getHeight() * this.scale.getHeight();
                prop.setViewportExt(width, height);
                ctx.updateWindowMapMode();
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Dimension2D getScale() {
            return this.scale;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("scale", this::getScale);
        }
    }

    public static class WmfScaleWindowExt
    implements HwmfRecord {
        protected final Dimension2D scale = new Dimension2DDouble();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.scaleWindowExt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            double yDenom = leis.readShort();
            double yNum = leis.readShort();
            double xDenom = leis.readShort();
            double xNum = leis.readShort();
            this.scale.setSize(xNum / xDenom, yNum / yDenom);
            return 8;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D old = prop.getWindow();
            if (this.scale.getWidth() != 1.0 || this.scale.getHeight() != 1.0) {
                double width = old.getWidth() * this.scale.getWidth();
                double height = old.getHeight() * this.scale.getHeight();
                ctx.getProperties().setWindowExt(width, height);
                ctx.updateWindowMapMode();
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Dimension2D getScale() {
            return this.scale;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("scale", this::getScale);
        }
    }

    public static class WmfOffsetWindowOrg
    implements HwmfRecord {
        protected final Point2D offset = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.offsetWindowOrg;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readPointS(leis, this.offset);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D old = prop.getWindow();
            if (this.offset.getX() != 0.0 || this.offset.getY() != 0.0) {
                prop.setWindowOrg(old.getX() + this.offset.getX(), old.getY() + this.offset.getY());
                ctx.updateWindowMapMode();
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getOffset() {
            return this.offset;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("offset", this::getOffset);
        }
    }

    public static class WmfSetWindowExt
    implements HwmfRecord {
        protected final Dimension2D size = new Dimension2DDouble();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setWindowExt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            short height = leis.readShort();
            short width = leis.readShort();
            this.size.setSize(width, height);
            return 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D old = prop.getWindow();
            double oldW = 0.0;
            double oldH = 0.0;
            if (old != null) {
                oldW = old.getWidth();
                oldH = old.getHeight();
            }
            if (oldW != this.size.getWidth() || oldH != this.size.getHeight()) {
                prop.setWindowExt(this.size.getWidth(), this.size.getHeight());
                ctx.updateWindowMapMode();
            }
        }

        public Dimension2D getSize() {
            return this.size;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("size", this::getSize);
        }
    }

    public static class WmfSetWindowOrg
    implements HwmfRecord {
        protected final Point2D origin = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setWindowOrg;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readPointS(leis, this.origin);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D old = prop.getWindow();
            if (old.getX() != this.getX() || old.getY() != this.getY()) {
                prop.setWindowOrg(this.getX(), this.getY());
                ctx.updateWindowMapMode();
            }
        }

        public double getY() {
            return this.origin.getY();
        }

        public double getX() {
            return this.origin.getX();
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getOrigin() {
            return this.origin;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("origin", this::getOrigin);
        }
    }

    public static class WmfOffsetViewportOrg
    implements HwmfRecord {
        protected final Point2D offset = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.offsetViewportOrg;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readPointS(leis, this.offset);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D viewport = prop.getViewport();
            if (this.offset.getX() != 0.0 || this.offset.getY() != 0.0) {
                double x = viewport == null ? 0.0 : viewport.getX();
                double y = viewport == null ? 0.0 : viewport.getY();
                prop.setViewportOrg(x + this.offset.getX(), y + this.offset.getY());
                ctx.updateWindowMapMode();
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getOffset() {
            return this.offset;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("offset", this::getOffset);
        }
    }

    public static class WmfSetViewportExt
    implements HwmfRecord {
        protected final Dimension2D extents = new Dimension2DDouble();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setViewportExt;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            short height = leis.readShort();
            short width = leis.readShort();
            this.extents.setSize(width, height);
            return 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            double oldH;
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D old = prop.getViewport();
            double oldW = old == null ? 0.0 : old.getWidth();
            double d = oldH = old == null ? 0.0 : old.getHeight();
            if (oldW != this.extents.getWidth() || oldH != this.extents.getHeight()) {
                prop.setViewportExt(this.extents.getWidth(), this.extents.getHeight());
                ctx.updateWindowMapMode();
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Dimension2D getExtents() {
            return this.extents;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("extents", this::getExtents);
        }
    }

    public static class WmfSetViewportOrg
    implements HwmfRecord {
        protected final Point2D origin = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setViewportOrg;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readPointS(leis, this.origin);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            double oldY;
            HwmfDrawProperties prop = ctx.getProperties();
            Rectangle2D old = prop.getViewport();
            double oldX = old == null ? 0.0 : old.getX();
            double d = oldY = old == null ? 0.0 : old.getY();
            if (oldX != this.origin.getX() || oldY != this.origin.getY()) {
                prop.setViewportOrg(this.origin.getX(), this.origin.getY());
                ctx.updateWindowMapMode();
            }
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getOrigin() {
            return this.origin;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("origin", this::getOrigin);
        }
    }
}

