/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.geom;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.ext.awt.geom.Cubic;
import org.apache.batik.ext.awt.geom.Linear;
import org.apache.batik.ext.awt.geom.Quadradic;
import org.apache.batik.ext.awt.geom.Segment;

public class SegmentList {
    List segments = new LinkedList();

    public SegmentList() {
    }

    public SegmentList(Shape s) {
        PathIterator pi = s.getPathIterator(null);
        float[] pts = new float[6];
        Point2D.Double loc = null;
        Point2D.Double openLoc = null;
        while (!pi.isDone()) {
            int type = pi.currentSegment(pts);
            switch (type) {
                case 0: {
                    openLoc = loc = new Point2D.Double(pts[0], pts[1]);
                    break;
                }
                case 1: {
                    Point2D.Double p0 = new Point2D.Double(pts[0], pts[1]);
                    this.segments.add(new Linear(loc, p0));
                    loc = p0;
                    break;
                }
                case 2: {
                    Point2D.Double p0 = new Point2D.Double(pts[0], pts[1]);
                    Point2D.Double p1 = new Point2D.Double(pts[2], pts[3]);
                    this.segments.add(new Quadradic(loc, p0, p1));
                    loc = p1;
                    break;
                }
                case 3: {
                    Point2D.Double p0 = new Point2D.Double(pts[0], pts[1]);
                    Point2D.Double p1 = new Point2D.Double(pts[2], pts[3]);
                    Point2D.Double p2 = new Point2D.Double(pts[4], pts[5]);
                    this.segments.add(new Cubic(loc, p0, p1, p2));
                    loc = p2;
                    break;
                }
                case 4: {
                    this.segments.add(new Linear(loc, openLoc));
                    loc = openLoc;
                }
            }
            pi.next();
        }
    }

    public Rectangle2D getBounds2D() {
        Iterator iter = this.iterator();
        if (!iter.hasNext()) {
            return null;
        }
        Rectangle2D ret = (Rectangle2D)((Segment)iter.next()).getBounds2D().clone();
        while (iter.hasNext()) {
            Segment seg = (Segment)iter.next();
            Rectangle2D segB = seg.getBounds2D();
            Rectangle2D.union(segB, ret, ret);
        }
        return ret;
    }

    public void add(Segment s) {
        this.segments.add(s);
    }

    public Iterator iterator() {
        return this.segments.iterator();
    }

    public int size() {
        return this.segments.size();
    }

    public SplitResults split(double y) {
        Iterator iter = this.segments.iterator();
        SegmentList above = new SegmentList();
        SegmentList below = new SegmentList();
        while (iter.hasNext()) {
            Segment[] resBelow;
            Segment[] resAbove;
            Segment seg = (Segment)iter.next();
            Segment.SplitResults results = seg.split(y);
            if (results == null) {
                Rectangle2D bounds = seg.getBounds2D();
                if (bounds.getY() > y) {
                    below.add(seg);
                    continue;
                }
                if (bounds.getY() == y) {
                    if (bounds.getHeight() == 0.0) continue;
                    below.add(seg);
                    continue;
                }
                above.add(seg);
                continue;
            }
            for (Segment aResAbove : resAbove = results.getAbove()) {
                above.add(aResAbove);
            }
            for (Segment aResBelow : resBelow = results.getBelow()) {
                below.add(aResBelow);
            }
        }
        return new SplitResults(above, below);
    }

    public static class SplitResults {
        final SegmentList above;
        final SegmentList below;

        public SplitResults(SegmentList above, SegmentList below) {
            this.above = above != null && above.size() > 0 ? above : null;
            this.below = below != null && below.size() > 0 ? below : null;
        }

        public SegmentList getAbove() {
            return this.above;
        }

        public SegmentList getBelow() {
            return this.below;
        }
    }
}

