/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaService
 *  com.octo.captcha.service.CaptchaServiceException
 */
package com.octo.captcha.service.image;

import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import java.awt.image.BufferedImage;
import java.util.Locale;

public interface ImageCaptchaService
extends CaptchaService {
    public BufferedImage getImageChallengeForID(String var1) throws CaptchaServiceException;

    public BufferedImage getImageChallengeForID(String var1, Locale var2) throws CaptchaServiceException;

    public void setCaptchaEngine(CaptchaEngine var1);
}

