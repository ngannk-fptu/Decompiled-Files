/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

class CloudyBorder {
    private static final double ANGLE_180_DEG = Math.PI;
    private static final double ANGLE_90_DEG = 1.5707963267948966;
    private static final double ANGLE_34_DEG = Math.toRadians(34.0);
    private static final double ANGLE_30_DEG = Math.toRadians(30.0);
    private static final double ANGLE_12_DEG = Math.toRadians(12.0);
    private final PDAppearanceContentStream output;
    private final PDRectangle annotRect;
    private final double intensity;
    private final double lineWidth;
    private PDRectangle rectWithDiff;
    private boolean outputStarted = false;
    private double bboxMinX;
    private double bboxMinY;
    private double bboxMaxX;
    private double bboxMaxY;

    CloudyBorder(PDAppearanceContentStream stream, double intensity, double lineWidth, PDRectangle rect) {
        this.output = stream;
        this.intensity = intensity;
        this.lineWidth = lineWidth;
        this.annotRect = rect;
    }

    void createCloudyRectangle(PDRectangle rd) throws IOException {
        this.rectWithDiff = this.applyRectDiff(rd, (float)(this.lineWidth / 2.0));
        double left = this.rectWithDiff.getLowerLeftX();
        double bottom = this.rectWithDiff.getLowerLeftY();
        double right = this.rectWithDiff.getUpperRightX();
        double top = this.rectWithDiff.getUpperRightY();
        this.cloudyRectangleImpl(left, bottom, right, top, false);
        this.finish();
    }

    void createCloudyPolygon(float[][] path) throws IOException {
        int n = path.length;
        Point2D.Double[] polygon = new Point2D.Double[n];
        for (int i = 0; i < n; ++i) {
            float[] array = path[i];
            if (array.length == 2) {
                polygon[i] = new Point2D.Double(array[0], array[1]);
                continue;
            }
            if (array.length != 6) continue;
            polygon[i] = new Point2D.Double(array[4], array[5]);
        }
        this.cloudyPolygonImpl(polygon, false);
        this.finish();
    }

    void createCloudyEllipse(PDRectangle rd) throws IOException {
        this.rectWithDiff = this.applyRectDiff(rd, 0.0f);
        double left = this.rectWithDiff.getLowerLeftX();
        double bottom = this.rectWithDiff.getLowerLeftY();
        double right = this.rectWithDiff.getUpperRightX();
        double top = this.rectWithDiff.getUpperRightY();
        this.cloudyEllipseImpl(left, bottom, right, top);
        this.finish();
    }

    PDRectangle getBBox() {
        return this.getRectangle();
    }

    PDRectangle getRectangle() {
        return new PDRectangle((float)this.bboxMinX, (float)this.bboxMinY, (float)(this.bboxMaxX - this.bboxMinX), (float)(this.bboxMaxY - this.bboxMinY));
    }

    AffineTransform getMatrix() {
        return AffineTransform.getTranslateInstance(-this.bboxMinX, -this.bboxMinY);
    }

    PDRectangle getRectDifference() {
        if (this.annotRect == null) {
            float d = (float)this.lineWidth / 2.0f;
            return new PDRectangle(d, d, (float)this.lineWidth, (float)this.lineWidth);
        }
        PDRectangle re = this.rectWithDiff != null ? this.rectWithDiff : this.annotRect;
        float left = re.getLowerLeftX() - (float)this.bboxMinX;
        float bottom = re.getLowerLeftY() - (float)this.bboxMinY;
        float right = (float)this.bboxMaxX - re.getUpperRightX();
        float top = (float)this.bboxMaxY - re.getUpperRightY();
        return new PDRectangle(left, bottom, right - left, top - bottom);
    }

    private static double cosine(double dx, double hypot) {
        if (Double.compare(hypot, 0.0) == 0) {
            return 0.0;
        }
        return dx / hypot;
    }

    private static double sine(double dy, double hypot) {
        if (Double.compare(hypot, 0.0) == 0) {
            return 0.0;
        }
        return dy / hypot;
    }

