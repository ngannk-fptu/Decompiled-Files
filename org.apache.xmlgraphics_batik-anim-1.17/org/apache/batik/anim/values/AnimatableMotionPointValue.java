/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableMotionPointValue
extends AnimatableValue {
    protected float x;
    protected float y;
    protected float angle;

    protected AnimatableMotionPointValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableMotionPointValue(AnimationTarget target, float x, float y, float angle) {
        super(target);
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableMotionPointValue res = result == null ? new AnimatableMotionPointValue(this.target) : (AnimatableMotionPointValue)result;
        float newX = this.x;
        float newY = this.y;
        float newAngle = this.angle;
        int angleCount = 1;
        if (to != null) {
            AnimatableMotionPointValue toValue = (AnimatableMotionPointValue)to;
            newX += interpolation * (toValue.x - this.x);
            newY += interpolation * (toValue.y - this.y);
            newAngle += toValue.angle;
            ++angleCount;
        }
        if (accumulation != null && multiplier != 0) {
            AnimatableMotionPointValue accValue = (AnimatableMotionPointValue)accumulation;
            newX += (float)multiplier * accValue.x;
            newY += (float)multiplier * accValue.y;
            newAngle += accValue.angle;
            ++angleCount;
        }
        if (res.x != newX || res.y != newY || res.angle != (newAngle /= (float)angleCount)) {
            res.x = newX;
            res.y = newY;
            res.angle = newAngle;
            res.hasChanged = true;
        }
        return res;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getAngle() {
        return this.angle;
    }

    @Override
    public boolean canPace() {
        return true;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        AnimatableMotionPointValue o = (AnimatableMotionPointValue)other;
        float dx = this.x - o.x;
        float dy = this.y - o.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableMotionPointValue(this.target, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public String toStringRep() {
        StringBuffer sb = new StringBuffer();
        sb.append(AnimatableMotionPointValue.formatNumber(this.x));
        sb.append(',');
        sb.append(AnimatableMotionPointValue.formatNumber(this.y));
        sb.append(',');
        sb.append(AnimatableMotionPointValue.formatNumber(this.angle));
        sb.append("rad");
        return sb.toString();
    }
}

