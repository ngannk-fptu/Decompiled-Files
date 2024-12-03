/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLOptGroupElement;

public class HTMLOptGroupElementImpl
extends HTMLElementImpl
implements HTMLOptGroupElement {
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

    public HTMLOptGroupElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

