/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import com.jhlabs.image.BicubicScaleFilter;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScaleForThumbEffect
extends BaseEffect
implements ImageEffect {
    public ScaleForThumbEffect(String effectName) {
        super(effectName);
    }

    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException {
        int width;
        int height = img.getHeight();
        if (Math.max(height, width = img.getWidth()) > 100) {
            float scaleFactor = 100.0f / (float)Math.max(height, width);
            BicubicScaleFilter filter = new BicubicScaleFilter((int)((float)width * scaleFactor), (int)((float)height * scaleFactor));
            return filter.filter(img, null);
        }
        return img;
    }
}

