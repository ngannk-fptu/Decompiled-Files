/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.AbstractCaptchaService
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 *  com.octo.captcha.service.image.ImageCaptchaService
 */
package com.atlassian.confluence.security;

import com.octo.captcha.Captcha;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.AbstractCaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.image.ImageCaptchaService;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Locale;

public class ConfluenceImageCaptchaService
extends AbstractCaptchaService
implements ImageCaptchaService {
    public ConfluenceImageCaptchaService(CaptchaStore store, CaptchaEngine engine) {
        super(store, engine);
    }

    public BufferedImage getImageChallengeForID(String ID) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(ID);
    }

    public BufferedImage getImageChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(ID, locale);
    }

    protected Object getChallengeClone(Captcha captcha) {
        BufferedImage ri = (BufferedImage)captcha.getChallenge();
        ColorModel cm = ri.getColorModel();
        return new BufferedImage(cm, ri.copyData(null), cm.isAlphaPremultiplied(), null);
    }

    public void setCaptchaEngine(CaptchaEngine captchaEngine) {
        this.engine = captchaEngine;
    }
}

