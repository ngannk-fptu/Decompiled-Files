/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableLengthValue;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatableLengthOrIdentValue
extends AnimatableLengthValue {
    protected boolean isIdent;
    protected String ident;

    protected AnimatableLengthOrIdentValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableLengthOrIdentValue(AnimationTarget target, short type, float v, short pcInterp) {
        super(target, type, v, pcInterp);
    }

    public AnimatableLengthOrIdentValue(AnimationTarget target, String ident) {
        super(target);
        this.ident = ident;
        this.isIdent = true;
    }

    public boolean isIdent() {
        return this.isIdent;
    }

    public String getIdent() {
        return this.ident;
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
        return new AnimatableLengthOrIdentValue(this.target, 1, 0.0f, this.percentageInterpretation);
    }

    @Override
    public String getCssText() {
        if (this.isIdent) {
            return this.ident;
        }
        return super.getCssText();
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableLengthOrIdentValue res = result == null ? new AnimatableLengthOrIdentValue(this.target) : (AnimatableLengthOrIdentValue)result;
        if (to == null) {
            if (this.isIdent) {
                res.hasChanged = !res.isIdent || !res.ident.equals(this.ident);
                res.ident = this.ident;
                res.isIdent = true;
            } else {
                short oldLengthType = res.lengthType;
                float oldLengthValue = res.lengthValue;
                short oldPercentageInterpretation = res.percentageInterpretation;
                super.interpolate(res, to, interpolation, accumulation, multiplier);
                if (res.lengthType != oldLengthType || res.lengthValue != oldLengthValue || res.percentageInterpretation != oldPercentageInterpretation) {
                    res.hasChanged = true;
                }
            }
        } else {
            AnimatableLengthOrIdentValue toValue = (AnimatableLengthOrIdentValue)to;
            if (this.isIdent || toValue.isIdent) {
                if ((double)interpolation >= 0.5) {
                    if (res.isIdent != toValue.isIdent || res.lengthType != toValue.lengthType || res.lengthValue != toValue.lengthValue || res.isIdent && toValue.isIdent && !toValue.ident.equals(this.ident)) {
                        res.isIdent = toValue.isIdent;
                        res.ident = toValue.ident;
                        res.lengthType = toValue.lengthType;
                        res.lengthValue = toValue.lengthValue;
                        res.hasChanged = true;
                    }
                } else if (res.isIdent != this.isIdent || res.lengthType != this.lengthType || res.lengthValue != this.lengthValue || res.isIdent && this.isIdent && !res.ident.equals(this.ident)) {
                    res.isIdent = this.isIdent;
                    res.ident = this.ident;
                    res.ident = this.ident;
                    res.lengthType = this.lengthType;
                    res.hasChanged = true;
                }
            } else {
                super.interpolate(res, to, interpolation, accumulation, multiplier);
            }
        }
        return res;
    }
}

