/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.BoxCollector;
import org.xhtmlrenderer.layout.InlineBoxing;
import org.xhtmlrenderer.layout.InlinePaintable;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.render.AnonymousBlockBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.CharCounts;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.TextDecoration;

public class InlineLayoutBox
extends Box
implements InlinePaintable {
    private int _baseline;
    private boolean _startsHere;
    private boolean _endsHere;
    private List _inlineChildren;
    private boolean _pending;
    private int _inlineWidth;
    private List _textDecorations;
    private int _containingBlockWidth;

    public InlineLayoutBox(LayoutContext c, Element elem, CalculatedStyle style, int cbWidth) {
        this();
        this.setElement(elem);
        this.setStyle(style);
        this.setContainingBlockWidth(cbWidth);
        this.setMarginTop(c, 0);
        this.setMarginBottom(c, 0);
        this.setPending(true);
        this.calculateHeight(c);
    }

    private InlineLayoutBox() {
        this.setState(3);
    }

    public InlineLayoutBox copyOf() {
        InlineLayoutBox result = new InlineLayoutBox();
        result.setElement(this.getElement());
        result.setStyle(this.getStyle());
        result.setHeight(this.getHeight());
        result._pending = this._pending;
        result.setContainingLayer(this.getContainingLayer());
        return result;
    }

    public void calculateHeight(LayoutContext c) {
        BorderPropertySet border = this.getBorder(c);
        RectPropertySet padding = this.getPadding(c);
        FSFontMetrics metrics = this.getStyle().getFSFontMetrics(c);
        this.setHeight((int)Math.ceil(border.top() + padding.top() + metrics.getAscent() + metrics.getDescent() + padding.bottom() + border.bottom()));
    }

    public int getBaseline() {
        return this._baseline;
    }

    public void setBaseline(int baseline) {
        this._baseline = baseline;
    }

    public int getInlineChildCount() {
        return this._inlineChildren == null ? 0 : this._inlineChildren.size();
    }

    public void addInlineChild(LayoutContext c, Object child) {
        this.addInlineChild(c, child, true);
    }

    public void addInlineChild(LayoutContext c, Object child, boolean callUnmarkPending) {
        if (this._inlineChildren == null) {
            this._inlineChildren = new ArrayList();
        }
        this._inlineChildren.add(child);
        if (callUnmarkPending && this.isPending()) {
            this.unmarkPending(c);
        }
        if (child instanceof Box) {
            Box b = (Box)child;
            b.setParent(this);
            b.initContainingLayer(c);
        } else if (child instanceof InlineText) {
            ((InlineText)child).setParent(this);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List getInlineChildren() {
        return this._inlineChildren == null ? Collections.EMPTY_LIST : this._inlineChildren;
    }

    public Object getInlineChild(int i) {
        if (this._inlineChildren == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this._inlineChildren.get(i);
    }

    public int getInlineWidth(CssContext cssCtx) {
        return this._inlineWidth;
    }

    public void prunePending() {
        if (this.getInlineChildCount() > 0) {
            Object child;
            for (int i = this.getInlineChildCount() - 1; i >= 0 && (child = this.getInlineChild(i)) instanceof InlineLayoutBox; --i) {
                InlineLayoutBox iB = (InlineLayoutBox)child;
                iB.prunePending();
                if (!iB.isPending()) break;
                this.removeChild(i);
            }
        }
    }

    public boolean isEndsHere() {
        return this._endsHere;
    }

    public void setEndsHere(boolean endsHere) {
        this._endsHere = endsHere;
    }

    public boolean isStartsHere() {
        return this._startsHere;
    }

    public void setStartsHere(boolean startsHere) {
        this._startsHere = startsHere;
    }

    public boolean isPending() {
        return this._pending;
    }

    public void setPending(boolean b) {
        this._pending = b;
    }

    public void unmarkPending(LayoutContext c) {
        InlineLayoutBox iB;
        this._pending = false;
        if (this.getParent() instanceof InlineLayoutBox && (iB = (InlineLayoutBox)this.getParent()).isPending()) {
            iB.unmarkPending(c);
        }
        this.setStartsHere(true);
        if (this.getStyle().requiresLayer()) {
            c.pushLayer(this);
            this.getLayer().setInline(true);
            this.connectChildrenToCurrentLayer(c);
        }
    }

    @Override
    public void connectChildrenToCurrentLayer(LayoutContext c) {
        if (this.getInlineChildCount() > 0) {
            for (int i = 0; i < this.getInlineChildCount(); ++i) {
                Object obj = this.getInlineChild(i);
                if (!(obj instanceof Box)) continue;
                Box box = (Box)obj;
                box.setContainingLayer(c.getLayer());
                box.connectChildrenToCurrentLayer(c);
            }
        }
    }

    public void paintSelection(RenderingContext c) {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object child = this.getInlineChild(i);
            if (!(child instanceof InlineText)) continue;
            ((InlineText)child).paintSelection(c);
        }
    }

    @Override
    public void paintInline(RenderingContext c) {
        IdentValue ident;
        List textDecorations;
        if (!this.getStyle().isVisible()) {
            return;
        }
        this.paintBackground(c);
        this.paintBorder(c);
        if (c.debugDrawInlineBoxes()) {
            this.paintDebugOutline(c);
        }
        if ((textDecorations = this.getTextDecorations()) != null) {
            for (TextDecoration tD : textDecorations) {
                ident = tD.getIdentValue();
                if (ident != IdentValue.UNDERLINE && ident != IdentValue.OVERLINE) continue;
                c.getOutputDevice().drawTextDecoration(c, this, tD);
            }
        }
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object child = this.getInlineChild(i);
            if (!(child instanceof InlineText)) continue;
            ((InlineText)child).paint(c);
        }
        if (textDecorations != null) {
            for (TextDecoration tD : textDecorations) {
                ident = tD.getIdentValue();
                if (ident != IdentValue.LINE_THROUGH) continue;
                c.getOutputDevice().drawTextDecoration(c, this, tD);
            }
        }
    }

    @Override
    public int getBorderSides() {
        int result = 5;
        if (this._startsHere) {
            result += 2;
        }
        if (this._endsHere) {
            result += 8;
        }
        return result;
    }

    @Override
    public Rectangle getBorderEdge(int left, int top, CssContext cssCtx) {
        float marginLeft = 0.0f;
        float marginRight = 0.0f;
        if (this._startsHere || this._endsHere) {
            RectPropertySet margin = this.getMargin(cssCtx);
            if (this._startsHere) {
                marginLeft = margin.left();
            }
            if (this._endsHere) {
                marginRight = margin.right();
            }
        }
        BorderPropertySet border = this.getBorder(cssCtx);
        RectPropertySet padding = this.getPadding(cssCtx);
        Rectangle result = new Rectangle((int)((float)left + marginLeft), (int)((float)top - border.top() - padding.top()), (int)((float)this.getInlineWidth(cssCtx) - marginLeft - marginRight), this.getHeight());
        return result;
    }

    @Override
    public Rectangle getMarginEdge(int left, int top, CssContext cssCtx, int tx, int ty) {
        Rectangle result = this.getBorderEdge(left, top, cssCtx);
        float marginLeft = 0.0f;
        float marginRight = 0.0f;
        if (this._startsHere || this._endsHere) {
            RectPropertySet margin = this.getMargin(cssCtx);
            if (this._startsHere) {
                marginLeft = margin.left();
            }
            if (this._endsHere) {
                marginRight = margin.right();
            }
        }
        if (marginRight > 0.0f) {
            result.width = (int)((float)result.width + marginRight);
        }
        if (marginLeft > 0.0f) {
            result.x = (int)((float)result.x - marginLeft);
            result.width = (int)((float)result.width + marginLeft);
        }
        result.translate(tx, ty);
        return result;
    }

    @Override
    public Rectangle getContentAreaEdge(int left, int top, CssContext cssCtx) {
        BorderPropertySet border = this.getBorder(cssCtx);
        RectPropertySet padding = this.getPadding(cssCtx);
        float marginLeft = 0.0f;
        float marginRight = 0.0f;
        float borderLeft = 0.0f;
        float borderRight = 0.0f;
        float paddingLeft = 0.0f;
        float paddingRight = 0.0f;
        if (this._startsHere || this._endsHere) {
            RectPropertySet margin = this.getMargin(cssCtx);
            if (this._startsHere) {
                marginLeft = margin.left();
                borderLeft = border.left();
                paddingLeft = padding.left();
            }
            if (this._endsHere) {
                marginRight = margin.right();
                borderRight = border.right();
                paddingRight = padding.right();
            }
        }
        Rectangle result = new Rectangle((int)((float)left + marginLeft + borderLeft + paddingLeft), (int)((float)top - border.top() - padding.top()), (int)((float)this.getInlineWidth(cssCtx) - marginLeft - borderLeft - paddingLeft - paddingRight - borderRight - marginRight), this.getHeight());
        return result;
    }

    public int getLeftMarginBorderPadding(CssContext cssCtx) {
        if (this._startsHere) {
            return this.getMarginBorderPadding(cssCtx, 1);
        }
        return 0;
    }

    public int getRightMarginPaddingBorder(CssContext cssCtx) {
        if (this._endsHere) {
            return this.getMarginBorderPadding(cssCtx, 2);
        }
        return 0;
    }

    public int getInlineWidth() {
        return this._inlineWidth;
    }

    public void setInlineWidth(int inlineWidth) {
        this._inlineWidth = inlineWidth;
    }

    public boolean isContainsVisibleContent() {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Box b;
            InlineLayoutBox iB;
            InlineText iT;
            Object child = this.getInlineChild(i);
            if (!(child instanceof InlineText ? !(iT = (InlineText)child).isEmpty() : (child instanceof InlineLayoutBox ? (iB = (InlineLayoutBox)child).isContainsVisibleContent() : (b = (Box)child).getWidth() > 0 || b.getHeight() > 0))) continue;
            return true;
        }
        return false;
    }

    public boolean intersectsInlineBlocks(CssContext cssCtx, Shape clip) {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            BoxCollector collector;
            boolean possibleResult;
            Object obj = this.getInlineChild(i);
            if (!(obj instanceof InlineLayoutBox ? (possibleResult = ((InlineLayoutBox)obj).intersectsInlineBlocks(cssCtx, clip)) : obj instanceof Box && (collector = new BoxCollector()).intersectsAny(cssCtx, clip, (Box)obj))) continue;
            return true;
        }
        return false;
    }

    public List getTextDecorations() {
        return this._textDecorations;
    }

    public void setTextDecorations(List textDecoration) {
        this._textDecorations = textDecoration;
    }

    private void addToContentList(List list) {
        list.add(this);
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object child = this.getInlineChild(i);
            if (child instanceof InlineLayoutBox) {
                ((InlineLayoutBox)child).addToContentList(list);
                continue;
            }
            if (!(child instanceof Box)) continue;
            list.add(child);
        }
    }

    public LineBox getLineBox() {
        Box b = this.getParent();
        while (!(b instanceof LineBox)) {
            b = b.getParent();
        }
        return (LineBox)b;
    }

    public List getElementWithContent() {
        ArrayList result = new ArrayList();
        BlockBox container = (BlockBox)this.getLineBox().getParent();
        do {
            List elementBoxes = container.getElementBoxes(this.getElement());
            for (int i = 0; i < elementBoxes.size(); ++i) {
                InlineLayoutBox iB = (InlineLayoutBox)elementBoxes.get(i);
                iB.addToContentList(result);
            }
        } while (container instanceof AnonymousBlockBox && !this.containsEnd(result) && (container = this.addFollowingBlockBoxes(container, result)) != null);
        return result;
    }

    private AnonymousBlockBox addFollowingBlockBoxes(BlockBox container, List result) {
        int current;
        Box parent = container.getParent();
        for (current = 0; current < parent.getChildCount(); ++current) {
            if (parent.getChild(current) != container) continue;
            ++current;
            break;
        }
        while (current < parent.getChildCount() && !(parent.getChild(current) instanceof AnonymousBlockBox)) {
            result.add(parent.getChild(current));
            ++current;
        }
        return current == parent.getChildCount() ? null : (AnonymousBlockBox)parent.getChild(current);
    }

    private boolean containsEnd(List result) {
        for (int i = 0; i < result.size(); ++i) {
            Box b = (Box)result.get(i);
            if (!(b instanceof InlineLayoutBox)) continue;
            InlineLayoutBox iB = (InlineLayoutBox)b;
            if (this.getElement() != iB.getElement() || !iB.isEndsHere()) continue;
            return true;
        }
        return false;
    }

    @Override
    public List getElementBoxes(Element elem) {
        ArrayList<Box> result = new ArrayList<Box>();
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object child = this.getInlineChild(i);
            if (!(child instanceof Box)) continue;
            Box b = (Box)child;
            if (b.getElement() == elem) {
                result.add(b);
            }
            result.addAll(b.getElementBoxes(elem));
        }
        return result;
    }

    @Override
    public Dimension positionRelative(CssContext cssCtx) {
        Dimension delta = super.positionRelative(cssCtx);
        this.setX(this.getX() - delta.width);
        this.setY(this.getY() - delta.height);
        List toTranslate = this.getElementWithContent();
        for (int i = 0; i < toTranslate.size(); ++i) {
            Box b = (Box)toTranslate.get(i);
            b.setX(b.getX() + delta.width);
            b.setY(b.getY() + delta.height);
            b.calcCanvasLocation();
            b.calcChildLocations();
        }
        return delta;
    }

    public void addAllChildren(List list, Layer layer) {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object child = this.getInlineChild(i);
            if (!(child instanceof Box) || ((Box)child).getContainingLayer() != layer) continue;
            list.add(child);
            if (!(child instanceof InlineLayoutBox)) continue;
            ((InlineLayoutBox)child).addAllChildren(list, layer);
        }
    }

    public void paintDebugOutline(RenderingContext c) {
        c.getOutputDevice().drawDebugOutline(c, this, FSRGBColor.BLUE);
    }

    @Override
    protected void resetChildren(LayoutContext c) {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object object = this.getInlineChild(i);
            if (!(object instanceof Box)) continue;
            ((Box)object).reset(c);
        }
    }

    @Override
    public void removeChild(Box child) {
        if (this._inlineChildren != null) {
            this._inlineChildren.remove(child);
        }
    }

    @Override
    public void removeChild(int i) {
        if (this._inlineChildren != null) {
            this._inlineChildren.remove(i);
        }
    }

    @Override
    protected Box getPrevious(Box child) {
        if (this._inlineChildren == null) {
            return null;
        }
        for (int i = 0; i < this._inlineChildren.size() - 1; ++i) {
            Object obj = this._inlineChildren.get(i);
            if (obj != child) continue;
            if (i == 0) {
                return null;
            }
            Object previous = this._inlineChildren.get(i - 1);
            return previous instanceof Box ? (Box)previous : null;
        }
        return null;
    }

    @Override
    protected Box getNext(Box child) {
        if (this._inlineChildren == null) {
            return null;
        }
        for (int i = 0; i < this._inlineChildren.size() - 1; ++i) {
            Object obj = this._inlineChildren.get(i);
            if (obj != child) continue;
            Object next = this._inlineChildren.get(i + 1);
            return next instanceof Box ? (Box)next : null;
        }
        return null;
    }

    @Override
    public void calcCanvasLocation() {
        LineBox lineBox = this.getLineBox();
        this.setAbsX(lineBox.getAbsX() + this.getX());
        this.setAbsY(lineBox.getAbsY() + this.getY());
    }

    @Override
    public void calcChildLocations() {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object obj = this.getInlineChild(i);
            if (!(obj instanceof Box)) continue;
            Box child = (Box)obj;
            child.calcCanvasLocation();
            child.calcChildLocations();
        }
    }

    @Override
    public void clearSelection(List modified) {
        boolean changed = false;
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object obj = this.getInlineChild(i);
            if (obj instanceof Box) {
                ((Box)obj).clearSelection(modified);
                continue;
            }
            changed |= ((InlineText)obj).clearSelection();
        }
        if (changed) {
            modified.add(this);
        }
    }

    @Override
    public void selectAll() {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object obj = this.getInlineChild(i);
            if (obj instanceof Box) {
                ((Box)obj).selectAll();
                continue;
            }
            ((InlineText)obj).selectAll();
        }
    }

    @Override
    protected void calcChildPaintingInfo(CssContext c, PaintingInfo result, boolean useCache) {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object obj = this.getInlineChild(i);
            if (!(obj instanceof Box)) continue;
            PaintingInfo info = ((Box)obj).calcPaintingInfo(c, useCache);
            this.moveIfGreater(result.getOuterMarginCorner(), info.getOuterMarginCorner());
            result.getAggregateBounds().add(info.getAggregateBounds());
        }
    }

    public void lookForDynamicFunctions(RenderingContext c) {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object obj = this.getInlineChild(i);
            if (obj instanceof InlineText) {
                InlineText iT = (InlineText)obj;
                if (!iT.isDynamicFunction()) continue;
                iT.updateDynamicValue(c);
                continue;
            }
            if (!(obj instanceof InlineLayoutBox)) continue;
            ((InlineLayoutBox)obj).lookForDynamicFunctions(c);
        }
    }

    public InlineText findTrailingText() {
        if (this.getInlineChildCount() == 0) {
            return null;
        }
        InlineText result = null;
        for (int offset = this.getInlineChildCount() - 1; offset >= 0; --offset) {
            Object child = this.getInlineChild(offset);
            if (child instanceof InlineText) {
                result = (InlineText)child;
                if (result.isEmpty()) continue;
                return result;
            }
            if (child instanceof InlineLayoutBox) {
                result = ((InlineLayoutBox)child).findTrailingText();
                if (result != null && result.isEmpty()) continue;
                return result;
            }
            return null;
        }
        return result;
    }

    public void calculateTextDecoration(LayoutContext c) {
        List decorations = InlineBoxing.calculateTextDecorations(this, this.getBaseline(), this.getStyle().getFSFontMetrics(c));
        this.setTextDecorations(decorations);
    }

    @Override
    public Box find(CssContext cssCtx, int absX, int absY, boolean findAnonymous) {
        PaintingInfo pI = this.getPaintingInfo();
        if (pI != null && !pI.getAggregateBounds().contains(absX, absY)) {
            return null;
        }
        Box result = null;
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object child = this.getInlineChild(i);
            if (!(child instanceof Box) || (result = ((Box)child).find(cssCtx, absX, absY, findAnonymous)) == null) continue;
            return result;
        }
        Rectangle edge = this.getContentAreaEdge(this.getAbsX(), this.getAbsY(), cssCtx);
        Box box = result = edge.contains(absX, absY) && this.getStyle().isVisible() ? this : null;
        if (!findAnonymous && result != null && this.getElement() == null) {
            return this.getParent().getParent();
        }
        return result;
    }

    @Override
    public int getContainingBlockWidth() {
        return this._containingBlockWidth;
    }

    public void setContainingBlockWidth(int containingBlockWidth) {
        this._containingBlockWidth = containingBlockWidth;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("InlineLayoutBox: ");
        if (this.getElement() != null) {
            result.append("<");
            result.append(this.getElement().getNodeName());
            result.append("> ");
        } else {
            result.append("(anonymous) ");
        }
        if (this.isStartsHere() || this.isEndsHere()) {
            result.append("(");
            if (this.isStartsHere()) {
                result.append("S");
            }
            if (this.isEndsHere()) {
                result.append("E");
            }
            result.append(") ");
        }
        result.append("(baseline=");
        result.append(this._baseline);
        result.append(") ");
        result.append("(" + this.getAbsX() + "," + this.getAbsY() + ")->(" + this.getInlineWidth() + " x " + this.getHeight() + ")");
        return result.toString();
    }

    @Override
    public String dump(LayoutContext c, String indent, int which) {
        if (which != 2) {
            throw new IllegalArgumentException();
        }
        StringBuffer result = new StringBuffer(indent);
        result.append(this);
        result.append('\n');
        Iterator i = this.getInlineChildren().iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof Box) {
                Box b = (Box)obj;
                result.append(b.dump(c, indent + "  ", which));
                if (result.charAt(result.length() - 1) == '\n') {
                    result.deleteCharAt(result.length() - 1);
                }
            } else {
                result.append(indent + "  ");
                result.append(obj.toString());
            }
            if (!i.hasNext()) continue;
            result.append('\n');
        }
        return result.toString();
    }

    @Override
    public void restyle(LayoutContext c) {
        super.restyle(c);
        this.calculateTextDecoration(c);
    }

    @Override
    protected void restyleChildren(LayoutContext c) {
        for (int i = 0; i < this.getInlineChildCount(); ++i) {
            Object obj = this.getInlineChild(i);
            if (!(obj instanceof Box)) continue;
            ((Box)obj).restyle(c);
        }
    }

    @Override
    public Box getRestyleTarget() {
        Box result = this.getParent();
        while (result instanceof InlineLayoutBox) {
            result = result.getParent();
        }
        return result.getParent();
    }

    @Override
    public void collectText(RenderingContext c, StringBuffer buffer) throws IOException {
        for (Object obj : this.getInlineChildren()) {
            if (obj instanceof InlineText) {
                buffer.append(((InlineText)obj).getTextExportText());
                continue;
            }
            ((Box)obj).collectText(c, buffer);
        }
    }

    public void countJustifiableChars(CharCounts counts) {
        boolean justifyThis = this.getStyle().isTextJustify();
        for (Object o : this.getInlineChildren()) {
            if (o instanceof InlineLayoutBox) {
                ((InlineLayoutBox)o).countJustifiableChars(counts);
                continue;
            }
            if (!(o instanceof InlineText) || !justifyThis) continue;
            ((InlineText)o).countJustifiableChars(counts);
        }
    }

    public float adjustHorizontalPosition(JustificationInfo info, float adjust) {
        float runningTotal = adjust;
        float result = 0.0f;
        for (Object o : this.getInlineChildren()) {
            float adj;
            if (o instanceof InlineText) {
                InlineText iT = (InlineText)o;
                iT.setX(iT.getX() + Math.round(result));
                adj = iT.calcTotalAdjustment(info);
                result += adj;
                runningTotal += adj;
                continue;
            }
            Box b = (Box)o;
            b.setX(b.getX() + Math.round(runningTotal));
            if (!(b instanceof InlineLayoutBox)) continue;
            adj = ((InlineLayoutBox)b).adjustHorizontalPosition(info, runningTotal);
            result += adj;
            runningTotal += adj;
        }
        return result;
    }

    @Override
    public int getEffectiveWidth() {
        return this.getInlineWidth();
    }
}

