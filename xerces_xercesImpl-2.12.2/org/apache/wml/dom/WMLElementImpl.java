/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml.dom;

import org.apache.wml.WMLElement;
import org.apache.wml.dom.WMLDocumentImpl;
import org.apache.xerces.dom.ElementImpl;

public class WMLElementImpl
extends ElementImpl
implements WMLElement {
    private static final long serialVersionUID = 3440984702956371604L;

    public WMLElementImpl(WMLDocumentImpl wMLDocumentImpl, String string) {
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

    public void setXmlLang(String string) {
        this.setAttribute("xml:lang", string);
    }

    public String getXmlLang() {
        return this.getAttribute("xml:lang");
    }

    @Override
    public void setId(String string) {
        this.setAttribute("id", string);
    }

    @Override
    public String getId() {
        return this.getAttribute("id");
    }

    void setAttribute(String string, boolean bl) {
        this.setAttribute(string, bl ? "true" : "false");
    }

    boolean getAttribute(String string, boolean bl) {
        boolean bl2 = bl;
        String string2 = this.getAttribute("emptyok");
        if (string2 != null && string2.equals("true")) {
            bl2 = true;
        }
        return bl2;
    }

    void setAttribute(String string, int n) {
        this.setAttribute(string, n + "");
    }

    int getAttribute(String string, int n) {
        int n2 = n;
        String string2 = this.getAttribute("emptyok");
        if (string2 != null) {
            n2 = Integer.parseInt(string2);
        }
        return n2;
    }
}

