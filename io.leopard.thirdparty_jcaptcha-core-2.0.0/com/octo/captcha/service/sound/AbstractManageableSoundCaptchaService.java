/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 */
package com.octo.captcha.service.sound;

import com.octo.captcha.Captcha;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.AbstractManageableCaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.sound.SoundCaptchaService;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;

public abstract class AbstractManageableSoundCaptchaService
extends AbstractManageableCaptchaService
implements SoundCaptchaService {
    protected AbstractManageableSoundCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }

    @Override
    public AudioInputStream getSoundChallengeForID(String ID) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(ID);
    }

    @Override
    public AudioInputStream getSoundChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(ID, locale);
    }

    @Override
    protected Object getChallengeClone(Captcha captcha) {
        AudioInputStream challenge = (AudioInputStream)captcha.getChallenge();
        AudioInputStream clone = new AudioInputStream(challenge, challenge.getFormat(), challenge.getFrameLength());
        return clone;
    }
}

