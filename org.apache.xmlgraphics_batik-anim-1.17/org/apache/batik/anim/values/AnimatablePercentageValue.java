/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableNumberValue;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatablePercentageValue
extends AnimatableNumberValue {
    protected AnimatablePercentageValue(AnimationTarget target) {
        super(target);
    }

    public AnimatablePercentageValue(AnimationTarget target, float v) {
        super(target, v);
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        if (result == null) {
            result = new AnimatablePercentageValue(this.target);
        }
        return super.interpolate(result, to, interpolation, accumulation, multiplier);
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatablePercentageValue(this.target, 0.0f);
    }

    @Override
    public String getCssText() {
        return super.getCssText() + "%";
    }
}

