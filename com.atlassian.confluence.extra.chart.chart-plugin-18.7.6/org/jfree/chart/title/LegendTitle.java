/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.title;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BlockFrame;
import org.jfree.chart.block.BlockResult;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.CenterArrangement;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.FlowArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.entity.TitleEntity;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.title.LegendGraphic;
import org.jfree.chart.title.LegendItemBlockContainer;
import org.jfree.chart.title.Title;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class LegendTitle
extends Title
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 2644010518533854633L;
    public static final Font DEFAULT_ITEM_FONT = new Font("SansSerif", 0, 12);
    public static final Paint DEFAULT_ITEM_PAINT = Color.black;
    private LegendItemSource[] sources;
    private transient Paint backgroundPaint;
    private RectangleEdge legendItemGraphicEdge;
    private RectangleAnchor legendItemGraphicAnchor;
    private RectangleAnchor legendItemGraphicLocation;
    private RectangleInsets legendItemGraphicPadding;
    private Font itemFont;
    private transient Paint itemPaint;
    private RectangleInsets itemLabelPadding;
    private BlockContainer items;
    private Arrangement hLayout;
    private Arrangement vLayout;
    private BlockContainer wrapper;

    public LegendTitle(LegendItemSource source) {
        this(source, new FlowArrangement(), new ColumnArrangement());
    }

    public LegendTitle(LegendItemSource source, Arrangement hLayout, Arrangement vLayout) {
        this.sources = new LegendItemSource[]{source};
        this.items = new BlockContainer(hLayout);
        this.hLayout = hLayout;
        this.vLayout = vLayout;
        this.backgroundPaint = null;
        this.legendItemGraphicEdge = RectangleEdge.LEFT;
        this.legendItemGraphicAnchor = RectangleAnchor.CENTER;
        this.legendItemGraphicLocation = RectangleAnchor.CENTER;
        this.legendItemGraphicPadding = new RectangleInsets(2.0, 2.0, 2.0, 2.0);
        this.itemFont = DEFAULT_ITEM_FONT;
        this.itemPaint = DEFAULT_ITEM_PAINT;
        this.itemLabelPadding = new RectangleInsets(2.0, 2.0, 2.0, 2.0);
    }

    public LegendItemSource[] getSources() {
        return this.sources;
    }

    public void setSources(LegendItemSource[] sources) {
        if (sources == null) {
            throw new IllegalArgumentException("Null 'sources' argument.");
        }
        this.sources = sources;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public RectangleEdge getLegendItemGraphicEdge() {
        return this.legendItemGraphicEdge;
    }

    public void setLegendItemGraphicEdge(RectangleEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Null 'edge' argument.");
        }
        this.legendItemGraphicEdge = edge;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public RectangleAnchor getLegendItemGraphicAnchor() {
        return this.legendItemGraphicAnchor;
    }

    public void setLegendItemGraphicAnchor(RectangleAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' point.");
        }
        this.legendItemGraphicAnchor = anchor;
    }

    public RectangleAnchor getLegendItemGraphicLocation() {
        return this.legendItemGraphicLocation;
    }

    public void setLegendItemGraphicLocation(RectangleAnchor anchor) {
        this.legendItemGraphicLocation = anchor;
    }

    public RectangleInsets getLegendItemGraphicPadding() {
        return this.legendItemGraphicPadding;
    }

    public void setLegendItemGraphicPadding(RectangleInsets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");
        }
        this.legendItemGraphicPadding = padding;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public Font getItemFont() {
        return this.itemFont;
    }

    public void setItemFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.itemFont = font;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public Paint getItemPaint() {
        return this.itemPaint;
    }

    public void setItemPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.itemPaint = paint;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public RectangleInsets getItemLabelPadding() {
        return this.itemLabelPadding;
    }

    public void setItemLabelPadding(RectangleInsets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");
        }
        this.itemLabelPadding = padding;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    protected void fetchLegendItems() {
        this.items.clear();
        RectangleEdge p = this.getPosition();
        if (RectangleEdge.isTopOrBottom(p)) {
            this.items.setArrangement(this.hLayout);
        } else {
            this.items.setArrangement(this.vLayout);
        }
        for (int s = 0; s < this.sources.length; ++s) {
            LegendItemCollection legendItems = this.sources[s].getLegendItems();
            if (legendItems == null) continue;
            for (int i = 0; i < legendItems.getItemCount(); ++i) {
                LegendItem item = legendItems.get(i);
                Block block = this.createLegendItemBlock(item);
                this.items.add(block);
            }
        }
    }

    protected Block createLegendItemBlock(LegendItem item) {
        Paint textPaint;
        BlockContainer result = null;
        LegendGraphic lg = new LegendGraphic(item.getShape(), item.getFillPaint());
        lg.setFillPaintTransformer(item.getFillPaintTransformer());
        lg.setShapeFilled(item.isShapeFilled());
        lg.setLine(item.getLine());
        lg.setLineStroke(item.getLineStroke());
        lg.setLinePaint(item.getLinePaint());
        lg.setLineVisible(item.isLineVisible());
        lg.setShapeVisible(item.isShapeVisible());
        lg.setShapeOutlineVisible(item.isShapeOutlineVisible());
        lg.setOutlinePaint(item.getOutlinePaint());
        lg.setOutlineStroke(item.getOutlineStroke());
        lg.setPadding(this.legendItemGraphicPadding);
        LegendItemBlockContainer legendItem = new LegendItemBlockContainer((Arrangement)new BorderArrangement(), item.getDataset(), item.getSeriesKey());
        lg.setShapeAnchor(this.getLegendItemGraphicAnchor());
        lg.setShapeLocation(this.getLegendItemGraphicLocation());
        legendItem.add(lg, this.legendItemGraphicEdge);
        Font textFont = item.getLabelFont();
        if (textFont == null) {
            textFont = this.itemFont;
        }
        if ((textPaint = item.getLabelPaint()) == null) {
            textPaint = this.itemPaint;
        }
        LabelBlock labelBlock = new LabelBlock(item.getLabel(), textFont, textPaint);
        labelBlock.setPadding(this.itemLabelPadding);
        legendItem.add(labelBlock);
        legendItem.setToolTipText(item.getToolTipText());
        legendItem.setURLText(item.getURLText());
        result = new BlockContainer(new CenterArrangement());
        result.add(legendItem);
        return result;
    }

    public BlockContainer getItemContainer() {
        return this.items;
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        Size2D result = new Size2D();
        this.fetchLegendItems();
        if (this.items.isEmpty()) {
            return result;
        }
        BlockContainer container = this.wrapper;
        if (container == null) {
            container = this.items;
        }
        RectangleConstraint c = this.toContentConstraint(constraint);
        Size2D size = container.arrange(g2, c);
        result.height = this.calculateTotalHeight(size.height);
        result.width = this.calculateTotalWidth(size.width);
        return result;
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        this.draw(g2, area, null);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        EntityCollection ec;
        Object val;
        Rectangle2D target = (Rectangle2D)area.clone();
        Rectangle2D hotspot = (Rectangle2D)area.clone();
        StandardEntityCollection sec = null;
        if (params instanceof EntityBlockParams && ((EntityBlockParams)params).getGenerateEntities()) {
            sec = new StandardEntityCollection();
            sec.add(new TitleEntity((Shape)hotspot, this));
        }
        target = this.trimMargin(target);
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(target);
        }
        BlockFrame border = this.getFrame();
        border.draw(g2, target);
        border.getInsets().trim(target);
        BlockContainer container = this.wrapper;
        if (container == null) {
            container = this.items;
        }
        if ((val = container.draw(g2, target = this.trimPadding(target), params)) instanceof BlockResult && (ec = ((BlockResult)val).getEntityCollection()) != null && sec != null) {
            sec.addAll(ec);
            ((BlockResult)val).setEntityCollection(sec);
        }
        return val;
    }

    public BlockContainer getWrapper() {
        return this.wrapper;
    }

    public void setWrapper(BlockContainer wrapper) {
        this.wrapper = wrapper;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LegendTitle)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        LegendTitle that = (LegendTitle)obj;
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (this.legendItemGraphicEdge != that.legendItemGraphicEdge) {
            return false;
        }
        if (this.legendItemGraphicAnchor != that.legendItemGraphicAnchor) {
            return false;
        }
        if (this.legendItemGraphicLocation != that.legendItemGraphicLocation) {
            return false;
        }
        if (!this.itemFont.equals(that.itemFont)) {
            return false;
        }
        if (!this.itemPaint.equals(that.itemPaint)) {
            return false;
        }
        if (!this.hLayout.equals(that.hLayout)) {
            return false;
        }
        return this.vLayout.equals(that.vLayout);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.itemPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.itemPaint = SerialUtilities.readPaint(stream);
    }
}

