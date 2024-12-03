/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.engine.CaptchaEngineException;
import java.util.Locale;

public interface CaptchaEngine {
    public Captcha getNextCaptcha();

    public Captcha getNextCaptcha(Locale var1);

    public CaptchaFactory[] getFactories();

    public void setFactories(CaptchaFactory[] var1) throws CaptchaEngineException;
}

