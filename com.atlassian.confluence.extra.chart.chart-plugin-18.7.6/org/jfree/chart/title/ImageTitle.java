/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.title.Title;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.ObjectUtilities;

public class ImageTitle
extends Title {
    private Image image;

    public ImageTitle(Image image) {
        this(image, image.getHeight(null), image.getWidth(null), Title.DEFAULT_POSITION, Title.DEFAULT_HORIZONTAL_ALIGNMENT, Title.DEFAULT_VERTICAL_ALIGNMENT, Title.DEFAULT_PADDING);
    }

    public ImageTitle(Image image, RectangleEdge position, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        this(image, image.getHeight(null), image.getWidth(null), position, horizontalAlignment, verticalAlignment, Title.DEFAULT_PADDING);
    }

    public ImageTitle(Image image, int height, int width, RectangleEdge position, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, RectangleInsets padding) {
        super(position, horizontalAlignment, verticalAlignment, padding);
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        this.setHeight(height);
        this.setWidth(width);
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        Size2D s = new Size2D(this.image.getWidth(null), this.image.getHeight(null));
        return new Size2D(this.calculateTotalWidth(s.getWidth()), this.calculateTotalHeight(s.getHeight()));
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        RectangleEdge position = this.getPosition();
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            this.drawHorizontal(g2, area);
        } else if (position == RectangleEdge.LEFT || position == RectangleEdge.RIGHT) {
            this.drawVertical(g2, area);
        } else {
            throw new RuntimeException("Invalid title position.");
        }
    }

    protected Size2D drawHorizontal(Graphics2D g2, Rectangle2D chartArea) {
        double startY = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;
        double w = this.getWidth();
        double h = this.getHeight();
        RectangleInsets padding = this.getPadding();
        topSpace = padding.calculateTopOutset(h);
        bottomSpace = padding.calculateBottomOutset(h);
        leftSpace = padding.calculateLeftOutset(w);
        rightSpace = padding.calculateRightOutset(w);
        startY = this.getPosition() == RectangleEdge.TOP ? chartArea.getY() + topSpace : chartArea.getY() + chartArea.getHeight() - bottomSpace - h;
        HorizontalAlignment horizontalAlignment = this.getHorizontalAlignment();
        double startX = 0.0;
        if (horizontalAlignment == HorizontalAlignment.CENTER) {
            startX = chartArea.getX() + leftSpace + chartArea.getWidth() / 2.0 - w / 2.0;
        } else if (horizontalAlignment == HorizontalAlignment.LEFT) {
            startX = chartArea.getX() + leftSpace;
        } else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
            startX = chartArea.getX() + chartArea.getWidth() - rightSpace - w;
        }
        g2.drawImage(this.image, (int)startX, (int)startY, (int)w, (int)h, null);
        return new Size2D(chartArea.getWidth() + leftSpace + rightSpace, h + topSpace + bottomSpace);
    }

    protected Size2D drawVertical(Graphics2D g2, Rectangle2D chartArea) {
        double startX = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;
        double w = this.getWidth();
        double h = this.getHeight();
        RectangleInsets padding = this.getPadding();
        if (padding != null) {
            topSpace = padding.calculateTopOutset(h);
            bottomSpace = padding.calculateBottomOutset(h);
            leftSpace = padding.calculateLeftOutset(w);
            rightSpace = padding.calculateRightOutset(w);
        }
        startX = this.getPosition() == RectangleEdge.LEFT ? chartArea.getX() + leftSpace : chartArea.getMaxX() - rightSpace - w;
        VerticalAlignment alignment = this.getVerticalAlignment();
        double startY = 0.0;
        if (alignment == VerticalAlignment.CENTER) {
            startY = chartArea.getMinY() + topSpace + chartArea.getHeight() / 2.0 - h / 2.0;
        } else if (alignment == VerticalAlignment.TOP) {
            startY = chartArea.getMinY() + topSpace;
        } else if (alignment == VerticalAlignment.BOTTOM) {
            startY = chartArea.getMaxY() - bottomSpace - h;
        }
        g2.drawImage(this.image, (int)startX, (int)startY, (int)w, (int)h, null);
        return new Size2D(chartArea.getWidth() + leftSpace + rightSpace, h + topSpace + bottomSpace);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        this.draw(g2, area);
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ImageTitle)) {
            return false;
        }
        ImageTitle that = (ImageTitle)obj;
        if (!ObjectUtilities.equal(this.image, that.image)) {
            return false;
        }
        return super.equals(obj);
    }
}

