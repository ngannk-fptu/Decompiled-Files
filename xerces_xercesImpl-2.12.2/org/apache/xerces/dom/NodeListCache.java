/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.Serializable;
import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.ParentNode;

class NodeListCache
implements Serializable {
    private static final long serialVersionUID = -7927529254918631002L;
    int fLength = -1;
    int fChildIndex = -1;
    ChildNode fChild;
    ParentNode fOwner;
    NodeListCache next;

    NodeListCache(ParentNode parentNode) {
        this.fOwner = parentNode;
    }
}

