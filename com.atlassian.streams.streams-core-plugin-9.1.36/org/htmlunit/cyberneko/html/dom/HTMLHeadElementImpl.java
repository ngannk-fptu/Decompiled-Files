/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLHeadElement;

public class HTMLHeadElementImpl
extends HTMLElementImpl
implements HTMLHeadElement {
    @Override
    public String getProfile() {
        return this.getAttribute("profile");
    }

    @Override
    public void setProfile(String profile) {
        this.setAttribute("profile", profile);
    }

    public HTMLHeadElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

