/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.AbstractAnimation
 *  org.apache.batik.anim.SetAnimation
 *  org.apache.batik.anim.dom.AnimatableElement
 *  org.apache.batik.anim.dom.AnimationTarget
 *  org.apache.batik.anim.values.AnimatableValue
 */
package org.apache.batik.bridge;

import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.SetAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.SVGAnimationElementBridge;

public class SVGSetElementBridge
extends SVGAnimationElementBridge {
    @Override
    public String getLocalName() {
        return "set";
    }

    @Override
    public Bridge getInstance() {
        return new SVGSetElementBridge();
    }

    @Override
    protected AbstractAnimation createAnimation(AnimationTarget target) {
        AnimatableValue to = this.parseAnimatableValue("to");
        return new SetAnimation(this.timedElement, (AnimatableElement)this, to);
    }

    @Override
    protected boolean canAnimateType(int type) {
        return true;
    }

    @Override
    protected boolean isConstantAnimation() {
        return true;
    }
}

