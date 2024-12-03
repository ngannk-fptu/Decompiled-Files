/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLMetaElement;

public class HTMLMetaElementImpl
extends HTMLElementImpl
implements HTMLMetaElement {
    @Override
    public String getContent() {
        return this.getAttribute("content");
    }

    @Override
    public void setContent(String content) {
        this.setAttribute("content", content);
    }

    @Override
    public String getHttpEquiv() {
        return this.getAttribute("http-equiv");
    }

    @Override
    public void setHttpEquiv(String httpEquiv) {
        this.setAttribute("http-equiv", httpEquiv);
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String name) {
        this.setAttribute("name", name);
    }

    @Override
    public String getScheme() {
        return this.getAttribute("scheme");
    }

    @Override
    public void setScheme(String scheme) {
        this.setAttribute("scheme", scheme);
    }

    public HTMLMetaElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

