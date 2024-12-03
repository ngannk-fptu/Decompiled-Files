/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha;

import com.octo.captcha.Captcha;
import java.util.Locale;

public interface CaptchaFactory {
    public Captcha getCaptcha();

    public Captcha getCaptcha(Locale var1);
}

