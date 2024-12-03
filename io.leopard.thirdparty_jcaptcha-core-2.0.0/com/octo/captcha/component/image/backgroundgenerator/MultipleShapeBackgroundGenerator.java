/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.backgroundgenerator;

import com.octo.captcha.component.image.backgroundgenerator.AbstractBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class MultipleShapeBackgroundGenerator
extends AbstractBackgroundGenerator {
    private ColorGenerator firstEllipseColorGenerator = new SingleColorGenerator(new Color(210, 210, 210));
    private ColorGenerator secondEllipseColorGenerator = new SingleColorGenerator(new Color(0, 0, 0));
    private ColorGenerator firstRectangleColorGenerator = new SingleColorGenerator(new Color(210, 210, 210));
    private ColorGenerator secondRectangleColorGenerator = new SingleColorGenerator(new Color(0, 0, 0));
    private Integer spaceBetweenLine = new Integer(10);
    private Integer spaceBetweenCircle = new Integer(10);
    private Integer ellipseHeight = new Integer(8);
    private Integer ellipseWidth = new Integer(8);
    private Integer rectangleWidth = new Integer(3);

    public MultipleShapeBackgroundGenerator(Integer width, Integer height) {
        super(width, height);
    }

    public MultipleShapeBackgroundGenerator(Integer width, Integer height, Color firstEllipseColor, Color secondEllipseColor, Integer spaceBetweenLine, Integer spaceBetweenCircle, Integer ellipseHeight, Integer ellipseWidth, Color firstRectangleColor, Color secondRectangleColor, Integer rectangleWidth) {
        super(width, height);
        if (firstEllipseColor != null) {
            this.firstEllipseColorGenerator = new SingleColorGenerator(firstEllipseColor);
        }
        if (secondEllipseColor != null) {
            this.secondEllipseColorGenerator = new SingleColorGenerator(secondEllipseColor);
        }
        if (spaceBetweenLine != null) {
            this.spaceBetweenLine = spaceBetweenCircle;
        }
        if (spaceBetweenCircle != null) {
            this.spaceBetweenCircle = spaceBetweenCircle;
        }
        if (ellipseHeight != null) {
            this.ellipseHeight = ellipseHeight;
        }
        if (ellipseWidth != null) {
            this.ellipseWidth = ellipseWidth;
        }
        if (firstRectangleColor != null) {
            this.firstRectangleColorGenerator = new SingleColorGenerator(firstRectangleColor);
        }
        if (secondRectangleColor != null) {
            this.secondRectangleColorGenerator = new SingleColorGenerator(secondRectangleColor);
        }
        if (rectangleWidth != null) {
            this.rectangleWidth = rectangleWidth;
        }
    }

    @Override
    public BufferedImage getBackground() {
        BufferedImage bi = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setBackground(Color.white);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int variableOnWidth = 0; variableOnWidth < this.getImageWidth(); variableOnWidth += this.getSpaceBetweenLine()) {
            Color firstEllipseColor = this.firstEllipseColorGenerator.getNextColor();
            Color secondEllipseColor = this.secondEllipseColorGenerator.getNextColor();
            Color firstRectangleColor = this.firstRectangleColorGenerator.getNextColor();
            Color secondRectangleColor = this.secondRectangleColorGenerator.getNextColor();
            for (int variableOnHeight = 0; variableOnHeight < this.getImageHeight(); variableOnHeight += this.getSpaceBetweenCircle()) {
                Ellipse2D.Double e2 = new Ellipse2D.Double(variableOnWidth, variableOnHeight, this.getEllipseWidth(), this.getEllipseHeight());
                GradientPaint gp = new GradientPaint(0.0f, this.getEllipseHeight(), firstEllipseColor, this.getEllipseWidth(), 0.0f, secondEllipseColor, true);
                g2.setPaint(gp);
                g2.fill(e2);
            }
            GradientPaint gp2 = new GradientPaint(0.0f, this.getImageHeight(), firstRectangleColor, this.getRectangleWidth(), 0.0f, secondRectangleColor, true);
            g2.setPaint(gp2);
            Rectangle2D.Double r2 = new Rectangle2D.Double(variableOnWidth, 0.0, this.getRectangleWidth(), this.getImageHeight());
            g2.fill(r2);
        }
        g2.dispose();
        return bi;
    }

    protected int getSpaceBetweenLine() {
        return this.spaceBetweenLine;
    }

    protected int getSpaceBetweenCircle() {
        return this.spaceBetweenCircle;
    }

    protected int getEllipseHeight() {
        return this.ellipseHeight;
    }

    protected int getEllipseWidth() {
        return this.ellipseWidth;
    }

    protected int getRectangleWidth() {
        return this.rectangleWidth;
    }
}

