/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;

public class HTMLOptionElementImpl
extends HTMLElementImpl
implements HTMLOptionElement {
    @Override
    public boolean getDefaultSelected() {
        return this.getBinary("default-selected");
    }

    @Override
    public void setDefaultSelected(boolean defaultSelected) {
        this.setAttribute("default-selected", defaultSelected);
    }

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
    public int getIndex() {
        Node parent;
        for (parent = this.getParentNode(); parent != null && !(parent instanceof HTMLSelectElement); parent = parent.getParentNode()) {
        }
        if (parent != null) {
            NodeList options = ((HTMLElement)parent).getElementsByTagName("OPTION");
            for (int i = 0; i < options.getLength(); ++i) {
                if (options.item(i) != this) continue;
                return i;
            }
        }
        return -1;
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
    public String getLabel() {
        return this.capitalize(this.getAttribute("label"));
    }

    @Override
    public void setLabel(String label) {
        this.setAttribute("label", label);
    }

    @Override
    public boolean getSelected() {
        return this.getBinary("selected");
    }

    @Override
    public void setSelected(boolean selected) {
        this.setAttribute("selected", selected);
    }

    @Override
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        this.setAttribute("value", value);
    }

    public HTMLOptionElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

