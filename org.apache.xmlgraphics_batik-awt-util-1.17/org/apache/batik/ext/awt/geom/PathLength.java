/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PathLength {
    protected Shape path;
    protected List segments;
    protected int[] segmentIndexes;
    protected float pathLength;
    protected boolean initialised;

    public PathLength(Shape path) {
        this.setPath(path);
    }

    public Shape getPath() {
        return this.path;
    }

    public void setPath(Shape v) {
        this.path = v;
        this.initialised = false;
    }

    public float lengthOfPath() {
        if (!this.initialised) {
            this.initialise();
        }
        return this.pathLength;
    }

    protected void initialise() {
        this.pathLength = 0.0f;
        PathIterator pi = this.path.getPathIterator(new AffineTransform());
        SingleSegmentPathIterator sspi = new SingleSegmentPathIterator();
        this.segments = new ArrayList(20);
        ArrayList<Integer> indexes = new ArrayList<Integer>(20);
        int index = 0;
        int origIndex = -1;
        float lastMoveX = 0.0f;
        float lastMoveY = 0.0f;
        float currentX = 0.0f;
        float currentY = 0.0f;
        float[] seg = new float[6];
        this.segments.add(new PathSegment(0, 0.0f, 0.0f, 0.0f, origIndex));
        block5: while (!pi.isDone()) {
            ++origIndex;
            indexes.add(index);
            int segType = pi.currentSegment(seg);
            switch (segType) {
                case 0: {
                    this.segments.add(new PathSegment(segType, seg[0], seg[1], this.pathLength, origIndex));
                    currentX = seg[0];
                    currentY = seg[1];
                    lastMoveX = currentX;
                    lastMoveY = currentY;
                    ++index;
                    pi.next();
                    continue block5;
                }
                case 1: {
                    this.pathLength = (float)((double)this.pathLength + Point2D.distance(currentX, currentY, seg[0], seg[1]));
                    this.segments.add(new PathSegment(segType, seg[0], seg[1], this.pathLength, origIndex));
                    currentX = seg[0];
                    currentY = seg[1];
                    ++index;
                    pi.next();
                    continue block5;
                }
                case 4: {
                    this.pathLength = (float)((double)this.pathLength + Point2D.distance(currentX, currentY, lastMoveX, lastMoveY));
                    this.segments.add(new PathSegment(1, lastMoveX, lastMoveY, this.pathLength, origIndex));
                    currentX = lastMoveX;
                    currentY = lastMoveY;
                    ++index;
                    pi.next();
                    continue block5;
                }
            }
            sspi.setPathIterator(pi, currentX, currentY);
            FlatteningPathIterator fpi = new FlatteningPathIterator(sspi, 0.01f);
            while (!fpi.isDone()) {
                segType = fpi.currentSegment(seg);
                if (segType == 1) {
                    this.pathLength = (float)((double)this.pathLength + Point2D.distance(currentX, currentY, seg[0], seg[1]));
                    this.segments.add(new PathSegment(segType, seg[0], seg[1], this.pathLength, origIndex));
                    currentX = seg[0];
                    currentY = seg[1];
                    ++index;
                }
                fpi.next();
            }
        }
        this.segmentIndexes = new int[indexes.size()];
        for (int i = 0; i < this.segmentIndexes.length; ++i) {
            this.segmentIndexes[i] = (Integer)indexes.get(i);
        }
        this.initialised = true;
    }

    public int getNumberOfSegments() {
        if (!this.initialised) {
            this.initialise();
        }
        return this.segmentIndexes.length;
    }

    public float getLengthAtSegment(int index) {
        if (!this.initialised) {
            this.initialise();
        }
        if (index <= 0) {
            return 0.0f;
        }
        if (index >= this.segmentIndexes.length) {
            return this.pathLength;
        }
        PathSegment seg = (PathSegment)this.segments.get(this.segmentIndexes[index]);
        return seg.getLength();
    }

    public int segmentAtLength(float length) {
        int upperIndex = this.findUpperIndex(length);
        if (upperIndex == -1) {
            return -1;
        }
        if (upperIndex == 0) {
            PathSegment upper = (PathSegment)this.segments.get(upperIndex);
            return upper.getIndex();
        }
        PathSegment lower = (PathSegment)this.segments.get(upperIndex - 1);
        return lower.getIndex();
    }

    public Point2D pointAtLength(int index, float proportion) {
        float end;
        if (!this.initialised) {
            this.initialise();
        }
        if (index < 0 || index >= this.segmentIndexes.length) {
            return null;
        }
        PathSegment seg = (PathSegment)this.segments.get(this.segmentIndexes[index]);
        float start = seg.getLength();
        if (index == this.segmentIndexes.length - 1) {
            end = this.pathLength;
        } else {
            seg = (PathSegment)this.segments.get(this.segmentIndexes[index + 1]);
            end = seg.getLength();
        }
        return this.pointAtLength(start + (end - start) * proportion);
    }

    public Point2D pointAtLength(float length) {
        int upperIndex = this.findUpperIndex(length);
        if (upperIndex == -1) {
            return null;
        }
        PathSegment upper = (PathSegment)this.segments.get(upperIndex);
        if (upperIndex == 0) {
            return new Point2D.Float(upper.getX(), upper.getY());
        }
        PathSegment lower = (PathSegment)this.segments.get(upperIndex - 1);
        float offset = length - lower.getLength();
        double theta = Math.atan2(upper.getY() - lower.getY(), upper.getX() - lower.getX());
        float xPoint = (float)((double)lower.getX() + (double)offset * Math.cos(theta));
        float yPoint = (float)((double)lower.getY() + (double)offset * Math.sin(theta));
        return new Point2D.Float(xPoint, yPoint);
    }

    public float angleAtLength(int index, float proportion) {
        float end;
        if (!this.initialised) {
            this.initialise();
        }
        if (index < 0 || index >= this.segmentIndexes.length) {
            return 0.0f;
        }
        PathSegment seg = (PathSegment)this.segments.get(this.segmentIndexes[index]);
        float start = seg.getLength();
        if (index == this.segmentIndexes.length - 1) {
            end = this.pathLength;
        } else {
            seg = (PathSegment)this.segments.get(this.segmentIndexes[index + 1]);
            end = seg.getLength();
        }
        return this.angleAtLength(start + (end - start) * proportion);
    }

    public float angleAtLength(float length) {
        int upperIndex = this.findUpperIndex(length);
        if (upperIndex == -1) {
            return 0.0f;
        }
        PathSegment upper = (PathSegment)this.segments.get(upperIndex);
        if (upperIndex == 0) {
            upperIndex = 1;
        }
        PathSegment lower = (PathSegment)this.segments.get(upperIndex - 1);
        return (float)Math.atan2(upper.getY() - lower.getY(), upper.getX() - lower.getX());
    }

    public int findUpperIndex(float length) {
        PathSegment ps;
        if (!this.initialised) {
            this.initialise();
        }
        if (length < 0.0f || length > this.pathLength) {
            return -1;
        }
        int lb = 0;
        int ub = this.segments.size() - 1;
        while (lb != ub) {
            int curr = lb + ub >> 1;
            PathSegment ps2 = (PathSegment)this.segments.get(curr);
            if (ps2.getLength() >= length) {
                ub = curr;
                continue;
            }
            lb = curr + 1;
        }
        while ((ps = (PathSegment)this.segments.get(ub)).getSegType() == 0 && ub != this.segments.size() - 1) {
            ++ub;
        }
        int upperIndex = -1;
        int numSegments = this.segments.size();
        for (int currentIndex = 0; upperIndex <= 0 && currentIndex < numSegments; ++currentIndex) {
            PathSegment ps3 = (PathSegment)this.segments.get(currentIndex);
            if (!(ps3.getLength() >= length) || ps3.getSegType() == 0) continue;
            upperIndex = currentIndex;
        }
        return upperIndex;
    }

    protected static class PathSegment {
        protected final int segType;
        protected float x;
        protected float y;
        protected float length;
        protected int index;

        PathSegment(int segType, float x, float y, float len, int idx) {
            this.segType = segType;
            this.x = x;
            this.y = y;
            this.length = len;
            this.index = idx;
        }

        public int getSegType() {
            return this.segType;
        }

        public float getX() {
            return this.x;
        }

        public void setX(float v) {
            this.x = v;
        }

        public float getY() {
            return this.y;
        }

        public void setY(float v) {
            this.y = v;
        }

        public float getLength() {
            return this.length;
        }

        public void setLength(float v) {
            this.length = v;
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int v) {
            this.index = v;
        }
    }

    protected static class SingleSegmentPathIterator
    implements PathIterator {
        protected PathIterator it;
        protected boolean done;
        protected boolean moveDone;
        protected double x;
        protected double y;

        protected SingleSegmentPathIterator() {
        }

        public void setPathIterator(PathIterator it, double x, double y) {
            this.it = it;
            this.x = x;
            this.y = y;
            this.done = false;
            this.moveDone = false;
        }

        @Override
        public int currentSegment(double[] coords) {
            int type = this.it.currentSegment(coords);
            if (!this.moveDone) {
                coords[0] = this.x;
                coords[1] = this.y;
                return 0;
            }
            return type;
        }

        @Override
        public int currentSegment(float[] coords) {
            int type = this.it.currentSegment(coords);
            if (!this.moveDone) {
                coords[0] = (float)this.x;
                coords[1] = (float)this.y;
                return 0;
            }
            return type;
        }

        @Override
        public int getWindingRule() {
            return this.it.getWindingRule();
        }

        @Override
        public boolean isDone() {
            return this.done || this.it.isDone();
        }

        @Override
        public void next() {
            if (!this.done) {
                if (!this.moveDone) {
                    this.moveDone = true;
                } else {
                    this.it.next();
                    this.done = true;
                }
            }
        }
    }
}

