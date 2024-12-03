/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;

public class HTMLOptionElementImpl
extends HTMLElementImpl
implements HTMLOptionElement {
    private static final long serialVersionUID = -4486774554137530907L;

    @Override
    public boolean getDefaultSelected() {
        return this.getBinary("default-selected");
    }

    @Override
    public void setDefaultSelected(boolean bl) {
        this.setAttribute("default-selected", bl);
    }

    @Override
    public String getText() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof Text)) continue;
            stringBuffer.append(((Text)node).getData());
        }
        return stringBuffer.toString();
    }

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
    public int getIndex() {
        Node node;
        for (node = this.getParentNode(); node != null && !(node instanceof HTMLSelectElement); node = node.getParentNode()) {
        }
        if (node != null) {
            NodeList nodeList = ((HTMLElement)node).getElementsByTagName("OPTION");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                if (nodeList.item(i) != this) continue;
                return i;
            }
        }
        return -1;
    }

    public void setIndex(int n) {
        NodeList nodeList;
        Node node;
        for (node = this.getParentNode(); node != null && !(node instanceof HTMLSelectElement); node = node.getParentNode()) {
        }
        if (node != null && (nodeList = ((HTMLElement)node).getElementsByTagName("OPTION")).item(n) != this) {
            this.getParentNode().removeChild(this);
            Node node2 = nodeList.item(n);
            node2.getParentNode().insertBefore(this, node2);
        }
    }

    @Override
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean bl) {
        this.setAttribute("disabled", bl);
    }

    @Override
    public String getLabel() {
        return this.capitalize(this.getAttribute("label"));
    }

    @Override
    public void setLabel(String string) {
        this.setAttribute("label", string);
    }

    @Override
    public boolean getSelected() {
        return this.getBinary("selected");
    }

    @Override
    public void setSelected(boolean bl) {
        this.setAttribute("selected", bl);
    }

    @Override
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String string) {
        this.setAttribute("value", string);
    }

    public HTMLOptionElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

