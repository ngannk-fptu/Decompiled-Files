/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfColorRef;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

public final class HwmfDraw {
    private HwmfDraw() {
    }

    static int readBounds(LittleEndianInputStream leis, Rectangle2D bounds) {
        short bottom = leis.readShort();
        short right = leis.readShort();
        short top = leis.readShort();
        short left = leis.readShort();
        int x = Math.min(left, right);
        int y = Math.min(top, bottom);
        int w = Math.abs(left - right - 1);
        int h = Math.abs(top - bottom - 1);
        bounds.setRect(x, y, w, h);
        return 8;
    }

    static int readRectS(LittleEndianInputStream leis, Rectangle2D bounds) {
        short left = leis.readShort();
        short top = leis.readShort();
        short right = leis.readShort();
        short bottom = leis.readShort();
        int x = Math.min(left, right);
        int y = Math.min(top, bottom);
        int w = Math.abs(left - right - 1);
        int h = Math.abs(top - bottom - 1);
        bounds.setRect(x, y, w, h);
        return 8;
    }

    static int readPointS(LittleEndianInputStream leis, Point2D point) {
        short y = leis.readShort();
        short x = leis.readShort();
        point.setLocation(x, y);
        return 4;
    }

    @Internal
    public static Rectangle2D normalizeBounds(Rectangle2D bounds) {
        return bounds.getWidth() >= 0.0 && bounds.getHeight() >= 0.0 ? bounds : new Rectangle2D.Double(bounds.getWidth() >= 0.0 ? bounds.getMinX() : bounds.getMaxX(), bounds.getHeight() >= 0.0 ? bounds.getMinY() : bounds.getMaxY(), Math.abs(bounds.getWidth()), Math.abs(bounds.getHeight()));
    }

    public static class WmfSelectObject
    implements HwmfRecord {
        protected int objectIndex;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.selectObject;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.objectIndex = leis.readUShort();
            return 2;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.applyObjectTableEntry(this.objectIndex);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public int getObjectIndex() {
            return this.objectIndex;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("objectIndex", this::getObjectIndex);
        }
    }

