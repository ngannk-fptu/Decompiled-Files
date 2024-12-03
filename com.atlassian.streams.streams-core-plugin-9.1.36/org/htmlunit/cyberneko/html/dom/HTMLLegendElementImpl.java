/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.html.HTMLLegendElement;

public class HTMLLegendElementImpl
extends HTMLElementImpl
implements HTMLLegendElement {
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
    public String getAlign() {
        return this.getAttribute("align");
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    public HTMLLegendElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

