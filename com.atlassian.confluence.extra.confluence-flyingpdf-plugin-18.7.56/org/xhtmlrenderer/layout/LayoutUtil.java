/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.layout.FloatLayoutResult;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.MarkerData;

public class LayoutUtil {
    public static void layoutAbsolute(LayoutContext c, LineBox currentLine, BlockBox box) {
        MarkerData markerData = c.getCurrentMarkerData();
        c.setCurrentMarkerData(null);
        if (box.getStyle().isFixed()) {
            box.setContainingBlock(c.getRootLayer().getMaster().getContainingBlock());
        } else {
            box.setContainingBlock(c.getLayer().getMaster());
        }
        box.setStaticEquivalent(currentLine);
        if (!c.isPrint()) {
            box.layout(c);
        } else {
            c.pushLayer(box);
            c.getLayer().setRequiresLayout(true);
            c.popLayer();
        }
        c.setCurrentMarkerData(markerData);
    }

    public static FloatLayoutResult layoutFloated(LayoutContext c, LineBox currentLine, BlockBox block, int avail, List pendingFloats) {
        FloatLayoutResult result = new FloatLayoutResult();
        MarkerData markerData = c.getCurrentMarkerData();
        c.setCurrentMarkerData(null);
        block.setContainingBlock(currentLine.getParent());
        block.setContainingLayer(currentLine.getContainingLayer());
        block.setStaticEquivalent(currentLine);
        if (pendingFloats != null) {
            block.setY(currentLine.getY() + block.getFloatedBoxData().getMarginFromSibling());
        } else {
            block.setY(currentLine.getY() + currentLine.getHeight());
        }
        block.calcInitialFloatedCanvasLocation(c);
        int initialY = block.getY();
        block.layout(c);
        c.getBlockFormattingContext().floatBox(c, block);
        if (pendingFloats != null && (pendingFloats.size() > 0 || block.getWidth() > avail) && currentLine.isContainsContent()) {
            block.reset(c);
            result.setPending(true);
        } else if (c.isPrint()) {
            LayoutUtil.positionFloatOnPage(c, currentLine, block, initialY != block.getY());
            c.getRootLayer().ensureHasPage(c, block);
        }
        result.setBlock(block);
        c.setCurrentMarkerData(markerData);
        return result;
    }

    private static void positionFloatOnPage(LayoutContext c, LineBox currentLine, BlockBox block, boolean movedVertically) {
        if (block.getStyle().isForcePageBreakBefore()) {
            block.forcePageBreakBefore(c, block.getStyle().getIdent(CSSName.PAGE_BREAK_BEFORE), false);
            block.calcCanvasLocation();
            LayoutUtil.resetAndFloatBlock(c, currentLine, block);
        } else if (block.getStyle().isAvoidPageBreakInside() && block.crossesPageBreak(c)) {
            int clearDelta = block.forcePageBreakBefore(c, block.getStyle().getIdent(CSSName.PAGE_BREAK_BEFORE), false);
            block.calcCanvasLocation();
            LayoutUtil.resetAndFloatBlock(c, currentLine, block);
            if (block.crossesPageBreak(c)) {
                block.setY(block.getY() - clearDelta);
                block.calcCanvasLocation();
                LayoutUtil.resetAndFloatBlock(c, currentLine, block);
            }
        } else if (movedVertically) {
            LayoutUtil.resetAndFloatBlock(c, currentLine, block);
        }
    }

    private static void resetAndFloatBlock(LayoutContext c, LineBox currentLine, BlockBox block) {
        block.reset(c);
        block.setContainingLayer(currentLine.getContainingLayer());
        block.layout(c);
        c.getBlockFormattingContext().floatBox(c, block);
    }
}

