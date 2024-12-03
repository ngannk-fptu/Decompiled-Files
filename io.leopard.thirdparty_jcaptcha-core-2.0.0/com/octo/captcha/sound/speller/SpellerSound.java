/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.sound.speller;

import com.octo.captcha.sound.SoundCaptcha;
import javax.sound.sampled.AudioInputStream;

public class SpellerSound
extends SoundCaptcha {
    private static final long serialVersionUID = 1L;

    public SpellerSound(String thequestion, AudioInputStream thechallenge, String theresponse) {
        super(thequestion, theresponse, thechallenge);
    }

    public Boolean validateResponse(Object theresponse) {
        if (theresponse != null && theresponse instanceof String) {
            return this.validateResponse((String)theresponse);
        }
        return Boolean.FALSE;
    }

    public Boolean validateResponse(String theresponse) {
        return this.response.equalsIgnoreCase(theresponse);
    }
}

