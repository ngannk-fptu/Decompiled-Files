/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import com.jhlabs.image.MirrorFilter;
import java.awt.image.BufferedImage;

public class MirrorEffect
extends BaseEffect
implements ImageEffect {
    public MirrorEffect(String effectName) {
        super(effectName);
    }

    @Override
    public BufferedImage processEffect(BufferedImage img, String label) {
        MirrorFilter filter = new MirrorFilter();
        filter.setCentreY(1.0f);
        filter.setOpacity(0.3f);
        int reflectHeight = (int)((float)img.getHeight() * 0.2f);
        BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight() + reflectHeight, 1);
        return filter.filter(img, dest);
    }
}

