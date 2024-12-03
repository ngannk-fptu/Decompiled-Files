/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractDocumentFragment;
import org.w3c.dom.Node;

public class GenericDocumentFragment
extends AbstractDocumentFragment {
    protected boolean readonly;

    protected GenericDocumentFragment() {
    }

    public GenericDocumentFragment(AbstractDocument owner) {
        this.ownerDocument = owner;
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
        return new GenericDocumentFragment();
    }
}

