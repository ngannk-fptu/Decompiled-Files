/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.backgroundgenerator;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.backgroundgenerator.AbstractBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class GradientBackgroundGenerator
extends AbstractBackgroundGenerator {
    ColorGenerator firstColor = null;
    ColorGenerator secondColor = null;

    public GradientBackgroundGenerator(Integer width, Integer height, Color firstColor, Color secondColor) {
        super(width, height);
        if (firstColor == null || secondColor == null) {
            throw new CaptchaException("Color is null");
        }
        this.firstColor = new SingleColorGenerator(firstColor);
        this.secondColor = new SingleColorGenerator(secondColor);
    }

    public GradientBackgroundGenerator(Integer width, Integer height, ColorGenerator firstColorGenerator, ColorGenerator secondColorGenerator) {
        super(width, height);
        if (firstColorGenerator == null || secondColorGenerator == null) {
            throw new CaptchaException("ColorGenerator is null");
        }
        this.firstColor = firstColorGenerator;
        this.secondColor = secondColorGenerator;
    }

    @Override
    public BufferedImage getBackground() {
        BufferedImage bi = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        Graphics2D pie = (Graphics2D)bi.getGraphics();
        pie.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0.0f, this.getImageHeight(), this.firstColor.getNextColor(), this.getImageWidth(), 0.0f, this.secondColor.getNextColor());
        pie.setPaint(gp);
        pie.fillRect(0, 0, this.getImageWidth(), this.getImageHeight());
        pie.dispose();
        return bi;
    }
}

