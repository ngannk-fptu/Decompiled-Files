/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.CaptchaFactory
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.engine.CaptchaEngineException
 */
package com.octo.captcha.engine.sound;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.engine.CaptchaEngineException;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.sound.SoundCaptchaFactory;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public abstract class SoundCaptchaEngine
implements CaptchaEngine {
    protected List factories = new ArrayList();
    protected Random myRandom = new SecureRandom();

    public final Captcha getNextCaptcha() {
        return this.getNextSoundCaptcha();
    }

    public final Captcha getNextCaptcha(Locale locale) {
        return this.getNextSoundCaptcha(locale);
    }

    public CaptchaFactory[] getFactories() {
        return this.factories.toArray(new CaptchaFactory[this.factories.size()]);
    }

    public void setFactories(CaptchaFactory[] factories) throws CaptchaEngineException {
        this.checkNotNullOrEmpty(factories);
        ArrayList<CaptchaFactory> tempFactories = new ArrayList<CaptchaFactory>();
        for (int i = 0; i < factories.length; ++i) {
            if (!SoundCaptchaFactory.class.isAssignableFrom(factories[i].getClass())) {
                throw new CaptchaEngineException("This factory is not an sound captcha factory " + factories[i].getClass());
            }
            tempFactories.add(factories[i]);
        }
        this.factories = tempFactories;
    }

    protected void checkNotNullOrEmpty(CaptchaFactory[] factories) {
        if (factories == null || factories.length == 0) {
            throw new CaptchaEngineException("impossible to set null or empty factories");
        }
    }

    public SoundCaptchaFactory getSoundCaptchaFactory() {
        return (SoundCaptchaFactory)this.factories.get(this.myRandom.nextInt(this.factories.size()));
    }

    public SoundCaptcha getNextSoundCaptcha() {
        return this.getSoundCaptchaFactory().getSoundCaptcha();
    }

    public SoundCaptcha getNextSoundCaptcha(Locale locale) {
        return this.getSoundCaptchaFactory().getSoundCaptcha(locale);
    }
}

