/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatablePreserveAspectRatioValue
extends AnimatableValue {
    protected static final String[] ALIGN_VALUES = new String[]{null, "none", "xMinYMin", "xMidYMin", "xMaxYMin", "xMinYMid", "xMidYMid", "xMaxYMid", "xMinYMax", "xMidYMax", "xMaxYMax"};
    protected static final String[] MEET_OR_SLICE_VALUES = new String[]{null, "meet", "slice"};
    protected short align;
    protected short meetOrSlice;

    protected AnimatablePreserveAspectRatioValue(AnimationTarget target) {
        super(target);
    }

    public AnimatablePreserveAspectRatioValue(AnimationTarget target, short align, short meetOrSlice) {
        super(target);
        this.align = align;
        this.meetOrSlice = meetOrSlice;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        short newMeetOrSlice;
        short newAlign;
        AnimatablePreserveAspectRatioValue res = result == null ? new AnimatablePreserveAspectRatioValue(this.target) : (AnimatablePreserveAspectRatioValue)result;
        if (to != null && (double)interpolation >= 0.5) {
            AnimatablePreserveAspectRatioValue toValue = (AnimatablePreserveAspectRatioValue)to;
            newAlign = toValue.align;
            newMeetOrSlice = toValue.meetOrSlice;
        } else {
            newAlign = this.align;
            newMeetOrSlice = this.meetOrSlice;
        }
        if (res.align != newAlign || res.meetOrSlice != newMeetOrSlice) {
            res.align = this.align;
            res.meetOrSlice = this.meetOrSlice;
            res.hasChanged = true;
        }
        return res;
    }

    public short getAlign() {
        return this.align;
    }

    public short getMeetOrSlice() {
        return this.meetOrSlice;
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
        return new AnimatablePreserveAspectRatioValue(this.target, 1, 1);
    }

    @Override
    public String toStringRep() {
        if (this.align < 1 || this.align > 10) {
            return null;
        }
        String value = ALIGN_VALUES[this.align];
        if (this.align == 1) {
            return value;
        }
        if (this.meetOrSlice < 1 || this.meetOrSlice > 2) {
            return null;
        }
        return value + ' ' + MEET_OR_SLICE_VALUES[this.meetOrSlice];
    }
}

