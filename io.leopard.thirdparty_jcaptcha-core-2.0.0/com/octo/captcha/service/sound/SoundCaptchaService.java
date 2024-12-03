/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.service.CaptchaService
 *  com.octo.captcha.service.CaptchaServiceException
 */
package com.octo.captcha.service.sound;

import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;

public interface SoundCaptchaService
extends CaptchaService {
    public AudioInputStream getSoundChallengeForID(String var1) throws CaptchaServiceException;

    public AudioInputStream getSoundChallengeForID(String var1, Locale var2) throws CaptchaServiceException;
}

