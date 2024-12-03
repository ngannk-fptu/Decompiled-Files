/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractComment;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;

public class GenericComment
extends AbstractComment {
    protected boolean readonly;

    public GenericComment() {
    }

    public GenericComment(String value, AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeValue(value);
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public void setReadonly(boolean v) {
        this.readonly = v;
    }

    @Override
    protected Node newNode() {
        return new GenericComment();
    }
}

