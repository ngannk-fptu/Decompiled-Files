/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableRowElement;

public class HTMLTableCellElementImpl
extends HTMLElementImpl
implements HTMLTableCellElement {
    private static final long serialVersionUID = -2406518157464313922L;

    @Override
    public int getCellIndex() {
        Node node = this.getParentNode();
        int n = 0;
        if (node instanceof HTMLTableRowElement) {
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (!(node2 instanceof HTMLTableCellElement)) continue;
                if (node2 == this) {
                    return n;
                }
                ++n;
            }
        }
        return -1;
    }

    public void setCellIndex(int n) {
        Node node = this.getParentNode();
        if (node instanceof HTMLTableRowElement) {
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (!(node2 instanceof HTMLTableCellElement)) continue;
                if (n == 0) {
                    if (this != node2) {
                        node.insertBefore(this, node2);
                    }
                    return;
                }
                --n;
            }
        }
        node.appendChild(this);
    }

    @Override
    public String getAbbr() {
        return this.getAttribute("abbr");
    }

    @Override
    public void setAbbr(String string) {
        this.setAttribute("abbr", string);
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
    public String getAxis() {
        return this.getAttribute("axis");
    }

    @Override
    public void setAxis(String string) {
        this.setAttribute("axis", string);
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
    public int getColSpan() {
        return this.getInteger(this.getAttribute("colspan"));
    }

    @Override
    public void setColSpan(int n) {
        this.setAttribute("colspan", String.valueOf(n));
    }

    @Override
    public String getHeaders() {
        return this.getAttribute("headers");
    }

    @Override
    public void setHeaders(String string) {
        this.setAttribute("headers", string);
    }

    @Override
    public String getHeight() {
        return this.getAttribute("height");
    }

    @Override
    public void setHeight(String string) {
        this.setAttribute("height", string);
    }

    @Override
    public boolean getNoWrap() {
        return this.getBinary("nowrap");
    }

    @Override
    public void setNoWrap(boolean bl) {
        this.setAttribute("nowrap", bl);
    }

    @Override
    public int getRowSpan() {
        return this.getInteger(this.getAttribute("rowspan"));
    }

    @Override
    public void setRowSpan(int n) {
        this.setAttribute("rowspan", String.valueOf(n));
    }

    @Override
    public String getScope() {
        return this.getAttribute("scope");
    }

    @Override
    public void setScope(String string) {
        this.setAttribute("scope", string);
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
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String string) {
        this.setAttribute("width", string);
    }

    public HTMLTableCellElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string);
    }
}

