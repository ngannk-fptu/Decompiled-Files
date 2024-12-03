/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.AbstractTextPaster;
import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsdecorator.GlyphsDecorator;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class GlyphsPaster
extends AbstractTextPaster {
    private GlyphsVisitors[] glyphVisitors;
    private GlyphsDecorator[] glyphsDecorators;

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength) {
        super(minAcceptedWordLength, maxAcceptedWordLength);
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, Color textColor) {
        super(minAcceptedWordLength, maxAcceptedWordLength, textColor);
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, GlyphsVisitors[] glyphVisitors) {
        super(minAcceptedWordLength, maxAcceptedWordLength);
        this.glyphVisitors = glyphVisitors;
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, Color textColor, GlyphsVisitors[] glyphVisitors) {
        super(minAcceptedWordLength, maxAcceptedWordLength, textColor);
        this.glyphVisitors = glyphVisitors;
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, GlyphsVisitors[] glyphVisitors) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
        this.glyphVisitors = glyphVisitors;
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, GlyphsVisitors[] glyphVisitors, GlyphsDecorator[] glyphsDecorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
        this.glyphVisitors = glyphVisitors;
        this.glyphsDecorators = glyphsDecorators;
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph, GlyphsVisitors[] glyphVisitors) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
        this.glyphVisitors = glyphVisitors;
    }

    public GlyphsPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph, GlyphsVisitors[] glyphVisitors, GlyphsDecorator[] glyphsDecorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
        this.glyphVisitors = glyphVisitors;
        this.glyphsDecorators = glyphsDecorators;
    }

    @Override
    public BufferedImage pasteText(BufferedImage background, AttributedString attributedWord) throws CaptchaException {
        int i;
        BufferedImage out = this.copyBackground(background);
        Graphics2D g2 = this.pasteBackgroundAndSetTextColor(out, background);
        this.customizeGraphicsRenderingHints(g2);
        AttributedCharacterIterator acit = attributedWord.getIterator();
        Glyphs glyphs = new Glyphs();
        for (int i2 = 0; i2 < acit.getEndIndex(); ++i2) {
            Font font = (Font)acit.getAttribute(TextAttribute.FONT);
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            glyphs.addGlyphVector(font.createGlyphVector(frc, new char[]{acit.current()}));
            acit.next();
        }
        Rectangle2D.Float backgroundBounds = new Rectangle2D.Float(0.0f, 0.0f, background.getWidth(), background.getHeight());
        if (this.glyphVisitors != null) {
            for (int i3 = 0; i3 < this.glyphVisitors.length; ++i3) {
                this.glyphVisitors[i3].visit(glyphs, backgroundBounds);
            }
        }
        for (i = 0; i < glyphs.size(); ++i) {
            g2.drawGlyphVector(glyphs.get(i), 0.0f, 0.0f);
            if (!this.isManageColorPerGlyph()) continue;
            g2.setColor(this.getColorGenerator().getNextColor());
        }
        if (this.glyphsDecorators != null) {
            for (i = 0; i < this.glyphsDecorators.length; ++i) {
                this.glyphsDecorators[i].decorate(g2, glyphs, background);
            }
        }
        g2.dispose();
        return out;
    }
}

