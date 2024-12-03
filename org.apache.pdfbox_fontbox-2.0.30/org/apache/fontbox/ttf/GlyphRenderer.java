/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.GlyphDescription;

class GlyphRenderer {
    private static final Log LOG = LogFactory.getLog(GlyphRenderer.class);
    private GlyphDescription glyphDescription;

    GlyphRenderer(GlyphDescription glyphDescription) {
        this.glyphDescription = glyphDescription;
    }

    public GeneralPath getPath() {
        Point[] points = this.describe(this.glyphDescription);
        return this.calculatePath(points);
    }

    private Point[] describe(GlyphDescription gd) {
        int endPtIndex = 0;
        int endPtOfContourIndex = -1;
        Point[] points = new Point[gd.getPointCount()];
        for (int i = 0; i < points.length; ++i) {
            boolean endPt;
            if (endPtOfContourIndex == -1) {
                endPtOfContourIndex = gd.getEndPtOfContours(endPtIndex);
            }
            boolean bl = endPt = endPtOfContourIndex == i;
            if (endPt) {
                ++endPtIndex;
                endPtOfContourIndex = -1;
            }
            points[i] = new Point(gd.getXCoordinate(i), gd.getYCoordinate(i), (gd.getFlags(i) & 1) != 0, endPt);
        }
        return points;
    }

    private GeneralPath calculatePath(Point[] points) {
        GeneralPath path = new GeneralPath();
        int start = 0;
        int len = points.length;
        for (int p = 0; p < len; ++p) {
            if (!points[p].endOfContour) continue;
            Point firstPoint = points[start];
            Point lastPoint = points[p];
            ArrayList<Point> contour = new ArrayList<Point>();
            for (int q = start; q <= p; ++q) {
                contour.add(points[q]);
            }
            if (points[start].onCurve) {
                contour.add(firstPoint);
            } else if (points[p].onCurve) {
                contour.add(0, lastPoint);
            } else {
                Point pmid = this.midValue(firstPoint, lastPoint);
                contour.add(0, pmid);
                contour.add(pmid);
            }
            this.moveTo(path, (Point)contour.get(0));
            int clen = contour.size();
            for (int j = 1; j < clen; ++j) {
                Point pnow = (Point)contour.get(j);
                if (pnow.onCurve) {
                    this.lineTo(path, pnow);
                    continue;
                }
                if (((Point)contour.get(j + 1)).onCurve) {
                    this.quadTo(path, pnow, (Point)contour.get(j + 1));
                    ++j;
                    continue;
                }
                this.quadTo(path, pnow, this.midValue(pnow, (Point)contour.get(j + 1)));
            }
            path.closePath();
            start = p + 1;
        }
        return path;
    }

    private void moveTo(GeneralPath path, Point point) {
        path.moveTo(point.x, point.y);
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("moveTo: " + String.format(Locale.US, "%d,%d", point.x, point.y)));
        }
    }

    private void lineTo(GeneralPath path, Point point) {
        path.lineTo(point.x, point.y);
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("lineTo: " + String.format(Locale.US, "%d,%d", point.x, point.y)));
        }
    }

    private void quadTo(GeneralPath path, Point ctrlPoint, Point point) {
        path.quadTo(ctrlPoint.x, ctrlPoint.y, point.x, point.y);
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("quadTo: " + String.format(Locale.US, "%d,%d %d,%d", ctrlPoint.x, ctrlPoint.y, point.x, point.y)));
        }
    }

    private int midValue(int a, int b) {
        return a + (b - a) / 2;
    }

    private Point midValue(Point point1, Point point2) {
        return new Point(this.midValue(point1.x, point2.x), this.midValue(point1.y, point2.y));
    }

    private static class Point {
        private int x = 0;
        private int y = 0;
        private boolean onCurve = true;
        private boolean endOfContour = false;

        Point(int xValue, int yValue, boolean onCurveValue, boolean endOfContourValue) {
            this.x = xValue;
            this.y = yValue;
            this.onCurve = onCurveValue;
            this.endOfContour = endOfContourValue;
        }

        Point(int xValue, int yValue) {
            this(xValue, yValue, true, false);
        }

        public String toString() {
            return String.format(Locale.US, "Point(%d,%d,%s,%s)", this.x, this.y, this.onCurve ? "onCurve" : "", this.endOfContour ? "endOfContour" : "");
        }
    }
}

