/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.layout.BreakAtLineContext;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.LayoutState;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.PageBox;

public class BlockBoxing {
    private static final int NO_PAGE_TRIM = -1;

    private BlockBoxing() {
    }

    public static void layoutContent(LayoutContext c, BlockBox block, int contentStart) {
        int offset = -1;
        ArrayList localChildren = block.getChildren();
        if (c.isPrint() && !(localChildren instanceof RandomAccess)) {
            localChildren = new ArrayList(localChildren);
        }
        int childOffset = block.getHeight() + contentStart;
        RelayoutDataList relayoutDataList = null;
        if (c.isPrint()) {
            relayoutDataList = new RelayoutDataList(localChildren.size());
        }
        int pageCount = -1;
        BlockBox previousChildBox = null;
        for (BlockBox child : localChildren) {
            Dimension relativeOffset;
            ++offset;
            RelayoutData relayoutData = null;
            boolean mayCheckKeepTogether = false;
            if (c.isPrint()) {
                relayoutData = relayoutDataList.get(offset);
                relayoutData.setLayoutState(c.copyStateForRelayout());
                relayoutData.setChildOffset(childOffset);
                pageCount = c.getRootLayer().getPages().size();
                child.setNeedPageClear(false);
                if ((child.getStyle().isAvoidPageBreakInside() || child.getStyle().isKeepWithInline()) && c.isMayCheckKeepTogether()) {
                    mayCheckKeepTogether = true;
                    c.setMayCheckKeepTogether(false);
                }
            }
            BlockBoxing.layoutBlockChild(c, block, child, false, childOffset, -1, relayoutData == null ? null : relayoutData.getLayoutState());
            if (c.isPrint()) {
                boolean needPageClear = child.isNeedPageClear();
                if (needPageClear || mayCheckKeepTogether) {
                    c.setMayCheckKeepTogether(mayCheckKeepTogether);
                    boolean tryToAvoidPageBreak = child.getStyle().isAvoidPageBreakInside() && child.crossesPageBreak(c);
                    boolean keepWithInline = child.isNeedsKeepWithInline(c);
                    if (tryToAvoidPageBreak || needPageClear || keepWithInline) {
                        c.restoreStateForRelayout(relayoutData.getLayoutState());
                        child.reset(c);
                        BlockBoxing.layoutBlockChild(c, block, child, true, childOffset, pageCount, relayoutData.getLayoutState());
                        if (tryToAvoidPageBreak && child.crossesPageBreak(c) && !keepWithInline) {
                            c.restoreStateForRelayout(relayoutData.getLayoutState());
                            child.reset(c);
                            BlockBoxing.layoutBlockChild(c, block, child, false, childOffset, pageCount, relayoutData.getLayoutState());
                        }
                    }
                }
                c.getRootLayer().ensureHasPage(c, child);
            }
            if ((childOffset = (relativeOffset = child.getRelativeOffset()) == null ? child.getY() + child.getHeight() : child.getY() - relativeOffset.height + child.getHeight()) > block.getHeight()) {
                block.setHeight(childOffset);
            }
            if (c.isPrint()) {
                RelayoutRunResult runResult;
                if (child.getStyle().isForcePageBreakAfter()) {
                    block.forcePageBreakAfter(c, child.getStyle().getIdent(CSSName.PAGE_BREAK_AFTER));
                    childOffset = block.getHeight();
                }
                if (previousChildBox != null) {
                    relayoutDataList.markRun(offset, previousChildBox, child);
                }
                if ((runResult = BlockBoxing.processPageBreakAvoidRun(c, block, localChildren, offset, relayoutDataList, relayoutData, child)).isChanged() && (childOffset = runResult.getChildOffset()) > block.getHeight()) {
                    block.setHeight(childOffset);
                }
            }
            previousChildBox = child;
        }
    }

    private static RelayoutRunResult processPageBreakAvoidRun(LayoutContext c, BlockBox block, List localChildren, int offset, RelayoutDataList relayoutDataList, RelayoutData relayoutData, BlockBox childBox) {
        RelayoutRunResult result = new RelayoutRunResult();
        if (offset > 0) {
            int runStart;
            RelayoutData previousRelayoutData;
            boolean mightNeedRelayout = false;
            int runEnd = -1;
            if (offset == localChildren.size() - 1 && relayoutData.isEndsRun()) {
                mightNeedRelayout = true;
                runEnd = offset;
            } else if (offset > 0 && (previousRelayoutData = relayoutDataList.get(offset - 1)).isEndsRun()) {
                mightNeedRelayout = true;
                runEnd = offset - 1;
            }
            if (mightNeedRelayout && BlockBoxing.isPageBreakBetweenChildBoxes(relayoutDataList, runStart = relayoutDataList.getRunStart(runEnd), runEnd, c, block)) {
                result.setChanged(true);
                block.resetChildren(c, runStart, offset);
                result.setChildOffset(BlockBoxing.relayoutRun(c, localChildren, block, relayoutDataList, runStart, offset, true));
                if (BlockBoxing.isPageBreakBetweenChildBoxes(relayoutDataList, runStart, runEnd, c, block)) {
                    block.resetChildren(c, runStart, offset);
                    result.setChildOffset(BlockBoxing.relayoutRun(c, localChildren, block, relayoutDataList, runStart, offset, false));
                }
            }
        }
        return result;
    }

