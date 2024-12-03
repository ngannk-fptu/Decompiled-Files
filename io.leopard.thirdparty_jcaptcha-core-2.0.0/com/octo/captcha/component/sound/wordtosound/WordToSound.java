/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.sound.wordtosound;

import com.octo.captcha.CaptchaException;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;

public interface WordToSound {
    public int getMaxAcceptedWordLenght();

    public int getMinAcceptedWordLenght();

    public int getMaxAcceptedWordLength();

    public int getMinAcceptedWordLength();

    public AudioInputStream getSound(String var1) throws CaptchaException;

    public AudioInputStream getSound(String var1, Locale var2) throws CaptchaException;
}

