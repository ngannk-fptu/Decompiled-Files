/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 */
package com.octo.captcha.text;

import com.octo.captcha.Captcha;

public abstract class TextCaptcha
implements Captcha {
    private static final long serialVersionUID = 1L;
    private Boolean hasChallengeBeenCalled = Boolean.FALSE;
    protected String question;
    protected String challenge;

    protected TextCaptcha(String question, String challenge) {
        this.challenge = challenge;
        this.question = question;
    }

    public String getQuestion() {
        return this.question;
    }

    public Object getChallenge() {
        return this.getTextChallenge();
    }

    public String getResponse() {
        return this.challenge;
    }

    public String getTextChallenge() {
        this.hasChallengeBeenCalled = Boolean.TRUE;
        return this.challenge;
    }

    public void disposeChallenge() {
        this.challenge = null;
    }

    public Boolean hasGetChalengeBeenCalled() {
        return this.hasChallengeBeenCalled;
    }
}

