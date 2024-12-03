/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.image;

import com.octo.captcha.engine.image.ImageCaptchaEngine;
import com.octo.captcha.image.ImageCaptchaFactory;
import java.util.Arrays;

public abstract class DefaultImageCaptchaEngine
extends ImageCaptchaEngine {
    public DefaultImageCaptchaEngine(ImageCaptchaFactory[] factories) {
        this.checkNotNullOrEmpty(factories);
        this.factories = Arrays.asList(factories);
    }
}

