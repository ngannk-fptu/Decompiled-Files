/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha;

import java.io.Serializable;

public interface Captcha
extends Serializable {
    public String getResponse();

    public String getQuestion();

    public Object getChallenge();

    public Boolean validateResponse(Object var1);

    public void disposeChallenge();

    public Boolean hasGetChalengeBeenCalled();
}

