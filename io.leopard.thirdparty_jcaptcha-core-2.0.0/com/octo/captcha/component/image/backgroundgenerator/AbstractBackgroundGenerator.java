/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.backgroundgenerator;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import java.security.SecureRandom;
import java.util.Random;

public abstract class AbstractBackgroundGenerator
implements BackgroundGenerator {
    private int height = 100;
    private int width = 200;
    Random myRandom = new SecureRandom();

    AbstractBackgroundGenerator(Integer width, Integer height) {
        this.width = width != null ? width : this.width;
        this.height = height != null ? height : this.height;
    }

    @Override
    public int getImageHeight() {
        return this.height;
    }

    @Override
    public int getImageWidth() {
        return this.width;
    }
}

