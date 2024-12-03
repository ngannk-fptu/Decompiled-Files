/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaService
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.octo.captcha.service;

import com.octo.captcha.Captcha;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCaptchaService
implements CaptchaService {
    protected CaptchaStore store;
    protected CaptchaEngine engine;
    protected Logger logger;

    protected AbstractCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine) {
        if (captchaEngine == null || captchaStore == null) {
            throw new IllegalArgumentException("Store or gimpy can't be null");
        }
        this.engine = captchaEngine;
        this.store = captchaStore;
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.logger.info("Init " + this.store.getClass().getName());
        this.store.initAndStart();
    }

    public Object getChallengeForID(String ID) throws CaptchaServiceException {
        return this.getChallengeForID(ID, Locale.getDefault());
    }

    public Object getChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        Captcha captcha;
        if (!this.store.hasCaptcha(ID)) {
            captcha = this.generateAndStoreCaptcha(locale, ID);
        } else {
            captcha = this.store.getCaptcha(ID);
            if (captcha == null) {
                captcha = this.generateAndStoreCaptcha(locale, ID);
            } else if (captcha.hasGetChalengeBeenCalled().booleanValue()) {
                captcha = this.generateAndStoreCaptcha(locale, ID);
            }
        }
        Object challenge = this.getChallengeClone(captcha);
        captcha.disposeChallenge();
        return challenge;
    }

    public String getQuestionForID(String ID, Locale locale) throws CaptchaServiceException {
        Captcha captcha;
        if (!this.store.hasCaptcha(ID)) {
            captcha = this.generateAndStoreCaptcha(locale, ID);
        } else {
            Locale storedlocale;
            captcha = this.store.getCaptcha(ID);
            if (captcha == null) {
                captcha = this.generateAndStoreCaptcha(locale, ID);
            } else if (locale != null && !locale.equals(storedlocale = this.store.getLocale(ID))) {
                captcha = this.generateAndStoreCaptcha(locale, ID);
            }
        }
        return captcha.getQuestion();
    }

    public String getQuestionForID(String ID) throws CaptchaServiceException {
        return this.getQuestionForID(ID, Locale.getDefault());
    }

    public Boolean validateResponseForID(String ID, Object response) throws CaptchaServiceException {
        if (!this.store.hasCaptcha(ID)) {
            throw new CaptchaServiceException("Invalid ID, could not validate unexisting or already validated captcha");
        }
        Boolean valid = this.store.getCaptcha(ID).validateResponse(response);
        this.store.removeCaptcha(ID);
        return valid;
    }

    protected Captcha generateAndStoreCaptcha(Locale locale, String ID) {
        Captcha captcha = this.engine.getNextCaptcha(locale);
        this.store.storeCaptcha(ID, captcha, locale);
        return captcha;
    }

    protected abstract Object getChallengeClone(Captcha var1);
}

