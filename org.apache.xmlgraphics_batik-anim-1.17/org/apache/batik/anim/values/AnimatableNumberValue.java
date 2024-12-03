/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableNumberValue
extends AnimatableValue {
    protected float value;

    protected AnimatableNumberValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableNumberValue(AnimationTarget target, float v) {
        super(target);
        this.value = v;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableNumberValue res = result == null ? new AnimatableNumberValue(this.target) : (AnimatableNumberValue)result;
        float v = this.value;
        if (to != null) {
            AnimatableNumberValue toNumber = (AnimatableNumberValue)to;
            v += interpolation * (toNumber.value - this.value);
        }
        if (accumulation != null) {
            AnimatableNumberValue accNumber = (AnimatableNumberValue)accumulation;
            v += (float)multiplier * accNumber.value;
        }
        if (res.value != v) {
            res.value = v;
            res.hasChanged = true;
        }
        return res;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public boolean canPace() {
        return true;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        AnimatableNumberValue o = (AnimatableNumberValue)other;
        return Math.abs(this.value - o.value);
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableNumberValue(this.target, 0.0f);
    }

    @Override
    public String getCssText() {
        return AnimatableNumberValue.formatNumber(this.value);
    }
}

