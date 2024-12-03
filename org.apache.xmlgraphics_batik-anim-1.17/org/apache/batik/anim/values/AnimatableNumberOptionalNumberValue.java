/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableNumberOptionalNumberValue
extends AnimatableValue {
    protected float number;
    protected boolean hasOptionalNumber;
    protected float optionalNumber;

    protected AnimatableNumberOptionalNumberValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableNumberOptionalNumberValue(AnimationTarget target, float n) {
        super(target);
        this.number = n;
    }

    public AnimatableNumberOptionalNumberValue(AnimationTarget target, float n, float on) {
        super(target);
        this.number = n;
        this.optionalNumber = on;
        this.hasOptionalNumber = true;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        boolean newHasOptionalNumber;
        float newOptionalNumber;
        float newNumber;
        AnimatableNumberOptionalNumberValue res = result == null ? new AnimatableNumberOptionalNumberValue(this.target) : (AnimatableNumberOptionalNumberValue)result;
        if (to != null && (double)interpolation >= 0.5) {
            AnimatableNumberOptionalNumberValue toValue = (AnimatableNumberOptionalNumberValue)to;
            newNumber = toValue.number;
            newOptionalNumber = toValue.optionalNumber;
            newHasOptionalNumber = toValue.hasOptionalNumber;
        } else {
            newNumber = this.number;
            newOptionalNumber = this.optionalNumber;
            newHasOptionalNumber = this.hasOptionalNumber;
        }
        if (res.number != newNumber || res.hasOptionalNumber != newHasOptionalNumber || res.optionalNumber != newOptionalNumber) {
            res.number = this.number;
            res.optionalNumber = this.optionalNumber;
            res.hasOptionalNumber = this.hasOptionalNumber;
            res.hasChanged = true;
        }
        return res;
    }

    public float getNumber() {
        return this.number;
    }

    public boolean hasOptionalNumber() {
        return this.hasOptionalNumber;
    }

    public float getOptionalNumber() {
        return this.optionalNumber;
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
        if (this.hasOptionalNumber) {
            return new AnimatableNumberOptionalNumberValue(this.target, 0.0f, 0.0f);
        }
        return new AnimatableNumberOptionalNumberValue(this.target, 0.0f);
    }

    @Override
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        sb.append(AnimatableNumberOptionalNumberValue.formatNumber(this.number));
        if (this.hasOptionalNumber) {
            sb.append(' ');
            sb.append(AnimatableNumberOptionalNumberValue.formatNumber(this.optionalNumber));
        }
        return sb.toString();
    }
}

