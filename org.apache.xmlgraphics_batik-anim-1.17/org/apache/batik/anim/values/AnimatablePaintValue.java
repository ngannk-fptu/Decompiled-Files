/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableColorValue;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatablePaintValue
extends AnimatableColorValue {
    public static final int PAINT_NONE = 0;
    public static final int PAINT_CURRENT_COLOR = 1;
    public static final int PAINT_COLOR = 2;
    public static final int PAINT_URI = 3;
    public static final int PAINT_URI_NONE = 4;
    public static final int PAINT_URI_CURRENT_COLOR = 5;
    public static final int PAINT_URI_COLOR = 6;
    public static final int PAINT_INHERIT = 7;
    protected int paintType;
    protected String uri;

    protected AnimatablePaintValue(AnimationTarget target) {
        super(target);
    }

    protected AnimatablePaintValue(AnimationTarget target, float r, float g, float b) {
        super(target, r, g, b);
    }

    public static AnimatablePaintValue createNonePaintValue(AnimationTarget target) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = 0;
        return v;
    }

    public static AnimatablePaintValue createCurrentColorPaintValue(AnimationTarget target) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = 1;
        return v;
    }

    public static AnimatablePaintValue createColorPaintValue(AnimationTarget target, float r, float g, float b) {
        AnimatablePaintValue v = new AnimatablePaintValue(target, r, g, b);
        v.paintType = 2;
        return v;
    }

    public static AnimatablePaintValue createURIPaintValue(AnimationTarget target, String uri) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = 3;
        return v;
    }

    public static AnimatablePaintValue createURINonePaintValue(AnimationTarget target, String uri) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = 4;
        return v;
    }

    public static AnimatablePaintValue createURICurrentColorPaintValue(AnimationTarget target, String uri) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = 5;
        return v;
    }

    public static AnimatablePaintValue createURIColorPaintValue(AnimationTarget target, String uri, float r, float g, float b) {
        AnimatablePaintValue v = new AnimatablePaintValue(target, r, g, b);
        v.uri = uri;
        v.paintType = 6;
        return v;
    }

    public static AnimatablePaintValue createInheritPaintValue(AnimationTarget target) {
        AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = 7;
        return v;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        float newBlue;
        float newGreen;
        float newRed;
        String newURI;
        int newPaintType;
        AnimatablePaintValue res = result == null ? new AnimatablePaintValue(this.target) : (AnimatablePaintValue)result;
        if (this.paintType == 2) {
            boolean canInterpolate = true;
            if (to != null) {
                AnimatablePaintValue toPaint = (AnimatablePaintValue)to;
                boolean bl = canInterpolate = toPaint.paintType == 2;
            }
            if (accumulation != null) {
                AnimatablePaintValue accPaint = (AnimatablePaintValue)accumulation;
                boolean bl = canInterpolate = canInterpolate && accPaint.paintType == 2;
            }
            if (canInterpolate) {
                res.paintType = 2;
                return super.interpolate(res, to, interpolation, accumulation, multiplier);
            }
        }
        if (to != null && (double)interpolation >= 0.5) {
            AnimatablePaintValue toValue = (AnimatablePaintValue)to;
            newPaintType = toValue.paintType;
            newURI = toValue.uri;
            newRed = toValue.red;
            newGreen = toValue.green;
            newBlue = toValue.blue;
        } else {
            newPaintType = this.paintType;
            newURI = this.uri;
            newRed = this.red;
            newGreen = this.green;
            newBlue = this.blue;
        }
        if (res.paintType != newPaintType || res.uri == null || !res.uri.equals(newURI) || res.red != newRed || res.green != newGreen || res.blue != newBlue) {
            res.paintType = newPaintType;
            res.uri = newURI;
            res.red = newRed;
            res.green = newGreen;
            res.blue = newBlue;
            res.hasChanged = true;
        }
        return res;
    }

    public int getPaintType() {
        return this.paintType;
    }

    public String getURI() {
        return this.uri;
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
        return AnimatablePaintValue.createColorPaintValue(this.target, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public String getCssText() {
        switch (this.paintType) {
            case 0: {
                return "none";
            }
            case 1: {
                return "currentColor";
            }
            case 2: {
                return super.getCssText();
            }
            case 3: {
                return "url(" + this.uri + ")";
            }
            case 4: {
                return "url(" + this.uri + ") none";
            }
            case 5: {
                return "url(" + this.uri + ") currentColor";
            }
            case 6: {
                return "url(" + this.uri + ") " + super.getCssText();
            }
        }
        return "inherit";
    }
}

