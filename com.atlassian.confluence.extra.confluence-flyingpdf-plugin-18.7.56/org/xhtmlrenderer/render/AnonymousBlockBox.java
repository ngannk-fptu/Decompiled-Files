/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.util.List;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.Styleable;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;

public class AnonymousBlockBox
extends BlockBox {
    private List _openInlineBoxes;

    public AnonymousBlockBox(Element element) {
        this.setElement(element);
    }

    @Override
    public void layout(LayoutContext c) {
        this.layoutInlineChildren(c, 0, this.calcInitialBreakAtLine(c), true);
    }

    @Override
    public int getContentWidth() {
        return this.getContainingBlock().getContentWidth();
    }

    @Override
    public Box find(CssContext cssCtx, int absX, int absY, boolean findAnonymous) {
        Box result = super.find(cssCtx, absX, absY, findAnonymous);
        if (!findAnonymous && result == this) {
            return this.getParent();
        }
        return result;
    }

    public List getOpenInlineBoxes() {
        return this._openInlineBoxes;
    }

    public void setOpenInlineBoxes(List openInlineBoxes) {
        this._openInlineBoxes = openInlineBoxes;
    }

    @Override
    public boolean isSkipWhenCollapsingMargins() {
        for (Styleable styleable : this.getInlineContent()) {
            CalculatedStyle style = styleable.getStyle();
            if (style.isFloated() || style.isAbsolute() || style.isFixed() || style.isRunning()) continue;
            return false;
        }
        return true;
    }

    public void provideSiblingMarginToFloats(int margin) {
        for (Styleable styleable : this.getInlineContent()) {
            BlockBox b;
            if (!(styleable instanceof BlockBox) || !(b = (BlockBox)styleable).isFloated()) continue;
            b.getFloatedBoxData().setMarginFromSibling(margin);
        }
    }

    @Override
    public boolean isMayCollapseMarginsWithChildren() {
        return false;
    }

    @Override
    public void styleText(LayoutContext c) {
        this.styleText(c, this.getParent().getStyle());
    }

    @Override
    public BlockBox copyOf() {
        throw new IllegalArgumentException("cannot be copied");
    }
}

