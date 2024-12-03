/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLTableCaptionElement;

public class HTMLTableCaptionElementImpl
extends HTMLElementImpl
implements HTMLTableCaptionElement {
    private static final long serialVersionUID = 183703024771848940L;

    @Override
    public String getAlign() {
        return this.getAttribute("align");
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    public HTMLTableCaptionElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

