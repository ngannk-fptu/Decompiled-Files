/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLDirectoryElement;

public class HTMLDirectoryElementImpl
extends HTMLElementImpl
implements HTMLDirectoryElement {
    @Override
    public boolean getCompact() {
        return this.getBinary("compact");
    }

    @Override
    public void setCompact(boolean compact) {
        this.setAttribute("compact", compact);
    }

    public HTMLDirectoryElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

