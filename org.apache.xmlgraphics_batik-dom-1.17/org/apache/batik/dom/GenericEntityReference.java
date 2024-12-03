/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractEntityReference;
import org.w3c.dom.Node;

public class GenericEntityReference
extends AbstractEntityReference {
    protected boolean readonly;

    protected GenericEntityReference() {
    }

    public GenericEntityReference(String name, AbstractDocument owner) {
        super(name, owner);
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
        return new GenericEntityReference();
    }
}

