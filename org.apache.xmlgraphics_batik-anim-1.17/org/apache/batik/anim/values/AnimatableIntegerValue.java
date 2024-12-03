/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableIntegerValue
extends AnimatableValue {
    protected int value;

    protected AnimatableIntegerValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableIntegerValue(AnimationTarget target, int v) {
        super(target);
        this.value = v;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableIntegerValue res = result == null ? new AnimatableIntegerValue(this.target) : (AnimatableIntegerValue)result;
        int v = this.value;
        if (to != null) {
            AnimatableIntegerValue toInteger = (AnimatableIntegerValue)to;
            v = (int)((float)v + ((float)this.value + interpolation * (float)(toInteger.getValue() - this.value)));
        }
        if (accumulation != null) {
            AnimatableIntegerValue accInteger = (AnimatableIntegerValue)accumulation;
            v += multiplier * accInteger.getValue();
        }
        if (res.value != v) {
            res.value = v;
            res.hasChanged = true;
        }
        return res;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean canPace() {
        return true;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        AnimatableIntegerValue o = (AnimatableIntegerValue)other;
        return Math.abs(this.value - o.value);
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableIntegerValue(this.target, 0);
    }

    @Override
    public String getCssText() {
        return Integer.toString(this.value);
    }
}

