/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.Cubic
 *  org.apache.batik.ext.awt.geom.ExtendedGeneralPath
 *  org.apache.batik.ext.awt.geom.ExtendedPathIterator
 *  org.apache.batik.ext.awt.geom.PathLength
 */
package org.apache.batik.anim;

import java.awt.Shape;
import java.awt.geom.Point2D;
import org.apache.batik.anim.InterpolatingAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableAngleValue;
import org.apache.batik.anim.values.AnimatableMotionPointValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.ext.awt.geom.Cubic;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.ext.awt.geom.ExtendedPathIterator;
import org.apache.batik.ext.awt.geom.PathLength;

public class MotionAnimation
extends InterpolatingAnimation {
    protected ExtendedGeneralPath path;
    protected PathLength pathLength;
    protected float[] keyPoints;
    protected boolean rotateAuto;
    protected boolean rotateAutoReverse;
    protected float rotateAngle;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public MotionAnimation(TimedElement timedElement, AnimatableElement animatableElement, int calcMode, float[] keyTimes, float[] keySplines, boolean additive, boolean cumulative, AnimatableValue[] values, AnimatableValue from, AnimatableValue to, AnimatableValue by, ExtendedGeneralPath path, float[] keyPoints, boolean rotateAuto, boolean rotateAutoReverse, float rotateAngle, short rotateAngleUnit) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines, additive, cumulative);
        int i;
        int j;
        int i2;
        this.rotateAuto = rotateAuto;
        this.rotateAutoReverse = rotateAutoReverse;
        this.rotateAngle = AnimatableAngleValue.rad(rotateAngle, rotateAngleUnit);
        if (path == null) {
            path = new ExtendedGeneralPath();
            if (values == null || values.length == 0) {
                if (from != null) {
                    AnimatableMotionPointValue fromPt = (AnimatableMotionPointValue)from;
                    float x = fromPt.getX();
                    float y = fromPt.getY();
                    path.moveTo(x, y);
                    if (to != null) {
                        AnimatableMotionPointValue toPt = (AnimatableMotionPointValue)to;
                        path.lineTo(toPt.getX(), toPt.getY());
                    } else {
                        if (by == null) throw timedElement.createException("values.to.by.path.missing", new Object[]{null});
                        AnimatableMotionPointValue byPt = (AnimatableMotionPointValue)by;
                        path.lineTo(x + byPt.getX(), y + byPt.getY());
                    }
                } else if (to != null) {
                    AnimatableMotionPointValue unPt = (AnimatableMotionPointValue)animatableElement.getUnderlyingValue();
                    AnimatableMotionPointValue toPt = (AnimatableMotionPointValue)to;
                    path.moveTo(unPt.getX(), unPt.getY());
                    path.lineTo(toPt.getX(), toPt.getY());
                    this.cumulative = false;
                } else {
                    if (by == null) throw timedElement.createException("values.to.by.path.missing", new Object[]{null});
                    AnimatableMotionPointValue byPt = (AnimatableMotionPointValue)by;
                    path.moveTo(0.0f, 0.0f);
                    path.lineTo(byPt.getX(), byPt.getY());
                    this.additive = true;
                }
            } else {
                AnimatableMotionPointValue pt = (AnimatableMotionPointValue)values[0];
                path.moveTo(pt.getX(), pt.getY());
                for (int i3 = 1; i3 < values.length; ++i3) {
                    pt = (AnimatableMotionPointValue)values[i3];
                    path.lineTo(pt.getX(), pt.getY());
                }
            }
        }
        this.path = path;
        this.pathLength = new PathLength((Shape)path);
        int segments = 0;
        ExtendedPathIterator epi = path.getExtendedPathIterator();
        while (!epi.isDone()) {
            int type = epi.currentSegment();
            if (type != 0) {
                ++segments;
            }
            epi.next();
        }
        int count = keyPoints == null ? segments + 1 : keyPoints.length;
        float totalLength = this.pathLength.lengthOfPath();
        if (this.keyTimes != null && calcMode != 2) {
            if (this.keyTimes.length != count) {
                throw timedElement.createException("attribute.malformed", new Object[]{null, "keyTimes"});
            }
        } else if (calcMode == 1 || calcMode == 3) {
            this.keyTimes = new float[count];
            for (i2 = 0; i2 < count; ++i2) {
                this.keyTimes[i2] = (float)i2 / (float)(count - 1);
            }
        } else if (calcMode == 0) {
            this.keyTimes = new float[count];
            for (i2 = 0; i2 < count; ++i2) {
                this.keyTimes[i2] = (float)i2 / (float)count;
            }
        } else {
            epi = path.getExtendedPathIterator();
            this.keyTimes = new float[count];
            j = 0;
            for (i = 0; i < count - 1; ++i) {
                while (epi.currentSegment() == 0) {
                    ++j;
                    epi.next();
                }
                this.keyTimes[i] = this.pathLength.getLengthAtSegment(j) / totalLength;
                ++j;
                epi.next();
            }
            this.keyTimes[count - 1] = 1.0f;
        }
        if (keyPoints != null) {
            if (keyPoints.length != this.keyTimes.length) {
                throw timedElement.createException("attribute.malformed", new Object[]{null, "keyPoints"});
            }
        } else {
            epi = path.getExtendedPathIterator();
            keyPoints = new float[count];
            j = 0;
            for (i = 0; i < count - 1; ++i) {
                while (epi.currentSegment() == 0) {
                    ++j;
                    epi.next();
                }
                keyPoints[i] = this.pathLength.getLengthAtSegment(j) / totalLength;
                ++j;
                epi.next();
            }
            keyPoints[count - 1] = 1.0f;
        }
        this.keyPoints = keyPoints;
    }

    @Override
    protected void sampledAtUnitTime(float unitTime, int repeatIteration) {
        AnimatableMotionPointValue accumulation;
        float ang;
        AnimatableMotionPointValue value;
        float interpolation = 0.0f;
        if (unitTime != 1.0f) {
            float ang2;
            int keyTimeIndex;
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes.length - 1 && unitTime >= this.keyTimes[keyTimeIndex + 1]; ++keyTimeIndex) {
            }
            if (keyTimeIndex == this.keyTimes.length - 1 && this.calcMode == 0) {
                keyTimeIndex = this.keyTimes.length - 2;
                interpolation = 1.0f;
            } else if (this.calcMode == 1 || this.calcMode == 2 || this.calcMode == 3) {
                interpolation = unitTime == 0.0f ? 0.0f : (unitTime - this.keyTimes[keyTimeIndex]) / (this.keyTimes[keyTimeIndex + 1] - this.keyTimes[keyTimeIndex]);
                if (this.calcMode == 3 && unitTime != 0.0f) {
                    float t;
                    Point2D.Double p;
                    double x;
                    Cubic c = this.keySplineCubics[keyTimeIndex];
                    float tolerance = 0.001f;
                    float min = 0.0f;
                    float max = 1.0f;
                    while (!(Math.abs((x = (p = c.eval((double)(t = (min + max) / 2.0f))).getX()) - (double)interpolation) < (double)tolerance)) {
                        if (x < (double)interpolation) {
                            min = t;
                            continue;
                        }
                        max = t;
                    }
                    interpolation = (float)p.getY();
                }
            }
            float point = this.keyPoints[keyTimeIndex];
            if (interpolation != 0.0f) {
                point += interpolation * (this.keyPoints[keyTimeIndex + 1] - this.keyPoints[keyTimeIndex]);
            }
            Point2D p = this.pathLength.pointAtLength(point *= this.pathLength.lengthOfPath());
            if (this.rotateAuto) {
                ang2 = this.pathLength.angleAtLength(point);
                if (this.rotateAutoReverse) {
                    ang2 = (float)((double)ang2 + Math.PI);
                }
            } else {
                ang2 = this.rotateAngle;
            }
            value = new AnimatableMotionPointValue(null, (float)p.getX(), (float)p.getY(), ang2);
        } else {
            Point2D p = this.pathLength.pointAtLength(this.pathLength.lengthOfPath());
            if (this.rotateAuto) {
                ang = this.pathLength.angleAtLength(this.pathLength.lengthOfPath());
                if (this.rotateAutoReverse) {
                    ang = (float)((double)ang + Math.PI);
                }
            } else {
                ang = this.rotateAngle;
            }
            value = new AnimatableMotionPointValue(null, (float)p.getX(), (float)p.getY(), ang);
        }
        if (this.cumulative) {
            Point2D p = this.pathLength.pointAtLength(this.pathLength.lengthOfPath());
            if (this.rotateAuto) {
                ang = this.pathLength.angleAtLength(this.pathLength.lengthOfPath());
                if (this.rotateAutoReverse) {
                    ang = (float)((double)ang + Math.PI);
                }
            } else {
                ang = this.rotateAngle;
            }
            accumulation = new AnimatableMotionPointValue(null, (float)p.getX(), (float)p.getY(), ang);
        } else {
            accumulation = null;
        }
        this.value = ((AnimatableValue)value).interpolate(this.value, null, interpolation, accumulation, repeatIteration);
        if (this.value.hasChanged()) {
            this.markDirty();
        }
    }
}

