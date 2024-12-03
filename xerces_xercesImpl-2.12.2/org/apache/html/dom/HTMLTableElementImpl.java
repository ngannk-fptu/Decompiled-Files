/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLCollectionImpl;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.apache.html.dom.HTMLTableCaptionElementImpl;
import org.apache.html.dom.HTMLTableRowElementImpl;
import org.apache.html.dom.HTMLTableSectionElementImpl;
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
    private static final long serialVersionUID = -1824053099870917532L;
    private HTMLCollectionImpl _rows;
    private HTMLCollectionImpl _bodies;

    @Override
    public synchronized HTMLTableCaptionElement getCaption() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof HTMLTableCaptionElement) || !node.getNodeName().equals("CAPTION")) continue;
            return (HTMLTableCaptionElement)node;
        }
        return null;
    }

    @Override
    public synchronized void setCaption(HTMLTableCaptionElement hTMLTableCaptionElement) {
        if (hTMLTableCaptionElement != null && !hTMLTableCaptionElement.getTagName().equals("CAPTION")) {
            throw new IllegalArgumentException("HTM016 Argument 'caption' is not an element of type <CAPTION>.");
        }
        this.deleteCaption();
        if (hTMLTableCaptionElement != null) {
            this.appendChild(hTMLTableCaptionElement);
        }
    }

    @Override
    public synchronized HTMLElement createCaption() {
        HTMLTableCaptionElement hTMLTableCaptionElement = this.getCaption();
        if (hTMLTableCaptionElement != null) {
            return hTMLTableCaptionElement;
        }
        hTMLTableCaptionElement = new HTMLTableCaptionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "CAPTION");
        this.appendChild(hTMLTableCaptionElement);
        return hTMLTableCaptionElement;
    }

    @Override
    public synchronized void deleteCaption() {
        HTMLTableCaptionElement hTMLTableCaptionElement = this.getCaption();
        if (hTMLTableCaptionElement != null) {
            this.removeChild(hTMLTableCaptionElement);
        }
    }

    @Override
    public synchronized HTMLTableSectionElement getTHead() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof HTMLTableSectionElement) || !node.getNodeName().equals("THEAD")) continue;
            return (HTMLTableSectionElement)node;
        }
        return null;
    }

    @Override
    public synchronized void setTHead(HTMLTableSectionElement hTMLTableSectionElement) {
        if (hTMLTableSectionElement != null && !hTMLTableSectionElement.getTagName().equals("THEAD")) {
            throw new IllegalArgumentException("HTM017 Argument 'tHead' is not an element of type <THEAD>.");
        }
        this.deleteTHead();
        if (hTMLTableSectionElement != null) {
            this.appendChild(hTMLTableSectionElement);
        }
    }

    @Override
    public synchronized HTMLElement createTHead() {
        HTMLTableSectionElement hTMLTableSectionElement = this.getTHead();
        if (hTMLTableSectionElement != null) {
            return hTMLTableSectionElement;
        }
        hTMLTableSectionElement = new HTMLTableSectionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "THEAD");
        this.appendChild(hTMLTableSectionElement);
        return hTMLTableSectionElement;
    }

    @Override
    public synchronized void deleteTHead() {
        HTMLTableSectionElement hTMLTableSectionElement = this.getTHead();
        if (hTMLTableSectionElement != null) {
            this.removeChild(hTMLTableSectionElement);
        }
    }

    @Override
    public synchronized HTMLTableSectionElement getTFoot() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof HTMLTableSectionElement) || !node.getNodeName().equals("TFOOT")) continue;
            return (HTMLTableSectionElement)node;
        }
        return null;
    }

    @Override
    public synchronized void setTFoot(HTMLTableSectionElement hTMLTableSectionElement) {
        if (hTMLTableSectionElement != null && !hTMLTableSectionElement.getTagName().equals("TFOOT")) {
            throw new IllegalArgumentException("HTM018 Argument 'tFoot' is not an element of type <TFOOT>.");
        }
        this.deleteTFoot();
        if (hTMLTableSectionElement != null) {
            this.appendChild(hTMLTableSectionElement);
        }
    }

    @Override
    public synchronized HTMLElement createTFoot() {
        HTMLTableSectionElement hTMLTableSectionElement = this.getTFoot();
        if (hTMLTableSectionElement != null) {
            return hTMLTableSectionElement;
        }
        hTMLTableSectionElement = new HTMLTableSectionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TFOOT");
        this.appendChild(hTMLTableSectionElement);
        return hTMLTableSectionElement;
    }

    @Override
    public synchronized void deleteTFoot() {
        HTMLTableSectionElement hTMLTableSectionElement = this.getTFoot();
        if (hTMLTableSectionElement != null) {
            this.removeChild(hTMLTableSectionElement);
        }
    }

    @Override
    public HTMLCollection getRows() {
        if (this._rows == null) {
            this._rows = new HTMLCollectionImpl(this, 7);
        }
        return this._rows;
    }

    @Override
    public HTMLCollection getTBodies() {
        if (this._bodies == null) {
            this._bodies = new HTMLCollectionImpl(this, -2);
        }
        return this._bodies;
    }

    @Override
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }

    @Override
    public void setAlign(String string) {
        this.setAttribute("align", string);
    }

    @Override
    public String getBgColor() {
        return this.getAttribute("bgcolor");
    }

    @Override
    public void setBgColor(String string) {
        this.setAttribute("bgcolor", string);
    }

    @Override
    public String getBorder() {
        return this.getAttribute("border");
    }

    @Override
    public void setBorder(String string) {
        this.setAttribute("border", string);
    }

    @Override
    public String getCellPadding() {
        return this.getAttribute("cellpadding");
    }

    @Override
    public void setCellPadding(String string) {
        this.setAttribute("cellpadding", string);
    }

    @Override
    public String getCellSpacing() {
        return this.getAttribute("cellspacing");
    }

    @Override
    public void setCellSpacing(String string) {
        this.setAttribute("cellspacing", string);
    }

    @Override
    public String getFrame() {
        return this.capitalize(this.getAttribute("frame"));
    }

    @Override
    public void setFrame(String string) {
        this.setAttribute("frame", string);
    }

    @Override
    public String getRules() {
        return this.capitalize(this.getAttribute("rules"));
    }

    @Override
    public void setRules(String string) {
        this.setAttribute("rules", string);
    }

    @Override
    public String getSummary() {
        return this.getAttribute("summary");
    }

    @Override
    public void setSummary(String string) {
        this.setAttribute("summary", string);
    }

    @Override
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String string) {
        this.setAttribute("width", string);
    }

    @Override
    public HTMLElement insertRow(int n) {
        HTMLTableRowElementImpl hTMLTableRowElementImpl = new HTMLTableRowElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TR");
        this.insertRowX(n, hTMLTableRowElementImpl);
        return hTMLTableRowElementImpl;
    }

    void insertRowX(int n, HTMLTableRowElementImpl hTMLTableRowElementImpl) {
        Node node = null;
        for (Node node2 = this.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2 instanceof HTMLTableRowElement) {
                if (n != 0) continue;
                this.insertBefore(hTMLTableRowElementImpl, node2);
                return;
            }
            if (!(node2 instanceof HTMLTableSectionElementImpl)) continue;
            node = node2;
            if ((n = ((HTMLTableSectionElementImpl)node2).insertRowX(n, hTMLTableRowElementImpl)) >= 0) continue;
            return;
        }
        if (node != null) {
            node.appendChild(hTMLTableRowElementImpl);
        } else {
            this.appendChild(hTMLTableRowElementImpl);
        }
    }

    @Override
    public synchronized void deleteRow(int n) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableRowElement) {
                if (n == 0) {
                    this.removeChild(node);
                    return;
                }
                --n;
                continue;
            }
            if (!(node instanceof HTMLTableSectionElementImpl) || (n = ((HTMLTableSectionElementImpl)node).deleteRowX(n)) >= 0) continue;
            return;
        }
    }

    @Override
    public Node cloneNode(boolean bl) {
        HTMLTableElementImpl hTMLTableElementImpl = (HTMLTableElementImpl)super.cloneNode(bl);
        hTMLTableElementImpl._rows = null;
        hTMLTableElementImpl._bodies = null;
        return hTMLTableElementImpl;
    }

    public HTMLTableElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

