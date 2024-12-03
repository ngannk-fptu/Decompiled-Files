/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLTitleElement;

public class HTMLTitleElementImpl
extends HTMLElementImpl
implements HTMLTitleElement {
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

    public HTMLTitleElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

