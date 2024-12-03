/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.fontgenerator;

import com.octo.captcha.component.image.fontgenerator.TwistedRandomFontGenerator;
import java.awt.Font;
import java.awt.geom.AffineTransform;

public class TwistedAndShearedRandomFontGenerator
extends TwistedRandomFontGenerator {
    public TwistedAndShearedRandomFontGenerator(Integer minFontSize, Integer maxFontSize) {
        super(minFontSize, maxFontSize);
    }

    @Override
    protected Font applyCustomDeformationOnGeneratedFont(Font font) {
        font = super.applyCustomDeformationOnGeneratedFont(font);
        double rx = this.myRandom.nextDouble() / 3.0;
        double ry = this.myRandom.nextDouble() / 3.0;
        AffineTransform at = AffineTransform.getShearInstance(rx, ry);
        return font.deriveFont(at);
    }
}

