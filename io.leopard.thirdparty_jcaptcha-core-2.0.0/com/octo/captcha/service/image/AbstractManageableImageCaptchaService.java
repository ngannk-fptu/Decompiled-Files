/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 */
package com.octo.captcha.service.image;

import com.octo.captcha.Captcha;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.AbstractManageableCaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.image.ImageCaptchaService;
import java.awt.image.BufferedImage;
import java.util.Locale;

public abstract class AbstractManageableImageCaptchaService
extends AbstractManageableCaptchaService
implements ImageCaptchaService {
    protected AbstractManageableImageCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }

    @Override
    public BufferedImage getImageChallengeForID(String ID) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(ID);
    }

    @Override
    public BufferedImage getImageChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(ID, locale);
    }

    @Override
    protected Object getChallengeClone(Captcha captcha) {
        BufferedImage challenge = (BufferedImage)captcha.getChallenge();
        BufferedImage clone = new BufferedImage(challenge.getWidth(), challenge.getHeight(), challenge.getType());
        clone.getGraphics().drawImage(challenge, 0, 0, clone.getWidth(), clone.getHeight(), null);
        clone.getGraphics().dispose();
        return clone;
    }
}

