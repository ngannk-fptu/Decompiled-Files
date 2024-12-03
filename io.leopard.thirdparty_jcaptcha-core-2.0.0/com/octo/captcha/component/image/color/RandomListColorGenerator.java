/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.color;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.Color;
import java.security.SecureRandom;
import java.util.Random;

public class RandomListColorGenerator
implements ColorGenerator {
    private Color[] colorsList = null;
    private Random random = new SecureRandom();

    public RandomListColorGenerator(Color[] colorsList) {
        if (colorsList == null) {
            throw new CaptchaException("Color list cannot be null");
        }
        for (int i = 0; i < colorsList.length; ++i) {
            if (colorsList[i] != null) continue;
            throw new CaptchaException("One or several color is null");
        }
        this.colorsList = colorsList;
    }

    @Override
    public Color getNextColor() {
        int index = this.random.nextInt(this.colorsList.length);
        return this.colorsList[index];
    }
}

