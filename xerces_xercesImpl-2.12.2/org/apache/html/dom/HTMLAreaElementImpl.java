/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLAreaElement;

public class HTMLAreaElementImpl
extends HTMLElementImpl
implements HTMLAreaElement {
    private static final long serialVersionUID = 7164004431531608995L;

    @Override
    public String getAccessKey() {
        String string = this.getAttribute("accesskey");
        if (string != null && string.length() > 1) {
            string = string.substring(0, 1);
        }
        return string;
    }

    @Override
    public void setAccessKey(String string) {
        if (string != null && string.length() > 1) {
            string = string.substring(0, 1);
        }
        this.setAttribute("accesskey", string);
    }

    @Override
    public String getAlt() {
        return this.getAttribute("alt");
    }

    @Override
    public void setAlt(String string) {
        this.setAttribute("alt", string);
    }

    @Override
    public String getCoords() {
        return this.getAttribute("coords");
    }

    @Override
    public void setCoords(String string) {
        this.setAttribute("coords", string);
    }

    @Override
    public String getHref() {
        return this.getAttribute("href");
    }

    @Override
    public void setHref(String string) {
        this.setAttribute("href", string);
    }

    @Override
    public boolean getNoHref() {
        return this.getBinary("nohref");
    }

    @Override
    public void setNoHref(boolean bl) {
        this.setAttribute("nohref", bl);
    }

    @Override
    public String getShape() {
        return this.capitalize(this.getAttribute("shape"));
    }

    @Override
    public void setShape(String string) {
        this.setAttribute("shape", string);
    }

    @Override
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
    }

    @Override
    public void setTabIndex(int n) {
        this.setAttribute("tabindex", String.valueOf(n));
    }

    @Override
    public String getTarget() {
        return this.getAttribute("target");
    }

    @Override
    public void setTarget(String string) {
        this.setAttribute("target", string);
    }

    public HTMLAreaElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

