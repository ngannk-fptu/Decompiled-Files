/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageEffect {
    public BufferedImage processEffect(BufferedImage var1, String var2) throws IOException, FontFormatException;

    public boolean handles(String var1);

    public String getEffectName();
}

