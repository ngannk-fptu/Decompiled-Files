/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.css.engine.CSSEngineEvent
 */
package org.apache.batik.bridge;

import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.w3c.dom.events.MutationEvent;

public interface BridgeUpdateHandler {
    public void handleDOMAttrModifiedEvent(MutationEvent var1);

    public void handleDOMNodeInsertedEvent(MutationEvent var1);

    public void handleDOMNodeRemovedEvent(MutationEvent var1);

    public void handleDOMCharacterDataModified(MutationEvent var1);

    public void handleCSSEngineEvent(CSSEngineEvent var1);

    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue var1);

    public void handleOtherAnimationChanged(String var1);

    public void dispose();
}

