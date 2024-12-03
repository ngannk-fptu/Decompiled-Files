/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.text.math;

import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.text.TextCaptcha;
import com.octo.captcha.text.TextCaptchaFactory;
import com.octo.captcha.text.math.MathCaptcha;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class MathCaptchaFactory
extends TextCaptchaFactory {
    private static final String BUNDLE_QUESTION_KEY = MathCaptcha.class.getName();
    Random myRamdom = new SecureRandom();

    @Override
    public TextCaptcha getTextCaptcha() {
        return this.getTextCaptcha(Locale.getDefault());
    }

    @Override
    public TextCaptcha getTextCaptcha(Locale locale) {
        int one = this.myRamdom.nextInt(50);
        int two = this.myRamdom.nextInt(50);
        MathCaptcha captcha = new MathCaptcha(this.getQuestion(locale), one + "+" + two, String.valueOf(one + two));
        return captcha;
    }

    protected String getQuestion(Locale locale) {
        return CaptchaQuestionHelper.getQuestion(locale, BUNDLE_QUESTION_KEY);
    }
}

