/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Rectangle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.render.BlockBox;

public class ViewportBox
extends BlockBox {
    private final Rectangle _viewport;

    public ViewportBox(Rectangle viewport) {
        this._viewport = viewport;
    }

    @Override
    public int getWidth() {
        return this._viewport.width;
    }

    @Override
    public int getHeight() {
        return this._viewport.height;
    }

    @Override
    public int getContentWidth() {
        return this._viewport.width;
    }

    @Override
    public Rectangle getContentAreaEdge(int left, int top, CssContext cssCtx) {
        return new Rectangle(-this._viewport.x, -this._viewport.y, this._viewport.width, this._viewport.height);
    }

    @Override
    public Rectangle getPaddingEdge(int left, int top, CssContext cssCtx) {
        return new Rectangle(-this._viewport.x, -this._viewport.y, this._viewport.width, this._viewport.height);
    }

    @Override
    protected int getPaddingWidth(CssContext cssCtx) {
        return this._viewport.width;
    }

    @Override
    public BlockBox copyOf() {
        throw new IllegalArgumentException("cannot be copied");
    }

    @Override
    public boolean isAutoHeight() {
        return false;
    }

    @Override
    protected int getCSSHeight(CssContext c) {
        return this._viewport.height;
    }

    @Override
    protected boolean isInitialContainingBlock() {
        return true;
    }

    public Rectangle getExtents() {
        return this._viewport;
    }
}

