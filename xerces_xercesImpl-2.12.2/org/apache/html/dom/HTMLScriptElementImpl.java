/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLScriptElement;

public class HTMLScriptElementImpl
extends HTMLElementImpl
implements HTMLScriptElement {
    private static final long serialVersionUID = 5090330049085326558L;

    @Override
    public String getText() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof Text)) continue;
            stringBuffer.append(((Text)node).getData());
        }
        return stringBuffer.toString();
    }

    @Override
    public void setText(String string) {
        Node node = this.getFirstChild();
        while (node != null) {
            Node node2 = node.getNextSibling();
            this.removeChild(node);
            node = node2;
        }
        this.insertBefore(this.getOwnerDocument().createTextNode(string), this.getFirstChild());
    }

    @Override
    public String getHtmlFor() {
        return this.getAttribute("for");
    }

    @Override
    public void setHtmlFor(String string) {
        this.setAttribute("for", string);
    }

    @Override
    public String getEvent() {
        return this.getAttribute("event");
    }

    @Override
    public void setEvent(String string) {
        this.setAttribute("event", string);
    }

    @Override
    public String getCharset() {
        return this.getAttribute("charset");
    }

    @Override
    public void setCharset(String string) {
        this.setAttribute("charset", string);
    }

    @Override
    public boolean getDefer() {
        return this.getBinary("defer");
    }

    @Override
    public void setDefer(boolean bl) {
        this.setAttribute("defer", bl);
    }

    @Override
    public String getSrc() {
        return this.getAttribute("src");
    }

    @Override
    public void setSrc(String string) {
        this.setAttribute("src", string);
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String string) {
        this.setAttribute("type", string);
    }

    public HTMLScriptElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

