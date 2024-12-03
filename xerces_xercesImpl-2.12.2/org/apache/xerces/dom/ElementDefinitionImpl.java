/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.NamedNodeMapImpl;
import org.apache.xerces.dom.ParentNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementDefinitionImpl
extends ParentNode {
    static final long serialVersionUID = -8373890672670022714L;
    protected String name;
    protected NamedNodeMapImpl attributes;

    public ElementDefinitionImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.name = string;
        this.attributes = new NamedNodeMapImpl(coreDocumentImpl);
    }

    @Override
    public short getNodeType() {
        return 21;
    }

    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public Node cloneNode(boolean bl) {
        ElementDefinitionImpl elementDefinitionImpl = (ElementDefinitionImpl)super.cloneNode(bl);
        elementDefinitionImpl.attributes = this.attributes.cloneMap(elementDefinitionImpl);
        return elementDefinitionImpl;
    }

    @Override
    public NamedNodeMap getAttributes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.attributes;
    }
}

