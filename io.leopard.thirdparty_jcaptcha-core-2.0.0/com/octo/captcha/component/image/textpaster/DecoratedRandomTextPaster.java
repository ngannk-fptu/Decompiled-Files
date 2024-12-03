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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public class DecoratedRandomTextPaster
extends AbstractTextPaster {
    protected final int kerning = 20;
    private TextDecorator[] decorators;

    public DecoratedRandomTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, TextDecorator[] decorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
        this.decorators = decorators;
    }

    public DecoratedRandomTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph, TextDecorator[] decorators) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
        this.decorators = decorators;
    }

    @Override
    public BufferedImage pasteText(BufferedImage background, AttributedString attributedWord) throws CaptchaException {
        BufferedImage out = this.copyBackground(background);
        Graphics2D g2 = this.pasteBackgroundAndSetTextColor(out, background);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        MutableAttributedString mas = new MutableAttributedString(g2, attributedWord, 20);
        mas.useMinimumSpacing(20.0);
        mas.moveToRandomSpot(background);
        if (this.isManageColorPerGlyph()) {
            mas.drawString(g2, this.getColorGenerator());
        } else {
            mas.drawString(g2);
        }
        if (this.decorators != null) {
            for (int i = 0; i < this.decorators.length; ++i) {
                this.decorators[i].decorateAttributedString(g2, mas);
            }
        }
        g2.dispose();
        return out;
    }
}

