/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.service;

import com.octo.captcha.service.CaptchaServiceException;
import java.util.Locale;

public interface CaptchaService {
    public Object getChallengeForID(String var1) throws CaptchaServiceException;

    public Object getChallengeForID(String var1, Locale var2) throws CaptchaServiceException;

    public String getQuestionForID(String var1) throws CaptchaServiceException;

    public String getQuestionForID(String var1, Locale var2) throws CaptchaServiceException;

    public Boolean validateResponseForID(String var1, Object var2) throws CaptchaServiceException;
}

