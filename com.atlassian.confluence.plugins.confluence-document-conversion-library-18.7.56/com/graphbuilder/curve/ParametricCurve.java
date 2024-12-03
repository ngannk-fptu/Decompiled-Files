/*
 * Decompiled with CFR 0.152.
 */
package com.graphbuilder.curve;

import com.graphbuilder.curve.ControlPath;
import com.graphbuilder.curve.Curve;
import com.graphbuilder.curve.GroupIterator;

public abstract class ParametricCurve
extends Curve {
    public ParametricCurve(ControlPath cp, GroupIterator gp) {
        super(cp, gp);
    }

    protected abstract void eval(double[] var1);

    public abstract int getSampleLimit();
}

