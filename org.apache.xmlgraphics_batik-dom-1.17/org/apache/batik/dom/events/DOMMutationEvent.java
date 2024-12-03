/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.events;

import org.apache.batik.dom.events.AbstractEvent;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class DOMMutationEvent
extends AbstractEvent
implements MutationEvent {
    private Node relatedNode;
    private String prevValue;
    private String newValue;
    private String attrName;
    private short attrChange;

    @Override
    public Node getRelatedNode() {
        return this.relatedNode;
    }

    @Override
    public String getPrevValue() {
        return this.prevValue;
    }

    @Override
    public String getNewValue() {
        return this.newValue;
    }

    @Override
    public String getAttrName() {
        return this.attrName;
    }

    @Override
    public short getAttrChange() {
        return this.attrChange;
    }

    @Override
    public void initMutationEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, Node relatedNodeArg, String prevValueArg, String newValueArg, String attrNameArg, short attrChangeArg) {
        this.initEvent(typeArg, canBubbleArg, cancelableArg);
        this.relatedNode = relatedNodeArg;
        this.prevValue = prevValueArg;
        this.newValue = newValueArg;
        this.attrName = attrNameArg;
        this.attrChange = attrChangeArg;
    }

    public void initMutationEventNS(String namespaceURIArg, String typeArg, boolean canBubbleArg, boolean cancelableArg, Node relatedNodeArg, String prevValueArg, String newValueArg, String attrNameArg, short attrChangeArg) {
        this.initEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg);
        this.relatedNode = relatedNodeArg;
        this.prevValue = prevValueArg;
        this.newValue = newValueArg;
        this.attrName = attrNameArg;
        this.attrChange = attrChangeArg;
    }
}

