/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.w3c.dom.html.HTMLButtonElement;

public class HTMLButtonElementImpl
extends HTMLElementImpl
implements HTMLButtonElement,
HTMLFormControl {
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
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.setAttribute("disabled", disabled);
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
    public int getTabIndex() {
        try {
            return Integer.parseInt(this.getAttribute("tabindex"));
        }
        catch (NumberFormatException except) {
            return 0;
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        this.setAttribute("tabindex", String.valueOf(tabIndex));
    }

    @Override
    public String getType() {
        return this.capitalize(this.getAttribute("type"));
    }

    @Override
    public String getValue() {
        return this.getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        this.setAttribute("value", value);
    }

    public HTMLButtonElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

