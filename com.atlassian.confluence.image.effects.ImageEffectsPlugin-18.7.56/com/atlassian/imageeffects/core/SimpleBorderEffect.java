/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import com.jhlabs.image.BorderFilter;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SimpleBorderEffect
extends BaseEffect
implements ImageEffect {
    public SimpleBorderEffect(String effectName) {
        super(effectName);
    }

    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException, FontFormatException {
        BorderFilter filter = new BorderFilter(20, 20, 20, 20, Color.white);
        return filter.filter(img, null);
    }
}

