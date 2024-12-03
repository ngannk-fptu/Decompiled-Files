/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHeadingElement;

public class HTMLHeadingElementImpl
extends HTMLElementImpl
implements HTMLHeadingElement {
    private static final long serialVersionUID = 6605827989383069095L;

    @Override
    public String getAlign() {
        return this.getCapitalized("align");
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    public HTMLHeadingElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

