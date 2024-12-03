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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public class SimpleTextPaster
extends AbstractTextPaster {
    public SimpleTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, Color textColor) {
        super(minAcceptedWordLength, maxAcceptedWordLength, textColor);
    }

    public SimpleTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
    }

    public SimpleTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
    }

    @Override
    public BufferedImage pasteText(BufferedImage background, AttributedString attributedWord) throws CaptchaException {
        int x = background.getWidth() / 20;
        int y = background.getHeight() / 2;
        BufferedImage out = this.copyBackground(background);
        Graphics2D g2 = this.pasteBackgroundAndSetTextColor(out, background);
        MutableAttributedString newAttrString = new MutableAttributedString(g2, attributedWord, 2);
        newAttrString.useMinimumSpacing(1.0);
        newAttrString.moveTo(x, y);
        if (this.isManageColorPerGlyph()) {
            newAttrString.drawString(g2, this.getColorGenerator());
        } else {
            newAttrString.drawString(g2);
        }
        g2.dispose();
        return out;
    }
}

