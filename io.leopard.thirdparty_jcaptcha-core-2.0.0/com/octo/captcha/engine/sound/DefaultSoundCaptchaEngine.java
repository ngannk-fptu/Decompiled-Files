/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.sound;

import com.octo.captcha.engine.sound.SoundCaptchaEngine;
import com.octo.captcha.sound.SoundCaptchaFactory;
import java.util.Arrays;

public class DefaultSoundCaptchaEngine
extends SoundCaptchaEngine {
    public DefaultSoundCaptchaEngine(SoundCaptchaFactory[] factories) {
        this.checkNotNullOrEmpty(factories);
        this.factories = Arrays.asList(factories);
    }
}

