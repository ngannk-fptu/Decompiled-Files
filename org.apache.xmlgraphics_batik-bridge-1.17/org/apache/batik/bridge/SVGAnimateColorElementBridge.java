/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.AbstractAnimation
 *  org.apache.batik.anim.ColorAnimation
 *  org.apache.batik.anim.dom.AnimatableElement
 *  org.apache.batik.anim.dom.AnimationTarget
 *  org.apache.batik.anim.values.AnimatableColorValue
 *  org.apache.batik.anim.values.AnimatablePaintValue
 *  org.apache.batik.anim.values.AnimatableValue
 */
package org.apache.batik.bridge;

import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.ColorAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableColorValue;
import org.apache.batik.anim.values.AnimatablePaintValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.SVGAnimateElementBridge;

public class SVGAnimateColorElementBridge
extends SVGAnimateElementBridge {
    @Override
    public String getLocalName() {
        return "animateColor";
    }

    @Override
    public Bridge getInstance() {
        return new SVGAnimateColorElementBridge();
    }

    @Override
    protected AbstractAnimation createAnimation(AnimationTarget target) {
        AnimatableValue from = this.parseAnimatableValue("from");
        AnimatableValue to = this.parseAnimatableValue("to");
        AnimatableValue by = this.parseAnimatableValue("by");
        return new ColorAnimation(this.timedElement, (AnimatableElement)this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(), from, to, by);
    }

    @Override
    protected boolean canAnimateType(int type) {
        return type == 6 || type == 7;
    }

    @Override
    protected boolean checkValueType(AnimatableValue v) {
        if (v instanceof AnimatablePaintValue) {
            return ((AnimatablePaintValue)v).getPaintType() == 2;
        }
        return v instanceof AnimatableColorValue;
    }
}

