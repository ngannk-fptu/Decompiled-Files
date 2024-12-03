/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.textdecorator;

import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.textpaster.MutableAttributedString;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.security.SecureRandom;
import java.util.Random;

public class LineTextDecorator
implements TextDecorator {
    private Random myRandom = new SecureRandom();
    private Integer numberOfLinesPerGlyph = new Integer(3);
    private ColorGenerator linesColorGenerator = null;
    private int alphaCompositeType = 3;

    public LineTextDecorator(Integer numberOfLinesPerGlyph, Color linesColor) {
        this.numberOfLinesPerGlyph = numberOfLinesPerGlyph != null ? numberOfLinesPerGlyph : this.numberOfLinesPerGlyph;
        this.linesColorGenerator = new SingleColorGenerator(linesColor != null ? linesColor : Color.white);
    }

    public LineTextDecorator(Integer numberOfLinesPerGlyph, ColorGenerator linesColorGenerator) {
        this.numberOfLinesPerGlyph = numberOfLinesPerGlyph != null ? numberOfLinesPerGlyph : this.numberOfLinesPerGlyph;
        this.linesColorGenerator = linesColorGenerator != null ? linesColorGenerator : new SingleColorGenerator(Color.white);
    }

    public LineTextDecorator(Integer numberOfLinesPerGlyph, ColorGenerator linesColorGenerator, Integer alphaCompositeType) {
        this(numberOfLinesPerGlyph, linesColorGenerator);
        this.alphaCompositeType = alphaCompositeType != null ? alphaCompositeType : this.alphaCompositeType;
    }

    @Override
    public void decorateAttributedString(Graphics2D g2, MutableAttributedString mutableAttributedString) {
        Color oldColor = g2.getColor();
        Composite oldComp = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(this.alphaCompositeType));
        for (int j = 0; j < mutableAttributedString.length(); ++j) {
            g2.setColor(this.linesColorGenerator.getNextColor());
            Rectangle2D bounds = mutableAttributedString.getBounds(j).getFrame();
            for (int i = 0; i < this.numberOfLinesPerGlyph; ++i) {
                double circlex = bounds.getMinX() + bounds.getWidth() * 0.7 * this.myRandom.nextDouble();
                double circley = bounds.getMinY() - bounds.getHeight() * 0.5 * this.myRandom.nextDouble();
                double width = 5 + this.myRandom.nextInt(25);
                double length = 5 + this.myRandom.nextInt(25);
                double angle = Math.PI * this.myRandom.nextDouble();
                AffineTransform transformation = new AffineTransform(Math.cos(angle), -Math.sin(angle), Math.sin(angle), Math.cos(angle), circlex, circley);
                QuadCurve2D.Double q = new QuadCurve2D.Double();
                ((QuadCurve2D)q).setCurve(0.0, 0.0, length / 2.0 + 15.0 * this.myRandom.nextDouble() * (double)(this.myRandom.nextBoolean() ? -1 : 1), width / 2.0 + 15.0 * this.myRandom.nextDouble() * (double)(this.myRandom.nextBoolean() ? -1 : 1), length, width);
                g2.setStroke(new BasicStroke(2 + this.myRandom.nextInt(4)));
                g2.draw(transformation.createTransformedShape(q));
            }
        }
        g2.setComposite(oldComp);
        g2.setColor(oldColor);
    }
}

