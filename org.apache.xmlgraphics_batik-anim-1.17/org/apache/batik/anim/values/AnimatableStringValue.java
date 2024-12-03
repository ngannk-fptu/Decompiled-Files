/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableStringValue
extends AnimatableValue {
    protected String string;

    protected AnimatableStringValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableStringValue(AnimationTarget target, String s) {
        super(target);
        this.string = s;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        String newString;
        AnimatableStringValue res = result == null ? new AnimatableStringValue(this.target) : (AnimatableStringValue)result;
        if (to != null && (double)interpolation >= 0.5) {
            AnimatableStringValue toValue = (AnimatableStringValue)to;
            newString = toValue.string;
        } else {
            newString = this.string;
        }
        if (res.string == null || !res.string.equals(newString)) {
            res.string = newString;
            res.hasChanged = true;
        }
        return res;
    }

    public String getString() {
        return this.string;
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
        return new AnimatableStringValue(this.target, "");
    }

    @Override
    public String getCssText() {
        return this.string;
    }
}

