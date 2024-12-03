/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.layout.BlockFormattingContext;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.LineBox;

public class FloatManager {
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private List _leftFloats = new ArrayList();
    private List _rightFloats = new ArrayList();
    private Box _master;

    public void floatBox(LayoutContext c, Layer layer, BlockFormattingContext bfc, BlockBox box) {
        if (box.getStyle().isFloatedLeft()) {
            this.position(c, bfc, box, 1);
            this.save(box, layer, bfc, 1);
        } else if (box.getStyle().isFloatedRight()) {
            this.position(c, bfc, box, 2);
            this.save(box, layer, bfc, 2);
        }
    }

    public void clear(CssContext cssCtx, BlockFormattingContext bfc, Box box) {
        if (box.getStyle().isClearLeft()) {
            this.moveClear(cssCtx, bfc, box, this.getFloats(1));
        }
        if (box.getStyle().isClearRight()) {
            this.moveClear(cssCtx, bfc, box, this.getFloats(2));
        }
    }

    private void save(BlockBox current, Layer layer, BlockFormattingContext bfc, int direction) {
        Point p = bfc.getOffset();
        this.getFloats(direction).add(new BoxOffset(current, p.x, p.y));
        layer.addFloat(current, bfc);
        current.getFloatedBoxData().setManager(this);
        current.calcCanvasLocation();
        current.calcChildLocations();
    }

    private void position(CssContext cssCtx, BlockFormattingContext bfc, BlockBox current, int direction) {
        this.moveAllTheWayOver(current, direction);
        this.alignToLastOpposingFloat(cssCtx, bfc, current, direction);
        this.alignToLastFloat(cssCtx, bfc, current, direction);
        if (!this.fitsInContainingBlock(current) || this.overlaps(cssCtx, bfc, current, this.getFloats(direction))) {
            this.moveAllTheWayOver(current, direction);
            this.moveFloatBelow(cssCtx, bfc, current, this.getFloats(direction));
        }
        if (this.overlaps(cssCtx, bfc, current, this.getOpposingFloats(direction))) {
            this.moveAllTheWayOver(current, direction);
            this.moveFloatBelow(cssCtx, bfc, current, this.getFloats(direction));
            this.moveFloatBelow(cssCtx, bfc, current, this.getOpposingFloats(direction));
        }
        if (current.getStyle().isCleared()) {
            if (current.getStyle().isClearLeft() && direction == 1) {
                this.moveAllTheWayOver(current, 1);
            } else if (current.getStyle().isClearRight() && direction == 2) {
                this.moveAllTheWayOver(current, 2);
            }
            this.moveFloatBelow(cssCtx, bfc, current, this.getFloats(direction));
        }
    }

    private List getFloats(int direction) {
        return direction == 1 ? this._leftFloats : this._rightFloats;
    }

    private List getOpposingFloats(int direction) {
        return direction == 1 ? this._rightFloats : this._leftFloats;
    }

    private void alignToLastFloat(CssContext cssCtx, BlockFormattingContext bfc, BlockBox current, int direction) {
        List floats = this.getFloats(direction);
        if (floats.size() > 0) {
            Point offset = bfc.getOffset();
            BoxOffset lastOffset = (BoxOffset)floats.get(floats.size() - 1);
            BlockBox last = lastOffset.getBox();
            Rectangle currentBounds = current.getMarginEdge(cssCtx, -offset.x, -offset.y);
            Rectangle lastBounds = last.getMarginEdge(cssCtx, -lastOffset.getX(), -lastOffset.getY());
            boolean moveOver = false;
            if (currentBounds.y < lastBounds.y) {
                currentBounds.translate(0, lastBounds.y - currentBounds.y);
                moveOver = true;
            }
            if (currentBounds.y >= lastBounds.y && currentBounds.y < lastBounds.y + lastBounds.height) {
                moveOver = true;
            }
            if (moveOver) {
                if (direction == 1) {
                    currentBounds.x = lastBounds.x + last.getWidth();
                } else if (direction == 2) {
                    currentBounds.x = lastBounds.x - current.getWidth();
                }
                currentBounds.translate(offset.x, offset.y);
                current.setX(currentBounds.x);
                current.setY(currentBounds.y);
            }
        }
    }