    private void cloudyRectangleImpl(double left, double bottom, double right, double top, boolean isEllipse) throws IOException {
        double w = right - left;
        double h = top - bottom;
        if (this.intensity <= 0.0) {
            this.output.addRect((float)left, (float)bottom, (float)w, (float)h);
            this.bboxMinX = left;
            this.bboxMinY = bottom;
            this.bboxMaxX = right;
            this.bboxMaxY = top;
            return;
        }
        Point2D.Double[] polygon = w < 1.0 ? new Point2D.Double[]{new Point2D.Double(left, bottom), new Point2D.Double(left, top), new Point2D.Double(left, bottom)} : (h < 1.0 ? new Point2D.Double[]{new Point2D.Double(left, bottom), new Point2D.Double(right, bottom), new Point2D.Double(left, bottom)} : new Point2D.Double[]{new Point2D.Double(left, bottom), new Point2D.Double(right, bottom), new Point2D.Double(right, top), new Point2D.Double(left, top), new Point2D.Double(left, bottom)});
        this.cloudyPolygonImpl(polygon, isEllipse);
    }

    private void cloudyPolygonImpl(Point2D.Double[] vertices, boolean isEllipse) throws IOException {
        double cloudRadius;
        Point2D.Double[] polygon = this.removeZeroLengthSegments(vertices);
        this.getPositivePolygon(polygon);
        int numPoints = polygon.length;
        if (numPoints < 2) {
            return;
        }
        if (this.intensity <= 0.0) {
            this.moveTo(polygon[0]);
            for (int i = 1; i < numPoints; ++i) {
                this.lineTo(polygon[i]);
            }
            return;
        }
        double d = cloudRadius = isEllipse ? this.getEllipseCloudRadius() : this.getPolygonCloudRadius();
        if (cloudRadius < 0.5) {
            cloudRadius = 0.5;
        }
        double k = Math.cos(ANGLE_34_DEG);
        double advIntermDefault = 2.0 * k * cloudRadius;
        double advCornerDefault = k * cloudRadius;
        double[] array = new double[2];
        double anglePrev = 0.0;
        int n0 = this.computeParamsPolygon(advIntermDefault, advCornerDefault, k, cloudRadius, polygon[numPoints - 2].distance(polygon[0]), array);
        double alphaPrev = n0 == 0 ? array[0] : ANGLE_34_DEG;
        int j = 0;
        while (j + 1 < numPoints) {
            Point2D.Double pt = polygon[j];
            Point2D.Double ptNext = polygon[j + 1];
            double length = pt.distance(ptNext);
            if (Double.compare(length, 0.0) == 0) {
                alphaPrev = ANGLE_34_DEG;
            } else {
                int n = this.computeParamsPolygon(advIntermDefault, advCornerDefault, k, cloudRadius, length, array);
                if (n < 0) {
                    if (!this.outputStarted) {
                        this.moveTo(pt);
                    }
                } else {
                    double alpha = array[0];
                    double dx = array[1];
                    double angleCur = Math.atan2(ptNext.y - pt.y, ptNext.x - pt.x);
                    if (j == 0) {
                        Point2D.Double ptPrev = polygon[numPoints - 2];
                        anglePrev = Math.atan2(pt.y - ptPrev.y, pt.x - ptPrev.x);
                    }
                    double cos = CloudyBorder.cosine(ptNext.x - pt.x, length);
                    double sin = CloudyBorder.sine(ptNext.y - pt.y, length);
                    double x = pt.x;
                    double y = pt.y;
                    this.addCornerCurl(anglePrev, angleCur, cloudRadius, pt.x, pt.y, alpha, alphaPrev, !this.outputStarted);
                    double adv = 2.0 * k * cloudRadius + 2.0 * dx;
                    x += adv * cos;
                    y += adv * sin;
                    int numInterm = n;
                    if (n >= 1) {
                        this.addFirstIntermediateCurl(angleCur, cloudRadius, alpha, x, y);
                        x += advIntermDefault * cos;
                        y += advIntermDefault * sin;
                        numInterm = n - 1;
                    }
                    Point2D.Double[] template = this.getIntermediateCurlTemplate(angleCur, cloudRadius);
                    for (int i = 0; i < numInterm; ++i) {
                        this.outputCurlTemplate(template, x, y);
                        x += advIntermDefault * cos;
                        y += advIntermDefault * sin;
                    }
                    anglePrev = angleCur;
                    alphaPrev = n == 0 ? alpha : ANGLE_34_DEG;
                }
            }
            ++j;
        }
    }

