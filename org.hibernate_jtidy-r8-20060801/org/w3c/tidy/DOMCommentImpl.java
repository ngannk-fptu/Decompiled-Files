/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.Comment;
import org.w3c.tidy.DOMCharacterDataImpl;
import org.w3c.tidy.Node;

public class DOMCommentImpl
extends DOMCharacterDataImpl
implements Comment {
    protected DOMCommentImpl(Node adaptee) {
        super(adaptee);
    }

    public String getNodeName() {
        return "#comment";
    }

    public short getNodeType() {
        return 8;
    }
}

