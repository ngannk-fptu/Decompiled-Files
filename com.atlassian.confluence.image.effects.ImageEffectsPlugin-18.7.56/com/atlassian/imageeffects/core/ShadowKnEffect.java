/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import com.jhlabs.image.GaussianFilter;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ShadowKnEffect
extends BaseEffect
implements ImageEffect {
    public ShadowKnEffect(String effectName) {
        super(effectName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException {
        BufferedImage out = new BufferedImage(img.getWidth() + 20, img.getHeight() + 35, 1);
        Graphics2D graphics = (Graphics2D)out.getGraphics();
        try {
            ShadowKnEffect.bgFill(out, graphics);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            int shadowWidth = img.getWidth() + 4;
            graphics.setPaint(new Color(0.1f, 0.1f, 0.1f, 0.2f));
            graphics.fillRect(4, 6, shadowWidth, img.getHeight());
            GeneralPath path = new GeneralPath();
            path.moveTo(0.0f, 20.0f);
            path.lineTo(0.0f, 0.0f);
            path.lineTo(shadowWidth, 0.0f);
            path.lineTo(shadowWidth, 20.0f);
            Arc2D.Float curve = new Arc2D.Float(-25.0f, 2.0f, shadowWidth + 50, 60.0f, 0.0f, 180.0f, 0);
            path.append(curve, true);
            GradientPaint p = new GradientPaint(0.0f, 0.0f, new Color(0.1f, 0.1f, 0.1f, 0.2f), shadowWidth / 2, 0.0f, new Color(0.0f, 0.0f, 0.0f, 0.9f), true);
            graphics.setPaint(p);
            graphics.translate(4, img.getHeight() + 6);
            graphics.setClip(new Rectangle2D.Float(0.0f, 0.0f, shadowWidth, 20.0f));
            graphics.fill(path);
            GaussianFilter filter = new GaussianFilter(6.0f);
            out = filter.filter(out, null);
            graphics = (Graphics2D)out.getGraphics();
            graphics.drawImage(img, 6, 6, img.getWidth(), img.getHeight(), null);
        }
        finally {
            graphics.dispose();
        }
        return out;
    }
}

