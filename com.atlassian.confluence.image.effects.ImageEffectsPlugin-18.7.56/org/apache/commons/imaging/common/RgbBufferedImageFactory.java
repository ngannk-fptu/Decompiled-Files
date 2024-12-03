/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.awt.image.BufferedImage;
import org.apache.commons.imaging.common.BufferedImageFactory;

public class RgbBufferedImageFactory
implements BufferedImageFactory {
    @Override
    public BufferedImage getColorBufferedImage(int width, int height, boolean hasAlpha) {
        if (hasAlpha) {
            return new BufferedImage(width, height, 2);
        }
        return new BufferedImage(width, height, 1);
    }

    @Override
    public BufferedImage getGrayscaleBufferedImage(int width, int height, boolean hasAlpha) {
        return this.getColorBufferedImage(width, height, hasAlpha);
    }
}

