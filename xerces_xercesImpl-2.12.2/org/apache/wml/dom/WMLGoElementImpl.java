/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLGoElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.wml.dom.WMLElementImpl;

public class WMLGoElementImpl
extends WMLElementImpl
implements WMLGoElement {
    private static final long serialVersionUID = -2052250142899797905L;

    public WMLGoElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
        super(wMLDocumentImpl, string);
    }

    @Override
    public void setSendreferer(String string) {
        this.setAttribute("sendreferer", string);
    }

    @Override
    public String getSendreferer() {
        return this.getAttribute("sendreferer");
    }

    @Override
    public void setAcceptCharset(String string) {
        this.setAttribute("accept-charset", string);
    }

    @Override
    public String getAcceptCharset() {
        return this.getAttribute("accept-charset");
    }

    @Override
    public void setHref(String string) {
        this.setAttribute("href", string);
    }

    @Override
    public String getHref() {
        return this.getAttribute("href");
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
    public void setMethod(String string) {
        this.setAttribute("method", string);
    }

    @Override
    public String getMethod() {
        return this.getAttribute("method");
    }
}

