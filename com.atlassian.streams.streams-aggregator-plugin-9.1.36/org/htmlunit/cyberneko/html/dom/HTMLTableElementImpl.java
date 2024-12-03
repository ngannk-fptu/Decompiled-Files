/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLCollectionImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableCaptionElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableRowElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableSectionElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableCaptionElement;
import org.w3c.dom.html.HTMLTableElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;

public class HTMLTableElementImpl
extends HTMLElementImpl
implements HTMLTableElement {
    private HTMLCollectionImpl rows_;
    private HTMLCollectionImpl bodies_;

    @Override
    public synchronized HTMLTableCaptionElement getCaption() {
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof HTMLTableCaptionElement) || !child.getNodeName().equals("CAPTION")) continue;
            return (HTMLTableCaptionElement)child;
        }
        return null;
    }

    @Override
    public synchronized void setCaption(HTMLTableCaptionElement caption) {
        if (caption != null && !caption.getTagName().equals("CAPTION")) {
            throw new IllegalArgumentException("HTM016 Argument 'caption' is not an element of type <CAPTION>.");
        }
        this.deleteCaption();
        if (caption != null) {
            this.appendChild(caption);
        }
    }

    @Override
    public synchronized HTMLElement createCaption() {
        HTMLTableCaptionElement section = this.getCaption();
        if (section != null) {
            return section;
        }
        section = new HTMLTableCaptionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "CAPTION");
        this.appendChild(section);
        return section;
    }

    @Override
    public synchronized void deleteCaption() {
        HTMLTableCaptionElement old = this.getCaption();
        if (old != null) {
            this.removeChild(old);
        }
    }

    @Override
    public synchronized HTMLTableSectionElement getTHead() {
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof HTMLTableSectionElement) || !child.getNodeName().equals("THEAD")) continue;
            return (HTMLTableSectionElement)child;
        }
        return null;
    }

    @Override
    public synchronized void setTHead(HTMLTableSectionElement tHead) {
        if (tHead != null && !tHead.getTagName().equals("THEAD")) {
            throw new IllegalArgumentException("HTM017 Argument 'tHead' is not an element of type <THEAD>.");
        }
        this.deleteTHead();
        if (tHead != null) {
            this.appendChild(tHead);
        }
    }

    @Override
    public synchronized HTMLElement createTHead() {
        HTMLTableSectionElement section = this.getTHead();
        if (section != null) {
            return section;
        }
        section = new HTMLTableSectionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "THEAD");
        this.appendChild(section);
        return section;
    }

    @Override
    public synchronized void deleteTHead() {
        HTMLTableSectionElement old = this.getTHead();
        if (old != null) {
            this.removeChild(old);
        }
    }

    @Override
    public synchronized HTMLTableSectionElement getTFoot() {
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof HTMLTableSectionElement) || !child.getNodeName().equals("TFOOT")) continue;
            return (HTMLTableSectionElement)child;
        }
        return null;
    }

    @Override
    public synchronized void setTFoot(HTMLTableSectionElement tFoot) {
        if (tFoot != null && !tFoot.getTagName().equals("TFOOT")) {
            throw new IllegalArgumentException("HTM018 Argument 'tFoot' is not an element of type <TFOOT>.");
        }
        this.deleteTFoot();
        if (tFoot != null) {
            this.appendChild(tFoot);
        }
    }

    @Override
    public synchronized HTMLElement createTFoot() {
        HTMLTableSectionElement section = this.getTFoot();
        if (section != null) {
            return section;
        }
        section = new HTMLTableSectionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TFOOT");
        this.appendChild(section);
        return section;
    }

    @Override
    public synchronized void deleteTFoot() {
        HTMLTableSectionElement old = this.getTFoot();
        if (old != null) {
            this.removeChild(old);
        }
    }

    @Override
    public HTMLCollection getRows() {
        if (this.rows_ == null) {
            this.rows_ = new HTMLCollectionImpl(this, 7);
        }
        return this.rows_;
    }

    @Override
    public HTMLCollection getTBodies() {
        if (this.bodies_ == null) {
            this.bodies_ = new HTMLCollectionImpl(this, -2);
        }
        return this.bodies_;
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
    public String getBorder() {
        return this.getAttribute("border");
    }

    @Override
    public void setBorder(String border) {
        this.setAttribute("border", border);
    }

    @Override
    public String getCellPadding() {
        return this.getAttribute("cellpadding");
    }

    @Override
    public void setCellPadding(String cellPadding) {
        this.setAttribute("cellpadding", cellPadding);
    }

    @Override
    public String getCellSpacing() {
        return this.getAttribute("cellspacing");
    }

    @Override
    public void setCellSpacing(String cellSpacing) {
        this.setAttribute("cellspacing", cellSpacing);
    }

    @Override
    public String getFrame() {
        return this.capitalize(this.getAttribute("frame"));
    }

    @Override
    public void setFrame(String frame) {
        this.setAttribute("frame", frame);
    }

    @Override
    public String getRules() {
        return this.capitalize(this.getAttribute("rules"));
    }

    @Override
    public void setRules(String rules) {
        this.setAttribute("rules", rules);
    }

    @Override
    public String getSummary() {
        return this.getAttribute("summary");
    }

    @Override
    public void setSummary(String summary) {
        this.setAttribute("summary", summary);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        this.setAttribute("width", width);
    }

    @Override
    public HTMLElement insertRow(int index) {
        HTMLTableRowElementImpl newRow = new HTMLTableRowElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TR");
        this.insertRowX(index, newRow);
        return newRow;
    }

    void insertRowX(int index, HTMLTableRowElementImpl newRow) {
        Node lastSection = null;
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof HTMLTableRowElement) {
                if (index != 0) continue;
                this.insertBefore(newRow, child);
                return;
            }
            if (!(child instanceof HTMLTableSectionElementImpl)) continue;
            lastSection = child;
            if ((index = ((HTMLTableSectionElementImpl)child).insertRowX(index, newRow)) >= 0) continue;
            return;
        }
        if (lastSection != null) {
            lastSection.appendChild(newRow);
        } else {
            this.appendChild(newRow);
        }
    }

    @Override
    public synchronized void deleteRow(int index) {
        for (Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof HTMLTableRowElement) {
                if (index == 0) {
                    this.removeChild(child);
                    return;
                }
                --index;
                continue;
            }
            if (!(child instanceof HTMLTableSectionElementImpl) || (index = ((HTMLTableSectionElementImpl)child).deleteRowX(index)) >= 0) continue;
            return;
        }
    }

    @Override
    public Node cloneNode(boolean deep) {
        HTMLTableElementImpl clonedNode = (HTMLTableElementImpl)super.cloneNode(deep);
        clonedNode.rows_ = null;
        clonedNode.bodies_ = null;
        return clonedNode;
    }

    public HTMLTableElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