    private int computeParamsPolygon(double advInterm, double advCorner, double k, double r, double length, double[] array) {
        double alpha;
        if (Double.compare(length, 0.0) == 0) {
            array[0] = ANGLE_34_DEG;
            array[1] = 0.0;
            return -1;
        }
        int n = (int)Math.ceil((length - 2.0 * advCorner) / advInterm);
        double e = length - (2.0 * advCorner + (double)n * advInterm);
        double dx = e / 2.0;
        double arg = (k * r + dx) / r;
        array[0] = alpha = arg < -1.0 || arg > 1.0 ? 0.0 : Math.acos(arg);
        array[1] = dx;
        return n;
    }

    private void addCornerCurl(double anglePrev, double angleCur, double radius, double cx, double cy, double alpha, double alphaPrev, boolean addMoveTo) throws IOException {
        double a = anglePrev + Math.PI + alphaPrev;
        double b = anglePrev + Math.PI + alphaPrev - Math.toRadians(22.0);
        this.getArcSegment(a, b, cx, cy, radius, radius, null, addMoveTo);
        a = b;
        b = angleCur - alpha;
        this.getArc(a, b, radius, radius, cx, cy, null, false);
    }

    private void addFirstIntermediateCurl(double angleCur, double r, double alpha, double cx, double cy) throws IOException {
        double a = angleCur + Math.PI;
        this.getArcSegment(a + alpha, a + alpha - ANGLE_30_DEG, cx, cy, r, r, null, false);
        this.getArcSegment(a + alpha - ANGLE_30_DEG, a + 1.5707963267948966, cx, cy, r, r, null, false);
        this.getArcSegment(a + 1.5707963267948966, a + Math.PI - ANGLE_34_DEG, cx, cy, r, r, null, false);
    }

    private Point2D.Double[] getIntermediateCurlTemplate(double angleCur, double r) throws IOException {
        ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
        double a = angleCur + Math.PI;
        this.getArcSegment(a + ANGLE_34_DEG, a + ANGLE_12_DEG, 0.0, 0.0, r, r, points, false);
        this.getArcSegment(a + ANGLE_12_DEG, a + 1.5707963267948966, 0.0, 0.0, r, r, points, false);
        this.getArcSegment(a + 1.5707963267948966, a + Math.PI - ANGLE_34_DEG, 0.0, 0.0, r, r, points, false);
        return points.toArray(new Point2D.Double[points.size()]);
    }

    private void outputCurlTemplate(Point2D.Double[] template, double x, double y) throws IOException {
        Point2D.Double a;
        int n = template.length;
        int i = 0;
        if (n % 3 == 1) {
            a = template[0];
            this.moveTo(a.x + x, a.y + y);
            ++i;
        }
        while (i + 2 < n) {
            a = template[i];
            Point2D.Double b = template[i + 1];
            Point2D.Double c = template[i + 2];
            this.curveTo(a.x + x, a.y + y, b.x + x, b.y + y, c.x + x, c.y + y);
            i += 3;
        }
    }

    private PDRectangle applyRectDiff(PDRectangle rd, float min) {
        float rdTop;
        float rdRight;
        float rdBottom;
        float rdLeft;
        float rectLeft = this.annotRect.getLowerLeftX();
        float rectBottom = this.annotRect.getLowerLeftY();
        float rectRight = this.annotRect.getUpperRightX();
        float rectTop = this.annotRect.getUpperRightY();
        rectLeft = Math.min(rectLeft, rectRight);
        rectBottom = Math.min(rectBottom, rectTop);
        rectRight = Math.max(rectLeft, rectRight);
        rectTop = Math.max(rectBottom, rectTop);
        if (rd != null) {
            rdLeft = Math.max(rd.getLowerLeftX(), min);
            rdBottom = Math.max(rd.getLowerLeftY(), min);
            rdRight = Math.max(rd.getUpperRightX(), min);
            rdTop = Math.max(rd.getUpperRightY(), min);
        } else {
            rdLeft = min;
            rdBottom = min;
            rdRight = min;
            rdTop = min;
        }
        return new PDRectangle(rectLeft += rdLeft, rectBottom += rdBottom, (rectRight -= rdRight) - rectLeft, (rectTop -= rdTop) - rectBottom);
    }

    private void reversePolygon(Point2D.Double[] points) {
        int len = points.length;
        int n = len / 2;
        for (int i = 0; i < n; ++i) {
            Point2D.Double pj;
            int j = len - i - 1;
            Point2D.Double pi = points[i];
            points[i] = pj = points[j];
            points[j] = pi;
        }
    }

    private void getPositivePolygon(Point2D.Double[] points) {
        if (this.getPolygonDirection(points) < 0.0) {
            this.reversePolygon(points);
        }
    }

