/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLTitleElement;

public class HTMLTitleElementImpl
extends HTMLElementImpl
implements HTMLTitleElement {
    private static final long serialVersionUID = 879646303512367875L;

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

    public HTMLTitleElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

