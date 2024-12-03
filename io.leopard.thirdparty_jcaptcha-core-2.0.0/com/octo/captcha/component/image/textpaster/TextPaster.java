/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public interface TextPaster {
    public int getMaxAcceptedWordLength();

    public int getMinAcceptedWordLength();

    public BufferedImage pasteText(BufferedImage var1, AttributedString var2) throws CaptchaException;
}

