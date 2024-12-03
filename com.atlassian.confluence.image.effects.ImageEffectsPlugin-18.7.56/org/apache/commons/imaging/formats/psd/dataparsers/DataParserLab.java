/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.dataparsers;

import org.apache.commons.imaging.color.ColorConversions;
import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;

public class DataParserLab
extends DataParser {
    @Override
    protected int getRGB(int[][][] data, int x, int y, PsdImageContents imageContents) {
        int cieL = 0xFF & data[0][y][x];
        int cieA = 0xFF & data[1][y][x];
        int cieB = 0xFF & data[2][y][x];
        return ColorConversions.convertCIELabtoARGBTest(cieL, cieA -= 128, cieB -= 128);
    }

    @Override
    public int getBasicChannelsCount() {
        return 3;
    }
}

