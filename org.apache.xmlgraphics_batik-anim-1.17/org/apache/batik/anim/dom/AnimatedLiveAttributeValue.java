/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.LiveAttributeValue
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AnimatedAttributeListener;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.LiveAttributeValue;

public interface AnimatedLiveAttributeValue
extends LiveAttributeValue {
    public String getNamespaceURI();

    public String getLocalName();

    public AnimatableValue getUnderlyingValue(AnimationTarget var1);

    public void addAnimatedAttributeListener(AnimatedAttributeListener var1);

    public void removeAnimatedAttributeListener(AnimatedAttributeListener var1);
}

