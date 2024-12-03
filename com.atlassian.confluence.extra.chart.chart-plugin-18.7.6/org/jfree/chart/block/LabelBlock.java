/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.block;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.block.AbstractBlock;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockResult;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.Size2D;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class LabelBlock
extends AbstractBlock
implements Block,
PublicCloneable {
    static final long serialVersionUID = 249626098864178017L;
    private String text;
    private TextBlock label;
    private Font font;
    private String toolTipText;
    private String urlText;
    public static final Paint DEFAULT_PAINT = Color.black;
    private transient Paint paint;
    private TextBlockAnchor contentAlignmentPoint;
    private RectangleAnchor textAnchor;

    public LabelBlock(String label) {
        this(label, new Font("SansSerif", 0, 10), DEFAULT_PAINT);
    }

    public LabelBlock(String text, Font font) {
        this(text, font, DEFAULT_PAINT);
    }

    public LabelBlock(String text, Font font, Paint paint) {
        this.text = text;
        this.paint = paint;
        this.label = TextUtilities.createTextBlock(text, font, this.paint);
        this.font = font;
        this.toolTipText = null;
        this.urlText = null;
        this.contentAlignmentPoint = TextBlockAnchor.CENTER;
        this.textAnchor = RectangleAnchor.CENTER;
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.font = font;
        this.label = TextUtilities.createTextBlock(this.text, font, this.paint);
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        this.label = TextUtilities.createTextBlock(this.text, this.font, this.paint);
    }

    public String getToolTipText() {
        return this.toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public String getURLText() {
        return this.urlText;
    }

    public void setURLText(String text) {
        this.urlText = text;
    }

    public TextBlockAnchor getContentAlignmentPoint() {
        return this.contentAlignmentPoint;
    }

    public void setContentAlignmentPoint(TextBlockAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.contentAlignmentPoint = anchor;
    }

    public RectangleAnchor getTextAnchor() {
        return this.textAnchor;
    }

    public void setTextAnchor(RectangleAnchor anchor) {
        this.textAnchor = anchor;
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        g2.setFont(this.font);
        Size2D s = this.label.calculateDimensions(g2);
        return new Size2D(this.calculateTotalWidth(s.getWidth()), this.calculateTotalHeight(s.getHeight()));
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        this.draw(g2, area, null);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        area = this.trimMargin(area);
        this.drawBorder(g2, area);
        area = this.trimBorder(area);
        area = this.trimPadding(area);
        EntityBlockParams ebp = null;
        StandardEntityCollection sec = null;
        Shape entityArea = null;
        if (params instanceof EntityBlockParams && (ebp = (EntityBlockParams)params).getGenerateEntities()) {
            sec = new StandardEntityCollection();
            entityArea = (Shape)area.clone();
        }
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        Point2D pt = RectangleAnchor.coordinates(area, this.textAnchor);
        this.label.draw(g2, (float)pt.getX(), (float)pt.getY(), this.contentAlignmentPoint);
        BlockResult result = null;
        if (ebp != null && sec != null && (this.toolTipText != null || this.urlText != null)) {
            ChartEntity entity = new ChartEntity(entityArea, this.toolTipText, this.urlText);
            sec.add(entity);
            result = new BlockResult();
            result.setEntityCollection(sec);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LabelBlock)) {
            return false;
        }
        LabelBlock that = (LabelBlock)obj;
        if (!this.text.equals(that.text)) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipText, that.toolTipText)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.urlText, that.urlText)) {
            return false;
        }
        if (!this.contentAlignmentPoint.equals(that.contentAlignmentPoint)) {
            return false;
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }
}

