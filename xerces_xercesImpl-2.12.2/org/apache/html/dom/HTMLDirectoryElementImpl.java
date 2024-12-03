/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLDirectoryElement;

public class HTMLDirectoryElementImpl
extends HTMLElementImpl
implements HTMLDirectoryElement {
    private static final long serialVersionUID = -1010376135190194454L;

    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean bl) {
        this.setAttribute("compact", bl);
    }

    public HTMLDirectoryElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

