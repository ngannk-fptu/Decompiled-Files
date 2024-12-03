/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

public class InlineBoxMeasurements {
    private int _textTop;
    private int _textBottom;
    private int _baseline;
    private int _inlineTop;
    private int _inlineBottom;
    private int _paintingTop;
    private int _paintingBottom;

    public int getBaseline() {
        return this._baseline;
    }

    public void setBaseline(int baseline) {
        this._baseline = baseline;
    }

    public int getInlineBottom() {
        return this._inlineBottom;
    }

    public void setInlineBottom(int inlineBottom) {
        this._inlineBottom = inlineBottom;
    }

    public int getInlineTop() {
        return this._inlineTop;
    }

    public void setInlineTop(int inlineTop) {
        this._inlineTop = inlineTop;
    }

    public int getTextBottom() {
        return this._textBottom;
    }

    public void setTextBottom(int textBottom) {
        this._textBottom = textBottom;
    }

    public int getTextTop() {
        return this._textTop;
    }

    public void setTextTop(int textTop) {
        this._textTop = textTop;
    }

    public int getPaintingBottom() {
        return this._paintingBottom;
    }

    public void setPaintingBottom(int paintingBottom) {
        this._paintingBottom = paintingBottom;
    }

    public int getPaintingTop() {
        return this._paintingTop;
    }

    public void setPaintingTop(int paintingTop) {
        this._paintingTop = paintingTop;
    }
}

