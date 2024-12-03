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

public class UniColorBackgroundGenerator
extends AbstractBackgroundGenerator {
    private BufferedImage backround;
    private ColorGenerator colorGenerator = null;

    public UniColorBackgroundGenerator(Integer width, Integer height) {
        this(width, height, Color.white);
    }

    public UniColorBackgroundGenerator(Integer width, Integer height, Color color) {
        super(width, height);
        this.colorGenerator = new SingleColorGenerator(color);
    }

    public UniColorBackgroundGenerator(Integer width, Integer height, ColorGenerator colorGenerator) {
        super(width, height);
        this.colorGenerator = colorGenerator;
    }

    @Override
    public BufferedImage getBackground() {
        this.backround = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        Graphics2D pie = (Graphics2D)this.backround.getGraphics();
        Color color = this.colorGenerator.getNextColor();
        pie.setColor(color != null ? color : Color.white);
        pie.setBackground(color != null ? color : Color.white);
        pie.fillRect(0, 0, this.getImageWidth(), this.getImageHeight());
        pie.dispose();
        return this.backround;
    }
}

