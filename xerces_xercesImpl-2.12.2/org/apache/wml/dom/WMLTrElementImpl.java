/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLTrElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLTrElementImpl
extends WMLElementImpl
implements WMLTrElement {
    private static final long serialVersionUID = -4304021232051604343L;

    public WMLTrElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
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
}

