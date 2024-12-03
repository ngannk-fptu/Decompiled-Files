/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.text.math;

import com.octo.captcha.text.TextCaptcha;

public class MathCaptcha
extends TextCaptcha {
    private String response;

    MathCaptcha(String question, String challenge, String response) {
        super(question, challenge);
        this.response = response;
    }

    public final Boolean validateResponse(Object response) {
        return null != response && response instanceof String ? this.validateResponse((String)response) : Boolean.FALSE;
    }

    private final Boolean validateResponse(String response) {
        return response.equals(this.response);
    }
}

