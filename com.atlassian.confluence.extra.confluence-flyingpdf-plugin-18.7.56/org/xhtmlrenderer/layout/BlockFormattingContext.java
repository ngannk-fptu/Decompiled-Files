/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Point;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.layout.FloatManager;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.PersistentBFC;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.LineBox;

public class BlockFormattingContext {
    private int _x = 0;
    private int _y = 0;
    private final PersistentBFC _persistentBFC;

    public BlockFormattingContext(BlockBox block, LayoutContext c) {
        this._persistentBFC = new PersistentBFC(block, c);
    }

    public Point getOffset() {
        return new Point(this._x, this._y);
    }

    public void translate(int x, int y) {
        this._x -= x;
        this._y -= y;
    }

    public FloatManager getFloatManager() {
        return this._persistentBFC.getFloatManager();
    }

    public int getLeftFloatDistance(CssContext cssCtx, LineBox line, int containingBlockWidth) {
        return this.getFloatManager().getLeftFloatDistance(cssCtx, this, line, containingBlockWidth);
    }

    public int getRightFloatDistance(CssContext cssCtx, LineBox line, int containingBlockWidth) {
        return this.getFloatManager().getRightFloatDistance(cssCtx, this, line, containingBlockWidth);
    }

    public int getFloatDistance(CssContext cssCtx, LineBox line, int containingBlockWidth) {
        return this.getLeftFloatDistance(cssCtx, line, containingBlockWidth) + this.getRightFloatDistance(cssCtx, line, containingBlockWidth);
    }

    public int getNextLineBoxDelta(CssContext cssCtx, LineBox line, int containingBlockWidth) {
        return this.getFloatManager().getNextLineBoxDelta(cssCtx, this, line, containingBlockWidth);
    }

    public void floatBox(LayoutContext c, BlockBox floated) {
        this.getFloatManager().floatBox(c, c.getLayer(), this, floated);
    }

    public void clear(LayoutContext c, Box current) {
        this.getFloatManager().clear(c, this, current);
    }

    public String toString() {
        return "BlockFormattingContext: (" + this._x + "," + this._y + ")";
    }
}

