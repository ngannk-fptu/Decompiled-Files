/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.CaptchaException
 *  com.octo.captcha.CaptchaFactory
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.engine.CaptchaEngineException
 */
package com.octo.captcha.engine;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.engine.CaptchaEngineException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class GenericCaptchaEngine
implements CaptchaEngine {
    private CaptchaFactory[] factories;
    private Random myRandom = new SecureRandom();

    public GenericCaptchaEngine(CaptchaFactory[] factories) {
        this.factories = factories;
        if (this.factories == null || this.factories.length == 0) {
            throw new CaptchaException("GenericCaptchaEngine cannot be constructed with a null or empty factories array");
        }
    }

    public CaptchaFactory[] getFactories() {
        return this.factories;
    }

    public void setFactories(CaptchaFactory[] factories) throws CaptchaEngineException {
        if (factories == null || factories.length == 0) {
            throw new CaptchaEngineException("impossible to set null or empty factories");
        }
        this.factories = factories;
    }

    public Captcha getNextCaptcha() {
        return this.factories[this.myRandom.nextInt(this.factories.length)].getCaptcha();
    }

    public Captcha getNextCaptcha(Locale locale) {
        return this.factories[this.myRandom.nextInt(this.factories.length)].getCaptcha(locale);
    }
}

