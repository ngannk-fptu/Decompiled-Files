/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsdecorator;

import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsdecorator.GlyphsDecorator;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class RandomLinesGlyphsDecorator
implements GlyphsDecorator {
    private static final double SQRT_2 = Math.sqrt(2.0);
    private Random myRandom = new SecureRandom();
    private double numberOfLinesPerGlyph = 3.0;
    private ColorGenerator linesColorGenerator = null;
    private double lineWidth;
    private double lineLength;
    private int alphaCompositeType = 3;

    public RandomLinesGlyphsDecorator(double numberOfLinesPerGlyph, ColorGenerator linesColorGenerator, double lineWidth, double lineLength) {
        this.numberOfLinesPerGlyph = numberOfLinesPerGlyph;
        this.linesColorGenerator = linesColorGenerator;
        this.lineWidth = lineWidth;
        this.lineLength = lineLength;
    }

    @Override
    public void decorate(Graphics2D g2, Glyphs glyphs, BufferedImage background) {
        Composite originalComposite = g2.getComposite();
        Stroke originalStroke = g2.getStroke();
        Color originalColor = g2.getColor();
        g2.setComposite(AlphaComposite.getInstance(this.alphaCompositeType));
        int j = 0;
        while ((long)j < Math.round((double)glyphs.size() * this.numberOfLinesPerGlyph)) {
            double length = this.around(this.lineLength, 0.5) / (2.0 * SQRT_2);
            double width = this.around(this.lineWidth, 0.3);
            double startX = ((double)background.getWidth() - this.lineWidth) * this.myRandom.nextDouble();
            double startY = ((double)background.getHeight() - this.lineWidth) * this.myRandom.nextDouble();
            double curveX = startX + this.around(length, 0.5) * this.nextSign();
            double curveY = startY + this.around(length, 0.5) * this.nextSign();
            double endX = curveX + this.around(length, 0.5) * this.nextSign();
            double endY = curveY + this.around(length, 0.5) * this.nextSign();
            QuadCurve2D.Double q = new QuadCurve2D.Double(startX, startY, curveX, curveY, endX, endY);
            g2.setColor(this.linesColorGenerator.getNextColor());
            g2.setStroke(new BasicStroke((float)width));
            g2.draw(q);
            ++j;
        }
        g2.setComposite(originalComposite);
        g2.setColor(originalColor);
        g2.setStroke(originalStroke);
    }

    private double around(double from, double precision) {
        double aFrom = from * precision;
        return 2.0 * aFrom * this.myRandom.nextDouble() + from - aFrom;
    }

    private double nextSign() {
        return this.myRandom.nextBoolean() ? 1.0 : -1.0;
    }
}

