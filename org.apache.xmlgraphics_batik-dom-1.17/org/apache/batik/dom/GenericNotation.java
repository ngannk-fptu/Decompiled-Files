/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractNotation;
import org.w3c.dom.Node;

public class GenericNotation
extends AbstractNotation {
    protected boolean readonly;

    protected GenericNotation() {
    }

    public GenericNotation(String name, String pubId, String sysId, AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeName(name);
        this.setPublicId(pubId);
        this.setSystemId(sysId);
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
        return new GenericNotation();
    }
}

