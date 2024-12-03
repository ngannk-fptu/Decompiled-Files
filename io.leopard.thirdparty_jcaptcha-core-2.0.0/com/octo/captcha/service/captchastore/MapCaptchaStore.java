/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 */
package com.octo.captcha.service.captchastore;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaAndLocale;
import com.octo.captcha.service.captchastore.CaptchaStore;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MapCaptchaStore
implements CaptchaStore {
    Map store = new HashMap();

    public boolean hasCaptcha(String id) {
        return this.store.containsKey(id);
    }

    public void storeCaptcha(String id, Captcha captcha) throws CaptchaServiceException {
        this.store.put(id, new CaptchaAndLocale(captcha));
    }

    public void storeCaptcha(String id, Captcha captcha, Locale locale) throws CaptchaServiceException {
        this.store.put(id, new CaptchaAndLocale(captcha, locale));
    }

    public Captcha getCaptcha(String id) throws CaptchaServiceException {
        Object captchaAndLocale = this.store.get(id);
        return captchaAndLocale != null ? ((CaptchaAndLocale)captchaAndLocale).getCaptcha() : null;
    }

    public Locale getLocale(String id) throws CaptchaServiceException {
        Object captchaAndLocale = this.store.get(id);
        return captchaAndLocale != null ? ((CaptchaAndLocale)captchaAndLocale).getLocale() : null;
    }

    public boolean removeCaptcha(String id) {
        if (this.store.get(id) != null) {
            this.store.remove(id);
            return true;
        }
        return false;
    }

    public int getSize() {
        return this.store.size();
    }

    public Collection getKeys() {
        return this.store.keySet();
    }

    public void empty() {
        this.store = new HashMap();
    }

    public void initAndStart() {
    }

    public void cleanAndShutdown() {
        this.store.clear();
    }
}

