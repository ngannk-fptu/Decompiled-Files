/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsdecorator;

import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsdecorator.GlyphsDecorator;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class BaffleGlyphsDecorator
implements GlyphsDecorator {
    private Random myRandom = new SecureRandom();
    private static double circleXRatio = 0.7;
    private static double circleYRatio = 0.5;
    private Integer numberOfHolesPerGlyph = new Integer(3);
    private ColorGenerator holesColorGenerator = null;
    private int alphaCompositeType = 3;

    public BaffleGlyphsDecorator(Integer numberOfHolesPerGlyph, Color holesColor) {
        this.numberOfHolesPerGlyph = numberOfHolesPerGlyph != null ? numberOfHolesPerGlyph : this.numberOfHolesPerGlyph;
        this.holesColorGenerator = new SingleColorGenerator(holesColor != null ? holesColor : Color.white);
    }

    public BaffleGlyphsDecorator(Integer numberOfHolesPerGlyph, ColorGenerator holesColorGenerator) {
        this.numberOfHolesPerGlyph = numberOfHolesPerGlyph != null ? numberOfHolesPerGlyph : this.numberOfHolesPerGlyph;
        this.holesColorGenerator = holesColorGenerator != null ? holesColorGenerator : new SingleColorGenerator(Color.white);
    }

    public BaffleGlyphsDecorator(Integer numberOfHolesPerGlyph, ColorGenerator holesColorGenerator, Integer alphaCompositeType) {
        this(numberOfHolesPerGlyph, holesColorGenerator);
        this.alphaCompositeType = alphaCompositeType != null ? alphaCompositeType : this.alphaCompositeType;
    }

    @Override
    public void decorate(Graphics2D g2, Glyphs glyphs, BufferedImage backround) {
        Color oldColor = g2.getColor();
        Composite oldComp = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(this.alphaCompositeType));
        for (int j = 0; j < glyphs.size(); ++j) {
            g2.setColor(this.holesColorGenerator.getNextColor());
            Rectangle2D bounds = glyphs.getBounds(j).getFrame();
            double circleMaxSize = bounds.getWidth() / 2.0;
            for (int i = 0; i < this.numberOfHolesPerGlyph; ++i) {
                double circleSize = circleMaxSize * (1.0 + this.myRandom.nextDouble()) / 2.0;
                double circlex = bounds.getMinX() + bounds.getWidth() * circleXRatio * this.myRandom.nextDouble();
                double circley = bounds.getMinY() - bounds.getHeight() * circleYRatio * this.myRandom.nextDouble();
                Ellipse2D.Double circle = new Ellipse2D.Double(circlex, circley, circleSize, circleSize);
                g2.fill(circle);
            }
        }
        g2.setColor(oldColor);
        g2.setComposite(oldComp);
    }
}

