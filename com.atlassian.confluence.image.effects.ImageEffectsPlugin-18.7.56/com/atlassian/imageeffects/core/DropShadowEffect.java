/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import com.jhlabs.image.GaussianFilter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DropShadowEffect
extends BaseEffect
implements ImageEffect {
    public DropShadowEffect(String effectName) {
        super(effectName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException {
        BufferedImage out = new BufferedImage(img.getWidth() + 65, img.getHeight() + 65, 1);
        Graphics2D graphics = (Graphics2D)out.getGraphics();
        try {
            DropShadowEffect.bgFill(out, graphics);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics.setPaint(new Color(0.0f, 0.0f, 0.0f, 0.3f));
            graphics.fillRect(32, 32, img.getWidth(), img.getHeight());
            GaussianFilter filter = new GaussianFilter(40.0f);
            out = filter.filter(out, null);
            graphics = (Graphics2D)out.getGraphics();
            graphics.drawImage(img, 17, 17, img.getWidth(), img.getHeight(), null);
        }
        finally {
            graphics.dispose();
        }
        return out;
    }
}

