/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLCollectionImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableRowElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;

public class HTMLTableSectionElementImpl
extends HTMLElementImpl
implements HTMLTableSectionElement {
    private HTMLCollectionImpl rows_;

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    @Override
    public String getCh() {
        String ch = this.getAttribute("char");
        if (ch.length() > 1) {
            ch = ch.substring(0, 1);
        }
        return ch;
    }

    @Override
    public void setCh(String ch) {
        if (ch != null && ch.length() > 1) {
            ch = ch.substring(0, 1);
        }
        this.setAttribute("char", ch);
    }

    @Override
    public String getChOff() {
        return this.getAttribute("charoff");
    }

    @Override
    public void setChOff(String chOff) {
        this.setAttribute("charoff", chOff);
    }

    @Override
    public String getVAlign() {
        return this.capitalize(this.getAttribute("valign"));
    }

    @Override
    public void setVAlign(String vAlign) {
        this.setAttribute("valign", vAlign);
    }

    @Override
    public HTMLCollection getRows() {
        if (this.rows_ == null) {
            this.rows_ = new HTMLCollectionImpl(this, 7);
        }
        return this.rows_;
    }

    @Override
    public HTMLElement insertRow(int index) {
        HTMLTableRowElementImpl newRow = new HTMLTableRowElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TR");
        newRow.insertCell(0);
        if (this.insertRowX(index, newRow) >= 0) {
            this.appendChild(newRow);
        }
        return newRow;
    }

    int insertRowX(int index, HTMLTableRowElementImpl newRow) {
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof HTMLTableRowElement)) continue;
            if (index == 0) {
                this.insertBefore(newRow, child);
                return -1;
            }
            --index;
        }
        return index;
    }

    @Override
    public void deleteRow(int index) {
        this.deleteRowX(index);
    }

    int deleteRowX(int index) {
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof HTMLTableRowElement)) continue;
            if (index == 0) {
                this.removeChild(child);
                return -1;
            }
            --index;
        }
        return index;
    }

    @Override
    public Node cloneNode(boolean deep) {
        HTMLTableSectionElementImpl clonedNode = (HTMLTableSectionElementImpl)super.cloneNode(deep);
        clonedNode.rows_ = null;
        return clonedNode;
    }

    public HTMLTableSectionElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

