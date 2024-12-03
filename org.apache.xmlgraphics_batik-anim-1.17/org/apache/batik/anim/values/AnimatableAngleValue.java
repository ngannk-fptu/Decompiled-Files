/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableNumberValue;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableAngleValue
extends AnimatableNumberValue {
    protected static final String[] UNITS = new String[]{"", "", "deg", "rad", "grad"};
    protected short unit;

    public AnimatableAngleValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableAngleValue(AnimationTarget target, float v, short unit) {
        super(target, v);
        this.unit = unit;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableAngleValue res = result == null ? new AnimatableAngleValue(this.target) : (AnimatableAngleValue)result;
        float v = this.value;
        short u = this.unit;
        if (to != null) {
            AnimatableAngleValue toAngle = (AnimatableAngleValue)to;
            if (toAngle.unit != u) {
                v = AnimatableAngleValue.rad(v, u);
                v += interpolation * (AnimatableAngleValue.rad(toAngle.value, toAngle.unit) - v);
                u = 3;
            } else {
                v += interpolation * (toAngle.value - v);
            }
        }
        if (accumulation != null) {
            AnimatableAngleValue accAngle = (AnimatableAngleValue)accumulation;
            if (accAngle.unit != u) {
                v += (float)multiplier * AnimatableAngleValue.rad(accAngle.value, accAngle.unit);
                u = 3;
            } else {
                v += (float)multiplier * accAngle.value;
            }
        }
        if (res.value != v || res.unit != u) {
            res.value = v;
            res.unit = u;
            res.hasChanged = true;
        }
        return res;
    }

    public short getUnit() {
        return this.unit;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        AnimatableAngleValue o = (AnimatableAngleValue)other;
        return Math.abs(AnimatableAngleValue.rad(this.value, this.unit) - AnimatableAngleValue.rad(o.value, o.unit));
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableAngleValue(this.target, 0.0f, 1);
    }

    @Override
    public String getCssText() {
        return super.getCssText() + UNITS[this.unit];
    }

    public static float rad(float v, short unit) {
        switch (unit) {
            case 3: {
                return v;
            }
            case 4: {
                return (float)Math.PI * v / 200.0f;
            }
        }
        return (float)Math.PI * v / 180.0f;
    }
}

