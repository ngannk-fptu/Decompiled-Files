/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLCollectionImpl;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLMapElement;

public class HTMLMapElementImpl
extends HTMLElementImpl
implements HTMLMapElement {
    private static final long serialVersionUID = 7520887584251976392L;
    private HTMLCollection _areas;

    @Override
    public HTMLCollection getAreas() {
        if (this._areas == null) {
            this._areas = new HTMLCollectionImpl(this, -1);
        }
        return this._areas;
    }

    @Override
    public String getName() {
        return this.getAttribute("name");
    }

    @Override
    public void setName(String string) {
        this.setAttribute("name", string);
    }

    @Override
    public Node cloneNode(boolean bl) {
        HTMLMapElementImpl hTMLMapElementImpl = (HTMLMapElementImpl)super.cloneNode(bl);
        hTMLMapElementImpl._areas = null;
        return hTMLMapElementImpl;
    }

    public HTMLMapElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

