/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.CaptchaFactory
 */
package com.octo.captcha.sound;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.sound.SoundCaptcha;
import java.util.Locale;

public abstract class SoundCaptchaFactory
implements CaptchaFactory {
    public Captcha getCaptcha() {
        return this.getSoundCaptcha();
    }

    public Captcha getCaptcha(Locale locale) {
        return this.getSoundCaptcha(locale);
    }

    public abstract SoundCaptcha getSoundCaptcha();

    public abstract SoundCaptcha getSoundCaptcha(Locale var1);
}

