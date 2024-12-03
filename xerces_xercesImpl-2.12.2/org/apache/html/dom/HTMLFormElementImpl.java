/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLCollectionImpl;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLFormElement;

public class HTMLFormElementImpl
extends HTMLElementImpl
implements HTMLFormElement {
    private static final long serialVersionUID = -7324749629151493210L;
    private HTMLCollectionImpl _elements;

    @Override
    public HTMLCollection getElements() {
        if (this._elements == null) {
            this._elements = new HTMLCollectionImpl(this, 8);
        }
        return this._elements;
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
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public String getAcceptCharset() {
        return this.getAttribute("accept-charset");
    }

    @Override
    public void setAcceptCharset(String string) {
        this.setAttribute("accept-charset", string);
    }

    @Override
    public String getAction() {
        return this.getAttribute("action");
    }

    @Override
    public void setAction(String string) {
        this.setAttribute("action", string);
    }

    @Override
    public String getEnctype() {
        return this.getAttribute("enctype");
    }

    @Override
    public void setEnctype(String string) {
        this.setAttribute("enctype", string);
    }

    @Override
    public String getMethod() {
        return this.capitalize(this.getAttribute("method"));
    }

    @Override
    public void setMethod(String string) {
        this.setAttribute("method", string);
    }

    @Override
    public String getTarget() {
        return this.getAttribute("target");
    }

    @Override
    public void setTarget(String string) {
        this.setAttribute("target", string);
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
    public Node cloneNode(boolean bl) {
        HTMLFormElementImpl hTMLFormElementImpl = (HTMLFormElementImpl)super.cloneNode(bl);
        hTMLFormElementImpl._elements = null;
        return hTMLFormElementImpl;
    }

    public HTMLFormElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

