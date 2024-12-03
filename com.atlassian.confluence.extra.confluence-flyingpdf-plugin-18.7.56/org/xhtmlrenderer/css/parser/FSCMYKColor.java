/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import org.xhtmlrenderer.css.parser.FSColor;

public class FSCMYKColor
implements FSColor {
    private final float _cyan;
    private final float _magenta;
    private final float _yellow;
    private final float _black;

    public FSCMYKColor(float c, float m, float y, float k) {
        if (c < 0.0f || c > 1.0f) {
            throw new IllegalArgumentException();
        }
        if (m < 0.0f || m > 1.0f) {
            throw new IllegalArgumentException();
        }
        if (y < 0.0f || y > 1.0f) {
            throw new IllegalArgumentException();
        }
        if (k < 0.0f || k > 1.0f) {
            throw new IllegalArgumentException();
        }
        this._cyan = c;
        this._magenta = m;
        this._yellow = y;
        this._black = k;
    }

    public float getCyan() {
        return this._cyan;
    }

    public float getMagenta() {
        return this._magenta;
    }

    public float getYellow() {
        return this._yellow;
    }

    public float getBlack() {
        return this._black;
    }

    public String toString() {
        return "cmyk(" + this._cyan + ", " + this._magenta + ", " + this._yellow + ", " + this._black + ")";
    }

    @Override
    public FSColor lightenColor() {
        return new FSCMYKColor(this._cyan * 0.8f, this._magenta * 0.8f, this._yellow * 0.8f, this._black);
    }

    @Override
    public FSColor darkenColor() {
        return new FSCMYKColor(Math.min(1.0f, this._cyan / 0.8f), Math.min(1.0f, this._magenta / 0.8f), Math.min(1.0f, this._yellow / 0.8f), this._black);
    }
}

