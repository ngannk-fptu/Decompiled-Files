/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.captchastore.CaptchaStore
 */
package com.octo.captcha.service.image;

import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.engine.image.gimpy.DefaultGimpyEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.AbstractManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public class DefaultManageableImageCaptchaService
extends AbstractManageableImageCaptchaService
implements ImageCaptchaService {
    public DefaultManageableImageCaptchaService() {
        super(new FastHashMapCaptchaStore(), new DefaultGimpyEngine(), 180, 100000, 75000);
    }

    public DefaultManageableImageCaptchaService(int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        super(new FastHashMapCaptchaStore(), new DefaultGimpyEngine(), minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }

    public DefaultManageableImageCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }
}

