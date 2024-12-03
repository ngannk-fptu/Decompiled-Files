/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.fontgenerator;

import com.octo.captcha.component.image.fontgenerator.FontGenerator;

public abstract class AbstractFontGenerator
implements FontGenerator {
    private int minFontSize = 10;
    private int maxFontSize = 14;

    AbstractFontGenerator(Integer minFontSize, Integer maxFontSize) {
        this.minFontSize = minFontSize != null ? minFontSize : this.minFontSize;
        this.maxFontSize = maxFontSize != null && maxFontSize >= this.minFontSize ? maxFontSize : Math.max(this.maxFontSize, this.minFontSize + 1);
    }

    @Override
    public int getMinFontSize() {
        return this.minFontSize;
    }

    @Override
    public int getMaxFontSize() {
        return this.maxFontSize;
    }
}

