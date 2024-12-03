/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLMenuElement;

public class HTMLMenuElementImpl
extends HTMLElementImpl
implements HTMLMenuElement {
    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean compact) {
        this.setAttribute("compact", compact);
    }

    public HTMLMenuElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

