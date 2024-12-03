/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import java.awt.image.BufferedImage;

public interface WordToImage {
    public int getMaxAcceptedWordLength();

    public int getMinAcceptedWordLength();

    public int getImageHeight();

    public int getImageWidth();

    public int getMinFontSize();

    public BufferedImage getImage(String var1) throws CaptchaException;
}