    public static class WmfChord
    extends WmfArc {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.chord;
        }
    }

    public static class WmfPie
    extends WmfArc {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.pie;
        }
    }

    public static class WmfArc
    implements HwmfRecord {
        protected final Point2D startPoint = new Point2D.Double();
        protected final Point2D endPoint = new Point2D.Double();
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.arc;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            HwmfDraw.readPointS(leis, this.endPoint);
            HwmfDraw.readPointS(leis, this.startPoint);
            HwmfDraw.readBounds(leis, this.bounds);
            return 16;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            this.getFillDrawStyle().handler.accept(ctx, this.getShape());
        }

        public WmfArcClosure getArcClosure() {
            switch (this.getWmfRecordType()) {
                default: {
                    return WmfArcClosure.ARC;
                }
                case chord: {
                    return WmfArcClosure.CHORD;
                }
                case pie: 
            }
            return WmfArcClosure.PIE;
        }

        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return this.getArcClosure().drawStyle;
        }

        protected Arc2D getShape() {
            double endAngle;
            double startAngle = Math.toDegrees(Math.atan2(-(this.startPoint.getY() - this.bounds.getCenterY()), this.startPoint.getX() - this.bounds.getCenterX()));
            double arcAngle = endAngle - startAngle + (double)((endAngle = Math.toDegrees(Math.atan2(-(this.endPoint.getY() - this.bounds.getCenterY()), this.endPoint.getX() - this.bounds.getCenterX()))) - startAngle > 0.0 ? 0 : 360);
            if (startAngle < 0.0) {
                startAngle += 360.0;
            }
            return new Arc2D.Double(this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), this.bounds.getHeight(), startAngle, arcAngle, this.getArcClosure().awtType);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getStartPoint() {
            return this.startPoint;
        }

        public Point2D getEndPoint() {
            return this.endPoint;
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            Arc2D arc = this.getShape();
            return GenericRecordUtil.getGenericProperties("startPoint", this::getStartPoint, "endPoint", this::getEndPoint, "startAngle", arc::getAngleStart, "extentAngle", arc::getAngleExtent, "bounds", this::getBounds);
        }

        public static enum WmfArcClosure {
            ARC(HwmfRecordType.arc, 0, HwmfGraphics.FillDrawStyle.DRAW),
            CHORD(HwmfRecordType.chord, 1, HwmfGraphics.FillDrawStyle.FILL_DRAW),
            PIE(HwmfRecordType.pie, 2, HwmfGraphics.FillDrawStyle.FILL_DRAW);

            public final HwmfRecordType recordType;
            public final int awtType;
            public final HwmfGraphics.FillDrawStyle drawStyle;

            private WmfArcClosure(HwmfRecordType recordType, int awtType, HwmfGraphics.FillDrawStyle drawStyle) {
                this.recordType = recordType;
                this.awtType = awtType;
                this.drawStyle = drawStyle;
            }
        }
    }

    public static class WmfRoundRect
    implements HwmfRecord {
        protected final Dimension2D corners = new Dimension2DDouble();
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.roundRect;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            short height = leis.readShort();
            short width = leis.readShort();
            this.corners.setSize(width, height);
            return 4 + HwmfDraw.readBounds(leis, this.bounds);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.fill(this.getShape());
        }

        protected RoundRectangle2D getShape() {
            return new RoundRectangle2D.Double(this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), this.bounds.getHeight(), this.corners.getWidth(), this.corners.getHeight());
        }

        public Dimension2D getCorners() {
            return this.corners;
        }

        public Rectangle2D getBounds() {
            return this.bounds;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("bounds", this::getBounds, "corners", this::getCorners);
        }
    }

    public static class WmfSetPixel
    implements HwmfRecord {
        protected final HwmfColorRef colorRef = new HwmfColorRef();
        protected final Point2D point = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.setPixel;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            int size = this.colorRef.init(leis);
            return size + HwmfDraw.readPointS(leis, this.point);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            Rectangle2D.Double s = new Rectangle2D.Double(this.point.getX(), this.point.getY(), 1.0, 1.0);
            ctx.fill(s);
        }

        public HwmfColorRef getColorRef() {
            return this.colorRef;
        }

        public Point2D getPoint() {
            return this.point;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("colorRef", this::getColorRef, "point", this::getPoint);
        }
    }

    public static class WmfRectangle
    implements HwmfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.rectangle;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readBounds(leis, this.bounds);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.fill(this.bounds);
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

    public static class WmfPolyPolygon
    implements HwmfRecord {
        protected final List<Path2D> polyList = new ArrayList<Path2D>();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.polyPolygon;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            int numberOfPolygons = leis.readUShort();
            int[] pointsPerPolygon = new int[numberOfPolygons];
            int size = 2;
            for (int i = 0; i < numberOfPolygons; ++i) {
                pointsPerPolygon[i] = leis.readUShort();
                size += 2;
            }
            for (int nPoints : pointsPerPolygon) {
                Path2D.Double poly = new Path2D.Double(0, nPoints);
                for (int i = 0; i < nPoints; ++i) {
                    short x = leis.readShort();
                    short y = leis.readShort();
                    size += 4;
                    if (i == 0) {
                        ((Path2D)poly).moveTo(x, y);
                        continue;
                    }
                    ((Path2D)poly).lineTo(x, y);
                }
                poly.closePath();
                this.polyList.add(poly);
            }
            return size;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            Shape shape = this.getShape(ctx);
            if (shape == null) {
                return;
            }
            switch (this.getFillDrawStyle()) {
                case DRAW: {
                    ctx.draw(shape);
                    break;
                }
                case FILL: {
                    ctx.fill(shape);
                    break;
                }
                case FILL_DRAW: {
                    ctx.fill(shape);
                    ctx.draw(shape);
                }
            }
        }

        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return HwmfGraphics.FillDrawStyle.FILL_DRAW;
        }

        protected boolean isClosed() {
            return true;
        }

        protected Shape getShape(HwmfGraphics ctx) {
            int windingRule = ctx.getProperties().getWindingRule();
            if (this.isClosed()) {
                Area area = null;
                for (Path2D poly : this.polyList) {
                    Path2D p = (Path2D)poly.clone();
                    p.setWindingRule(windingRule);
                    Area newArea = new Area(p);
                    if (area == null) {
                        area = newArea;
                        continue;
                    }
                    area.exclusiveOr(newArea);
                }
                return area;
            }
            Path2D.Double path = new Path2D.Double();
            path.setWindingRule(windingRule);
            for (Path2D poly : this.polyList) {
                path.append(poly, false);
            }
            return path;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public List<Path2D> getPolyList() {
            return this.polyList;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("polyList", this::getPolyList);
        }
    }

    public static class WmfFrameRegion
    implements HwmfRecord {
        protected int regionIndex;
        protected int brushIndex;
        protected final Dimension2D frame = new Dimension2DDouble();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.frameRegion;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            this.regionIndex = leis.readUShort();
            this.brushIndex = leis.readUShort();
            short height = leis.readShort();
            short width = leis.readShort();
            this.frame.setSize(width, height);
            return 8;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.applyObjectTableEntry(this.brushIndex);
            ctx.applyObjectTableEntry(this.regionIndex);
            Rectangle inner = ctx.getProperties().getRegion().getBounds();
            double x = ((RectangularShape)inner).getX() - this.frame.getWidth();
            double y = ((RectangularShape)inner).getY() - this.frame.getHeight();
            double w = ((RectangularShape)inner).getWidth() + 2.0 * this.frame.getWidth();
            double h = ((RectangularShape)inner).getHeight() + 2.0 * this.frame.getHeight();
            Rectangle2D.Double outer = new Rectangle2D.Double(x, y, w, h);
            Area frame = new Area(outer);
            frame.subtract(new Area(inner));
            ctx.fill(frame);
        }

        public int getRegionIndex() {
            return this.regionIndex;
        }

        public int getBrushIndex() {
            return this.brushIndex;
        }

        public Dimension2D getFrame() {
            return this.frame;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("regionIndex", this::getRegionIndex, "brushIndex", this::getBrushIndex, "frame", this::getFrame);
        }
    }

    public static class WmfEllipse
    implements HwmfRecord {
        protected final Rectangle2D bounds = new Rectangle2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.ellipse;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readBounds(leis, this.bounds);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.fill(this.getShape());
        }

        protected Ellipse2D getShape() {
            return new Ellipse2D.Double(this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), this.bounds.getHeight());
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

    public static class WmfPolyline
    extends WmfPolygon {
        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.polyline;
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

    public static class WmfPolygon
    implements HwmfRecord {
        protected Path2D poly;

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.polygon;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            int numberOfPoints = leis.readShort();
            this.poly = new Path2D.Double(0, numberOfPoints);
            for (int i = 0; i < numberOfPoints; ++i) {
                short x = leis.readShort();
                short y = leis.readShort();
                if (i == 0) {
                    this.poly.moveTo(x, y);
                    continue;
                }
                this.poly.lineTo(x, y);
            }
            if (numberOfPoints > 0 && this.addClose()) {
                this.poly.closePath();
            }
            return 2 + numberOfPoints * 4;
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            Path2D p = (Path2D)this.poly.clone();
            p.setWindingRule(ctx.getProperties().getWindingRule());
            this.getFillDrawStyle().handler.accept(ctx, p);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        protected HwmfGraphics.FillDrawStyle getFillDrawStyle() {
            return HwmfGraphics.FillDrawStyle.FILL;
        }

        public Path2D getPoly() {
            return this.poly;
        }

        protected boolean addClose() {
            return true;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("poly", this::getPoly);
        }
    }

    public static class WmfLineTo
    implements HwmfRecord {
        protected final Point2D point = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.lineTo;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readPointS(leis, this.point);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            Point2D start = ctx.getProperties().getLocation();
            Line2D.Double line = new Line2D.Double(start, this.point);
            ctx.draw(line);
            ctx.getProperties().setLocation(this.point);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getPoint() {
            return this.point;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("point", this::getPoint);
        }
    }

    public static class WmfMoveTo
    implements HwmfRecord {
        protected final Point2D point = new Point2D.Double();

        @Override
        public HwmfRecordType getWmfRecordType() {
            return HwmfRecordType.moveTo;
        }

        @Override
        public int init(LittleEndianInputStream leis, long recordSize, int recordFunction) throws IOException {
            return HwmfDraw.readPointS(leis, this.point);
        }

        @Override
        public void draw(HwmfGraphics ctx) {
            ctx.getProperties().setLocation(this.point);
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public Point2D getPoint() {
            return this.point;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("point", this::getPoint);
        }
    }
}

