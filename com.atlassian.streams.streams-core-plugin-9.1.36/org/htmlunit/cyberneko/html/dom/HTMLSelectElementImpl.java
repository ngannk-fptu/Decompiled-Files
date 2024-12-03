/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLCollectionImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.htmlunit.cyberneko.html.dom.HTMLOptionElementImpl;
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
    private HTMLCollection options_;

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        this.setAttribute("value", value);
    }

    @Override
    public int getSelectedIndex() {
        NodeList options = this.getElementsByTagName("OPTION");
        for (int i = 0; i < options.getLength(); ++i) {
            if (!((HTMLOptionElement)options.item(i)).getSelected()) continue;
            return i;
        }
        return -1;
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        NodeList options = this.getElementsByTagName("OPTION");
        for (int i = 0; i < options.getLength(); ++i) {
            ((HTMLOptionElementImpl)options.item(i)).setSelected(i == selectedIndex);
        }
    }

    @Override
    public HTMLCollection getOptions() {
        if (this.options_ == null) {
            this.options_ = new HTMLCollectionImpl(this, 6);
        }
        return this.options_;
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
    public void setDisabled(boolean disabled) {
        this.setAttribute("disabled", disabled);
    }

    @Override
    public boolean getMultiple() {
        return this.getBinary("multiple");
    }

    @Override
    public void setMultiple(boolean multiple) {
        this.setAttribute("multiple", multiple);
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
    public int getSize() {
        return this.getInteger(this.getAttribute("size"));
    }

    @Override
    public void setSize(int size) {
        this.setAttribute("size", String.valueOf(size));
    }

    @Override
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
    }

    @Override
    public void setTabIndex(int tabIndex) {
        this.setAttribute("tabindex", String.valueOf(tabIndex));
    }

    @Override
    public void add(HTMLElement element, HTMLElement before) {
        this.insertBefore(element, before);
    }

    @Override
    public void remove(int index) {
        NodeList options = this.getElementsByTagName("OPTION");
        Node removed = options.item(index);
        if (removed != null) {
            removed.getParentNode().removeChild(removed);
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
    public Node cloneNode(boolean deep) {
        HTMLSelectElementImpl clonedNode = (HTMLSelectElementImpl)super.cloneNode(deep);
        clonedNode.options_ = null;
        return clonedNode;
    }

    public HTMLSelectElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

