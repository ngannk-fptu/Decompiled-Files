/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.dataparsers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import org.apache.commons.imaging.formats.psd.PsdHeaderInfo;
import org.apache.commons.imaging.formats.psd.PsdImageContents;

public abstract class DataParser {
    public final void parseData(int[][][] data, BufferedImage bi, PsdImageContents imageContents) {
        DataBuffer buffer = bi.getRaster().getDataBuffer();
        PsdHeaderInfo header = imageContents.header;
        int width = header.columns;
        int height = header.rows;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int rgb = this.getRGB(data, x, y, imageContents);
                buffer.setElem(y * width + x, rgb);
            }
        }
    }

    protected abstract int getRGB(int[][][] var1, int var2, int var3, PsdImageContents var4);

    public abstract int getBasicChannelsCount();
}

