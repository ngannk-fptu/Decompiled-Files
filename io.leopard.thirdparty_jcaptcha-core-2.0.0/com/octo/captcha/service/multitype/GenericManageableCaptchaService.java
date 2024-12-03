/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 */
package com.octo.captcha.service.multitype;

import com.octo.captcha.Captcha;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.service.AbstractManageableCaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.multitype.MultiTypeCaptchaService;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.text.TextCaptcha;
import java.awt.image.BufferedImage;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;

public class GenericManageableCaptchaService
extends AbstractManageableCaptchaService
implements MultiTypeCaptchaService {
    public GenericManageableCaptchaService(CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        this(new FastHashMapCaptchaStore(), captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }

    public GenericManageableCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
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
    public AudioInputStream getSoundChallengeForID(String ID) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(ID);
    }

    @Override
    public AudioInputStream getSoundChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(ID, locale);
    }

    @Override
    public String getTextChallengeForID(String ID) throws CaptchaServiceException {
        return (String)this.getChallengeForID(ID);
    }

    @Override
    public String getTextChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        return (String)this.getChallengeForID(ID, locale);
    }

    @Override
    protected Object getChallengeClone(Captcha captcha) {
        Class<?> captchaClass = captcha.getClass();
        if (ImageCaptcha.class.isAssignableFrom(captchaClass)) {
            BufferedImage challenge = (BufferedImage)captcha.getChallenge();
            BufferedImage clone = new BufferedImage(challenge.getWidth(), challenge.getHeight(), challenge.getType());
            clone.getGraphics().drawImage(challenge, 0, 0, clone.getWidth(), clone.getHeight(), null);
            clone.getGraphics().dispose();
            return clone;
        }
        if (SoundCaptcha.class.isAssignableFrom(captchaClass)) {
            AudioInputStream challenge = (AudioInputStream)captcha.getChallenge();
            AudioInputStream clone = new AudioInputStream(challenge, challenge.getFormat(), challenge.getFrameLength());
            return clone;
        }
        if (TextCaptcha.class.isAssignableFrom(captchaClass)) {
            return String.valueOf(captcha.getChallenge());
        }
        throw new CaptchaServiceException("Unknown captcha type, can't clone challenge captchaClass:'" + captcha.getClass() + "'");
    }
}

