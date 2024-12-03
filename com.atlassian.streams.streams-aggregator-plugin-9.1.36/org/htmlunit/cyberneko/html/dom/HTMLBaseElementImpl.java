/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLBaseElement;

public class HTMLBaseElementImpl
extends HTMLElementImpl
implements HTMLBaseElement {
    @Override
    public String getHref() {
        return this.getAttribute("href");
    }

    @Override
    public void setHref(String href) {
        this.setAttribute("href", href);
    }

    @Override
    public String getTarget() {
        return this.getAttribute("target");
    }

    @Override
    public void setTarget(String target) {
        this.setAttribute("target", target);
    }

    public HTMLBaseElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

