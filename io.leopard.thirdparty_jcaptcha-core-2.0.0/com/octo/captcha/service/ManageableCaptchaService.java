/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaService
 */
package com.octo.captcha.service;

import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.CaptchaService;

public interface ManageableCaptchaService
extends CaptchaService {
    public String getCaptchaEngineClass();

    public void setCaptchaEngineClass(String var1) throws IllegalArgumentException;

    public CaptchaEngine getEngine();

    public void setCaptchaEngine(CaptchaEngine var1);

    public int getMinGuarantedStorageDelayInSeconds();

    public void setMinGuarantedStorageDelayInSeconds(int var1);

    public long getNumberOfGeneratedCaptchas();

    public long getNumberOfCorrectResponses();

    public long getNumberOfUncorrectResponses();

    public int getCaptchaStoreSize();

    public int getNumberOfGarbageCollectableCaptchas();

    public long getNumberOfGarbageCollectedCaptcha();

    public void setCaptchaStoreMaxSize(int var1);

    public int getCaptchaStoreMaxSize();

    public void garbageCollectCaptchaStore();

    public void emptyCaptchaStore();

    public int getCaptchaStoreSizeBeforeGarbageCollection();

    public void setCaptchaStoreSizeBeforeGarbageCollection(int var1);
}

