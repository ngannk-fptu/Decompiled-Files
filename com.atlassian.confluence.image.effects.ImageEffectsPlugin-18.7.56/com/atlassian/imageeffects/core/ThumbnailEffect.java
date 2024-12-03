/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ThumbnailEffect
extends BaseEffect
implements ImageEffect {
    public ThumbnailEffect(String effectName) {
        super(effectName);
    }

    @Override
    public BufferedImage processEffect(BufferedImage img, String label) {
        int max = Math.max(img.getHeight(), img.getWidth());
        double scale = 200.0 / (double)max;
        if (scale < 1.0) {
            AffineTransform af = new AffineTransform();
            af.scale(scale, scale);
            AffineTransformOp filter = new AffineTransformOp(af, null);
            return filter.filter(img, null);
        }
        return img;
    }
}

