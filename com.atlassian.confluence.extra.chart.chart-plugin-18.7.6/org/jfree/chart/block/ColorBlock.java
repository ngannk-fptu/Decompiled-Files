/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.block.AbstractBlock;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Size2D;
import org.jfree.util.PaintUtilities;

public class ColorBlock
extends AbstractBlock
implements Block {
    static final long serialVersionUID = 3383866145634010865L;
    private transient Paint paint;

    public ColorBlock(Paint paint, double width, double height) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        this.setWidth(width);
        this.setHeight(height);
    }

    public Paint getPaint() {
        return this.paint;
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        return new Size2D(this.calculateTotalWidth(this.getWidth()), this.calculateTotalHeight(this.getHeight()));
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        area = this.trimMargin(area);
        this.drawBorder(g2, area);
        area = this.trimBorder(area);
        area = this.trimPadding(area);
        g2.setPaint(this.paint);
        g2.fill(area);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        this.draw(g2, area);
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColorBlock)) {
            return false;
        }
        ColorBlock that = (ColorBlock)obj;
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        return super.equals(obj);
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

