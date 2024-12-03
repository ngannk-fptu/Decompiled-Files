/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Rectangle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.render.BlockBox;

public class MarginBox
extends BlockBox {
    private Rectangle _bounds;

    public MarginBox(Rectangle bounds) {
        this._bounds = bounds;
    }

    @Override
    public int getWidth() {
        return this._bounds.width;
    }

    @Override
    public int getHeight() {
        return this._bounds.height;
    }

    @Override
    public int getContentWidth() {
        return this._bounds.width;
    }

    @Override
    public Rectangle getContentAreaEdge(int left, int top, CssContext cssCtx) {
        return new Rectangle(-this._bounds.x, -this._bounds.y, this._bounds.width, this._bounds.height);
    }

    @Override
    public Rectangle getPaddingEdge(int left, int top, CssContext cssCtx) {
        return new Rectangle(-this._bounds.x, -this._bounds.y, this._bounds.width, this._bounds.height);
    }

    @Override
    protected int getContainingBlockWidth() {
        return this._bounds.width;
    }

    @Override
    protected int getPaddingWidth(CssContext cssCtx) {
        return this._bounds.width;
    }

    @Override
    public BlockBox copyOf() {
        throw new IllegalArgumentException("cannot be copied");
    }
}

