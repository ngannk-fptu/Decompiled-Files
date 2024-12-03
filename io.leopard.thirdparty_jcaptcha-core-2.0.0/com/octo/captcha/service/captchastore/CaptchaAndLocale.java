/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 */
package com.octo.captcha.service.captchastore;

import com.octo.captcha.Captcha;
import java.io.Serializable;
import java.util.Locale;

public class CaptchaAndLocale
implements Serializable {
    private Captcha captcha;
    private Locale locale;

    public CaptchaAndLocale(Captcha captcha) {
        this.captcha = captcha;
    }

    public CaptchaAndLocale(Captcha captcha, Locale locale) {
        this.captcha = captcha;
        this.locale = locale;
    }

    public Captcha getCaptcha() {
        return this.captcha;
    }

    public void setCaptcha(Captcha captcha) {
        this.captcha = captcha;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}

