/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLModElement;

public class HTMLModElementImpl
extends HTMLElementImpl
implements HTMLModElement {
    @Override
    public String getCite() {
        return this.getAttribute("cite");
    }

    @Override
    public void setCite(String cite) {
        this.setAttribute("cite", cite);
    }

    @Override
    public String getDateTime() {
        return this.getAttribute("datetime");
    }

    @Override
    public void setDateTime(String dateTime) {
        this.setAttribute("datetime", dateTime);
    }

    public HTMLModElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

