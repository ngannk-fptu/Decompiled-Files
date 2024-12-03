/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.awt.image.BufferedImage;

public interface BufferedImageFactory {
    public BufferedImage getColorBufferedImage(int var1, int var2, boolean var3);

    public BufferedImage getGrayscaleBufferedImage(int var1, int var2, boolean var3);
}

