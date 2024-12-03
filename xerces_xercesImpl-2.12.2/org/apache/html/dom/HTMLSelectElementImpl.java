/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLCollectionImpl;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.apache.html.dom.HTMLFormControl;
import org.apache.html.dom.HTMLOptionElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;

public class HTMLSelectElementImpl
extends HTMLElementImpl
implements HTMLSelectElement,
HTMLFormControl {
    private static final long serialVersionUID = -6998282711006968187L;
    private HTMLCollection _options;

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String string) {
        this.setAttribute("value", string);
    }

    @Override
    public int getSelectedIndex() {
        NodeList nodeList = this.getElementsByTagName("OPTION");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (!((HTMLOptionElement)nodeList.item(i)).getSelected()) continue;
            return i;
        }
        return -1;
    }

    @Override
    public void setSelectedIndex(int n) {
        NodeList nodeList = this.getElementsByTagName("OPTION");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            ((HTMLOptionElementImpl)nodeList.item(i)).setSelected(i == n);
        }
    }

    @Override
    public HTMLCollection getOptions() {
        if (this._options == null) {
            this._options = new HTMLCollectionImpl(this, 6);
        }
        return this._options;
    }

    @Override
    public int getLength() {
        return this.getOptions().getLength();
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
    public boolean getMultiple() {
        return this.getBinary("multiple");
    }

    @Override
    public void setMultiple(boolean bl) {
        this.setAttribute("multiple", bl);
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
    public int getSize() {
        return this.getInteger(this.getAttribute("size"));
    }

    @Override
    public void setSize(int n) {
        this.setAttribute("size", String.valueOf(n));
    }

    @Override
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
    }

    @Override
    public void setTabIndex(int n) {
        this.setAttribute("tabindex", String.valueOf(n));
    }

    @Override
    public void add(HTMLElement hTMLElement, HTMLElement hTMLElement2) {
        this.insertBefore(hTMLElement, hTMLElement2);
    }

    @Override
    public void remove(int n) {
        NodeList nodeList = this.getElementsByTagName("OPTION");
        Node node = nodeList.item(n);
        if (node != null) {
            node.getParentNode().removeChild(node);
        }
    }

    @Override
    public void blur() {
    }

    @Override
    public void focus() {
    }

    @Override
    public NodeList getChildNodes() {
        return this.getChildNodesUnoptimized();
    }

    @Override
    public Node cloneNode(boolean bl) {
        HTMLSelectElementImpl hTMLSelectElementImpl = (HTMLSelectElementImpl)super.cloneNode(bl);
        hTMLSelectElementImpl._options = null;
        return hTMLSelectElementImpl;
    }

    public HTMLSelectElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

