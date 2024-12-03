/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableRowElement;

public class HTMLTableCellElementImpl
extends HTMLElementImpl
implements HTMLTableCellElement {
    @Override
    public int getCellIndex() {
        Node parent = this.getParentNode();
        int index = 0;
        if (parent instanceof HTMLTableRowElement) {
            for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (!(child instanceof HTMLTableCellElement)) continue;
                if (child == this) {
                    return index;
                }
                ++index;
            }
        }
        return -1;
    }

    @Override
    public String getAbbr() {
        return this.getAttribute("abbr");
    }

    @Override
    public void setAbbr(String abbr) {
        this.setAttribute("abbr", abbr);
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
    public String getAxis() {
        return this.getAttribute("axis");
    }

    @Override
    public void setAxis(String axis) {
        this.setAttribute("axis", axis);
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
    public int getColSpan() {
        return this.getInteger(this.getAttribute("colspan"));
    }

    @Override
    public void setColSpan(int colspan) {
        this.setAttribute("colspan", String.valueOf(colspan));
    }

    @Override
    public String getHeaders() {
        return this.getAttribute("headers");
    }

    @Override
    public void setHeaders(String headers) {
        this.setAttribute("headers", headers);
    }

    @Override
    public String getHeight() {
        return this.getAttribute("height");
    }

    @Override
    public void setHeight(String height) {
        this.setAttribute("height", height);
    }

    @Override
    public boolean getNoWrap() {
        return this.getBinary("nowrap");
    }

    @Override
    public void setNoWrap(boolean noWrap) {
        this.setAttribute("nowrap", noWrap);
    }

    @Override
    public int getRowSpan() {
        return this.getInteger(this.getAttribute("rowspan"));
    }

    @Override
    public void setRowSpan(int rowspan) {
        this.setAttribute("rowspan", String.valueOf(rowspan));
    }

    @Override
    public String getScope() {
        return this.getAttribute("scope");
    }

    @Override
    public void setScope(String scope) {
        this.setAttribute("scope", scope);
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
    public String getWidth() {
        return this.getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        this.setAttribute("width", width);
    }

    public HTMLTableCellElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

