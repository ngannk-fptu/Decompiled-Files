/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.XBLOMShadowTreeElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;

public class BindableElement
extends SVGGraphicsElement {
    protected String namespaceURI;
    protected String localName;
    protected XBLOMShadowTreeElement xblShadowTree;

    protected BindableElement() {
    }

    public BindableElement(String prefix, AbstractDocument owner, String ns, String ln) {
        super(prefix, owner);
        this.namespaceURI = ns;
        this.localName = ln;
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getLocalName() {
        return this.localName;
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return null;
    }

    protected Node newNode() {
        return new BindableElement(null, null, this.namespaceURI, this.localName);
    }

    public void setShadowTree(XBLOMShadowTreeElement s) {
        this.xblShadowTree = s;
    }

    public XBLOMShadowTreeElement getShadowTree() {
        return this.xblShadowTree;
    }

    @Override
    public Node getCSSFirstChild() {
        if (this.xblShadowTree != null) {
            return this.xblShadowTree.getFirstChild();
        }
        return null;
    }

    @Override
    public Node getCSSLastChild() {
        return this.getCSSFirstChild();
    }
}

