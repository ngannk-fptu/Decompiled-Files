/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.wordtoimage.AbstractWordToImage;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public class SimpleWordToImage
extends AbstractWordToImage {
    @Override
    public int getMaxAcceptedWordLength() {
        return 10;
    }

    @Override
    public int getMinAcceptedWordLength() {
        return 1;
    }

    public int getMaxAcceptedWordLenght() {
        return 10;
    }

    public int getMinAcceptedWordLenght() {
        return 1;
    }

    @Override
    public int getImageHeight() {
        return 50;
    }

    @Override
    public int getImageWidth() {
        return 100;
    }

    @Override
    public int getMinFontSize() {
        return 10;
    }

    @Override
    public Font getFont() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()[0];
    }

    @Override
    public BufferedImage getBackground() {
        BufferedImage background = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        return background;
    }

    @Override
    BufferedImage pasteText(BufferedImage background, AttributedString attributedWord) throws CaptchaException {
        Graphics graph = background.getGraphics();
        int x = (this.getImageWidth() - this.getMaxAcceptedWordLength()) / 2;
        int y = (this.getImageHeight() - this.getMinFontSize()) / 2;
        graph.drawString(attributedWord.getIterator(), x, y);
        graph.dispose();
        return background;
    }
}

