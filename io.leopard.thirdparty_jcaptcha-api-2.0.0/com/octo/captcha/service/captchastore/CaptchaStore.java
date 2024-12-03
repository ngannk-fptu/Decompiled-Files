/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.service.captchastore;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import java.util.Collection;
import java.util.Locale;

public interface CaptchaStore {
    public boolean hasCaptcha(String var1);

    public void storeCaptcha(String var1, Captcha var2) throws CaptchaServiceException;

    public void storeCaptcha(String var1, Captcha var2, Locale var3) throws CaptchaServiceException;

    public boolean removeCaptcha(String var1);

    public Captcha getCaptcha(String var1) throws CaptchaServiceException;

    public Locale getLocale(String var1) throws CaptchaServiceException;

    public int getSize();

    public Collection getKeys();

    public void empty();

    public void initAndStart();

    public void cleanAndShutdown();
}