    private static boolean isPageBreakBetweenChildBoxes(RelayoutDataList relayoutDataList, int runStart, int runEnd, LayoutContext c, BlockBox block) {
        for (int i = runStart; i < runEnd; ++i) {
            Box prevChild = block.getChild(i);
            Box nextChild = block.getChild(i + 1);
            Box nextLine = BlockBoxing.getFirstLine(nextChild) == null ? nextChild : BlockBoxing.getFirstLine(nextChild);
            int prevChildEnd = prevChild.getAbsY() + prevChild.getHeight();
            int nextLineEnd = nextLine.getAbsY() + nextLine.getHeight();
            if (!c.getRootLayer().crossesPageBreak(c, prevChildEnd, nextLineEnd)) continue;
            return true;
        }
        return false;
    }

    private static LineBox getFirstLine(Box box) {
        Box child = box;
        while (child.getChildCount() > 0) {
            if (child instanceof LineBox) {
                return (LineBox)child;
            }
            child = child.getChild(0);
        }
        return null;
    }

    private static int relayoutRun(LayoutContext c, List localChildren, BlockBox block, RelayoutDataList relayoutDataList, int start, int end, boolean onNewPage) {
        int childOffset = relayoutDataList.get(start).getChildOffset();
        if (onNewPage) {
            Box startBox = (Box)localChildren.get(start);
            PageBox startPageBox = c.getRootLayer().getFirstPage(c, startBox);
            childOffset += startPageBox.getBottom() - startBox.getAbsY();
        }
        block.setHeight(childOffset);
        for (int i = start; i <= end; ++i) {
            BlockBox child = (BlockBox)localChildren.get(i);
            RelayoutData relayoutData = relayoutDataList.get(i);
            int pageCount = c.getRootLayer().getPages().size();
            c.restoreStateForRelayout(relayoutData.getLayoutState());
            relayoutData.setChildOffset(childOffset);
            boolean mayCheckKeepTogether = false;
            if ((child.getStyle().isAvoidPageBreakInside() || child.getStyle().isKeepWithInline()) && c.isMayCheckKeepTogether()) {
                mayCheckKeepTogether = true;
                c.setMayCheckKeepTogether(false);
            }
            BlockBoxing.layoutBlockChild(c, block, child, false, childOffset, -1, relayoutData.getLayoutState());
            if (mayCheckKeepTogether) {
                c.setMayCheckKeepTogether(true);
                boolean tryToAvoidPageBreak = child.getStyle().isAvoidPageBreakInside() && child.crossesPageBreak(c);
                boolean needPageClear = child.isNeedPageClear();
                boolean keepWithInline = child.isNeedsKeepWithInline(c);
                if (tryToAvoidPageBreak || needPageClear || keepWithInline) {
                    c.restoreStateForRelayout(relayoutData.getLayoutState());
                    child.reset(c);
                    BlockBoxing.layoutBlockChild(c, block, child, true, childOffset, pageCount, relayoutData.getLayoutState());
                    if (tryToAvoidPageBreak && child.crossesPageBreak(c) && !keepWithInline) {
                        c.restoreStateForRelayout(relayoutData.getLayoutState());
                        child.reset(c);
                        BlockBoxing.layoutBlockChild(c, block, child, false, childOffset, pageCount, relayoutData.getLayoutState());
                    }
                }
            }
            c.getRootLayer().ensureHasPage(c, child);
            Dimension relativeOffset = child.getRelativeOffset();
            childOffset = relativeOffset == null ? child.getY() + child.getHeight() : child.getY() - relativeOffset.height + child.getHeight();
            if (childOffset > block.getHeight()) {
                block.setHeight(childOffset);
            }
            if (!child.getStyle().isForcePageBreakAfter()) continue;
            block.forcePageBreakAfter(c, child.getStyle().getIdent(CSSName.PAGE_BREAK_AFTER));
            childOffset = block.getHeight();
        }
        return childOffset;
    }

    private static void layoutBlockChild(LayoutContext c, BlockBox parent, BlockBox child, boolean needPageClear, int childOffset, int trimmedPageCount, LayoutState layoutState) {
        BlockBoxing.layoutBlockChild0(c, parent, child, needPageClear, childOffset, trimmedPageCount);
        BreakAtLineContext bContext = child.calcBreakAtLineContext(c);
        if (bContext != null) {
            c.setBreakAtLineContext(bContext);
            c.restoreStateForRelayout(layoutState);
            child.reset(c);
            BlockBoxing.layoutBlockChild0(c, parent, child, needPageClear, childOffset, trimmedPageCount);
            c.setBreakAtLineContext(null);
        }
    }

