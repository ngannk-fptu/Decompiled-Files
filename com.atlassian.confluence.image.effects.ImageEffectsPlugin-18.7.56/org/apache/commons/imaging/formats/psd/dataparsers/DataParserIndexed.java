/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.dataparsers;

import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;

public class DataParserIndexed
extends DataParser {
    private final int[] colorTable = new int[256];

    public DataParserIndexed(byte[] colorModeData) {
        for (int i = 0; i < 256; ++i) {
            int rgb;
            int red = 0xFF & colorModeData[0 + i];
            int green = 0xFF & colorModeData[256 + i];
            int blue = 0xFF & colorModeData[512 + i];
            int alpha = 255;
            this.colorTable[i] = rgb = 0xFF000000 | (0xFF & red) << 16 | (0xFF & green) << 8 | (0xFF & blue) << 0;
        }
    }

    @Override
    protected int getRGB(int[][][] data, int x, int y, PsdImageContents imageContents) {
        int sample = 0xFF & data[0][y][x];
        return this.colorTable[sample];
    }

    @Override
    public int getBasicChannelsCount() {
        return 1;
    }
}

