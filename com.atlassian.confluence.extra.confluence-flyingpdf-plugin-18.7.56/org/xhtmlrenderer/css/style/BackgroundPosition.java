/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import org.xhtmlrenderer.css.parser.PropertyValue;

public class BackgroundPosition {
    private final PropertyValue _horizontal;
    private final PropertyValue _vertical;

    public BackgroundPosition(PropertyValue horizontal, PropertyValue vertical) {
        this._horizontal = horizontal;
        this._vertical = vertical;
    }

    public PropertyValue getHorizontal() {
        return this._horizontal;
    }

    public PropertyValue getVertical() {
        return this._vertical;
    }
}

