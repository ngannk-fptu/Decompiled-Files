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

public class SingleColorGenerator
implements ColorGenerator {
    public Color color = null;

    public SingleColorGenerator(Color color) {
        if (color == null) {
            throw new CaptchaException("Color is null");
        }
        this.color = color;
    }

    @Override
    public Color getNextColor() {
        return this.color;
    }
}