    private double getPolygonDirection(Point2D.Double[] points) {
        double a = 0.0;
        int len = points.length;
        for (int i = 0; i < len; ++i) {
            int j = (i + 1) % len;
            a += points[i].x * points[j].y - points[i].y * points[j].x;
        }
        return a;
    }

    private void getArc(double startAng, double endAng, double rx, double ry, double cx, double cy, ArrayList<Point2D.Double> out, boolean addMoveTo) throws IOException {
        double angleTodo;
        double angleIncr = 1.5707963267948966;
        double startx = rx * Math.cos(startAng) + cx;
        double starty = ry * Math.sin(startAng) + cy;
        for (angleTodo = endAng - startAng; angleTodo < 0.0; angleTodo += Math.PI * 2) {
        }
        double sweep = angleTodo;
        double angleDone = 0.0;
        if (addMoveTo) {
            if (out != null) {
                out.add(new Point2D.Double(startx, starty));
            } else {
                this.moveTo(startx, starty);
            }
        }
        while (angleTodo > 1.5707963267948966) {
            this.getArcSegment(startAng + angleDone, startAng + angleDone + 1.5707963267948966, cx, cy, rx, ry, out, false);
            angleDone += 1.5707963267948966;
            angleTodo -= 1.5707963267948966;
        }
        if (angleTodo > 0.0) {
            this.getArcSegment(startAng + angleDone, startAng + sweep, cx, cy, rx, ry, out, false);
        }
    }

    private void getArcSegment(double startAng, double endAng, double cx, double cy, double rx, double ry, ArrayList<Point2D.Double> out, boolean addMoveTo) throws IOException {
        double cosA = Math.cos(startAng);
        double sinA = Math.sin(startAng);
        double cosB = Math.cos(endAng);
        double sinB = Math.sin(endAng);
        double denom = Math.sin((endAng - startAng) / 2.0);
        if (Double.compare(denom, 0.0) == 0) {
            if (addMoveTo) {
                double xs = cx + rx * cosA;
                double ys = cy + ry * sinA;
                if (out != null) {
                    out.add(new Point2D.Double(xs, ys));
                } else {
                    this.moveTo(xs, ys);
                }
            }
            return;
        }
        double bcp = 1.333333333 * (1.0 - Math.cos((endAng - startAng) / 2.0)) / denom;
        double p1x = cx + rx * (cosA - bcp * sinA);
        double p1y = cy + ry * (sinA + bcp * cosA);
        double p2x = cx + rx * (cosB + bcp * sinB);
        double p2y = cy + ry * (sinB - bcp * cosB);
        double p3x = cx + rx * cosB;
        double p3y = cy + ry * sinB;
        if (addMoveTo) {
            double xs = cx + rx * cosA;
            double ys = cy + ry * sinA;
            if (out != null) {
                out.add(new Point2D.Double(xs, ys));
            } else {
                this.moveTo(xs, ys);
            }
        }
        if (out != null) {
            out.add(new Point2D.Double(p1x, p1y));
            out.add(new Point2D.Double(p2x, p2y));
            out.add(new Point2D.Double(p3x, p3y));
        } else {
            this.curveTo(p1x, p1y, p2x, p2y, p3x, p3y);
        }
    }

    private static Point2D.Double[] flattenEllipse(double left, double bottom, double right, double top) {
        Ellipse2D.Double ellipse = new Ellipse2D.Double(left, bottom, right - left, top - bottom);
        double flatness = 0.5;
        PathIterator iterator = ellipse.getPathIterator(null, 0.5);
        double[] coords = new double[6];
        ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
        while (!iterator.isDone()) {
            switch (iterator.currentSegment(coords)) {
                case 0: 
                case 1: {
                    points.add(new Point2D.Double(coords[0], coords[1]));
                    break;
                }
            }
            iterator.next();
        }
        int size = points.size();
        double closeTestLimit = 0.05;
        if (size >= 2 && ((Point2D.Double)points.get(size - 1)).distance((Point2D)points.get(0)) > 0.05) {
            points.add((Point2D.Double)points.get(points.size() - 1));
        }
        return points.toArray(new Point2D.Double[points.size()]);
    }

