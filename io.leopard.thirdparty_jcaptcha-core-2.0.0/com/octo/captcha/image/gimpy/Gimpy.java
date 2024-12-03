/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.image.gimpy;

import com.octo.captcha.image.ImageCaptcha;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Gimpy
extends ImageCaptcha
implements Serializable {
    private boolean caseSensitive = true;

    Gimpy(String question, BufferedImage challenge, String response, boolean caseSensitive) {
        super(question, response, challenge);
        this.caseSensitive = caseSensitive;
    }

    Gimpy(String question, BufferedImage challenge, String response) {
        this(question, challenge, response, true);
    }

    public final Boolean validateResponse(Object response) {
        return null != response && response instanceof String ? this.validateResponse((String)response) : Boolean.FALSE;
    }

    private final Boolean validateResponse(String response) {
        return this.caseSensitive ? response.equals(this.response) : response.equalsIgnoreCase(this.response);
    }
}

