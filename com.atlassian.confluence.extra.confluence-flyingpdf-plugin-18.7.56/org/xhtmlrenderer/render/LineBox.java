/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.layout.BoxCollector;
import org.xhtmlrenderer.layout.InlineBoxing;
import org.xhtmlrenderer.layout.InlinePaintable;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.CharCounts;
import org.xhtmlrenderer.render.ContentLimitContainer;
import org.xhtmlrenderer.render.FloatDistances;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.render.MarkerData;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.XRRuntimeException;

public class LineBox
extends Box
implements InlinePaintable {
    private static final float JUSTIFY_NON_SPACE_SHARE = 0.2f;
    private static final float JUSTIFY_SPACE_SHARE = 0.8f;
    private boolean _endsOnNL;
    private boolean _containsContent;
    private boolean _containsBlockLevelContent;
    private FloatDistances _floatDistances;
    private List _textDecorations;
    private int _paintingTop;
    private int _paintingHeight;
    private List _nonFlowContent;
    private MarkerData _markerData;
    private boolean _containsDynamicFunction;
    private int _contentStart;
    private int _baseline;
    private JustificationInfo _justificationInfo;

    @Override
    public String dump(LayoutContext c, String indent, int which) {
        if (which != 2) {
            throw new IllegalArgumentException();
        }
        StringBuffer result = new StringBuffer(indent);
        result.append(this);
        result.append('\n');
        this.dumpBoxes(c, indent, this.getNonFlowContent(), 2, result);
        if (this.getNonFlowContent().size() > 0) {
            result.append('\n');
        }
        this.dumpBoxes(c, indent, this.getChildren(), 2, result);
        return result.toString();
    }

    @Override
    public String toString() {
        return "LineBox: (" + this.getAbsX() + "," + this.getAbsY() + ")->(" + this.getWidth() + "," + this.getHeight() + ")";
    }

    @Override
    public Rectangle getMarginEdge(CssContext cssCtx, int tx, int ty) {
        Rectangle result = new Rectangle(this.getX(), this.getY(), this.getContentWidth(), this.getHeight());
        result.translate(tx, ty);
        return result;
    }

    @Override
    public void paintInline(RenderingContext c) {
        if (!this.getParent().getStyle().isVisible()) {
            return;
        }
        if (this.isContainsDynamicFunction()) {
            this.lookForDynamicFunctions(c);
            int totalLineWidth = InlineBoxing.positionHorizontally((CssContext)c, this, 0);
            this.setContentWidth(totalLineWidth);
            this.calcChildLocations();
            this.align(true);
            this.calcPaintingInfo(c, false);
        }
        if (this._textDecorations != null) {
            c.getOutputDevice().drawTextDecoration(c, this);
        }
        if (c.debugDrawLineBoxes()) {
            c.getOutputDevice().drawDebugOutline(c, this, FSRGBColor.GREEN);
        }
    }

    private void lookForDynamicFunctions(RenderingContext c) {
        if (this.getChildCount() > 0) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                Box b = this.getChild(i);
                if (!(b instanceof InlineLayoutBox)) continue;
                ((InlineLayoutBox)b).lookForDynamicFunctions(c);
            }
        }
    }

    public boolean isFirstLine() {
        Box parent = this.getParent();
        return parent != null && parent.getChildCount() > 0 && parent.getChild(0) == this;
    }

    public void prunePendingInlineBoxes() {
        if (this.getChildCount() > 0) {
            Box b;
            for (int i = this.getChildCount() - 1; i >= 0 && (b = this.getChild(i)) instanceof InlineLayoutBox; --i) {
                InlineLayoutBox iB = (InlineLayoutBox)b;
                iB.prunePending();
                if (!iB.isPending()) continue;
                this.removeChild(i);
            }
        }
    }

    public boolean isContainsContent() {
        return this._containsContent;
    }

    public void setContainsContent(boolean containsContent) {
        this._containsContent = containsContent;
    }

    public boolean isEndsOnNL() {
        return this._endsOnNL;
    }

    public void setEndsOnNL(boolean endsOnNL) {
        this._endsOnNL = endsOnNL;
    }

    public void align(boolean dynamic) {
        IdentValue align = this.getParent().getStyle().getIdent(CSSName.TEXT_ALIGN);
        int calcX = 0;
        if (align == IdentValue.LEFT || align == IdentValue.JUSTIFY) {
            int floatDistance = this.getFloatDistances().getLeftFloatDistance();
            calcX = this.getContentStart() + floatDistance;
            if (align == IdentValue.JUSTIFY && dynamic) {
                this.justify();
            }
        } else if (align == IdentValue.CENTER) {
            int leftFloatDistance = this.getFloatDistances().getLeftFloatDistance();
            int rightFloatDistance = this.getFloatDistances().getRightFloatDistance();
            int midpoint = leftFloatDistance + (this.getParent().getContentWidth() - leftFloatDistance - rightFloatDistance) / 2;
            calcX = midpoint - (this.getContentWidth() + this.getContentStart()) / 2;
        } else if (align == IdentValue.RIGHT) {
            int floatDistance = this.getFloatDistances().getRightFloatDistance();
            calcX = this.getParent().getContentWidth() - floatDistance - this.getContentWidth();
        }
        if (calcX != this.getX()) {
            this.setX(calcX);
            this.calcCanvasLocation();
            this.calcChildLocations();
        }
    }

    public void justify() {
        if (!this.isLastLineWithContent()) {
            int leftFloatDistance = this.getFloatDistances().getLeftFloatDistance();
            int rightFloatDistance = this.getFloatDistances().getRightFloatDistance();
            int available = this.getParent().getContentWidth() - leftFloatDistance - rightFloatDistance - this.getContentStart();
            if (available > this.getContentWidth()) {
                int toAdd = available - this.getContentWidth();
                CharCounts counts = this.countJustifiableChars();
                JustificationInfo info = new JustificationInfo();
                if (!this.getParent().getStyle().isIdent(CSSName.LETTER_SPACING, IdentValue.NORMAL)) {
                    info.setNonSpaceAdjust(0.0f);
                    info.setSpaceAdjust((float)toAdd / (float)counts.getSpaceCount());
                } else {
                    if (counts.getNonSpaceCount() > 1) {
                        info.setNonSpaceAdjust((float)toAdd * 0.2f / (float)(counts.getNonSpaceCount() - 1));
                    } else {
                        info.setNonSpaceAdjust(0.0f);
                    }
                    if (counts.getSpaceCount() > 0) {
                        info.setSpaceAdjust((float)toAdd * 0.8f / (float)counts.getSpaceCount());
                    } else {
                        info.setSpaceAdjust(0.0f);
                    }
                }
                this.adjustChildren(info);
                this.setJustificationInfo(info);
            }
        }
    }

    private void adjustChildren(JustificationInfo info) {
        float adjust = 0.0f;
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            Box b = (Box)i.next();
            b.setX(b.getX() + Math.round(adjust));
            if (!(b instanceof InlineLayoutBox)) continue;
            adjust += ((InlineLayoutBox)b).adjustHorizontalPosition(info, adjust);
        }
        this.calcChildLocations();
    }

    private boolean isLastLineWithContent() {
        if (!this._endsOnNL) {
            for (LineBox current = (LineBox)this.getNextSibling(); current != null; current = (LineBox)current.getNextSibling()) {
                if (!current.isContainsContent()) continue;
                return false;
            }
        }
        return true;
    }

    private CharCounts countJustifiableChars() {
        CharCounts result = new CharCounts();
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            Box b = (Box)i.next();
            if (!(b instanceof InlineLayoutBox)) continue;
            ((InlineLayoutBox)b).countJustifiableChars(result);
        }
        return result;
    }

    public FloatDistances getFloatDistances() {
        return this._floatDistances;
    }

    public void setFloatDistances(FloatDistances floatDistances) {
        this._floatDistances = floatDistances;
    }

    public boolean isContainsBlockLevelContent() {
        return this._containsBlockLevelContent;
    }

    public void setContainsBlockLevelContent(boolean containsBlockLevelContent) {
        this._containsBlockLevelContent = containsBlockLevelContent;
    }

    @Override
    public boolean intersects(CssContext cssCtx, Shape clip) {
        return clip == null || this.intersectsLine(cssCtx, clip) || this.isContainsBlockLevelContent() && this.intersectsInlineBlocks(cssCtx, clip);
    }

    private boolean intersectsLine(CssContext cssCtx, Shape clip) {
        Rectangle result = this.getPaintingClipEdge(cssCtx);
        return clip.intersects(result);
    }

    @Override
    public Rectangle getPaintingClipEdge(CssContext cssCtx) {
        Box parent = this.getParent();
        Rectangle result = null;
        result = parent.getStyle().isIdent(CSSName.FS_TEXT_DECORATION_EXTENT, IdentValue.BLOCK) || this.getJustificationInfo() != null ? new Rectangle(this.getAbsX(), this.getAbsY() + this._paintingTop, parent.getAbsX() + parent.getTx() + parent.getContentWidth() - this.getAbsX(), this._paintingHeight) : new Rectangle(this.getAbsX(), this.getAbsY() + this._paintingTop, this.getContentWidth(), this._paintingHeight);
        return result;
    }

    private boolean intersectsInlineBlocks(CssContext cssCtx, Shape clip) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            BoxCollector collector;
            boolean possibleResult;
            Box child = this.getChild(i);
            if (!(child instanceof InlineLayoutBox ? (possibleResult = ((InlineLayoutBox)child).intersectsInlineBlocks(cssCtx, clip)) : (collector = new BoxCollector()).intersectsAny(cssCtx, clip, child))) continue;
            return true;
        }
        return false;
    }

    public List getTextDecorations() {
        return this._textDecorations;
    }

    public void setTextDecorations(List textDecorations) {
        this._textDecorations = textDecorations;
    }

    public int getPaintingHeight() {
        return this._paintingHeight;
    }

    public void setPaintingHeight(int paintingHeight) {
        this._paintingHeight = paintingHeight;
    }

    public int getPaintingTop() {
        return this._paintingTop;
    }

    public void setPaintingTop(int paintingTop) {
        this._paintingTop = paintingTop;
    }

    public void addAllChildren(List list, Layer layer) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            if (this.getContainingLayer() != layer) continue;
            list.add(child);
            if (!(child instanceof InlineLayoutBox)) continue;
            ((InlineLayoutBox)child).addAllChildren(list, layer);
        }
    }

    public List getNonFlowContent() {
        return this._nonFlowContent == null ? Collections.EMPTY_LIST : this._nonFlowContent;
    }

    public void addNonFlowContent(BlockBox box) {
        if (this._nonFlowContent == null) {
            this._nonFlowContent = new ArrayList();
        }
        this._nonFlowContent.add(box);
    }

    @Override
    public void reset(LayoutContext c) {
        for (int i = 0; i < this.getNonFlowContent().size(); ++i) {
            Box content = (Box)this.getNonFlowContent().get(i);
            content.reset(c);
        }
        if (this._markerData != null) {
            this._markerData.restorePreviousReferenceLine(this);
        }
        super.reset(c);
    }

    @Override
    public void calcCanvasLocation() {
        Box parent = this.getParent();
        if (parent == null) {
            throw new XRRuntimeException("calcCanvasLocation() called with no parent");
        }
        this.setAbsX(parent.getAbsX() + parent.getTx() + this.getX());
        this.setAbsY(parent.getAbsY() + parent.getTy() + this.getY());
    }

    @Override
    public void calcChildLocations() {
        super.calcChildLocations();
        for (int i = 0; i < this.getNonFlowContent().size(); ++i) {
            Box content = (Box)this.getNonFlowContent().get(i);
            if (!content.getStyle().isAbsolute()) continue;
            content.calcCanvasLocation();
            content.calcChildLocations();
        }
    }

    public MarkerData getMarkerData() {
        return this._markerData;
    }

    public void setMarkerData(MarkerData markerData) {
        this._markerData = markerData;
    }

    public boolean isContainsDynamicFunction() {
        return this._containsDynamicFunction;
    }

    public void setContainsDynamicFunction(boolean containsPageCounter) {
        this._containsDynamicFunction |= containsPageCounter;
    }

    public int getContentStart() {
        return this._contentStart;
    }

    public void setContentStart(int contentOffset) {
        this._contentStart = contentOffset;
    }

    public InlineText findTrailingText() {
        if (this.getChildCount() == 0) {
            return null;
        }
        for (int offset = this.getChildCount() - 1; offset >= 0; --offset) {
            Box child = this.getChild(offset);
            if (child instanceof InlineLayoutBox) {
                InlineText result = ((InlineLayoutBox)child).findTrailingText();
                if (result != null && result.isEmpty()) continue;
                return result;
            }
            return null;
        }
        return null;
    }

    public void trimTrailingSpace(LayoutContext c) {
        InlineLayoutBox iB;
        IdentValue whitespace;
        InlineText text = this.findTrailingText();
        if (text != null && ((whitespace = (iB = text.getParent()).getStyle().getWhitespace()) == IdentValue.NORMAL || whitespace == IdentValue.NOWRAP)) {
            text.trimTrailingSpace(c);
        }
    }

    @Override
    public Box find(CssContext cssCtx, int absX, int absY, boolean findAnonymous) {
        PaintingInfo pI = this.getPaintingInfo();
        if (pI != null && !pI.getAggregateBounds().contains(absX, absY)) {
            return null;
        }
        Box result = null;
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            result = child.find(cssCtx, absX, absY, findAnonymous);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public int getBaseline() {
        return this._baseline;
    }

    public void setBaseline(int baseline) {
        this._baseline = baseline;
    }

    public boolean isContainsOnlyBlockLevelContent() {
        if (!this.isContainsBlockLevelContent()) {
            return false;
        }
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box b = this.getChild(i);
            if (b instanceof BlockBox) continue;
            return false;
        }
        return true;
    }

    @Override
    public Box getRestyleTarget() {
        return this.getParent();
    }

    @Override
    public void restyle(LayoutContext c) {
        Box parent = this.getParent();
        Element e = parent.getElement();
        if (e != null) {
            CalculatedStyle style = c.getSharedContext().getStyle(e, true);
            this.setStyle(style.createAnonymousStyle(IdentValue.BLOCK));
        }
        this.restyleChildren(c);
    }

    public boolean isContainsVisibleContent() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            boolean maybeResult;
            Box b = this.getChild(i);
            if (!(b instanceof BlockBox ? b.getWidth() > 0 || b.getHeight() > 0 : (maybeResult = ((InlineLayoutBox)b).isContainsVisibleContent()))) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clearSelection(List modified) {
        for (Box b : this.getNonFlowContent()) {
            b.clearSelection(modified);
        }
        super.clearSelection(modified);
    }

    @Override
    public void selectAll() {
        for (BlockBox box : this.getNonFlowContent()) {
            box.selectAll();
        }
        super.selectAll();
    }

    @Override
    public void collectText(RenderingContext c, StringBuffer buffer) throws IOException {
        for (Box b : this.getNonFlowContent()) {
            b.collectText(c, buffer);
        }
        if (this.isContainsDynamicFunction()) {
            this.lookForDynamicFunctions(c);
        }
        super.collectText(c, buffer);
    }

    @Override
    public void exportText(RenderingContext c, Writer writer) throws IOException {
        int baselinePos = this.getAbsY() + this.getBaseline();
        if (baselinePos >= c.getPage().getBottom() && this.isInDocumentFlow()) {
            this.exportPageBoxText(c, writer, baselinePos);
        }
        for (Box b : this.getNonFlowContent()) {
            b.exportText(c, writer);
        }
        if (this.isContainsContent()) {
            StringBuffer result = new StringBuffer();
            this.collectText(c, result);
            writer.write(result.toString().trim());
            writer.write(LINE_SEPARATOR);
        }
    }

    @Override
    public void analyzePageBreaks(LayoutContext c, ContentLimitContainer container) {
        container.updateTop(c, this.getAbsY());
        container.updateBottom(c, this.getAbsY() + this.getHeight());
    }

    public void checkPagePosition(LayoutContext c, boolean alwaysBreak) {
        if (!c.isPageBreaksAllowed()) {
            return;
        }
        PageBox pageBox = c.getRootLayer().getFirstPage(c, this);
        if (pageBox != null) {
            boolean needsPageBreak;
            boolean bl = needsPageBreak = alwaysBreak || this.getAbsY() + this.getHeight() >= pageBox.getBottom() - c.getExtraSpaceBottom();
            if (needsPageBreak) {
                this.forcePageBreakBefore(c, IdentValue.ALWAYS, false);
                this.calcCanvasLocation();
            } else if (pageBox.getTop() + c.getExtraSpaceTop() > this.getAbsY()) {
                int diff = pageBox.getTop() + c.getExtraSpaceTop() - this.getAbsY();
                this.setY(this.getY() + diff);
                this.calcCanvasLocation();
            }
        }
    }

    public JustificationInfo getJustificationInfo() {
        return this._justificationInfo;
    }

    private void setJustificationInfo(JustificationInfo justificationInfo) {
        this._justificationInfo = justificationInfo;
    }
}

