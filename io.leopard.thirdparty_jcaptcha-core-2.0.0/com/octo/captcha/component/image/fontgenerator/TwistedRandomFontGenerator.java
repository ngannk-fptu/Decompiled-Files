/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.fontgenerator;

import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import java.awt.Font;
import java.awt.geom.AffineTransform;

public class TwistedRandomFontGenerator
extends RandomFontGenerator {
    public TwistedRandomFontGenerator(Integer minFontSize, Integer maxFontSize) {
        super(minFontSize, maxFontSize);
    }

    @Override
    protected Font applyCustomDeformationOnGeneratedFont(Font font) {
        AffineTransform at = new AffineTransform();
        float angle = this.myRandom.nextFloat() / 3.0f;
        at.rotate(this.myRandom.nextBoolean() ? (double)angle : (double)(-angle));
        return font.deriveFont(at);
    }
}