    private void alignToLastOpposingFloat(CssContext cssCtx, BlockFormattingContext bfc, BlockBox current, int direction) {
        List floats = this.getOpposingFloats(direction);
        if (floats.size() > 0) {
            Point offset = bfc.getOffset();
            BoxOffset lastOffset = (BoxOffset)floats.get(floats.size() - 1);
            Rectangle currentBounds = current.getMarginEdge(cssCtx, -offset.x, -offset.y);
            Rectangle lastBounds = lastOffset.getBox().getMarginEdge(cssCtx, -lastOffset.getX(), -lastOffset.getY());
            if (currentBounds.y < lastBounds.y) {
                currentBounds.translate(0, lastBounds.y - currentBounds.y);
                currentBounds.translate(offset.x, offset.y);
                current.setY(currentBounds.y);
            }
        }
    }

    private void moveAllTheWayOver(BlockBox current, int direction) {
        if (direction == 1) {
            current.setX(0);
        } else if (direction == 2) {
            current.setX(current.getContainingBlock().getContentWidth() - current.getWidth());
        }
    }

    private boolean fitsInContainingBlock(BlockBox current) {
        return current.getX() >= 0 && current.getX() + current.getWidth() <= current.getContainingBlock().getContentWidth();
    }

    private int findLowestY(CssContext cssCtx, List floats) {
        int result = 0;
        for (BoxOffset floater : floats) {
            Rectangle bounds = floater.getBox().getMarginEdge(cssCtx, -floater.getX(), -floater.getY());
            if (bounds.y + bounds.height <= result) continue;
            result = bounds.y + bounds.height;
        }
        return result;
    }

    public int getClearDelta(CssContext cssCtx, int bfcRelativeY) {
        int lowestLeftY = this.findLowestY(cssCtx, this.getFloats(1));
        int lowestRightY = this.findLowestY(cssCtx, this.getFloats(2));
        int lowestY = Math.max(lowestLeftY, lowestRightY);
        return lowestY - bfcRelativeY;
    }

    private boolean overlaps(CssContext cssCtx, BlockFormattingContext bfc, BlockBox current, List floats) {
        Point offset = bfc.getOffset();
        Rectangle bounds = current.getMarginEdge(cssCtx, -offset.x, -offset.y);
        for (BoxOffset floater : floats) {
            Rectangle floaterBounds = floater.getBox().getMarginEdge(cssCtx, -floater.getX(), -floater.getY());
            if (!floaterBounds.intersects(bounds)) continue;
            return true;
        }
        return false;
    }

    private void moveFloatBelow(CssContext cssCtx, BlockFormattingContext bfc, Box current, List floats) {
        if (floats.size() == 0) {
            return;
        }
        Point offset = bfc.getOffset();
        int boxY = current.getY() - offset.y;
        int floatY = this.findLowestY(cssCtx, floats);
        if (floatY - boxY > 0) {
            current.setY(current.getY() + (floatY - boxY));
        }
    }

    private void moveClear(CssContext cssCtx, BlockFormattingContext bfc, Box current, List floats) {
        if (floats.size() == 0) {
            return;
        }
        Point offset = bfc.getOffset();
        Rectangle bounds = current.getBorderEdge(current.getX() - offset.x, current.getY() - offset.y, cssCtx);
        int y = this.findLowestY(cssCtx, floats);
        if (bounds.y < y) {
            bounds.y = y;
            bounds.translate(offset.x, offset.y);
            current.setY(bounds.y - (int)current.getMargin(cssCtx).top());
        }
    }

    public void removeFloat(BlockBox floater) {
        this.removeFloat(floater, this.getFloats(1));
        this.removeFloat(floater, this.getFloats(2));
    }

    private void removeFloat(BlockBox floater, List floats) {
        Iterator i = floats.iterator();
        while (i.hasNext()) {
            BoxOffset boxOffset = (BoxOffset)i.next();
            if (!boxOffset.getBox().equals(floater)) continue;
            i.remove();
            floater.getFloatedBoxData().setManager(null);
        }
    }

    public void calcFloatLocations() {
        this.calcFloatLocations(this.getFloats(1));
        this.calcFloatLocations(this.getFloats(2));
    }

    private void calcFloatLocations(List floats) {
        for (BoxOffset boxOffset : floats) {
            boxOffset.getBox().calcCanvasLocation();
            boxOffset.getBox().calcChildLocations();
        }
    }

    private void applyLineHeightHack(CssContext cssCtx, Box line, Rectangle bounds) {
        if (line.getHeight() == 0) {
            bounds.height = (int)line.getStyle().getLineHeight(cssCtx);
        }
    }

    public int getNextLineBoxDelta(CssContext cssCtx, BlockFormattingContext bfc, LineBox line, int containingBlockContentWidth) {
        BoxDistance left = this.getFloatDistance(cssCtx, bfc, line, containingBlockContentWidth, this._leftFloats, 1);
        BoxDistance right = this.getFloatDistance(cssCtx, bfc, line, containingBlockContentWidth, this._rightFloats, 2);
        int leftDelta = left.getBox() != null ? this.calcDelta(cssCtx, line, left) : 0;
        int rightDelta = right.getBox() != null ? this.calcDelta(cssCtx, line, right) : 0;
        return Math.max(leftDelta, rightDelta);
    }

