/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableNumberListValue
extends AnimatableValue {
    protected float[] numbers;

    protected AnimatableNumberListValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableNumberListValue(AnimationTarget target, float[] numbers) {
        super(target);
        this.numbers = numbers;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableNumberListValue res;
        AnimatableNumberListValue toNumList = (AnimatableNumberListValue)to;
        AnimatableNumberListValue accNumList = (AnimatableNumberListValue)accumulation;
        boolean hasTo = to != null;
        boolean hasAcc = accumulation != null;
        boolean canInterpolate = !(hasTo && toNumList.numbers.length != this.numbers.length || hasAcc && accNumList.numbers.length != this.numbers.length);
        float[] baseValues = !canInterpolate && hasTo && (double)interpolation >= 0.5 ? toNumList.numbers : this.numbers;
        int len = baseValues.length;
        if (result == null) {
            res = new AnimatableNumberListValue(this.target);
            res.numbers = new float[len];
        } else {
            res = (AnimatableNumberListValue)result;
            if (res.numbers == null || res.numbers.length != len) {
                res.numbers = new float[len];
            }
        }
        for (int i = 0; i < len; ++i) {
            float newValue = baseValues[i];
            if (canInterpolate) {
                if (hasTo) {
                    newValue += interpolation * (toNumList.numbers[i] - newValue);
                }
                if (hasAcc) {
                    newValue += (float)multiplier * accNumList.numbers[i];
                }
            }
            if (res.numbers[i] == newValue) continue;
            res.numbers[i] = newValue;
            res.hasChanged = true;
        }
        return res;
    }

    public float[] getNumbers() {
        return this.numbers;
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
        return new AnimatableNumberListValue(this.target, ns);
    }

    @Override
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.numbers[0]);
        for (int i = 1; i < this.numbers.length; ++i) {
            sb.append(' ');
            sb.append(this.numbers[i]);
        }
        return sb.toString();
    }
}

