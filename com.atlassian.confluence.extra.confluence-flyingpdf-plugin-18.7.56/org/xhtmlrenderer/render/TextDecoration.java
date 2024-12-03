/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import org.xhtmlrenderer.css.constants.IdentValue;

public class TextDecoration {
    private IdentValue _identValue;
    private int _offset;
    private int _thickness;

    public TextDecoration(IdentValue identValue) {
        this._identValue = identValue;
    }

    public int getOffset() {
        return this._offset;
    }

    public void setOffset(int offset) {
        this._offset = offset;
    }

    public int getThickness() {
        return this._thickness;
    }

    public void setThickness(int thickness) {
        this._thickness = thickness == 0 ? 1 : thickness;
    }

    public IdentValue getIdentValue() {
        return this._identValue;
    }
}