    private int calcDelta(CssContext cssCtx, LineBox line, BoxDistance boxDistance) {
        BlockBox floated = boxDistance.getBox();
        Rectangle rect = floated.getBorderEdge(floated.getAbsX(), floated.getAbsY(), cssCtx);
        int bottom = rect.y + rect.height;
        return bottom - line.getAbsY();
    }

    public int getLeftFloatDistance(CssContext cssCtx, BlockFormattingContext bfc, LineBox line, int containingBlockContentWidth) {
        return this.getFloatDistance(cssCtx, bfc, line, containingBlockContentWidth, this._leftFloats, 1).getDistance();
    }

    public int getRightFloatDistance(CssContext cssCtx, BlockFormattingContext bfc, LineBox line, int containingBlockContentWidth) {
        return this.getFloatDistance(cssCtx, bfc, line, containingBlockContentWidth, this._rightFloats, 2).getDistance();
    }

    private BoxDistance getFloatDistance(CssContext cssCtx, BlockFormattingContext bfc, LineBox line, int containingBlockContentWidth, List floatsList, int direction) {
        if (floatsList.size() == 0) {
            return new BoxDistance(null, 0);
        }
        Point offset = bfc.getOffset();
        Rectangle lineBounds = line.getMarginEdge(cssCtx, -offset.x, -offset.y);
        lineBounds.width = containingBlockContentWidth;
        int farthestOver = direction == 1 ? lineBounds.x : lineBounds.x + lineBounds.width;
        this.applyLineHeightHack(cssCtx, line, lineBounds);
        BlockBox farthestOverBox = null;
        for (int i = 0; i < floatsList.size(); ++i) {
            BoxOffset floater = (BoxOffset)floatsList.get(i);
            Rectangle fr = floater.getBox().getMarginEdge(cssCtx, -floater.getX(), -floater.getY());
            if (!lineBounds.intersects(fr)) continue;
            if (direction == 1 && fr.x + fr.width > farthestOver) {
                farthestOver = fr.x + fr.width;
            } else if (direction == 2 && fr.x < farthestOver) {
                farthestOver = fr.x;
            }
            farthestOverBox = floater.getBox();
        }
        if (direction == 1) {
            return new BoxDistance(farthestOverBox, farthestOver - lineBounds.x);
        }
        return new BoxDistance(farthestOverBox, lineBounds.x + lineBounds.width - farthestOver);
    }

    public void setMaster(Box owner) {
        this._master = owner;
    }

    public Box getMaster() {
        return this._master;
    }

    public Point getOffset(BlockBox floater) {
        return this.getOffset(floater, floater.getStyle().isFloatedLeft() ? this.getFloats(1) : this.getFloats(2));
    }

    private Point getOffset(BlockBox floater, List floats) {
        for (BoxOffset boxOffset : floats) {
            BlockBox box = boxOffset.getBox();
            if (!box.equals(floater)) continue;
            return new Point(boxOffset.getX(), boxOffset.getY());
        }
        return null;
    }

    private void performFloatOperation(FloatOperation op, List floats) {
        for (BoxOffset boxOffset : floats) {
            BlockBox box = boxOffset.getBox();
            box.setAbsX(box.getX() + this.getMaster().getAbsX() - boxOffset.getX());
            box.setAbsY(box.getY() + this.getMaster().getAbsY() - boxOffset.getY());
            op.operate(box);
        }
    }

    public void performFloatOperation(FloatOperation op) {
        this.performFloatOperation(op, this.getFloats(1));
        this.performFloatOperation(op, this.getFloats(2));
    }

    public static interface FloatOperation {
        public void operate(Box var1);
    }

    private static class BoxDistance {
        private BlockBox _box;
        private int _distance;

        public BoxDistance(BlockBox box, int distance) {
            this._box = box;
            this._distance = distance;
        }

        BlockBox getBox() {
            return this._box;
        }

        int getDistance() {
            return this._distance;
        }
    }

    private static class BoxOffset {
        private BlockBox _box;
        private int _x;
        private int _y;

        public BoxOffset(BlockBox box, int x, int y) {
            this._box = box;
            this._x = x;
            this._y = y;
        }

        public BlockBox getBox() {
            return this._box;
        }

        public int getX() {
            return this._x;
        }

        public int getY() {
            return this._y;
        }
    }
}

