/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfDraw;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hwmf.record.HwmfDraw;
import org.apache.poi.hwmf.record.HwmfRegionMode;
import org.apache.poi.hwmf.record.HwmfWindowing;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfWindowing {
    private static int readScale(LittleEndianInputStream leis, Dimension2D scale) {
        double xNum = leis.readInt();
        double xDenom = leis.readInt();
        double yNum = leis.readInt();
        double yDenom = leis.readInt();
        scale.setSize(xNum / xDenom, yNum / yDenom);
        return 16;
    }

    public static class EmfSelectClipPath
    implements HemfRecord {
        protected HwmfRegionMode regionMode;

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.selectClipPath;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.regionMode = HwmfRegionMode.valueOf(leis.readInt());
            return 4L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            ctx.setClip(prop.getPath(), this.regionMode, false);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HwmfRegionMode getRegionMode() {
            return this.regionMode;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("regionMode", this::getRegionMode);
        }
    }

    public static class EmfScaleWindowExtEx
    extends HwmfWindowing.WmfScaleWindowExt
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.scaleWindowExtEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfWindowing.readScale(leis, this.scale);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D window = holder.getWindow();
            double x = window.getX();
            double y = window.getY();
            double w = window.getWidth();
            double h = window.getHeight();
            window.setRect(x, y, w * this.scale.getWidth(), h * this.scale.getHeight());
        }
    }

    public static class EmfScaleViewportExtEx
    extends HwmfWindowing.WmfScaleViewportExt
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.scaleViewportExtEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfWindowing.readScale(leis, this.scale);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D viewport = holder.getViewport();
            double x = viewport.getX();
            double y = viewport.getY();
            double w = viewport.getWidth();
            double h = viewport.getHeight();
            viewport.setRect(x, y, w * this.scale.getWidth(), h * this.scale.getHeight());
        }
    }

    public static class EmfSetIntersectClipRect
    extends HwmfWindowing.WmfIntersectClipRect
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setIntersectClipRect;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readRectL(leis, HwmfDraw.normalizeBounds(this.bounds));
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D b = holder.getBounds();
            if (b.isEmpty()) {
                b.setRect(this.bounds);
            } else {
                b.add(this.bounds);
            }
        }
    }

    public static class EmfSetExcludeClipRect
    extends HwmfWindowing.WmfExcludeClipRect
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setExcludeClipRect;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readRectL(leis, this.bounds);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetOffsetClipRgn
    extends HwmfWindowing.WmfOffsetClipRgn
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setOffsetClipRgn;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readPointL(leis, this.offset);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetViewportOrgEx
    extends HwmfWindowing.WmfSetViewportOrg
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setViewportOrgEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readPointL(leis, this.origin);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D viewport = holder.getViewport();
            double w = viewport.getWidth();
            double h = viewport.getHeight();
            viewport.setRect(this.origin.getX(), this.origin.getY(), w, h);
        }
    }

    public static class EmfSetViewportExtEx
    extends HwmfWindowing.WmfSetViewportExt
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setViewportExtEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readDimensionInt(leis, this.extents);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D viewport = holder.getViewport();
            double x = viewport.getX();
            double y = viewport.getY();
            viewport.setRect(x, y, this.extents.getWidth(), this.extents.getHeight());
        }
    }

    public static class EmfSetWindowOrgEx
    extends HwmfWindowing.WmfSetWindowOrg
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setWindowOrgEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readPointL(leis, this.origin);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D window = holder.getWindow();
            double w = window.getWidth();
            double h = window.getHeight();
            window.setRect(this.origin.getX(), this.origin.getY(), w, h);
        }
    }

    public static class EmfSetWindowExtEx
    extends HwmfWindowing.WmfSetWindowExt
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setWindowExtEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readDimensionInt(leis, this.size);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D window = holder.getWindow();
            double x = window.getX();
            double y = window.getY();
            window.setRect(x, y, this.size.getWidth(), this.size.getHeight());
        }
    }
}

