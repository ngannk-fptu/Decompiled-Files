/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLAnchorElement;

public class HTMLAnchorElementImpl
extends HTMLElementImpl
implements HTMLAnchorElement {
    private static final long serialVersionUID = -140558580924061847L;

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
    public String getCharset() {
        return this.getAttribute("charset");
    }

    @Override
    public void setCharset(String string) {
        this.setAttribute("charset", string);
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
    public String getHreflang() {
        return this.getAttribute("hreflang");
    }

    @Override
    public void setHreflang(String string) {
        this.setAttribute("hreflang", string);
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public String getRel() {
        return this.getAttribute("rel");
    }

    @Override
    public void setRel(String string) {
        this.setAttribute("rel", string);
    }

    @Override
    public String getRev() {
        return this.getAttribute("rev");
    }

    @Override
    public void setRev(String string) {
        this.setAttribute("rev", string);
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

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String string) {
        this.setAttribute("type", string);
    }

    @Override
    public void blur() {
    }

    @Override
    public void focus() {
    }

    public HTMLAnchorElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

