/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.SVGContext
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AnimationTargetListener;
import org.apache.batik.dom.svg.SVGContext;

public interface SVGAnimationTargetContext
extends SVGContext {
    public void addTargetListener(String var1, AnimationTargetListener var2);

    public void removeTargetListener(String var1, AnimationTargetListener var2);
}

