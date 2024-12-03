/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.MultiPixelPackedSampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.TiledImage;

public class ROIShape
extends ROI {
    transient Shape theShape = null;

    private static Point2D.Double getIntersection(double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2) {
        double[][] a = new double[2][2];
        a[0][0] = y2 - y1;
        a[0][1] = x1 - x2;
        a[1][0] = v2 - v1;
        a[1][1] = u1 - u2;
        double[] c = new double[]{y1 * (x1 - x2) + x1 * (y2 - y1), v1 * (u1 - u2) + u1 * (v2 - v1)};
        double det = a[0][0] * a[1][1] - a[0][1] * a[1][0];
        double tmp = a[0][0];
        a[0][0] = a[1][1] / det;
        a[0][1] = -a[0][1] / det;
        a[1][0] = -a[1][0] / det;
        a[1][1] = tmp / det;
        double x = a[0][0] * c[0] + a[0][1] * c[1];
        double y = a[1][0] * c[0] + a[1][1] * c[1];
        return new Point2D.Double(x, y);
    }

    private LinkedList polygonToRunLengthList(Rectangle clip, Polygon poly) {
        PolyShape ps = new PolyShape(poly, clip);
        return ps.getAsRectList();
    }

    private static int[][] rectangleListToBitmask(LinkedList rectangleList, Rectangle clip, int[][] mask) {
        int bitField = Integer.MIN_VALUE;
        int bitmaskIntWidth = (clip.width + 31) / 32;
        if (mask == null) {
            mask = new int[clip.height][bitmaskIntWidth];
        } else if (mask.length < clip.height || mask[0].length < bitmaskIntWidth) {
            throw new RuntimeException(JaiI18N.getString("ROIShape0"));
        }
        ListIterator rectangleIter = rectangleList.listIterator(0);
        while (rectangleIter.hasNext()) {
            Rectangle rect = (Rectangle)rectangleIter.next();
            if (!clip.intersects(rect)) continue;
            rect = clip.intersection(rect);
            int yMin = rect.y - clip.y;
            int xMin = rect.x - clip.x;
            int yMax = yMin + rect.height - 1;
            int xMax = xMin + rect.width - 1;
            for (int y = yMin; y <= yMax; ++y) {
                int[] bitrow = mask[y];
                for (int x = xMin; x <= xMax; ++x) {
                    int index = x / 32;
                    int shift = x % 32;
                    int n = index;
                    bitrow[n] = bitrow[n] | bitField >>> shift;
                }
            }
        }
        return mask;
    }

    public ROIShape(Shape s) {
        if (s == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ROIShape2"));
        }
        this.theShape = s;
    }

    public ROIShape(Area a) {
        AffineTransform at = new AffineTransform();
        PathIterator pi = a.getPathIterator(at);
        GeneralPath gp = new GeneralPath(pi.getWindingRule());
        gp.append(pi, false);
        this.theShape = gp;
    }

    public Rectangle getBounds() {
        return this.theShape.getBounds();
    }

    public Rectangle2D getBounds2D() {
        return this.theShape.getBounds2D();
    }

    public boolean contains(Point p) {
        if (p == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.contains(p.x, p.y);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.contains((int)p.getX(), (int)p.getY());
    }

    public boolean contains(int x, int y) {
        return this.theShape.contains(x, y);
    }

    public boolean contains(double x, double y) {
        return this.contains((int)x, (int)y);
    }

    public boolean contains(Rectangle rect) {
        if (rect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.contains(new Rectangle2D.Float(rect.x, rect.y, rect.width, rect.height));
    }

    public boolean contains(Rectangle2D rect) {
        if (rect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.theShape.contains(rect);
    }

    public boolean contains(int x, int y, int w, int h) {
        return this.contains(new Rectangle2D.Float(x, y, w, h));
    }

    public boolean contains(double x, double y, double w, double h) {
        return this.theShape.contains(x, y, w, h);
    }

    public boolean intersects(Rectangle r) {
        if (r == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.intersects(new Rectangle2D.Float(r.x, r.y, r.width, r.height));
    }

    public boolean intersects(Rectangle2D r) {
        if (r == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.theShape.intersects(r);
    }

    public boolean intersects(int x, int y, int w, int h) {
        return this.intersects(new Rectangle2D.Float(x, y, w, h));
    }

    public boolean intersects(double x, double y, double w, double h) {
        return this.theShape.intersects(x, y, w, h);
    }

    public ROI add(ROI roi) {
        if (roi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
        }
        if (!(roi instanceof ROIShape)) {
            return super.add(roi);
        }
        ROIShape rois = (ROIShape)roi;
        Area a1 = new Area(this.theShape);
        Area a2 = new Area(rois.theShape);
        a1.add(a2);
        return new ROIShape(a1);
    }

    public ROI subtract(ROI roi) {
        if (roi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
        }
        if (!(roi instanceof ROIShape)) {
            return super.subtract(roi);
        }
        ROIShape rois = (ROIShape)roi;
        Area a1 = new Area(this.theShape);
        Area a2 = new Area(rois.theShape);
        a1.subtract(a2);
        return new ROIShape(a1);
    }

    public ROI intersect(ROI roi) {
        if (roi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
        }
        if (!(roi instanceof ROIShape)) {
            return super.intersect(roi);
        }
        ROIShape rois = (ROIShape)roi;
        Area a1 = new Area(this.theShape);
        Area a2 = new Area(rois.theShape);
        a1.intersect(a2);
        return new ROIShape(a1);
    }

    public ROI exclusiveOr(ROI roi) {
        if (roi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
        }
        if (!(roi instanceof ROIShape)) {
            return super.exclusiveOr(roi);
        }
        ROIShape rois = (ROIShape)roi;
        Area a1 = new Area(this.theShape);
        Area a2 = new Area(rois.theShape);
        a1.exclusiveOr(a2);
        return new ROIShape(a1);
    }

    public Shape getAsShape() {
        return this.theShape;
    }

    public PlanarImage getAsImage() {
        Graphics2D g2d;
        PlanarImage pi;
        if (this.theImage != null) {
            return this.theImage;
        }
        Rectangle r = this.theShape.getBounds();
        if (r.x == 0 && r.y == 0) {
            BufferedImage bi = new BufferedImage(r.width, r.height, 12);
            pi = PlanarImage.wrapRenderedImage(bi);
            g2d = bi.createGraphics();
        } else {
            MultiPixelPackedSampleModel sm = new MultiPixelPackedSampleModel(0, r.width, r.height, 1);
            TiledImage ti = new TiledImage(r.x, r.y, r.width, r.height, r.x, r.y, sm, PlanarImage.createColorModel(sm));
            pi = ti;
            g2d = ti.createGraphics();
        }
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fill(this.theShape);
        this.theImage = pi;
        return this.theImage;
    }

    public ROI transform(AffineTransform at) {
        if (at == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return new ROIShape(at.createTransformedShape(this.theShape));
    }

    public int[][] getAsBitmask(int x, int y, int width, int height, int[][] mask) {
        LinkedList rectList = this.getAsRectangleList(x, y, width, height, false);
        if (rectList == null) {
            return null;
        }
        return ROIShape.rectangleListToBitmask(rectList, new Rectangle(x, y, width, height), mask);
    }

    public LinkedList getAsRectangleList(int x, int y, int width, int height) {
        return this.getAsRectangleList(x, y, width, height, true);
    }

    protected LinkedList getAsRectangleList(int x, int y, int width, int height, boolean mergeRectangles) {
        LinkedList<Rectangle> rectangleList = null;
        Rectangle clip = new Rectangle(x, y, width, height);
        if (!new Area(this.theShape).intersects(clip)) {
            return null;
        }
        if (this.theShape instanceof Rectangle2D) {
            Rectangle2D.Double dstRect = new Rectangle2D.Double();
            Rectangle2D.intersect((Rectangle2D)this.theShape, clip, dstRect);
            int rectX = (int)Math.round(dstRect.getMinX());
            int rectY = (int)Math.round(dstRect.getMinY());
            int rectW = (int)Math.round(dstRect.getMaxX() - (double)rectX);
            int rectH = (int)Math.round(dstRect.getMaxY() - (double)rectY);
            rectangleList = new LinkedList<Rectangle>();
            rectangleList.addLast(new Rectangle(rectX, rectY, rectW, rectH));
        } else if (this.theShape instanceof Polygon) {
            rectangleList = this.polygonToRunLengthList(clip, (Polygon)this.theShape);
            if (mergeRectangles && rectangleList != null) {
                rectangleList = ROIShape.mergeRunLengthList(rectangleList);
            }
        } else {
            this.getAsImage();
            rectangleList = super.getAsRectangleList(x, y, width, height, mergeRectangles);
        }
        return rectangleList;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        LinkedList rectList = null;
        if (this.theShape == null) {
            rectList = new LinkedList();
        } else {
            Rectangle r = this.getBounds();
            rectList = this.getAsRectangleList(r.x, r.y, r.width, r.height);
        }
        out.defaultWriteObject();
        out.writeObject(rectList);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        LinkedList rectList = null;
        in.defaultReadObject();
        rectList = (LinkedList)in.readObject();
        Area a = new Area();
        int listSize = rectList.size();
        for (int i = 0; i < listSize; ++i) {
            a.add(new Area((Rectangle)rectList.get(i)));
        }
        this.theShape = a;
    }

    private class PolyShape {
        private static final int POLYGON_UNCLASSIFIED = 0;
        private static final int POLYGON_DEGENERATE = 1;
        private static final int POLYGON_CONVEX = 2;
        private static final int POLYGON_CONCAVE = 3;
        private Polygon poly;
        private Rectangle clip;
        private int type = 0;
        private boolean insidePolygon = false;

        PolyShape(Polygon polygon, Rectangle clipRect) {
            this.poly = polygon;
            this.clip = clipRect;
            this.insidePolygon = this.poly.contains(clipRect);
            this.type = 0;
        }

        public LinkedList getAsRectList() {
            LinkedList rectList = new LinkedList();
            if (this.insidePolygon) {
                rectList.addLast(this.poly.getBounds());
            } else {
                this.classifyPolygon();
                switch (this.type) {
                    case 1: {
                        rectList = null;
                        break;
                    }
                    case 2: {
                        rectList = this.scanConvex(rectList);
                        break;
                    }
                    case 3: {
                        rectList = this.scanConcave(rectList);
                        break;
                    }
                    default: {
                        throw new RuntimeException(JaiI18N.getString("ROIShape1"));
                    }
                }
            }
            return rectList;
        }

        private int classifyPolygon() {
            boolean allZero;
            if (this.type != 0) {
                return this.type;
            }
            int n = this.poly.npoints;
            if (n < 3) {
                this.type = 1;
                return this.type;
            }
            if (this.poly.getBounds().contains(this.clip)) {
                this.type = 2;
                return this.type;
            }
            int[] x = this.poly.xpoints;
            int[] y = this.poly.ypoints;
            int previousSign = this.sgn((x[0] - x[1]) * (y[1] - y[2]) - (x[1] - x[2]) * (y[0] - y[1]));
            boolean bl = allZero = previousSign == 0;
            int previousDirection = x[0] < x[1] ? -1 : (x[0] > x[1] ? 1 : (y[0] < y[1] ? -1 : (y[0] > y[1] ? 1 : 0)));
            int numDirectionChanges = 0;
            for (int i = 1; i < n; ++i) {
                int j = (i + 1) % n;
                int k = (i + 2) % n;
                int currentDirection = x[i] < x[j] ? -1 : (x[i] > x[j] ? 1 : (y[i] < y[j] ? -1 : (y[i] > y[j] ? 1 : 0)));
                if (currentDirection != 0 && currentDirection == -previousDirection) {
                    ++numDirectionChanges;
                }
                previousDirection = currentDirection;
                int sign = this.sgn((x[i] - x[j]) * (y[j] - y[k]) - (x[j] - x[k]) * (y[i] - y[j]));
                boolean bl2 = allZero = allZero && sign == 0;
                if (allZero) continue;
                if (sign != 0 && sign == -previousSign) {
                    this.type = 3;
                    break;
                }
                if (sign == 0) continue;
                previousSign = sign;
            }
            if (this.type == 0) {
                this.type = allZero ? 1 : (numDirectionChanges > 2 ? 3 : 2);
            }
            return this.type;
        }

        private final int sgn(int i) {
            int sign = i > 0 ? 1 : (i < 0 ? -1 : 0);
            return sign;
        }

        private LinkedList scanConvex(LinkedList rectList) {
            int intYLeft;
            if (rectList == null) {
                rectList = new LinkedList<Rectangle>();
            }
            int yMin = this.poly.ypoints[0];
            int topVertex = 0;
            int n = this.poly.npoints;
            for (int i = 1; i < n; ++i) {
                if (this.poly.ypoints[i] >= yMin) continue;
                yMin = this.poly.ypoints[i];
                topVertex = i;
            }
            int leftIndex = topVertex;
            int rightIndex = topVertex;
            int numRemaining = n;
            int y = yMin;
            int intYRight = intYLeft = y - 1;
            double[] px = this.intArrayToDoubleArray(this.poly.xpoints);
            int[] py = this.poly.ypoints;
            double[] leftX = new double[1];
            double[] leftDX = new double[1];
            double[] rightX = new double[1];
            double[] rightDX = new double[1];
            while (numRemaining > 0) {
                int i;
                while (intYLeft <= y && numRemaining > 0) {
                    --numRemaining;
                    i = leftIndex - 1;
                    if (i < 0) {
                        i = n - 1;
                    }
                    this.intersectX(px[leftIndex], py[leftIndex], px[i], py[i], y, leftX, leftDX);
                    intYLeft = py[i];
                    leftIndex = i;
                }
                while (intYRight <= y && numRemaining > 0) {
                    --numRemaining;
                    i = rightIndex + 1;
                    if (i >= n) {
                        i = 0;
                    }
                    this.intersectX(px[rightIndex], py[rightIndex], px[i], py[i], y, rightX, rightDX);
                    intYRight = py[i];
                    rightIndex = i;
                }
                while (y < intYLeft && y < intYRight) {
                    Rectangle rect;
                    if (y >= this.clip.y && (double)y < this.clip.getMaxY() && (rect = leftX[0] <= rightX[0] ? this.scanSegment(y, leftX[0], rightX[0]) : this.scanSegment(y, rightX[0], leftX[0])) != null) {
                        rectList.addLast(rect);
                    }
                    ++y;
                    leftX[0] = leftX[0] + leftDX[0];
                    rightX[0] = rightX[0] + rightDX[0];
                }
            }
            return rectList;
        }

        private Rectangle scanSegment(int y, double leftX, double rightX) {
            double x = leftX - 0.5;
            int xl = x < (double)this.clip.x ? this.clip.x : (int)Math.ceil(x);
            int xr = (int)Math.floor(rightX - 0.5);
            if (xr >= this.clip.x + this.clip.width) {
                xr = this.clip.x + this.clip.width - 1;
            }
            if (xl > xr) {
                return null;
            }
            return new Rectangle(xl, y, xr - xl + 1, 1);
        }

        private void intersectX(double x1, int y1, double x2, int y2, int y, double[] x, double[] dx) {
            int dy = y2 - y1;
            if (dy == 0) {
                dy = 1;
            }
            double frac = (double)(y - y1) + 0.5;
            dx[0] = (x2 - x1) / (double)dy;
            x[0] = x1 + dx[0] * frac;
        }

        private LinkedList scanConcave(LinkedList rectList) {
            int numVertices;
            if (rectList == null) {
                rectList = new LinkedList<Rectangle>();
            }
            if ((numVertices = this.poly.npoints) <= 0) {
                return null;
            }
            Vector<Integer> indVector = new Vector<Integer>();
            indVector.add(new Integer(0));
            for (int count = 1; count < numVertices; ++count) {
                int elt;
                int index;
                int value = this.poly.ypoints[count];
                for (index = 0; index < count && value > this.poly.ypoints[elt = ((Integer)indVector.get(index)).intValue()]; ++index) {
                }
                indVector.insertElementAt(new Integer(count), index);
            }
            int[] ind = this.vectorToIntArray(indVector);
            Vector<PolyEdge> activeEdges = new Vector<PolyEdge>(numVertices);
            int y0 = Math.max((int)this.clip.getMinY(), (int)Math.ceil((float)this.poly.ypoints[ind[0]] - 0.5f));
            int y1 = Math.min((int)this.clip.getMaxY(), (int)Math.floor((float)this.poly.ypoints[ind[numVertices - 1]] - 0.5f));
            int nextVertex = 0;
            for (int y = y0; y <= y1; ++y) {
                while (nextVertex < numVertices && (float)this.poly.ypoints[ind[nextVertex]] <= (float)y + 0.5f) {
                    int j;
                    int i = ind[nextVertex];
                    int n = j = i > 0 ? i - 1 : numVertices - 1;
                    if ((float)this.poly.ypoints[j] <= (float)y - 0.5f) {
                        this.deleteEdge(activeEdges, j);
                    } else if ((float)this.poly.ypoints[j] > (float)y + 0.5f) {
                        this.appendEdge(activeEdges, j, y);
                    }
                    int n2 = j = i < numVertices - 1 ? i + 1 : 0;
                    if ((float)this.poly.ypoints[j] <= (float)y - 0.5f) {
                        this.deleteEdge(activeEdges, i);
                    } else if ((float)this.poly.ypoints[j] > (float)y + 0.5f) {
                        this.appendEdge(activeEdges, i, y);
                    }
                    ++nextVertex;
                }
                Object[] edges = activeEdges.toArray();
                Arrays.sort(edges, (PolyEdge)edges[0]);
                int numActive = activeEdges.size();
                for (int k = 0; k < numActive; k += 2) {
                    int xr;
                    PolyEdge edge1 = (PolyEdge)edges[k];
                    PolyEdge edge2 = (PolyEdge)edges[k + 1];
                    int xl = (int)Math.ceil(edge1.x - 0.5);
                    if ((double)xl < this.clip.getMinX()) {
                        xl = (int)this.clip.getMinX();
                    }
                    if ((double)(xr = (int)Math.floor(edge2.x - 0.5)) > this.clip.getMaxX()) {
                        xr = (int)this.clip.getMaxX();
                    }
                    if (xl <= xr) {
                        Rectangle r = new Rectangle(xl, y, xr - xl + 1, 1);
                        rectList.addLast(r);
                    }
                    edge1.x += edge1.dx;
                    activeEdges.setElementAt(edge1, k);
                    edge2.x += edge2.dx;
                    activeEdges.setElementAt(edge2, k + 1);
                }
            }
            return rectList;
        }

        private void deleteEdge(Vector edges, int i) {
            int j;
            int numActive = edges.size();
            for (j = 0; j < numActive; ++j) {
                PolyEdge edge = (PolyEdge)edges.get(j);
                if (edge.i == i) break;
            }
            if (j < numActive) {
                edges.removeElementAt(j);
            }
        }

        private void appendEdge(Vector edges, int i, int y) {
            int iq;
            int ip;
            int j = (i + 1) % this.poly.npoints;
            if (this.poly.ypoints[i] < this.poly.ypoints[j]) {
                ip = i;
                iq = j;
            } else {
                ip = j;
                iq = i;
            }
            double dx = (double)(this.poly.xpoints[iq] - this.poly.xpoints[ip]) / (double)(this.poly.ypoints[iq] - this.poly.ypoints[ip]);
            double x = dx * (double)((float)y + 0.5f - (float)this.poly.ypoints[ip]) + (double)this.poly.xpoints[ip];
            edges.add(new PolyEdge(x, dx, i));
        }

        private double[] intArrayToDoubleArray(int[] intArray) {
            int length = intArray.length;
            double[] doubleArray = new double[length];
            for (int i = 0; i < length; ++i) {
                doubleArray[i] = intArray[i];
            }
            return doubleArray;
        }

        private int[] vectorToIntArray(Vector vector) {
            int size = vector.size();
            int[] array = new int[size];
            Object[] objects = vector.toArray();
            for (int i = 0; i < size; ++i) {
                array[i] = (Integer)objects[i];
            }
            return array;
        }

        private class PolyEdge
        implements Comparator {
            public double x;
            public double dx;
            public int i;

            PolyEdge(double x, double dx, int i) {
                this.x = x;
                this.dx = dx;
                this.i = i;
            }

            public int compare(Object o1, Object o2) {
                double x1 = ((PolyEdge)o1).x;
                double x2 = ((PolyEdge)o2).x;
                int returnValue = x1 < x2 ? -1 : (x1 > x2 ? 1 : 0);
                return returnValue;
            }
        }
    }
}

