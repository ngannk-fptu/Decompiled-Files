/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHeadElement;

public class HTMLHeadElementImpl
extends HTMLElementImpl
implements HTMLHeadElement {
    private static final long serialVersionUID = 6438668473721292232L;

    @Override
    public String getProfile() {
        return this.getAttribute("profile");
    }

    @Override
    public void setProfile(String string) {
        this.setAttribute("profile", string);
    }

    public HTMLHeadElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

