/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.hemf.record.emf.HemfRecordWithoutProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfDraw;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public final class HemfDraw {
    private HemfDraw() {
    }

    static long readRectL(LittleEndianInputStream leis, Rectangle2D bounds) {
        double left = leis.readInt();
        double top = leis.readInt();
        double right = leis.readInt();
        double bottom = leis.readInt();
        bounds.setRect(left, top, right - left, bottom - top);
        return 16L;
    }

    static long readPointS(LittleEndianInputStream leis, Point2D point) {
        short x = leis.readShort();
        short y = leis.readShort();
        point.setLocation(x, y);
        return 4L;
    }

    static long readPointL(LittleEndianInputStream leis, Point2D point) {
        int x = leis.readInt();
        int y = leis.readInt();
        point.setLocation(x, y);
        return 8L;
    }

    static long readDimensionFloat(LittleEndianInputStream leis, Dimension2D dimension) {
        double width = leis.readFloat();
        double height = leis.readFloat();
        dimension.setSize(width, height);
        return 8L;
    }

    static long readDimensionInt(LittleEndianInputStream leis, Dimension2D dimension) {
        double width = leis.readInt();
        double height = leis.readInt();
        dimension.setSize(width, height);
        return 8L;
    }

    private static void polyTo(HemfGraphics ctx, Path2D poly, HwmfGraphics.FillDrawStyle fillDrawStyle) {
        if (poly.getCurrentPoint() == null) {
            return;
        }
        PathIterator pi = poly.getPathIterator(null);
        pi.next();
        if (pi.isDone()) {
            return;
        }
        ctx.draw(path -> path.append(pi, true), fillDrawStyle);
    }

    public static class EmfStrokeAndFillPath
    extends EmfStrokePath {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.strokeAndFillPath;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties props = ctx.getProperties();
            Path2D path = props.getPath();
            path.closePath();
            path.setWindingRule(ctx.getProperties().getWindingRule());
            ctx.fill(path);
            ctx.draw(path);
        }
    }

    public static class EmfFillPath
    extends EmfStrokePath {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.fillPath;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            Path2D origPath = prop.getPath();
            if (origPath.getCurrentPoint() == null) {
                return;
            }
            Path2D path = (Path2D)origPath.clone();
            path.closePath();
            path.setWindingRule(ctx.getProperties().getWindingRule());
            ctx.fill(path);
        }
    }

    public static class EmfStrokePath
    implements HemfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.strokePath;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return recordSize == 0L ? 0L : HemfDraw.readRectL(leis, this.bounds);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties props = ctx.getProperties();
            Path2D path = props.getPath();
            path.setWindingRule(ctx.getProperties().getWindingRule());
            ctx.draw(path);
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

    public static class EmfWidenPath
    implements HemfRecordWithoutProperties {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.widenPath;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return 0L;
        }

        public String toString() {
            return "{}";
        }
    }

    public static class EmfFlattenPath
    implements HemfRecordWithoutProperties {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.flattenPath;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return 0L;
        }
    }

    public static class EmfCloseFigure
    implements HemfRecordWithoutProperties {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.closeFigure;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return 0L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            Path2D path = prop.getPath();
            if (path != null && path.getCurrentPoint() != null) {
                path.closePath();
                prop.setLocation(path.getCurrentPoint());
            }
        }

        public String toString() {
            return "{}";
        }
    }

    public static class EmfAbortPath
    implements HemfRecordWithoutProperties {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.abortPath;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return 0L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setPath(null);
            prop.setUsePathBracket(false);
        }

        public String toString() {
            return "{}";
        }
    }

    public static class EmfEndPath
    implements HemfRecordWithoutProperties {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.endPath;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return 0L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setUsePathBracket(false);
        }

        public String toString() {
            return "{}";
        }
    }

    public static class EmfBeginPath
    implements HemfRecordWithoutProperties {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.beginPath;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return 0L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDrawProperties prop = ctx.getProperties();
            prop.setPath(new Path2D.Double());
            prop.setUsePathBracket(true);
        }

        public String toString() {
            return "{}";
        }
    }

    public static class EmfPolyDraw16
    extends EmfPolyDraw {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyDraw16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolyDraw
    extends HwmfDraw.WmfPolygon
    implements HemfRecord {
        private final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyDraw;
        }

        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointL(leis, point);
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            int i;
            long size = HemfDraw.readRectL(leis, this.bounds);
            int count = (int)leis.readUInt();
            size += 4L;
            Point2D[] points = new Point2D[count];
            for (i = 0; i < count; ++i) {
                points[i] = new Point2D.Double();
                size += this.readPoint(leis, points[i]);
            }
            this.poly = new Path2D.Double(0, count);
            for (i = 0; i < count; ++i) {
                int mode = leis.readUByte();
                switch (mode & 6) {
                    case 2: {
                        this.poly.lineTo(points[i].getX(), points[i].getY());
                        break;
                    }
                    case 4: {
                        int mode2 = leis.readUByte();
                        int mode3 = leis.readUByte();
                        assert (mode2 == 4 && (mode3 == 4 || mode3 == 5));
                        if (i + 2 >= points.length) {
                            throw new IllegalStateException("Points index causes index out of bounds");
                        }
                        this.poly.curveTo(points[i].getX(), points[i].getY(), points[i + 1].getX(), points[i + 1].getY(), points[i + 2].getX(), points[i + 2].getY());
                        mode = mode3;
                        i += 2;
                        break;
                    }
                    case 6: {
                        this.poly.moveTo(points[i].getX(), points[i].getY());
                        break;
                    }
                }
                if ((mode & 1) != 1) continue;
                this.poly.closePath();
            }
            return size += (long)count;
        }

        @Override
        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return HwmfGraphics.FillDrawStyle.DRAW;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.poly, false), this.getFillDrawStyle());
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        protected boolean addClose() {
            return false;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "bounds", this::getBounds);
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

    public static class EmfArcTo
    extends HwmfDraw.WmfArc
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.arcTo;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            size += HemfDraw.readPointL(leis, this.startPoint);
            return size += HemfDraw.readPointL(leis, this.endPoint);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            Arc2D arc = this.getShape();
            ctx.draw(path -> path.append(arc, true), this.getFillDrawStyle());
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

    public static class EmfLineTo
    extends HwmfDraw.WmfLineTo
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.lineTo;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readPointL(leis, this.point);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.lineTo(this.point.getX(), this.point.getY()), HwmfGraphics.FillDrawStyle.DRAW);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }

        @Override
        public void calcBounds(HemfRecord.RenderBounds holder) {
            Rectangle2D b = holder.getBounds();
            if (!b.isEmpty()) {
                b.add(this.point);
            }
        }
    }

    public static class EmfRoundRect
    extends HwmfDraw.WmfRoundRect
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.roundRect;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            int width = (int)leis.readUInt();
            int height = (int)leis.readUInt();
            this.corners.setSize(width, height);
            return size + 8L;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.getShape(), false), HwmfGraphics.FillDrawStyle.FILL_DRAW);
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

    public static class EmfRectangle
    extends HwmfDraw.WmfRectangle
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.rectangle;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readRectL(leis, this.bounds);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(HwmfDraw.normalizeBounds(this.bounds), false), HwmfGraphics.FillDrawStyle.FILL_DRAW);
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

    public static class EmfEllipse
    extends HwmfDraw.WmfEllipse
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.ellipse;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readRectL(leis, this.bounds);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.getShape(), false), HwmfGraphics.FillDrawStyle.FILL_DRAW);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfPie
    extends HwmfDraw.WmfPie
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.pie;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            size += HemfDraw.readPointL(leis, this.startPoint);
            return size += HemfDraw.readPointL(leis, this.endPoint);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.getShape(), false), this.getFillDrawStyle());
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfChord
    extends HwmfDraw.WmfChord
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.chord;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            size += HemfDraw.readPointL(leis, this.startPoint);
            return size += HemfDraw.readPointL(leis, this.endPoint);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.getShape(), false), this.getFillDrawStyle());
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfArc
    extends HwmfDraw.WmfArc
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.arc;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            size += HemfDraw.readPointL(leis, this.startPoint);
            return size += HemfDraw.readPointL(leis, this.endPoint);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.getShape(), false), this.getFillDrawStyle());
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetMoveToEx
    extends HwmfDraw.WmfMoveTo
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setMoveToEx;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            return HemfDraw.readPointL(leis, this.point);
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.moveTo(this.point.getX(), this.point.getY()), HwmfGraphics.FillDrawStyle.NONE);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfSetPixelV
    extends HwmfDraw.WmfSetPixel
    implements HemfRecord {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.setPixelV;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readPointL(leis, this.point);
            return size += (long)this.colorRef.init(leis);
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }

    public static class EmfPolyPolyline16
    extends EmfPolyPolyline {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyPolyline16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolyPolyline
    extends EmfPolyPolygon {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyPolyline;
        }

        @Override
        protected boolean isClosed() {
            return false;
        }

        @Override
        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return HwmfGraphics.FillDrawStyle.DRAW;
        }
    }

    public static class EmfPolyPolygon16
    extends EmfPolyPolygon {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyPolygon16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolyPolygon
    extends HwmfDraw.WmfPolyPolygon
    implements HemfRecord {
        private final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyPolygon;
        }

        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointL(leis, point);
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            long numberOfPolygons = leis.readUInt();
            long count = Math.min(16384L, leis.readUInt());
            size += 8L;
            long[] polygonPointCount = new long[(int)numberOfPolygons];
            size += numberOfPolygons * 4L;
            int i = 0;
            while ((long)i < numberOfPolygons) {
                polygonPointCount[i] = leis.readUInt();
                ++i;
            }
            Point2D.Double pnt = new Point2D.Double();
            for (long nPoints : polygonPointCount) {
                Path2D.Double poly = new Path2D.Double(0, (int)nPoints);
                int i2 = 0;
                while ((long)i2 < nPoints) {
                    size += this.readPoint(leis, pnt);
                    if (i2 == 0) {
                        ((Path2D)poly).moveTo(((Point2D)pnt).getX(), ((Point2D)pnt).getY());
                    } else {
                        ((Path2D)poly).lineTo(((Point2D)pnt).getX(), ((Point2D)pnt).getY());
                    }
                    ++i2;
                }
                if (this.isClosed()) {
                    poly.closePath();
                }
                this.polyList.add(poly);
            }
            return size;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            Shape shape = this.getShape(ctx);
            if (shape == null) {
                return;
            }
            ctx.draw(path -> path.append(shape, false), this.getFillDrawStyle());
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

    public static class EmfPolylineTo16
    extends EmfPolylineTo {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polylineTo16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolylineTo
    extends EmfPolyline {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polylineTo;
        }

        @Override
        protected boolean hasStartPoint() {
            return false;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDraw.polyTo(ctx, this.poly, this.getFillDrawStyle());
        }
    }

    public static class EmfPolyBezierTo16
    extends EmfPolyBezierTo {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyBezierTo16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolyBezierTo
    extends EmfPolyBezier {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyBezierTo;
        }

        @Override
        protected boolean hasStartPoint() {
            return false;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            HemfDraw.polyTo(ctx, this.poly, this.getFillDrawStyle());
        }
    }

    public static class EmfPolyline16
    extends EmfPolyline {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyline16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolyline
    extends EmfPolygon {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyline;
        }

        @Override
        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return HwmfGraphics.FillDrawStyle.DRAW;
        }

        @Override
        protected boolean addClose() {
            return false;
        }
    }

    public static class EmfPolygon16
    extends EmfPolygon {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polygon16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolygon
    extends HwmfDraw.WmfPolygon
    implements HemfRecord {
        private final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polygon;
        }

        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointL(leis, point);
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            int count = (int)leis.readUInt();
            int points = Math.min(count, 16384);
            size += 4L;
            this.poly = new Path2D.Double(0, points);
            Point2D.Double pnt = new Point2D.Double();
            for (int i = 0; i < points; ++i) {
                size += this.readPoint(leis, pnt);
                if (i == 0) {
                    if (this.hasStartPoint()) {
                        this.poly.moveTo(((Point2D)pnt).getX(), ((Point2D)pnt).getY());
                        continue;
                    }
                    this.poly.moveTo(0.0, 0.0);
                    this.poly.lineTo(((Point2D)pnt).getX(), ((Point2D)pnt).getY());
                    continue;
                }
                this.poly.lineTo(((Point2D)pnt).getX(), ((Point2D)pnt).getY());
            }
            return size;
        }

        protected boolean hasStartPoint() {
            return true;
        }

        @Override
        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return HwmfGraphics.FillDrawStyle.FILL_DRAW;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.poly, false), this.getFillDrawStyle());
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

    public static class EmfPolyBezier16
    extends EmfPolyBezier {
        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyBezier16;
        }

        @Override
        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointS(leis, point);
        }
    }

    public static class EmfPolyBezier
    extends HwmfDraw.WmfPolygon
    implements HemfRecord {
        private final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.polyBezier;
        }

        protected long readPoint(LittleEndianInputStream leis, Point2D point) {
            return HemfDraw.readPointL(leis, point);
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            long size = HemfDraw.readRectL(leis, this.bounds);
            int count = (int)leis.readUInt();
            int points = Math.min(count, 16384);
            size += 4L;
            this.poly = new Path2D.Double(0, points + 2);
            Point2D[] pnt = new Point2D[]{new Point2D.Double(), new Point2D.Double(), new Point2D.Double()};
            int i = 0;
            if (this.hasStartPoint()) {
                if (i < points) {
                    size += this.readPoint(leis, pnt[0]);
                    this.poly.moveTo(pnt[0].getX(), pnt[0].getY());
                    ++i;
                }
            } else {
                this.poly.moveTo(0.0, 0.0);
            }
            while (i + 2 < points) {
                size += this.readPoint(leis, pnt[0]);
                size += this.readPoint(leis, pnt[1]);
                size += this.readPoint(leis, pnt[2]);
                this.poly.curveTo(pnt[0].getX(), pnt[0].getY(), pnt[1].getX(), pnt[1].getY(), pnt[2].getX(), pnt[2].getY());
                i += 3;
            }
            return size;
        }

        protected boolean hasStartPoint() {
            return true;
        }

        @Override
        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return HwmfGraphics.FillDrawStyle.DRAW;
        }

        @Override
        public void draw(HemfGraphics ctx) {
            ctx.draw(path -> path.append(this.poly, !this.hasStartPoint()), this.getFillDrawStyle());
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        protected boolean addClose() {
            return false;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "bounds", this::getBounds);
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

    public static class EmfSelectObject
    extends HwmfDraw.WmfSelectObject
    implements HemfRecord {
        private static final int[] IDX_MASKS = IntStream.rangeClosed(Integer.MIN_VALUE, -2147483629).toArray();
        private static final String[] IDX_NAMES = new String[]{"WHITE_BRUSH", "LTGRAY_BRUSH", "GRAY_BRUSH", "DKGRAY_BRUSH", "BLACK_BRUSH", "NULL_BRUSH", "WHITE_PEN", "BLACK_PEN", "NULL_PEN", "INVALID", "OEM_FIXED_FONT", "ANSI_FIXED_FONT", "ANSI_VAR_FONT", "SYSTEM_FONT", "DEVICE_DEFAULT_FONT", "DEFAULT_PALETTE", "SYSTEM_FIXED_FONT", "DEFAULT_GUI_FONT", "DC_BRUSH", "DC_PEN"};

        @Override
        public HemfRecordType getEmfRecordType() {
            return HemfRecordType.selectObject;
        }

        @Override
        public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
            this.objectIndex = leis.readInt();
            return 4L;
        }

        @Override
        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("objectIndex", GenericRecordUtil.getEnumBitsAsString(this::getObjectIndex, IDX_MASKS, IDX_NAMES));
        }

        @Override
        public HemfRecordType getGenericRecordType() {
            return this.getEmfRecordType();
        }
    }
}

