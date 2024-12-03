/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLTemplateElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLTemplateElementImpl
extends WMLElementImpl
implements WMLTemplateElement {
    private static final long serialVersionUID = 4231732841621131049L;

    public WMLTemplateElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setOnTimer(String string) {
        this.setAttribute("ontimer", string);
    }

    @Override
    public String getOnTimer() {
        return this.getAttribute("ontimer");
    }

    @Override
    public void setOnEnterBackward(String string) {
        this.setAttribute("onenterbackward", string);
    }

    @Override
    public String getOnEnterBackward() {
        return this.getAttribute("onenterbackward");
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
    public void setOnEnterForward(String string) {
        this.setAttribute("onenterforward", string);
    }

    @Override
    public String getOnEnterForward() {
        return this.getAttribute("onenterforward");
    }
}

