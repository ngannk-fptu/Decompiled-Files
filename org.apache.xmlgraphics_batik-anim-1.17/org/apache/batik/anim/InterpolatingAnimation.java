/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.Cubic
 */
package org.apache.batik.anim;

import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.ext.awt.geom.Cubic;

public abstract class InterpolatingAnimation
extends AbstractAnimation {
    protected int calcMode;
    protected float[] keyTimes;
    protected float[] keySplines;
    protected Cubic[] keySplineCubics;
    protected boolean additive;
    protected boolean cumulative;

    public InterpolatingAnimation(TimedElement timedElement, AnimatableElement animatableElement, int calcMode, float[] keyTimes, float[] keySplines, boolean additive, boolean cumulative) {
        super(timedElement, animatableElement);
        this.calcMode = calcMode;
        this.keyTimes = keyTimes;
        this.keySplines = keySplines;
        this.additive = additive;
        this.cumulative = cumulative;
        if (calcMode == 3) {
            if (keySplines == null || keySplines.length % 4 != 0) {
                throw timedElement.createException("attribute.malformed", new Object[]{null, "keySplines"});
            }
            this.keySplineCubics = new Cubic[keySplines.length / 4];
            for (int i = 0; i < keySplines.length / 4; ++i) {
                this.keySplineCubics[i] = new Cubic(0.0, 0.0, (double)keySplines[i * 4], (double)keySplines[i * 4 + 1], (double)keySplines[i * 4 + 2], (double)keySplines[i * 4 + 3], 1.0, 1.0);
            }
        }
        if (keyTimes != null) {
            boolean invalidKeyTimes = false;
            if ((calcMode == 1 || calcMode == 3 || calcMode == 2) && (keyTimes.length < 2 || keyTimes[0] != 0.0f || keyTimes[keyTimes.length - 1] != 1.0f) || calcMode == 0 && (keyTimes.length == 0 || keyTimes[0] != 0.0f)) {
                invalidKeyTimes = true;
            }
            if (!invalidKeyTimes) {
                for (int i = 1; i < keyTimes.length; ++i) {
                    if (!(keyTimes[i] < 0.0f) && !(keyTimes[1] > 1.0f) && !(keyTimes[i] < keyTimes[i - 1])) continue;
                    invalidKeyTimes = true;
                    break;
                }
            }
            if (invalidKeyTimes) {
                throw timedElement.createException("attribute.malformed", new Object[]{null, "keyTimes"});
            }
        }
    }

    @Override
    protected boolean willReplace() {
        return !this.additive;
    }

    @Override
    protected void sampledLastValue(int repeatIteration) {
        this.sampledAtUnitTime(1.0f, repeatIteration);
    }

    @Override
    protected void sampledAt(float simpleTime, float simpleDur, int repeatIteration) {
        float unitTime = simpleDur == Float.POSITIVE_INFINITY ? 0.0f : simpleTime / simpleDur;
        this.sampledAtUnitTime(unitTime, repeatIteration);
    }

    protected abstract void sampledAtUnitTime(float var1, int var2);
}

