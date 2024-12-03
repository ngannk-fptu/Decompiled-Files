/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.w3c.dom.Element;

public interface AnimatedAttributeListener {
    public void animatedAttributeChanged(Element var1, AnimatedLiveAttributeValue var2);

    public void otherAnimationChanged(Element var1, String var2);
}

