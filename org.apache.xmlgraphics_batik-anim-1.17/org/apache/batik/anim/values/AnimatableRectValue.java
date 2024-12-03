/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableRectValue
extends AnimatableValue {
    protected float x;
    protected float y;
    protected float width;
    protected float height;

    protected AnimatableRectValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableRectValue(AnimationTarget target, float x, float y, float w, float h) {
        super(target);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableRectValue res = result == null ? new AnimatableRectValue(this.target) : (AnimatableRectValue)result;
        float newX = this.x;
        float newY = this.y;
        float newWidth = this.width;
        float newHeight = this.height;
        if (to != null) {
            AnimatableRectValue toValue = (AnimatableRectValue)to;
            newX += interpolation * (toValue.x - this.x);
            newY += interpolation * (toValue.y - this.y);
            newWidth += interpolation * (toValue.width - this.width);
            newHeight += interpolation * (toValue.height - this.height);
        }
        if (accumulation != null && multiplier != 0) {
            AnimatableRectValue accValue = (AnimatableRectValue)accumulation;
            newX += (float)multiplier * accValue.x;
            newY += (float)multiplier * accValue.y;
            newWidth += (float)multiplier * accValue.width;
            newHeight += (float)multiplier * accValue.height;
        }
        if (res.x != newX || res.y != newY || res.width != newWidth || res.height != newHeight) {
            res.x = newX;
            res.y = newY;
            res.width = newWidth;
            res.height = newHeight;
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

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
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
        return new AnimatableRectValue(this.target, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public String toStringRep() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.x);
        sb.append(',');
        sb.append(this.y);
        sb.append(',');
        sb.append(this.width);
        sb.append(',');
        sb.append(this.height);
        return sb.toString();
    }
}

