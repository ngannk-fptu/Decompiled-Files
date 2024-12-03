/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.service.CaptchaService
 *  com.octo.captcha.service.CaptchaServiceException
 */
package com.octo.captcha.service.text;

import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import java.util.Locale;

public interface TextCaptchaService
extends CaptchaService {
    public String getTextChallengeForID(String var1) throws CaptchaServiceException;

    public String getTextChallengeForID(String var1, Locale var2) throws CaptchaServiceException;
}

