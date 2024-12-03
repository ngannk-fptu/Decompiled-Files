/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.ChildNode;
import org.htmlunit.cyberneko.xerces.dom.ParentNode;

class NodeListCache {
    int fLength = -1;
    int fChildIndex = -1;
    ChildNode fChild;
    ParentNode fOwner;
    NodeListCache next;

    NodeListCache(ParentNode owner) {
        this.fOwner = owner;
    }
}

