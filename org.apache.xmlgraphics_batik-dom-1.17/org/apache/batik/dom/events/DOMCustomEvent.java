/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.w3c.dom.events.CustomEvent
 */
package org.apache.batik.dom.events;

import org.apache.batik.dom.events.DOMEvent;
import org.apache.batik.w3c.dom.events.CustomEvent;

public class DOMCustomEvent
extends DOMEvent
implements CustomEvent {
    protected Object detail;

    public Object getDetail() {
        return this.detail;
    }

    public void initCustomEventNS(String namespaceURIArg, String typeArg, boolean canBubbleArg, boolean cancelableArg, Object detailArg) {
        this.initEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg);
        this.detail = detailArg;
    }
}

