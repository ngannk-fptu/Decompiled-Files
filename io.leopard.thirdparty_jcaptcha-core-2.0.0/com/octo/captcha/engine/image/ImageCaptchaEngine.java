/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.CaptchaFactory
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.engine.CaptchaEngineException
 */
package com.octo.captcha.engine.image;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.engine.CaptchaEngineException;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public abstract class ImageCaptchaEngine
implements CaptchaEngine {
    protected List factories = new ArrayList();
    protected Random myRandom = new SecureRandom();

    public ImageCaptchaFactory getImageCaptchaFactory() {
        return (ImageCaptchaFactory)this.factories.get(this.myRandom.nextInt(this.factories.size()));
    }

    public final ImageCaptcha getNextImageCaptcha() {
        return this.getImageCaptchaFactory().getImageCaptcha();
    }

    public ImageCaptcha getNextImageCaptcha(Locale locale) {
        return this.getImageCaptchaFactory().getImageCaptcha(locale);
    }

    public final Captcha getNextCaptcha() {
        return this.getImageCaptchaFactory().getImageCaptcha();
    }

    public Captcha getNextCaptcha(Locale locale) {
        return this.getImageCaptchaFactory().getImageCaptcha(locale);
    }

    public CaptchaFactory[] getFactories() {
        return this.factories.toArray(new CaptchaFactory[this.factories.size()]);
    }

    public void setFactories(CaptchaFactory[] factories) throws CaptchaEngineException {
        this.checkNotNullOrEmpty(factories);
        ArrayList<CaptchaFactory> tempFactories = new ArrayList<CaptchaFactory>();
        for (int i = 0; i < factories.length; ++i) {
            if (!ImageCaptchaFactory.class.isAssignableFrom(factories[i].getClass())) {
                throw new CaptchaEngineException("This factory is not an image captcha factory " + factories[i].getClass());
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
}

