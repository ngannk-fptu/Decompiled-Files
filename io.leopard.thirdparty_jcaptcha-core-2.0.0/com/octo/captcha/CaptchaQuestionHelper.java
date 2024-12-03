/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha;

import java.util.Locale;
import java.util.ResourceBundle;

public final class CaptchaQuestionHelper {
    public static final String BUNDLE_NAME = CaptchaQuestionHelper.class.getName();

    private CaptchaQuestionHelper() {
    }

    public static String getQuestion(Locale locale, String key) {
        return ResourceBundle.getBundle(BUNDLE_NAME, locale).getString(key);
    }
}

