/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.block.AbstractBlock;
import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockResult;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.ui.Size2D;
import org.jfree.util.PublicCloneable;

public class BlockContainer
extends AbstractBlock
implements Block,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 8199508075695195293L;
    private List blocks;
    private Arrangement arrangement;

    public BlockContainer() {
        this(new BorderArrangement());
    }

    public BlockContainer(Arrangement arrangement) {
        if (arrangement == null) {
            throw new IllegalArgumentException("Null 'arrangement' argument.");
        }
        this.arrangement = arrangement;
        this.blocks = new ArrayList();
    }

    public Arrangement getArrangement() {
        return this.arrangement;
    }

    public void setArrangement(Arrangement arrangement) {
        if (arrangement == null) {
            throw new IllegalArgumentException("Null 'arrangement' argument.");
        }
        this.arrangement = arrangement;
    }

    public boolean isEmpty() {
        return this.blocks.isEmpty();
    }

    public List getBlocks() {
        return Collections.unmodifiableList(this.blocks);
    }

    public void add(Block block) {
        this.add(block, null);
    }

    public void add(Block block, Object key) {
        this.blocks.add(block);
        this.arrangement.add(block, key);
    }

    public void clear() {
        this.blocks.clear();
        this.arrangement.clear();
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        return this.arrangement.arrange(this, g2, constraint);
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        this.draw(g2, area, null);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        EntityBlockParams ebp = null;
        StandardEntityCollection sec = null;
        if (params instanceof EntityBlockParams && (ebp = (EntityBlockParams)params).getGenerateEntities()) {
            sec = new StandardEntityCollection();
        }
        Rectangle2D contentArea = (Rectangle2D)area.clone();
        contentArea = this.trimMargin(contentArea);
        this.drawBorder(g2, contentArea);
        contentArea = this.trimBorder(contentArea);
        contentArea = this.trimPadding(contentArea);
        Iterator iterator = this.blocks.iterator();
        while (iterator.hasNext()) {
            Block block = (Block)iterator.next();
            Rectangle2D bounds = block.getBounds();
            Rectangle2D.Double drawArea = new Rectangle2D.Double(bounds.getX() + area.getX(), bounds.getY() + area.getY(), bounds.getWidth(), bounds.getHeight());
            Object r = block.draw(g2, drawArea, params);
            if (sec == null || !(r instanceof EntityBlockResult)) continue;
            EntityBlockResult ebr = (EntityBlockResult)r;
            EntityCollection ec = ebr.getEntityCollection();
            sec.addAll(ec);
        }
        BlockResult result = null;
        if (sec != null) {
            result = new BlockResult();
            result.setEntityCollection(sec);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BlockContainer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        BlockContainer that = (BlockContainer)obj;
        if (!this.arrangement.equals(that.arrangement)) {
            return false;
        }
        return ((Object)this.blocks).equals(that.blocks);
    }

    public Object clone() throws CloneNotSupportedException {
        BlockContainer clone = (BlockContainer)super.clone();
        return clone;
    }
}

