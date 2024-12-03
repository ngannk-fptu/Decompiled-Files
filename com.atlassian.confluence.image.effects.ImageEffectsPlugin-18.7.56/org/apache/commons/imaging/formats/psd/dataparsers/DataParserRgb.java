/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.dataparsers;

import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;

public class DataParserRgb
extends DataParser {
    @Override
    protected int getRGB(int[][][] data, int x, int y, PsdImageContents imageContents) {
        int red = 0xFF & data[0][y][x];
        int green = 0xFF & data[1][y][x];
        int blue = 0xFF & data[2][y][x];
        int alpha = 255;
        return 0xFF000000 | (0xFF & red) << 16 | (0xFF & green) << 8 | (0xFF & blue) << 0;
    }

    @Override
    public int getBasicChannelsCount() {
        return 3;
    }
}

