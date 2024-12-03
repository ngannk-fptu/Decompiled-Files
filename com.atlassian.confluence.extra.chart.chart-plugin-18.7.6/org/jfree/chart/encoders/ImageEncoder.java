/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public interface ImageEncoder {
    public byte[] encode(BufferedImage var1) throws IOException;

    public void encode(BufferedImage var1, OutputStream var2) throws IOException;

    public float getQuality();

    public void setQuality(float var1);

    public boolean isEncodingAlpha();

    public void setEncodingAlpha(boolean var1);
}