    private void cloudyEllipseImpl(double leftOrig, double bottomOrig, double rightOrig, double topOrig) throws IOException {
        double mid;
        if (this.intensity <= 0.0) {
            this.drawBasicEllipse(leftOrig, bottomOrig, rightOrig, topOrig);
            return;
        }
        double left = leftOrig;
        double bottom = bottomOrig;
        double right = rightOrig;
        double top = topOrig;
        double width = right - left;
        double height = top - bottom;
        double cloudRadius = this.getEllipseCloudRadius();
        double threshold1 = 0.5 * cloudRadius;
        if (width < threshold1 && height < threshold1) {
            this.drawBasicEllipse(left, bottom, right, top);
            return;
        }
        double threshold2 = 5.0;
        if (width < 5.0 && height > 20.0 || width > 20.0 && height < 5.0) {
            this.cloudyRectangleImpl(left, bottom, right, top, true);
            return;
        }
        double radiusAdj = Math.sin(ANGLE_12_DEG) * cloudRadius - 1.5;
        if (width > 2.0 * radiusAdj) {
            left += radiusAdj;
            right -= radiusAdj;
        } else {
            mid = (left + right) / 2.0;
            left = mid - 0.1;
            right = mid + 0.1;
        }
        if (height > 2.0 * radiusAdj) {
            top -= radiusAdj;
            bottom += radiusAdj;
        } else {
            mid = (top + bottom) / 2.0;
            top = mid + 0.1;
            bottom = mid - 0.1;
        }
        Point2D.Double[] flatPolygon = CloudyBorder.flattenEllipse(left, bottom, right, top);
        int numPoints = flatPolygon.length;
        if (numPoints < 2) {
            return;
        }
        double totLen = 0.0;
        for (int i = 1; i < numPoints; ++i) {
            totLen += flatPolygon[i - 1].distance(flatPolygon[i]);
        }
        double k = Math.cos(ANGLE_34_DEG);
        double curlAdvance = 2.0 * k * cloudRadius;
        int n = (int)Math.ceil(totLen / curlAdvance);
        if (n < 2) {
            this.drawBasicEllipse(leftOrig, bottomOrig, rightOrig, topOrig);
            return;
        }
        curlAdvance = totLen / (double)n;
        cloudRadius = curlAdvance / (2.0 * k);
        if (cloudRadius < 0.5) {
            cloudRadius = 0.5;
            curlAdvance = 2.0 * k * cloudRadius;
        } else if (cloudRadius < 3.0) {
            this.drawBasicEllipse(leftOrig, bottomOrig, rightOrig, topOrig);
            return;
        }
        int centerPointsLength = n;
        Point2D.Double[] centerPoints = new Point2D.Double[centerPointsLength];
        int centerPointsIndex = 0;
        double lengthRemain = 0.0;
        double comparisonToler = this.lineWidth * 0.1;
        int i = 0;
        while (i + 1 < numPoints) {
            Point2D.Double p1 = flatPolygon[i];
            Point2D.Double p2 = flatPolygon[i + 1];
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double length = p1.distance(p2);
            if (Double.compare(length, 0.0) != 0) {
                double lengthTodo = length + lengthRemain;
                if (lengthTodo >= curlAdvance - comparisonToler || i == numPoints - 2) {
                    double cos = CloudyBorder.cosine(dx, length);
                    double sin = CloudyBorder.sine(dy, length);
                    double d = curlAdvance - lengthRemain;
                    do {
                        double x = p1.x + d * cos;
                        double y = p1.y + d * sin;
                        if (centerPointsIndex < centerPointsLength) {
                            centerPoints[centerPointsIndex++] = new Point2D.Double(x, y);
                        }
                        d += curlAdvance;
                    } while ((lengthTodo -= curlAdvance) >= curlAdvance - comparisonToler);
                    lengthRemain = lengthTodo;
                    if (lengthRemain < 0.0) {
                        lengthRemain = 0.0;
                    }
                } else {
                    lengthRemain += length;
                }
            }
            ++i;
        }
        numPoints = centerPointsIndex;
        double anglePrev = 0.0;
        double alphaPrev = 0.0;
        for (int i2 = 0; i2 < numPoints; ++i2) {
            int idxNext = i2 + 1;
            if (i2 + 1 >= numPoints) {
                idxNext = 0;
            }
            Point2D.Double pt = centerPoints[i2];
            Point2D.Double ptNext = centerPoints[idxNext];
            if (i2 == 0) {
                Point2D.Double ptPrev = centerPoints[numPoints - 1];
                anglePrev = Math.atan2(pt.y - ptPrev.y, pt.x - ptPrev.x);
                alphaPrev = this.computeParamsEllipse(ptPrev, pt, cloudRadius, curlAdvance);
            }
            double angleCur = Math.atan2(ptNext.y - pt.y, ptNext.x - pt.x);
            double alpha = this.computeParamsEllipse(pt, ptNext, cloudRadius, curlAdvance);
            this.addCornerCurl(anglePrev, angleCur, cloudRadius, pt.x, pt.y, alpha, alphaPrev, !this.outputStarted);
            anglePrev = angleCur;
            alphaPrev = alpha;
        }
    }

