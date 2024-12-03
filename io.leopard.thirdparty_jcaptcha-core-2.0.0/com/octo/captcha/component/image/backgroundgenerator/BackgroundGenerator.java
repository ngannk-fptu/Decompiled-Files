/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.backgroundgenerator;

import java.awt.image.BufferedImage;

public interface BackgroundGenerator {
    public int getImageHeight();

    public int getImageWidth();

    public BufferedImage getBackground();
}

