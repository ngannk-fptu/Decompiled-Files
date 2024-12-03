/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLCollectionImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLMapElement;

public class HTMLMapElementImpl
extends HTMLElementImpl
implements HTMLMapElement {
    private HTMLCollection areas_;

    @Override
    public HTMLCollection getAreas() {
        if (this.areas_ == null) {
            this.areas_ = new HTMLCollectionImpl(this, -1);
        }
        return this.areas_;
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String name) {
        this.setAttribute("name", name);
    }

    @Override
    public Node cloneNode(boolean deep) {
        HTMLMapElementImpl clonedNode = (HTMLMapElementImpl)super.cloneNode(deep);
        clonedNode.areas_ = null;
        return clonedNode;
    }

    public HTMLMapElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

