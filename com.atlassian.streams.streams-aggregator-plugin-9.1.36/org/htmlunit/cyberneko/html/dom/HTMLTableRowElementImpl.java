/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLCollectionImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableCellElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;

public class HTMLTableRowElementImpl
extends HTMLElementImpl
implements HTMLTableRowElement {
    private HTMLCollection cells_;

    @Override
    public int getRowIndex() {
        Node parent = this.getParentNode();
        if (parent instanceof HTMLTableSectionElement) {
            parent = parent.getParentNode();
        }
        if (parent instanceof HTMLTableElement) {
            return this.getRowIndex(parent);
        }
        return -1;
    }

    @Override
    public int getSectionRowIndex() {
        Node parent = this.getParentNode();
        if (parent instanceof HTMLTableSectionElement) {
            return this.getRowIndex(parent);
        }
        return -1;
    }

    int getRowIndex(Node parent) {
        NodeList rows = ((HTMLElement)parent).getElementsByTagName("TR");
        for (int i = 0; i < rows.getLength(); ++i) {
            if (rows.item(i) != this) continue;
            return i;
        }
        return -1;
    }

    @Override
    public HTMLCollection getCells() {
        if (this.cells_ == null) {
            this.cells_ = new HTMLCollectionImpl(this, -3);
        }
        return this.cells_;
    }

    @Override
    public HTMLElement insertCell(int index) {
        HTMLTableCellElementImpl newCell = new HTMLTableCellElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TD");
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof HTMLTableCellElement)) continue;
            if (index == 0) {
                this.insertBefore(newCell, child);
                return newCell;
            }
            --index;
        }
        this.appendChild(newCell);
        return newCell;
    }

    @Override
    public void deleteCell(int index) {
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof HTMLTableCellElement)) continue;
            if (index == 0) {
                this.removeChild(child);
                return;
            }
            --index;
        }
    }

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String align) {
        this.setAttribute("align", align);
    }

    @Override
    public String getBgColor() {
        return this.getAttribute("bgcolor");
    }

    @Override
    public void setBgColor(String bgColor) {
        this.setAttribute("bgcolor", bgColor);
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
    public Node cloneNode(boolean deep) {
        HTMLTableRowElementImpl clonedNode = (HTMLTableRowElementImpl)super.cloneNode(deep);
        clonedNode.cells_ = null;
        return clonedNode;
    }

    public HTMLTableRowElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

