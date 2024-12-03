/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AnimationTarget
 *  org.apache.batik.anim.dom.AnimationTargetListener
 *  org.apache.batik.anim.dom.SVGAnimationTargetContext
 */
package org.apache.batik.bridge;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.AnimationTargetListener;
import org.apache.batik.anim.dom.SVGAnimationTargetContext;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.w3c.dom.Element;

public abstract class AnimatableSVGBridge
extends AbstractSVGBridge
implements SVGAnimationTargetContext {
    protected Element e;
    protected BridgeContext ctx;
    protected HashMap targetListeners;

    public void addTargetListener(String pn, AnimationTargetListener l) {
        LinkedList<AnimationTargetListener> ll;
        if (this.targetListeners == null) {
            this.targetListeners = new HashMap();
        }
        if ((ll = (LinkedList<AnimationTargetListener>)this.targetListeners.get(pn)) == null) {
            ll = new LinkedList<AnimationTargetListener>();
            this.targetListeners.put(pn, ll);
        }
        ll.add(l);
    }

    public void removeTargetListener(String pn, AnimationTargetListener l) {
        LinkedList ll = (LinkedList)this.targetListeners.get(pn);
        ll.remove(l);
    }

    protected void fireBaseAttributeListeners(String pn) {
        LinkedList ll;
        if (this.targetListeners != null && (ll = (LinkedList)this.targetListeners.get(pn)) != null) {
            for (Object aLl : ll) {
                AnimationTargetListener l = (AnimationTargetListener)aLl;
                l.baseValueChanged((AnimationTarget)this.e, null, pn, true);
            }
        }
    }
}

