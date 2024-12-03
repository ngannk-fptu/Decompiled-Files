/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLFrameSetElement;

public class HTMLFrameSetElementImpl
extends HTMLElementImpl
implements HTMLFrameSetElement {
    @Override
    public String getCols() {
        return this.getAttribute("cols");
    }

    @Override
    public void setCols(String cols) {
        this.setAttribute("cols", cols);
    }

    @Override
    public String getRows() {
        return this.getAttribute("rows");
    }

    @Override
    public void setRows(String rows) {
        this.setAttribute("rows", rows);
    }

    public HTMLFrameSetElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

