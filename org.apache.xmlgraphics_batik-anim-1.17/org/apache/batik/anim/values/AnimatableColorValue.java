/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableColorValue
extends AnimatableValue {
    protected float red;
    protected float green;
    protected float blue;

    protected AnimatableColorValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableColorValue(AnimationTarget target, float r, float g, float b) {
        super(target);
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableColorValue res = result == null ? new AnimatableColorValue(this.target) : (AnimatableColorValue)result;
        float oldRed = res.red;
        float oldGreen = res.green;
        float oldBlue = res.blue;
        res.red = this.red;
        res.green = this.green;
        res.blue = this.blue;
        AnimatableColorValue toColor = (AnimatableColorValue)to;
        AnimatableColorValue accColor = (AnimatableColorValue)accumulation;
        if (to != null) {
            res.red += interpolation * (toColor.red - res.red);
            res.green += interpolation * (toColor.green - res.green);
            res.blue += interpolation * (toColor.blue - res.blue);
        }
        if (accumulation != null) {
            res.red += (float)multiplier * accColor.red;
            res.green += (float)multiplier * accColor.green;
            res.blue += (float)multiplier * accColor.blue;
        }
        if (res.red != oldRed || res.green != oldGreen || res.blue != oldBlue) {
            res.hasChanged = true;
        }
        return res;
    }

    @Override
    public boolean canPace() {
        return true;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        AnimatableColorValue o = (AnimatableColorValue)other;
        float dr = this.red - o.red;
        float dg = this.green - o.green;
        float db = this.blue - o.blue;
        return (float)Math.sqrt(dr * dr + dg * dg + db * db);
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableColorValue(this.target, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public String getCssText() {
        return "rgb(" + Math.round(this.red * 255.0f) + ',' + Math.round(this.green * 255.0f) + ',' + Math.round(this.blue * 255.0f) + ')';
    }
}

