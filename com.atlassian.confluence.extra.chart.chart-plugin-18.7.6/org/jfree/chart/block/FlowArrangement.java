/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;

public class FlowArrangement
implements Arrangement,
Serializable {
    private static final long serialVersionUID = 4543632485478613800L;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private double horizontalGap;
    private double verticalGap;

    public FlowArrangement() {
        this(HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 2.0, 2.0);
    }

    public FlowArrangement(HorizontalAlignment hAlign, VerticalAlignment vAlign, double hGap, double vGap) {
        this.horizontalAlignment = hAlign;
        this.verticalAlignment = vAlign;
        this.horizontalGap = hGap;
        this.verticalGap = vGap;
    }

    public void add(Block block, Object key) {
    }

    public Size2D arrange(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        LengthConstraintType w = constraint.getWidthConstraintType();
        LengthConstraintType h = constraint.getHeightConstraintType();
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                return this.arrangeNN(container, g2);
            }
            if (h == LengthConstraintType.FIXED) {
                return this.arrangeNF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                return this.arrangeFN(container, g2, constraint);
            }
            if (h == LengthConstraintType.FIXED) {
                return this.arrangeFF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                return this.arrangeFR(container, g2, constraint);
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                return this.arrangeRN(container, g2, constraint);
            }
            if (h == LengthConstraintType.FIXED) {
                return this.arrangeRF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                return this.arrangeRR(container, g2, constraint);
            }
        }
        throw new RuntimeException("Unrecognised constraint type.");
    }

    protected Size2D arrangeFN(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        List blocks = container.getBlocks();
        double width = constraint.getWidth();
        double x = 0.0;
        double y = 0.0;
        double maxHeight = 0.0;
        ArrayList<Block> itemsInRow = new ArrayList<Block>();
        for (int i = 0; i < blocks.size(); ++i) {
            Block block = (Block)blocks.get(i);
            Size2D size = block.arrange(g2, RectangleConstraint.NONE);
            if (x + size.width <= width) {
                itemsInRow.add(block);
                block.setBounds(new Rectangle2D.Double(x, y, size.width, size.height));
                x = x + size.width + this.horizontalGap;
                maxHeight = Math.max(maxHeight, size.height);
                continue;
            }
            if (itemsInRow.isEmpty()) {
                block.setBounds(new Rectangle2D.Double(x, y, Math.min(size.width, width - x), size.height));
                x = 0.0;
                y = y + size.height + this.verticalGap;
                continue;
            }
            itemsInRow.clear();
            x = 0.0;
            y = y + maxHeight + this.verticalGap;
            maxHeight = size.height;
            block.setBounds(new Rectangle2D.Double(x, y, Math.min(size.width, width), size.height));
            x = size.width + this.horizontalGap;
            itemsInRow.add(block);
        }
        return new Size2D(constraint.getWidth(), y + maxHeight);
    }

    protected Size2D arrangeFR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s = this.arrangeFN(container, g2, constraint);
        if (constraint.getHeightRange().contains(s.height)) {
            return s;
        }
        RectangleConstraint c = constraint.toFixedHeight(constraint.getHeightRange().constrain(s.getHeight()));
        return this.arrangeFF(container, g2, c);
    }

    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        return this.arrangeFN(container, g2, constraint);
    }

    protected Size2D arrangeRR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s1 = this.arrangeNN(container, g2);
        if (constraint.getWidthRange().contains(s1.width)) {
            return s1;
        }
        RectangleConstraint c = constraint.toFixedWidth(constraint.getWidthRange().getUpperBound());
        return this.arrangeFR(container, g2, c);
    }

    protected Size2D arrangeRF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s = this.arrangeNF(container, g2, constraint);
        if (constraint.getWidthRange().contains(s.width)) {
            return s;
        }
        RectangleConstraint c = constraint.toFixedWidth(constraint.getWidthRange().constrain(s.getWidth()));
        return this.arrangeFF(container, g2, c);
    }

    protected Size2D arrangeRN(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s1 = this.arrangeNN(container, g2);
        if (constraint.getWidthRange().contains(s1.width)) {
            return s1;
        }
        RectangleConstraint c = constraint.toFixedWidth(constraint.getWidthRange().getUpperBound());
        return this.arrangeFN(container, g2, c);
    }

    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        double x = 0.0;
        double width = 0.0;
        double maxHeight = 0.0;
        List blocks = container.getBlocks();
        int blockCount = blocks.size();
        if (blockCount > 0) {
            int i;
            Size2D[] sizes = new Size2D[blocks.size()];
            for (i = 0; i < blocks.size(); ++i) {
                Block block = (Block)blocks.get(i);
                sizes[i] = block.arrange(g2, RectangleConstraint.NONE);
                width += sizes[i].getWidth();
                maxHeight = Math.max(sizes[i].height, maxHeight);
                block.setBounds(new Rectangle2D.Double(x, 0.0, sizes[i].width, sizes[i].height));
                x = x + sizes[i].width + this.horizontalGap;
            }
            if (blockCount > 1) {
                width += this.horizontalGap * (double)(blockCount - 1);
            }
            if (this.verticalAlignment != VerticalAlignment.TOP) {
                for (i = 0; i < blocks.size(); ++i) {
                    if (this.verticalAlignment != VerticalAlignment.CENTER && this.verticalAlignment != VerticalAlignment.BOTTOM) continue;
                }
            }
        }
        return new Size2D(width, maxHeight);
    }

    protected Size2D arrangeNF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        return this.arrangeNN(container, g2);
    }

    public void clear() {
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FlowArrangement)) {
            return false;
        }
        FlowArrangement that = (FlowArrangement)obj;
        if (this.horizontalAlignment != that.horizontalAlignment) {
            return false;
        }
        if (this.verticalAlignment != that.verticalAlignment) {
            return false;
        }
        if (this.horizontalGap != that.horizontalGap) {
            return false;
        }
        return this.verticalGap == that.verticalGap;
    }
}

