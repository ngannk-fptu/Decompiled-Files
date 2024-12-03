/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLLinkElement;

public class HTMLLinkElementImpl
extends HTMLElementImpl
implements HTMLLinkElement {
    private static final long serialVersionUID = 874345520063418879L;

    @Override
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean bl) {
        this.setAttribute("disabled", bl);
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
    public String getMedia() {
        return this.getAttribute("media");
    }

    @Override
    public void setMedia(String string) {
        this.setAttribute("media", string);
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

    public HTMLLinkElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

