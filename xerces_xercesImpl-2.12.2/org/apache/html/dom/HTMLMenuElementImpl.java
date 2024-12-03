/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLMenuElement;

public class HTMLMenuElementImpl
extends HTMLElementImpl
implements HTMLMenuElement {
    private static final long serialVersionUID = -1489696654903916901L;

    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean bl) {
        this.setAttribute("compact", bl);
    }

    public HTMLMenuElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

