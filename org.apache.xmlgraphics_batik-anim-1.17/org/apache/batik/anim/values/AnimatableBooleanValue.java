/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableBooleanValue
extends AnimatableValue {
    protected boolean value;

    protected AnimatableBooleanValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableBooleanValue(AnimationTarget target, boolean b) {
        super(target);
        this.value = b;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        boolean newValue;
        AnimatableBooleanValue res = result == null ? new AnimatableBooleanValue(this.target) : (AnimatableBooleanValue)result;
        if (to != null && (double)interpolation >= 0.5) {
            AnimatableBooleanValue toValue = (AnimatableBooleanValue)to;
            newValue = toValue.value;
        } else {
            newValue = this.value;
        }
        if (res.value != newValue) {
            res.value = newValue;
            res.hasChanged = true;
        }
        return res;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public boolean canPace() {
        return false;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        return 0.0f;
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableBooleanValue(this.target, false);
    }

    @Override
    public String getCssText() {
        return this.value ? "true" : "false";
    }
}

