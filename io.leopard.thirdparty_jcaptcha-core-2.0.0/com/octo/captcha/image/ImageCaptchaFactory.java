/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.CaptchaFactory
 */
package com.octo.captcha.image;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.image.ImageCaptcha;
import java.util.Locale;

public abstract class ImageCaptchaFactory
implements CaptchaFactory {
    public final Captcha getCaptcha() {
        return this.getImageCaptcha();
    }

    public final Captcha getCaptcha(Locale locale) {
        return this.getImageCaptcha(locale);
    }

    public abstract ImageCaptcha getImageCaptcha();

    public abstract ImageCaptcha getImageCaptcha(Locale var1);
}

