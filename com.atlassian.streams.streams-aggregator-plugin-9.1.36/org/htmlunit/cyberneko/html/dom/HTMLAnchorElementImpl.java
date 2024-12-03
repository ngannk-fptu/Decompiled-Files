/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLAnchorElement;

public class HTMLAnchorElementImpl
extends HTMLElementImpl
implements HTMLAnchorElement {
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
    public String getCharset() {
        return this.getAttribute("charset");
    }

    @Override
    public void setCharset(String charset) {
        this.setAttribute("charset", charset);
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
    public String getHreflang() {
        return this.getAttribute("hreflang");
    }

    @Override
    public void setHreflang(String hreflang) {
        this.setAttribute("hreflang", hreflang);
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
    public String getRel() {
        return this.getAttribute("rel");
    }

    @Override
    public void setRel(String rel) {
        this.setAttribute("rel", rel);
    }

    @Override
    public String getRev() {
        return this.getAttribute("rev");
    }

    @Override
    public void setRev(String rev) {
        this.setAttribute("rev", rev);
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

    @Override
    public String getType() {
        return this.getAttribute("type");
    }

    @Override
    public void setType(String type) {
        this.setAttribute("type", type);
    }

    @Override
    public void blur() {
    }

    @Override
    public void focus() {
    }

    public HTMLAnchorElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

