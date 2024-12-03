/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import java.awt.FontFormatException;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class RotateEffect
extends BaseEffect
implements ImageEffect {
    public RotateEffect(String effectName) {
        super(effectName);
    }

    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException, FontFormatException {
        AffineTransform af = new AffineTransform();
        af.rotate(Math.toRadians(7.0));
        af.translate((double)img.getHeight() * Math.sin(Math.toRadians(7.0)), 0.0);
        HashMap<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        AffineTransformOp filter = new AffineTransformOp(af, new RenderingHints(hints));
        return filter.filter(img, null);
    }
}

