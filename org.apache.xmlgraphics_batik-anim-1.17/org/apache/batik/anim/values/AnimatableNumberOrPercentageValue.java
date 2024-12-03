/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableNumberValue;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableNumberOrPercentageValue
extends AnimatableNumberValue {
    protected boolean isPercentage;

    protected AnimatableNumberOrPercentageValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableNumberOrPercentageValue(AnimationTarget target, float n) {
        super(target, n);
    }

    public AnimatableNumberOrPercentageValue(AnimationTarget target, float n, boolean isPercentage) {
        super(target, n);
        this.isPercentage = isPercentage;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        boolean newIsPercentage;
        float newValue;
        AnimatableNumberOrPercentageValue res = result == null ? new AnimatableNumberOrPercentageValue(this.target) : (AnimatableNumberOrPercentageValue)result;
        AnimatableNumberOrPercentageValue toValue = (AnimatableNumberOrPercentageValue)to;
        AnimatableNumberOrPercentageValue accValue = (AnimatableNumberOrPercentageValue)accumulation;
        if (to != null) {
            if (toValue.isPercentage == this.isPercentage) {
                newValue = this.value + interpolation * (toValue.value - this.value);
                newIsPercentage = this.isPercentage;
            } else if ((double)interpolation >= 0.5) {
                newValue = toValue.value;
                newIsPercentage = toValue.isPercentage;
            } else {
                newValue = this.value;
                newIsPercentage = this.isPercentage;
            }
        } else {
            newValue = this.value;
            newIsPercentage = this.isPercentage;
        }
        if (accumulation != null && accValue.isPercentage == newIsPercentage) {
            newValue += (float)multiplier * accValue.value;
        }
        if (res.value != newValue || res.isPercentage != newIsPercentage) {
            res.value = newValue;
            res.isPercentage = newIsPercentage;
            res.hasChanged = true;
        }
        return res;
    }

    public boolean isPercentage() {
        return this.isPercentage;
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
        return new AnimatableNumberOrPercentageValue(this.target, 0.0f, this.isPercentage);
    }

    @Override
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        sb.append(AnimatableNumberOrPercentageValue.formatNumber(this.value));
        if (this.isPercentage) {
            sb.append('%');
        }
        return sb.toString();
    }
}

