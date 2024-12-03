/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLCardElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLCardElementImpl
extends WMLElementImpl
implements WMLCardElement {
    private static final long serialVersionUID = -3571126568344328924L;

    public WMLCardElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
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
    public void setOrdered(boolean bl) {
        this.setAttribute("ordered", bl);
    }

    @Override
    public boolean getOrdered() {
        return this.getAttribute("ordered", true);
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
    public void setXmlLang(String string) {
        this.setAttribute("xml:lang", string);
    }

    @Override
    public String getXmlLang() {
        return this.getAttribute("xml:lang");
    }

    @Override
    public void setTitle(String string) {
        this.setAttribute("title", string);
    }

    @Override
    public String getTitle() {
        return this.getAttribute("title");
    }

    @Override
    public void setNewContext(boolean bl) {
        this.setAttribute("newcontext", bl);
    }

    @Override
    public boolean getNewContext() {
        return this.getAttribute("newcontext", false);
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