    private static void layoutBlockChild0(LayoutContext c, BlockBox parent, BlockBox child, boolean needPageClear, int childOffset, int trimmedPageCount) {
        child.setNeedPageClear(needPageClear);
        child.initStaticPos(c, parent, childOffset);
        child.initContainingLayer(c);
        child.calcCanvasLocation();
        c.translate(0, childOffset);
        BlockBoxing.repositionBox(c, child, trimmedPageCount);
        child.layout(c);
        c.translate(-child.getX(), -child.getY());
    }

    private static void repositionBox(LayoutContext c, BlockBox child, int trimmedPageCount) {
        boolean moved = false;
        if (child.getStyle().isRelative()) {
            Dimension delta = child.positionRelative(c);
            c.translate(delta.width, delta.height);
            moved = true;
        }
        if (c.isPrint()) {
            boolean pageClear = child.isNeedPageClear() || child.getStyle().isForcePageBreakBefore();
            boolean needNewPageContext = child.checkPageContext(c);
            if (needNewPageContext && trimmedPageCount != -1) {
                c.getRootLayer().trimPageCount(trimmedPageCount);
            }
            if (pageClear || needNewPageContext) {
                int delta = child.forcePageBreakBefore(c, child.getStyle().getIdent(CSSName.PAGE_BREAK_BEFORE), needNewPageContext);
                c.translate(0, delta);
                moved = true;
                child.setNeedPageClear(false);
            }
        }
        if (moved) {
            child.calcCanvasLocation();
        }
    }

    private static class RelayoutData {
        private LayoutState _layoutState;
        private int _listIndex;
        private boolean _startsRun;
        private boolean _endsRun;
        private boolean _inRun;
        private int _childOffset;

        public boolean isEndsRun() {
            return this._endsRun;
        }

        public void setEndsRun(boolean endsRun) {
            this._endsRun = endsRun;
        }

        public boolean isInRun() {
            return this._inRun;
        }

        public void setInRun(boolean inRun) {
            this._inRun = inRun;
        }

        public LayoutState getLayoutState() {
            return this._layoutState;
        }

        public void setLayoutState(LayoutState layoutState) {
            this._layoutState = layoutState;
        }

        public boolean isStartsRun() {
            return this._startsRun;
        }

        public void setStartsRun(boolean startsRun) {
            this._startsRun = startsRun;
        }

        public int getChildOffset() {
            return this._childOffset;
        }

        public void setChildOffset(int childOffset) {
            this._childOffset = childOffset;
        }

        public int getListIndex() {
            return this._listIndex;
        }

        public void setListIndex(int listIndex) {
            this._listIndex = listIndex;
        }
    }

    private static class RelayoutRunResult {
        private boolean _changed;
        private int _childOffset;

        private RelayoutRunResult() {
        }

        public boolean isChanged() {
            return this._changed;
        }

        public void setChanged(boolean changed) {
            this._changed = changed;
        }

        public int getChildOffset() {
            return this._childOffset;
        }

        public void setChildOffset(int childOffset) {
            this._childOffset = childOffset;
        }
    }

    private static class RelayoutDataList {
        private List _hints;

        public RelayoutDataList(int size) {
            this._hints = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                this._hints.add(new RelayoutData());
            }
        }

        public RelayoutData get(int index) {
            return (RelayoutData)this._hints.get(index);
        }

        public void markRun(int offset, BlockBox previous, BlockBox current) {
            RelayoutData previousData = this.get(offset - 1);
            RelayoutData currentData = this.get(offset);
            IdentValue previousAfter = previous.getStyle().getIdent(CSSName.PAGE_BREAK_AFTER);
            IdentValue currentBefore = current.getStyle().getIdent(CSSName.PAGE_BREAK_BEFORE);
            if (previousAfter == IdentValue.AVOID && currentBefore == IdentValue.AUTO || previousAfter == IdentValue.AUTO && currentBefore == IdentValue.AVOID || previousAfter == IdentValue.AVOID && currentBefore == IdentValue.AVOID) {
                if (!previousData.isInRun()) {
                    previousData.setStartsRun(true);
                }
                previousData.setInRun(true);
                currentData.setInRun(true);
                if (offset == this._hints.size() - 1) {
                    currentData.setEndsRun(true);
                }
            } else if (previousData.isInRun()) {
                previousData.setEndsRun(true);
            }
        }

        public int getRunStart(int runEnd) {
            int offset = runEnd;
            RelayoutData current = this.get(offset);
            if (!current.isEndsRun()) {
                throw new RuntimeException("Not the end of a run");
            }
            while (!current.isStartsRun()) {
                current = this.get(--offset);
            }
            return offset;
        }
    }
}

