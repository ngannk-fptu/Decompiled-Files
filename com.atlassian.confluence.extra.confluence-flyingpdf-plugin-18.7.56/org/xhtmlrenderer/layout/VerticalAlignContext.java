/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.layout.InlineBoxMeasurements;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.InlineLayoutBox;

public class VerticalAlignContext {
    private List _measurements = new ArrayList();
    private int _inlineTop;
    private boolean _inlineTopSet = false;
    private int _inlineBottom;
    private boolean _inlineBottomSet = false;
    private int _paintingTop;
    private boolean _paintingTopSet = false;
    private int _paintingBottom;
    private boolean _paintingBottomSet = false;
    private List _children = new ArrayList();
    private VerticalAlignContext _parent = null;

    private void moveTrackedValues(int ty) {
        if (this._inlineTopSet) {
            this._inlineTop += ty;
        }
        if (this._inlineBottomSet) {
            this._inlineBottom += ty;
        }
        if (this._paintingTopSet) {
            this._paintingTop += ty;
        }
        if (this._paintingBottomSet) {
            this._paintingBottom += ty;
        }
    }

    public int getInlineBottom() {
        return this._inlineBottom;
    }

    public int getInlineTop() {
        return this._inlineTop;
    }

    public void updateInlineTop(int inlineTop) {
        if (!this._inlineTopSet || inlineTop < this._inlineTop) {
            this._inlineTop = inlineTop;
            this._inlineTopSet = true;
        }
    }

    public void updatePaintingTop(int paintingTop) {
        if (!this._paintingTopSet || paintingTop < this._paintingTop) {
            this._paintingTop = paintingTop;
            this._paintingTopSet = true;
        }
    }

    public void updateInlineBottom(int inlineBottom) {
        if (!this._inlineBottomSet || inlineBottom > this._inlineBottom) {
            this._inlineBottom = inlineBottom;
            this._inlineBottomSet = true;
        }
    }

    public void updatePaintingBottom(int paintingBottom) {
        if (!this._paintingBottomSet || paintingBottom > this._paintingBottom) {
            this._paintingBottom = paintingBottom;
            this._paintingBottomSet = true;
        }
    }

    public int getLineBoxHeight() {
        return this._inlineBottom - this._inlineTop;
    }

    public void pushMeasurements(InlineBoxMeasurements measurements) {
        this._measurements.add(measurements);
        this.updateInlineTop(measurements.getInlineTop());
        this.updateInlineBottom(measurements.getInlineBottom());
        this.updatePaintingTop(measurements.getPaintingTop());
        this.updatePaintingBottom(measurements.getPaintingBottom());
    }

    public InlineBoxMeasurements getParentMeasurements() {
        return (InlineBoxMeasurements)this._measurements.get(this._measurements.size() - 1);
    }

    public void popMeasurements() {
        this._measurements.remove(this._measurements.size() - 1);
    }

    public int getPaintingBottom() {
        return this._paintingBottom;
    }

    public int getPaintingTop() {
        return this._paintingTop;
    }

    public VerticalAlignContext createChild(Box root) {
        VerticalAlignContext result = new VerticalAlignContext();
        VerticalAlignContext vaRoot = this.getRoot();
        result.setParent(vaRoot);
        InlineBoxMeasurements initial = (InlineBoxMeasurements)vaRoot._measurements.get(0);
        result.pushMeasurements(initial);
        if (vaRoot._children == null) {
            vaRoot._children = new ArrayList();
        }
        vaRoot._children.add(new ChildContextData(root, result));
        return result;
    }

    public List getChildren() {
        return this._children == null ? Collections.EMPTY_LIST : this._children;
    }

    public VerticalAlignContext getParent() {
        return this._parent;
    }

    public void setParent(VerticalAlignContext parent) {
        this._parent = parent;
    }

    private VerticalAlignContext getRoot() {
        VerticalAlignContext result = this;
        return result.getParent() != null ? result.getParent() : this;
    }

    private void merge(VerticalAlignContext context) {
        this.updateInlineBottom(context.getInlineBottom());
        this.updateInlineTop(context.getInlineTop());
        this.updatePaintingBottom(context.getPaintingBottom());
        this.updatePaintingTop(context.getPaintingTop());
    }

    public void alignChildren() {
        List children = this.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            ChildContextData data = (ChildContextData)children.get(i);
            data.align();
            this.merge(data.getVerticalAlignContext());
        }
    }

    public void setInitialMeasurements(InlineBoxMeasurements measurements) {
        this._measurements.add(measurements);
    }

    private static final class ChildContextData {
        private Box _root;
        private VerticalAlignContext _verticalAlignContext;

        public ChildContextData() {
        }

        public ChildContextData(Box root, VerticalAlignContext vaContext) {
            this._root = root;
            this._verticalAlignContext = vaContext;
        }

        public Box getRoot() {
            return this._root;
        }

        public void setRoot(Box root) {
            this._root = root;
        }

        public VerticalAlignContext getVerticalAlignContext() {
            return this._verticalAlignContext;
        }

        public void setVerticalAlignContext(VerticalAlignContext verticalAlignContext) {
            this._verticalAlignContext = verticalAlignContext;
        }

        private void moveContextContents(int ty) {
            this.moveInlineContents(this._root, ty);
        }

        private void moveInlineContents(Box box, int ty) {
            if (this.canBeMoved(box)) {
                box.setY(box.getY() + ty);
                if (box instanceof InlineLayoutBox) {
                    InlineLayoutBox iB = (InlineLayoutBox)box;
                    for (int i = 0; i < iB.getInlineChildCount(); ++i) {
                        Object child = iB.getInlineChild(i);
                        if (!(child instanceof Box)) continue;
                        this.moveInlineContents((Box)child, ty);
                    }
                }
            }
        }

        private boolean canBeMoved(Box box) {
            IdentValue vAlign = box.getStyle().getIdent(CSSName.VERTICAL_ALIGN);
            return box == this._root || vAlign != IdentValue.TOP && vAlign != IdentValue.BOTTOM;
        }

        public void align() {
            IdentValue vAlign = this._root.getStyle().getIdent(CSSName.VERTICAL_ALIGN);
            int delta = 0;
            if (vAlign == IdentValue.TOP) {
                delta = this._verticalAlignContext.getRoot().getInlineTop() - this._verticalAlignContext.getInlineTop();
            } else if (vAlign == IdentValue.BOTTOM) {
                delta = this._verticalAlignContext.getRoot().getInlineBottom() - this._verticalAlignContext.getInlineBottom();
            } else {
                throw new RuntimeException("internal error");
            }
            this._verticalAlignContext.moveTrackedValues(delta);
            this.moveContextContents(delta);
        }
    }
}

