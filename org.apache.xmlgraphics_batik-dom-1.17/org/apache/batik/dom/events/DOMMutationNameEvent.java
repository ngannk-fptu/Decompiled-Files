/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.w3c.dom.events.MutationNameEvent
 */
package org.apache.batik.dom.events;

import org.apache.batik.dom.events.DOMMutationEvent;
import org.apache.batik.w3c.dom.events.MutationNameEvent;
import org.w3c.dom.Node;

public class DOMMutationNameEvent
extends DOMMutationEvent
implements MutationNameEvent {
    protected String prevNamespaceURI;
    protected String prevNodeName;

    public void initMutationNameEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, Node relatedNodeArg, String prevNamespaceURIArg, String prevNodeNameArg) {
        this.initMutationEvent(typeArg, canBubbleArg, cancelableArg, relatedNodeArg, null, null, null, (short)0);
        this.prevNamespaceURI = prevNamespaceURIArg;
        this.prevNodeName = prevNodeNameArg;
    }

    public void initMutationNameEventNS(String namespaceURI, String typeArg, boolean canBubbleArg, boolean cancelableArg, Node relatedNodeArg, String prevNamespaceURIArg, String prevNodeNameArg) {
        this.initMutationEventNS(namespaceURI, typeArg, canBubbleArg, cancelableArg, relatedNodeArg, null, null, null, (short)0);
        this.prevNamespaceURI = prevNamespaceURIArg;
        this.prevNodeName = prevNodeNameArg;
    }

    public String getPrevNamespaceURI() {
        return this.prevNamespaceURI;
    }

    public String getPrevNodeName() {
        return this.prevNodeName;
    }
}

