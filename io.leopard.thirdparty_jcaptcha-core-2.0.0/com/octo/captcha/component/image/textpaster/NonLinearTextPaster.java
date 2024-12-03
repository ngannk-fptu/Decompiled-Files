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

public class NonLinearTextPaster
extends AbstractTextPaster {
    public NonLinearTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, Color textColor) {
        super(minAcceptedWordLength, maxAcceptedWordLength, textColor);
    }

    public NonLinearTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator);
    }

    public NonLinearTextPaster(Integer minAcceptedWordLength, Integer maxAcceptedWordLength, ColorGenerator colorGenerator, Boolean manageColorPerGlyph) {
        super(minAcceptedWordLength, maxAcceptedWordLength, colorGenerator, manageColorPerGlyph);
    }

    @Override
    public BufferedImage pasteText(BufferedImage background, AttributedString attributedWord) throws CaptchaException {
        BufferedImage out = this.copyBackground(background);
        Graphics2D g2 = this.pasteBackgroundAndSetTextColor(out, background);
        MutableAttributedString newAttrString = new MutableAttributedString(g2, attributedWord, 2);
        newAttrString.useMinimumSpacing(6.0);
        newAttrString.shiftBoundariesToNonLinearLayout(background.getWidth(), background.getHeight());
        if (this.isManageColorPerGlyph()) {
            newAttrString.drawString(g2, this.getColorGenerator());
        } else {
            newAttrString.drawString(g2);
        }
        g2.dispose();
        return out;
    }
}

