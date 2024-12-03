/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableNumberListValue;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatablePointListValue
extends AnimatableNumberListValue {
    protected AnimatablePointListValue(AnimationTarget target) {
        super(target);
    }

    public AnimatablePointListValue(AnimationTarget target, float[] numbers) {
        super(target, numbers);
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        if (result == null) {
            result = new AnimatablePointListValue(this.target);
        }
        return super.interpolate(result, to, interpolation, accumulation, multiplier);
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
        float[] ns = new float[this.numbers.length];
        return new AnimatablePointListValue(this.target, ns);
    }
}

