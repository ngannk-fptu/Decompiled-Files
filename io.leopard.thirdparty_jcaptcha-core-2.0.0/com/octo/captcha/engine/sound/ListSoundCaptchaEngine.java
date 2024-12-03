/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.engine.sound;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.engine.sound.SoundCaptchaEngine;
import com.octo.captcha.sound.SoundCaptchaFactory;
import java.util.Arrays;

public abstract class ListSoundCaptchaEngine
extends SoundCaptchaEngine {
    public ListSoundCaptchaEngine() {
        this.buildInitialFactories();
        this.checkFactoriesSize();
    }

    protected abstract void buildInitialFactories();

    public boolean addFactory(SoundCaptchaFactory factory) {
        return factory != null && this.factories.add(factory);
    }

    public void addFactories(SoundCaptchaFactory[] factories) {
        this.checkNotNullOrEmpty(factories);
        this.factories.addAll(Arrays.asList(factories));
    }

    private void checkFactoriesSize() {
        if (this.factories.size() == 0) {
            throw new CaptchaException("This soundEngine has no factories. Please initialize it properly with the buildInitialFactory() called by the constructor or the addFactory() mehtod later!");
        }
    }
}

