/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.w3c.dom.html.HTMLLabelElement;

public class HTMLLabelElementImpl
extends HTMLElementImpl
implements HTMLLabelElement,
HTMLFormControl {
    @Override
    public String getAccessKey() {
        String accessKey = this.getAttribute("accesskey");
        if (accessKey != null && accessKey.length() > 1) {
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
    public String getHtmlFor() {
        return this.getAttribute("for");
    }

    @Override
    public void setHtmlFor(String htmlFor) {
        this.setAttribute("for", htmlFor);
    }

    public HTMLLabelElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

