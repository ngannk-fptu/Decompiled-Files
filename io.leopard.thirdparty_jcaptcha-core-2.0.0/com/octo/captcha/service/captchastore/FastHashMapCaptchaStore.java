/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.FastHashMap
 */
package com.octo.captcha.service.captchastore;

import com.octo.captcha.service.captchastore.MapCaptchaStore;
import org.apache.commons.collections.FastHashMap;

public class FastHashMapCaptchaStore
extends MapCaptchaStore {
    public FastHashMapCaptchaStore() {
        this.store = new FastHashMap();
    }
}

