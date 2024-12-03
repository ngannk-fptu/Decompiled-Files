/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import org.xhtmlrenderer.layout.FloatManager;
import org.xhtmlrenderer.layout.Layer;

public class FloatedBoxData {
    private Layer _drawingLayer;
    private FloatManager _manager;
    private int _marginFromSibling;

    public Layer getDrawingLayer() {
        return this._drawingLayer;
    }

    public void setDrawingLayer(Layer drawingLayer) {
        this._drawingLayer = drawingLayer;
    }

    public FloatManager getManager() {
        return this._manager;
    }

    public void setManager(FloatManager manager) {
        this._manager = manager;
    }

    public int getMarginFromSibling() {
        return this._marginFromSibling;
    }

    public void setMarginFromSibling(int marginFromSibling) {
        this._marginFromSibling = marginFromSibling;
    }
}

