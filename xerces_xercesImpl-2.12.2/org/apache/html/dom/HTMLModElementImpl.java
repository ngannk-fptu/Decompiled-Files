/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLModElement;

public class HTMLModElementImpl
extends HTMLElementImpl
implements HTMLModElement {
    private static final long serialVersionUID = 6424581972706750120L;

    @Override
    public String getCite() {
        return this.getAttribute("cite");
    }

    @Override
    public void setCite(String string) {
        this.setAttribute("cite", string);
    }

    @Override
    public String getDateTime() {
        return this.getAttribute("datetime");
    }

    @Override
    public void setDateTime(String string) {
        this.setAttribute("datetime", string);
    }

    public HTMLModElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

