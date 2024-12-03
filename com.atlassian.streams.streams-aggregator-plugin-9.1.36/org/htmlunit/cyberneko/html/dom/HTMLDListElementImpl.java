/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLDListElement;

public class HTMLDListElementImpl
extends HTMLElementImpl
implements HTMLDListElement {
    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean compact) {
        this.setAttribute("compact", compact);
    }

    public HTMLDListElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

