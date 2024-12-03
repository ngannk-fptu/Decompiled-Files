/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaAndLocale
 *  com.octo.captcha.service.captchastore.CaptchaStore
 */
package com.atlassian.confluence.impl.security.login;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaAndLocale;
import com.octo.captcha.service.captchastore.CaptchaStore;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class CacheBackedCaptchaStore
implements CaptchaStore {
    private final Cache<String, CaptchaAndLocale> cache;

    public CacheBackedCaptchaStore(CacheFactory cacheFactory) {
        this.cache = CoreCache.CAPTCHA_BY_ID.getCache(cacheFactory);
    }

    public boolean hasCaptcha(String id) {
        return this.cache.get((Object)id) != null;
    }

    public void storeCaptcha(String id, Captcha captcha) throws CaptchaServiceException {
        this.storeCaptcha(id, captcha, Locale.getDefault());
    }

    public void storeCaptcha(String id, Captcha captcha, Locale locale) throws CaptchaServiceException {
        boolean added;
        boolean bl = added = this.cache.putIfAbsent((Object)id, (Object)new CaptchaAndLocale(captcha, locale)) == null;
        if (!added) {
            throw new CaptchaServiceException("Captcha already exists for id: " + id);
        }
    }

    public Captcha getCaptcha(String id) throws CaptchaServiceException {
        return Optional.ofNullable((CaptchaAndLocale)this.cache.get((Object)id)).map(CaptchaAndLocale::getCaptcha).orElse(null);
    }

    public Locale getLocale(String id) throws CaptchaServiceException {
        return Optional.ofNullable((CaptchaAndLocale)this.cache.get((Object)id)).map(CaptchaAndLocale::getLocale).orElse(null);
    }

    public boolean removeCaptcha(String id) {
        if (this.hasCaptcha(id)) {
            this.cache.remove((Object)id);
            return true;
        }
        return false;
    }

    public int getSize() {
        throw new UnsupportedOperationException("CaptchaStore#getSize is not implemented");
    }

    public Collection getKeys() {
        throw new UnsupportedOperationException("CaptchaStore#getKeys is not implemented");
    }

    public void empty() {
        this.cache.removeAll();
    }

    public void initAndStart() {
    }

    public void cleanAndShutdown() {
    }
}

