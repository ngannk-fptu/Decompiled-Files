/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLScriptElement;

public class HTMLScriptElementImpl
extends HTMLElementImpl
implements HTMLScriptElement {
    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof Text)) continue;
            text.append(((Text)child).getData());
        }
        return text.toString();
    }

    @Override
    public void setText(String text) {
        Node child = this.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            this.removeChild(child);
            child = next;
        }
        this.insertBefore(this.getOwnerDocument().createTextNode(text), this.getFirstChild());
    }

    @Override
    public String getHtmlFor() {
        return this.getAttribute("for");
    }

    @Override
    public void setHtmlFor(String htmlFor) {
        this.setAttribute("for", htmlFor);
    }

    @Override
    public String getEvent() {
        return this.getAttribute("event");
    }

    @Override
    public void setEvent(String event) {
        this.setAttribute("event", event);
    }

    @Override
    public String getCharset() {
        return this.getAttribute("charset");
    }

    @Override
    public void setCharset(String charset) {
        this.setAttribute("charset", charset);
    }

    @Override
    public boolean getDefer() {
        return this.getBinary("defer");
    }

    @Override
    public void setDefer(boolean defer) {
        this.setAttribute("defer", defer);
    }

    @Override
    public String getSrc() {
        return this.getAttribute("src");
    }

    @Override
    public void setSrc(String src) {
        this.setAttribute("src", src);
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String type) {
        this.setAttribute("type", type);
    }

    public HTMLScriptElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

