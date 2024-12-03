/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.fontgenerator;

import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import java.awt.Font;
import java.awt.geom.AffineTransform;

public class DeformedRandomFontGenerator
extends RandomFontGenerator {
    public DeformedRandomFontGenerator(Integer minFontSize, Integer maxFontSize) {
        super(minFontSize, maxFontSize);
    }

    @Override
    protected Font applyCustomDeformationOnGeneratedFont(Font font) {
        float theta = (float)(this.myRandom.nextBoolean() ? 1 : -1) * this.myRandom.nextFloat() / 3.0f;
        AffineTransform at = new AffineTransform();
        at.rotate(theta, this.myRandom.nextDouble(), this.myRandom.nextDouble());
        return font.deriveFont(at);
    }
}

