/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import com.jhlabs.image.GaussianFilter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BlurBorderEffect
extends BaseEffect
implements ImageEffect {
    public BlurBorderEffect(String effectName) {
        super(effectName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException {
        BufferedImage out = new BufferedImage(img.getWidth() + 12, img.getHeight() + 12, 1);
        Graphics graphics = out.getGraphics();
        try {
            BlurBorderEffect.bgFill(out, graphics);
            graphics.setColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));
            graphics.fillRect(6, 6, img.getWidth() + 1, img.getHeight() + 1);
            GaussianFilter filter = new GaussianFilter(8.0f);
            out = filter.filter(out, null);
            graphics = out.getGraphics();
            graphics.drawImage(img, 6, 6, img.getWidth(), img.getHeight(), null);
        }
        finally {
            graphics.dispose();
        }
        return out;
    }
}

