/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLAreaElement;

public class HTMLAreaElementImpl
extends HTMLElementImpl
implements HTMLAreaElement {
    @Override
    public String getAccessKey() {
        String accessKey = this.getAttribute("accesskey");
        if (accessKey.length() > 1) {
            accessKey = accessKey.substring(0, 1);
        }
        return accessKey;
    }

    @Override
    public void setAccessKey(String accessKey) {
        if (accessKey != null && accessKey.length() > 1) {
            accessKey = accessKey.substring(0, 1);
        }
        this.setAttribute("accesskey", accessKey);
    }

    @Override
    public String getAlt() {
        return this.getAttribute("alt");
    }

    @Override
    public void setAlt(String alt) {
        this.setAttribute("alt", alt);
    }

    @Override
    public String getCoords() {
        return this.getAttribute("coords");
    }

    @Override
    public void setCoords(String coords) {
        this.setAttribute("coords", coords);
    }

    @Override
    public String getHref() {
        return this.getAttribute("href");
    }

    @Override
    public void setHref(String href) {
        this.setAttribute("href", href);
    }

    @Override
    public boolean getNoHref() {
        return this.getBinary("nohref");
    }

    @Override
    public void setNoHref(boolean nohref) {
        this.setAttribute("nohref", nohref);
    }

    @Override
    public String getShape() {
        return this.capitalize(this.getAttribute("shape"));
    }

    @Override
    public void setShape(String shape) {
        this.setAttribute("shape", shape);
    }

    @Override
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
    }

    @Override
    public void setTabIndex(int tabIndex) {
        this.setAttribute("tabindex", String.valueOf(tabIndex));
    }

    @Override
    public String getTarget() {
        return this.getAttribute("target");
    }

    @Override
    public void setTarget(String target) {
        this.setAttribute("target", target);
    }

    public HTMLAreaElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

