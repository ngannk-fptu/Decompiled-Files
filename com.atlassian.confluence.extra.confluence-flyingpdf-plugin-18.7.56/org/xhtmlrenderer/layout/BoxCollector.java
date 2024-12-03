/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.layout.BoxRange;
import org.xhtmlrenderer.layout.BoxRangeData;
import org.xhtmlrenderer.layout.BoxRangeLists;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.newtable.TableBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.RenderingContext;

public class BoxCollector {
    public void collect(CssContext c, Shape clip, Layer layer, List blockContent, List inlineContent, BoxRangeLists rangeLists) {
        if (layer.isInline()) {
            this.collectInlineLayer(c, clip, layer, blockContent, inlineContent, rangeLists);
        } else {
            this.collect(c, clip, layer, layer.getMaster(), blockContent, inlineContent, rangeLists);
        }
    }

    public boolean intersectsAny(CssContext c, Shape clip, Box master) {
        return this.intersectsAny(c, clip, master, master);
    }

    private void collectInlineLayer(CssContext c, Shape clip, Layer layer, List blockContent, List inlineContent, BoxRangeLists rangeLists) {
        InlineLayoutBox iB = (InlineLayoutBox)layer.getMaster();
        List content = iB.getElementWithContent();
        for (int i = 0; i < content.size(); ++i) {
            Box b = (Box)content.get(i);
            if (!b.intersects(c, clip)) continue;
            if (b instanceof InlineLayoutBox) {
                inlineContent.add(b);
                continue;
            }
            BlockBox bb = (BlockBox)b;
            if (bb.isInline()) {
                if (!this.intersectsAny(c, clip, b)) continue;
                inlineContent.add(b);
                continue;
            }
            this.collect(c, clip, layer, bb, blockContent, inlineContent, rangeLists);
        }
    }

    private boolean intersectsAggregateBounds(Shape clip, Box box) {
        if (clip == null) {
            return true;
        }
        PaintingInfo info = box.getPaintingInfo();
        if (info == null) {
            return false;
        }
        Rectangle bounds = info.getAggregateBounds();
        return clip.intersects(bounds);
    }

    public void collect(CssContext c, Shape clip, Layer layer, Box container, List blockContent, List inlineContent, BoxRangeLists rangeLists) {
        if (layer != container.getContainingLayer()) {
            return;
        }
        boolean isBlock = container instanceof BlockBox;
        int blockStart = 0;
        int inlineStart = 0;
        int blockRangeStart = 0;
        int inlineRangeStart = 0;
        if (isBlock) {
            blockStart = blockContent.size();
            inlineStart = inlineContent.size();
            blockRangeStart = rangeLists.getBlock().size();
            inlineRangeStart = rangeLists.getInline().size();
        }
        if (container instanceof LineBox) {
            if (this.intersectsAggregateBounds(clip, container) || container.getPaintingInfo() == null && container.intersects(c, clip)) {
                inlineContent.add(container);
                ((LineBox)container).addAllChildren(inlineContent, layer);
            }
        } else {
            boolean intersectsAggregateBounds = this.intersectsAggregateBounds(clip, container);
            if ((container.getLayer() == null || !(container instanceof BlockBox)) && (intersectsAggregateBounds || container.getPaintingInfo() == null && container.intersects(c, clip))) {
                TableBox table;
                blockContent.add(container);
                if (container.getStyle().isTable() && c instanceof RenderingContext && (table = (TableBox)container).hasContentLimitContainer()) {
                    table.updateHeaderFooterPosition((RenderingContext)c);
                }
            }
            if (!(container.getPaintingInfo() != null && !intersectsAggregateBounds || container.getLayer() != null && container != layer.getMaster())) {
                for (int i = 0; i < container.getChildCount(); ++i) {
                    Box child = container.getChild(i);
                    this.collect(c, clip, layer, child, blockContent, inlineContent, rangeLists);
                }
            }
        }
        this.saveRangeData(c, container, blockContent, inlineContent, rangeLists, isBlock, blockStart, inlineStart, blockRangeStart, inlineRangeStart);
    }

    private void saveRangeData(CssContext c, Box container, List blockContent, List inlineContent, BoxRangeLists rangeLists, boolean isBlock, int blockStart, int inlineStart, int blockRangeStart, int inlineRangeStart) {
        BlockBox blockBox;
        if (isBlock && c instanceof RenderingContext && (blockBox = (BlockBox)container).isNeedsClipOnPaint((RenderingContext)c)) {
            int inlineEnd;
            int blockEnd = blockContent.size();
            if (blockStart != blockEnd) {
                BoxRange range = new BoxRange(blockStart, blockEnd);
                rangeLists.getBlock().add(blockRangeStart, new BoxRangeData(blockBox, range));
            }
            if (inlineStart != (inlineEnd = inlineContent.size())) {
                BoxRange range = new BoxRange(inlineStart, inlineEnd);
                rangeLists.getInline().add(inlineRangeStart, new BoxRangeData(blockBox, range));
            }
        }
    }

    private boolean intersectsAny(CssContext c, Shape clip, Box master, Box container) {
        if (container instanceof LineBox) {
            if (container.intersects(c, clip)) {
                return true;
            }
        } else {
            if ((container.getLayer() == null || !(container instanceof BlockBox)) && container.intersects(c, clip)) {
                return true;
            }
            if (container.getLayer() == null || container == master) {
                for (int i = 0; i < container.getChildCount(); ++i) {
                    Box child = container.getChild(i);
                    boolean possibleResult = this.intersectsAny(c, clip, master, child);
                    if (!possibleResult) continue;
                    return true;
                }
            }
        }
        return false;
    }
}

