/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.newtable;

import org.w3c.dom.Element;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.Styleable;

public class TableColumn
implements Styleable {
    private Element _element;
    private CalculatedStyle _style;
    private TableColumn _parent;

    public TableColumn() {
    }

    public TableColumn(Element element, CalculatedStyle style) {
        this._element = element;
        this._style = style;
    }

    @Override
    public Element getElement() {
        return this._element;
    }

    @Override
    public String getPseudoElementOrClass() {
        return null;
    }

    @Override
    public CalculatedStyle getStyle() {
        return this._style;
    }

    @Override
    public void setElement(Element e) {
        this._element = e;
    }

    @Override
    public void setStyle(CalculatedStyle style) {
        this._style = style;
    }

    public TableColumn getParent() {
        return this._parent;
    }

    public void setParent(TableColumn parent) {
        this._parent = parent;
    }
}