    private double computeParamsEllipse(Point2D.Double pt, Point2D.Double ptNext, double r, double curlAdv) {
        double length = pt.distance(ptNext);
        if (Double.compare(length, 0.0) == 0) {
            return ANGLE_34_DEG;
        }
        double e = length - curlAdv;
        double arg = (curlAdv / 2.0 + e / 2.0) / r;
        return arg < -1.0 || arg > 1.0 ? 0.0 : Math.acos(arg);
    }

    private Point2D.Double[] removeZeroLengthSegments(Point2D.Double[] polygon) {
        int np = polygon.length;
        if (np <= 2) {
            return polygon;
        }
        double toler = 0.5;
        int npNew = np;
        Point2D.Double ptPrev = polygon[0];
        for (int i = 1; i < np; ++i) {
            Point2D.Double pt = polygon[i];
            if (Math.abs(pt.x - ptPrev.x) < 0.5 && Math.abs(pt.y - ptPrev.y) < 0.5) {
                polygon[i] = null;
                --npNew;
            }
            ptPrev = pt;
        }
        if (npNew == np) {
            return polygon;
        }
        Point2D.Double[] polygonNew = new Point2D.Double[npNew];
        int j = 0;
        for (int i = 0; i < np; ++i) {
            Point2D.Double pt = polygon[i];
            if (pt == null) continue;
            polygonNew[j++] = pt;
        }
        return polygonNew;
    }

    private void drawBasicEllipse(double left, double bottom, double right, double top) throws IOException {
        double rx = Math.abs(right - left) / 2.0;
        double ry = Math.abs(top - bottom) / 2.0;
        double cx = (left + right) / 2.0;
        double cy = (bottom + top) / 2.0;
        this.getArc(0.0, Math.PI * 2, rx, ry, cx, cy, null, true);
    }

    private void beginOutput(double x, double y) throws IOException {
        this.bboxMinX = x;
        this.bboxMinY = y;
        this.bboxMaxX = x;
        this.bboxMaxY = y;
        this.outputStarted = true;
        this.output.setLineJoinStyle(2);
    }

    private void updateBBox(double x, double y) {
        this.bboxMinX = Math.min(this.bboxMinX, x);
        this.bboxMinY = Math.min(this.bboxMinY, y);
        this.bboxMaxX = Math.max(this.bboxMaxX, x);
        this.bboxMaxY = Math.max(this.bboxMaxY, y);
    }

    private void moveTo(Point2D.Double p) throws IOException {
        this.moveTo(p.x, p.y);
    }

    private void moveTo(double x, double y) throws IOException {
        if (this.outputStarted) {
            this.updateBBox(x, y);
        } else {
            this.beginOutput(x, y);
        }
        this.output.moveTo((float)x, (float)y);
    }

    private void lineTo(Point2D.Double p) throws IOException {
        this.lineTo(p.x, p.y);
    }

    private void lineTo(double x, double y) throws IOException {
        if (this.outputStarted) {
            this.updateBBox(x, y);
        } else {
            this.beginOutput(x, y);
        }
        this.output.lineTo((float)x, (float)y);
    }

    private void curveTo(double ax, double ay, double bx, double by, double cx, double cy) throws IOException {
        this.updateBBox(ax, ay);
        this.updateBBox(bx, by);
        this.updateBBox(cx, cy);
        this.output.curveTo((float)ax, (float)ay, (float)bx, (float)by, (float)cx, (float)cy);
    }

    private void finish() throws IOException {
        if (this.outputStarted) {
            this.output.closePath();
        }
        if (this.lineWidth > 0.0) {
            double d = this.lineWidth / 2.0;
            this.bboxMinX -= d;
            this.bboxMinY -= d;
            this.bboxMaxX += d;
            this.bboxMaxY += d;
        }
    }

    private double getEllipseCloudRadius() {
        return 4.75 * this.intensity + 0.5 * this.lineWidth;
    }

    private double getPolygonCloudRadius() {
        return 4.0 * this.intensity + 0.5 * this.lineWidth;
    }
}

