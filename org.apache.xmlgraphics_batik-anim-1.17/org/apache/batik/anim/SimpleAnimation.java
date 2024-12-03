/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.Cubic
 */
package org.apache.batik.anim;

import java.awt.geom.Point2D;
import org.apache.batik.anim.InterpolatingAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.ext.awt.geom.Cubic;

public class SimpleAnimation
extends InterpolatingAnimation {
    protected AnimatableValue[] values;
    protected AnimatableValue from;
    protected AnimatableValue to;
    protected AnimatableValue by;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SimpleAnimation(TimedElement timedElement, AnimatableElement animatableElement, int calcMode, float[] keyTimes, float[] keySplines, boolean additive, boolean cumulative, AnimatableValue[] values, AnimatableValue from, AnimatableValue to, AnimatableValue by) {
        super(timedElement, animatableElement, calcMode, keyTimes, keySplines, additive, cumulative);
        this.from = from;
        this.to = to;
        this.by = by;
        if (values == null) {
            if (from != null) {
                values = new AnimatableValue[2];
                values[0] = from;
                if (to != null) {
                    values[1] = to;
                } else {
                    if (by == null) throw timedElement.createException("values.to.by.missing", new Object[]{null});
                    values[1] = from.interpolate(null, null, 0.0f, by, 1);
                }
            } else if (to != null) {
                values = new AnimatableValue[]{animatableElement.getUnderlyingValue(), to};
                this.cumulative = false;
                this.toAnimation = true;
            } else {
                if (by == null) throw timedElement.createException("values.to.by.missing", new Object[]{null});
                this.additive = true;
                values = new AnimatableValue[]{by.getZeroValue(), by};
            }
        }
        this.values = values;
        if (this.keyTimes != null && calcMode != 2) {
            if (this.keyTimes.length != values.length) {
                throw timedElement.createException("attribute.malformed", new Object[]{null, "keyTimes"});
            }
        } else if (calcMode == 1 || calcMode == 3 || calcMode == 2 && !values[0].canPace()) {
            int count = values.length == 1 ? 2 : values.length;
            this.keyTimes = new float[count];
            for (int i = 0; i < count; ++i) {
                this.keyTimes[i] = (float)i / (float)(count - 1);
            }
        } else if (calcMode == 0) {
            int count = values.length;
            this.keyTimes = new float[count];
            for (int i = 0; i < count; ++i) {
                this.keyTimes[i] = (float)i / (float)count;
            }
        } else {
            int count = values.length;
            float[] cumulativeDistances = new float[count];
            cumulativeDistances[0] = 0.0f;
            for (int i = 1; i < count; ++i) {
                cumulativeDistances[i] = cumulativeDistances[i - 1] + values[i - 1].distanceTo(values[i]);
            }
            float totalLength = cumulativeDistances[count - 1];
            this.keyTimes = new float[count];
            this.keyTimes[0] = 0.0f;
            for (int i = 1; i < count - 1; ++i) {
                this.keyTimes[i] = cumulativeDistances[i] / totalLength;
            }
            this.keyTimes[count - 1] = 1.0f;
        }
        if (calcMode != 3 || keySplines.length == (this.keyTimes.length - 1) * 4) return;
        throw timedElement.createException("attribute.malformed", new Object[]{null, "keySplines"});
    }

    @Override
    protected void sampledAtUnitTime(float unitTime, int repeatIteration) {
        AnimatableValue nextValue;
        AnimatableValue value;
        float interpolation = 0.0f;
        if (unitTime != 1.0f) {
            int keyTimeIndex;
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes.length - 1 && unitTime >= this.keyTimes[keyTimeIndex + 1]; ++keyTimeIndex) {
            }
            value = this.values[keyTimeIndex];
            if (this.calcMode == 1 || this.calcMode == 2 || this.calcMode == 3) {
                nextValue = this.values[keyTimeIndex + 1];
                interpolation = (unitTime - this.keyTimes[keyTimeIndex]) / (this.keyTimes[keyTimeIndex + 1] - this.keyTimes[keyTimeIndex]);
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
            } else {
                nextValue = null;
            }
        } else {
            value = this.values[this.values.length - 1];
            nextValue = null;
        }
        AnimatableValue accumulation = this.cumulative ? this.values[this.values.length - 1] : null;
        this.value = value.interpolate(this.value, nextValue, interpolation, accumulation, repeatIteration);
        if (this.value.hasChanged()) {
            this.markDirty();
        }
    }
}

