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
import com.octo.captcha.component.image.textpaster.MutableAttributedString;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.textpaster.textvisitor.TextVisitor;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public class VisitedAndDecoratedTextPaster
extends AbstractTextPaster {
    protected final int kerning = 20;
    private TextVisitor[] textVisitors;
    private TextDecorator[] textDecorators;

    public VisitedAndDecoratedTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, TextVisitor[] textVisitors, TextDecorator[] textDecorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }

    public VisitedAndDecoratedTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, Color textColor, TextVisitor[] textVisitors, TextDecorator[] textDecorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength, textColor);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }

    public VisitedAndDecoratedTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, TextVisitor[] textVisitors, TextDecorator[] textDecorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }

    public VisitedAndDecoratedTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph, TextVisitor[] textVisitors, TextDecorator[] textDecorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }

    @Override
    public BufferedImage pasteText(BufferedImage background, AttributedString attributedWord) throws CaptchaException {
        int i;
        BufferedImage out = this.copyBackground(background);
        Graphics2D g2 = this.pasteBackgroundAndSetTextColor(out, background);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        MutableAttributedString mas = new MutableAttributedString(g2, attributedWord, 20);
        if (this.textVisitors != null) {
            for (i = 0; i < this.textVisitors.length; ++i) {
                this.textVisitors[i].visit(mas);
            }
        }
        mas.moveToRandomSpot(background);
        if (this.isManageColorPerGlyph()) {
            mas.drawString(g2, this.getColorGenerator());
        } else {
            mas.drawString(g2);
        }
        if (this.textDecorators != null) {
            for (i = 0; i < this.textDecorators.length; ++i) {
                this.textDecorators[i].decorateAttributedString(g2, mas);
            }
        }
        g2.dispose();
        return out;
    }
}

