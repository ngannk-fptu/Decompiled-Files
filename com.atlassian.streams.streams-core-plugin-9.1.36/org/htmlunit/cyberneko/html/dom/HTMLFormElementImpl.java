/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLCollectionImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLFormElement;

public class HTMLFormElementImpl
extends HTMLElementImpl
implements HTMLFormElement {
    private HTMLCollectionImpl elements_;

    @Override
    public HTMLCollection getElements() {
        if (this.elements_ == null) {
            this.elements_ = new HTMLCollectionImpl(this, 8);
        }
        return this.elements_;
    }

    @Override
    public int getLength() {
        return this.getElements().getLength();
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
    public String getAcceptCharset() {
        return this.getAttribute("accept-charset");
    }

    @Override
    public void setAcceptCharset(String acceptCharset) {
        this.setAttribute("accept-charset", acceptCharset);
    }

    @Override
    public String getAction() {
        return this.getAttribute("action");
    }

    @Override
    public void setAction(String action) {
        this.setAttribute("action", action);
    }

    @Override
    public String getEnctype() {
        return this.getAttribute("enctype");
    }

    @Override
    public void setEnctype(String enctype) {
        this.setAttribute("enctype", enctype);
    }

    @Override
    public String getMethod() {
        return this.capitalize(this.getAttribute("method"));
    }

    @Override
    public void setMethod(String method) {
        this.setAttribute("method", method);
    }

    @Override
    public String getTarget() {
        return this.getAttribute("target");
    }

    @Override
    public void setTarget(String target) {
        this.setAttribute("target", target);
    }

    @Override
    public void submit() {
    }

    @Override
    public void reset() {
    }

    @Override
    public NodeList getChildNodes() {
        return this.getChildNodesUnoptimized();
    }

    @Override
    public Node cloneNode(boolean deep) {
        HTMLFormElementImpl clonedNode = (HTMLFormElementImpl)super.cloneNode(deep);
        clonedNode.elements_ = null;
        return clonedNode;
    }

    public HTMLFormElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

