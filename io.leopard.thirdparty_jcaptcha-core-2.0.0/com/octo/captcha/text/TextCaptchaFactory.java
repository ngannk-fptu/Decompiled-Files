/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.CaptchaFactory
 */
package com.octo.captcha.text;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.text.TextCaptcha;
import java.util.Locale;

public abstract class TextCaptchaFactory
implements CaptchaFactory {
    public final Captcha getCaptcha() {
        return this.getTextCaptcha();
    }

    public final Captcha getCaptcha(Locale locale) {
        return this.getTextCaptcha(locale);
    }

    public abstract TextCaptcha getTextCaptcha();

    public abstract TextCaptcha getTextCaptcha(Locale var1);
}

