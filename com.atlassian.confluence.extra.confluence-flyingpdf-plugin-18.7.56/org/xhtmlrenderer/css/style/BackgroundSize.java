/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import org.xhtmlrenderer.css.parser.PropertyValue;

public class BackgroundSize {
    private boolean _contain;
    private boolean _cover;
    private boolean _bothAuto;
    private PropertyValue _width;
    private PropertyValue _height;

    public BackgroundSize(boolean contain, boolean cover, boolean bothAuto) {
        this._contain = contain;
        this._cover = cover;
        this._bothAuto = bothAuto;
    }

    public BackgroundSize(PropertyValue width, PropertyValue height) {
        this._width = width;
        this._height = height;
    }

    public boolean isContain() {
        return this._contain;
    }

    public boolean isCover() {
        return this._cover;
    }

    public boolean isBothAuto() {
        return this._bothAuto;
    }

    public PropertyValue getWidth() {
        return this._width;
    }

    public PropertyValue getHeight() {
        return this._height;
    }
}

