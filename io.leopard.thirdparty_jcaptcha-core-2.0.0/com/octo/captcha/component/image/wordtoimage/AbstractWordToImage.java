/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public abstract class AbstractWordToImage
implements WordToImage {
    private boolean manageFontByCharacter = true;

    protected AbstractWordToImage() {
    }

    protected AbstractWordToImage(boolean manageFontByCharacter) {
        this.manageFontByCharacter = manageFontByCharacter;
    }

    @Override
    public BufferedImage getImage(String word) throws CaptchaException {
        int wordLength = this.checkWordLength(word);
        AttributedString attributedWord = this.getAttributedString(word, wordLength);
        BufferedImage background = this.getBackground();
        return this.pasteText(background, attributedWord);
    }

    AttributedString getAttributedString(String word, int wordLength) {
        AttributedString attributedWord = new AttributedString(word);
        Font font = this.getFont();
        for (int i = 0; i < wordLength; ++i) {
            attributedWord.addAttribute(TextAttribute.FONT, font, i, i + 1);
            if (!this.manageFontByCharacter) continue;
            font = this.getFont();
        }
        return attributedWord;
    }

    int checkWordLength(String word) throws CaptchaException {
        if (word == null) {
            throw new CaptchaException("null word");
        }
        int wordLength = word.length();
        if (wordLength > this.getMaxAcceptedWordLength() || wordLength < this.getMinAcceptedWordLength()) {
            throw new CaptchaException("invalid length word");
        }
        return wordLength;
    }

    abstract Font getFont();

    abstract BufferedImage getBackground();

    abstract BufferedImage pasteText(BufferedImage var1, AttributedString var2) throws CaptchaException;
}

