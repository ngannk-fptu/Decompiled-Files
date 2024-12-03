/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLOneventElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLOneventElementImpl
extends WMLElementImpl
implements WMLOneventElement {
    private static final long serialVersionUID = -4279215241146570871L;

    public WMLOneventElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setClassName(String string) {
        this.setAttribute("class", string);
    }

    @Override
    public String getClassName() {
        return this.getAttribute("class");
    }

    @Override
    public void setId(String string) {
        this.setAttribute("id", string);
    }

    @Override
    public String getId() {
        return this.getAttribute("id");
    }

    @Override
    public void setType(String string) {
        this.setAttribute("type", string);
    }

    @Override
    public String getType() {
        return this.getAttribute("type");
    }
}

