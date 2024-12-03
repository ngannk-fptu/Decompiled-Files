/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLLinkElement;

public class HTMLLinkElementImpl
extends HTMLElementImpl
implements HTMLLinkElement {
    @Override
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.setAttribute("disabled", disabled);
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
    public String getMedia() {
        return this.getAttribute("media");
    }

    @Override
    public void setMedia(String media) {
        this.setAttribute("media", media);
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

    public HTMLLinkElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

