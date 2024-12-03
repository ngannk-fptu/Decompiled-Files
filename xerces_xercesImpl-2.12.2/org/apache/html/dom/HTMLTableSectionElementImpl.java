/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLCollectionImpl;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.apache.html.dom.HTMLTableRowElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;

public class HTMLTableSectionElementImpl
extends HTMLElementImpl
implements HTMLTableSectionElement {
    private static final long serialVersionUID = 1016412997716618027L;
    private HTMLCollectionImpl _rows;

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    @Override
    public String getCh() {
        String string = this.getAttribute("char");
        if (string != null && string.length() > 1) {
            string = string.substring(0, 1);
        }
        return string;
    }

    @Override
    public void setCh(String string) {
        if (string != null && string.length() > 1) {
            string = string.substring(0, 1);
        }
        this.setAttribute("char", string);
    }

    @Override
    public String getChOff() {
        return this.getAttribute("charoff");
    }

    @Override
    public void setChOff(String string) {
        this.setAttribute("charoff", string);
    }

    @Override
    public String getVAlign() {
        return this.capitalize(this.getAttribute("valign"));
    }

    @Override
    public void setVAlign(String string) {
        this.setAttribute("valign", string);
    }

    @Override
    public HTMLCollection getRows() {
        if (this._rows == null) {
            this._rows = new HTMLCollectionImpl(this, 7);
        }
        return this._rows;
    }

    @Override
    public HTMLElement insertRow(int n) {
        HTMLTableRowElementImpl hTMLTableRowElementImpl = new HTMLTableRowElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TR");
        hTMLTableRowElementImpl.insertCell(0);
        if (this.insertRowX(n, hTMLTableRowElementImpl) >= 0) {
            this.appendChild(hTMLTableRowElementImpl);
        }
        return hTMLTableRowElementImpl;
    }

    int insertRowX(int n, HTMLTableRowElementImpl hTMLTableRowElementImpl) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof HTMLTableRowElement)) continue;
            if (n == 0) {
                this.insertBefore(hTMLTableRowElementImpl, node);
                return -1;
            }
            --n;
        }
        return n;
    }

    @Override
    public void deleteRow(int n) {
        this.deleteRowX(n);
    }

    int deleteRowX(int n) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof HTMLTableRowElement)) continue;
            if (n == 0) {
                this.removeChild(node);
                return -1;
            }
            --n;
        }
        return n;
    }

    @Override
    public Node cloneNode(boolean bl) {
        HTMLTableSectionElementImpl hTMLTableSectionElementImpl = (HTMLTableSectionElementImpl)super.cloneNode(bl);
        hTMLTableSectionElementImpl._rows = null;
        return hTMLTableSectionElementImpl;
    }

    public HTMLTableSectionElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

