/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.backgroundgenerator;

import com.octo.captcha.component.image.backgroundgenerator.AbstractBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FunkyBackgroundGenerator
extends AbstractBackgroundGenerator {
    ColorGenerator colorGeneratorLeftUp = null;
    ColorGenerator colorGeneratorLeftDown = null;
    ColorGenerator colorGeneratorRightUp = null;
    ColorGenerator colorGeneratorRightDown = null;
    float perturbationlevel = 0.1f;

    public FunkyBackgroundGenerator(Integer width, Integer height) {
        this(width, height, new SingleColorGenerator(Color.yellow), new SingleColorGenerator(Color.red), new SingleColorGenerator(Color.yellow), new SingleColorGenerator(Color.green), 0.5f);
    }

    public FunkyBackgroundGenerator(Integer width, Integer height, ColorGenerator colorGenerator) {
        this(width, height, colorGenerator, colorGenerator, colorGenerator, colorGenerator, 0.5f);
    }

    public FunkyBackgroundGenerator(Integer width, Integer height, ColorGenerator colorGeneratorLeftUp, ColorGenerator colorGeneratorLeftDown, ColorGenerator colorGeneratorRightUp, ColorGenerator colorGeneratorRightDown, float perturbationLevel) {
        super(width, height);
        this.colorGeneratorLeftUp = colorGeneratorLeftUp;
        this.colorGeneratorLeftDown = colorGeneratorLeftDown;
        this.colorGeneratorRightDown = colorGeneratorRightDown;
        this.colorGeneratorRightUp = colorGeneratorRightUp;
        this.perturbationlevel = perturbationLevel;
    }

    @Override
    public BufferedImage getBackground() {
        Color colorLeftUp = this.colorGeneratorLeftUp.getNextColor();
        Color colorLeftDown = this.colorGeneratorLeftDown.getNextColor();
        Color colorRightUp = this.colorGeneratorRightUp.getNextColor();
        Color colorRightDown = this.colorGeneratorRightDown.getNextColor();
        BufferedImage bimgTP = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 4);
        Graphics2D g2d = bimgTP.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, this.getImageHeight(), this.getImageWidth());
        float height = this.getImageHeight();
        float width = this.getImageWidth();
        for (int j = 0; j < this.getImageHeight(); ++j) {
            for (int i = 0; i < this.getImageWidth(); ++i) {
                float leftUpRatio = (1.0f - (float)i / width) * (1.0f - (float)j / height);
                float leftDownRatio = (1.0f - (float)i / width) * ((float)j / height);
                float rightUpRatio = (float)i / width * (1.0f - (float)j / height);
                float rightDownRatio = (float)i / width * ((float)j / height);
                float red = (float)colorLeftUp.getRed() / 255.0f * leftUpRatio + (float)colorLeftDown.getRed() / 255.0f * leftDownRatio + (float)colorRightUp.getRed() / 255.0f * rightUpRatio + (float)colorRightDown.getRed() / 255.0f * rightDownRatio;
                float green = (float)colorLeftUp.getGreen() / 255.0f * leftUpRatio + (float)colorLeftDown.getGreen() / 255.0f * leftDownRatio + (float)colorRightUp.getGreen() / 255.0f * rightUpRatio + (float)colorRightDown.getGreen() / 255.0f * rightDownRatio;
                float blue = (float)colorLeftUp.getBlue() / 255.0f * leftUpRatio + (float)colorLeftDown.getBlue() / 255.0f * leftDownRatio + (float)colorRightUp.getBlue() / 255.0f * rightUpRatio + (float)colorRightDown.getBlue() / 255.0f * rightDownRatio;
                if (this.myRandom.nextFloat() > this.perturbationlevel) {
                    g2d.setColor(new Color(red, green, blue, 1.0f));
                } else {
                    g2d.setColor(new Color(this.compute(red), this.compute(green), this.compute(blue), 1.0f));
                }
                g2d.drawLine(i, j, i, j);
            }
        }
        g2d.dispose();
        return bimgTP;
    }

    private float compute(float f) {
        float range;
        float f2 = range = 1.0f - f < f ? 1.0f - f : f;
        if (this.myRandom.nextFloat() > 0.5f) {
            return f - this.myRandom.nextFloat() * range;
        }
        return f + this.myRandom.nextFloat() * range;
    }
}

