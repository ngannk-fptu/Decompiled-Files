/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom.events;

import org.apache.xerces.dom.events.EventImpl;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class MutationEventImpl
extends EventImpl
implements MutationEvent {
    Node relatedNode = null;
    String prevValue = null;
    String newValue = null;
    String attrName = null;
    public short attrChange;
    public static final String DOM_SUBTREE_MODIFIED = "DOMSubtreeModified";
    public static final String DOM_NODE_INSERTED = "DOMNodeInserted";
    public static final String DOM_NODE_REMOVED = "DOMNodeRemoved";
    public static final String DOM_NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";
    public static final String DOM_NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";
    public static final String DOM_ATTR_MODIFIED = "DOMAttrModified";
    public static final String DOM_CHARACTER_DATA_MODIFIED = "DOMCharacterDataModified";

    @Override
    public String getAttrName() {
        return this.attrName;
    }

    @Override
    public short getAttrChange() {
        return this.attrChange;
    }

    @Override
    public String getNewValue() {
        return this.newValue;
    }

    @Override
    public String getPrevValue() {
        return this.prevValue;
    }

    @Override
    public Node getRelatedNode() {
        return this.relatedNode;
    }

    @Override
    public void initMutationEvent(String string, boolean bl, boolean bl2, Node node, String string2, String string3, String string4, short s) {
        this.relatedNode = node;
        this.prevValue = string2;
        this.newValue = string3;
        this.attrName = string4;
        this.attrChange = s;
        super.initEvent(string, bl, bl2);
    }
}

