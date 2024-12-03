/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.dataparsers;

import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;

public class DataParserBitmap
extends DataParser {
    @Override
    protected int getRGB(int[][][] data, int x, int y, PsdImageContents imageContents) {
        int sample = 0xFF & data[0][y][x];
        sample = sample == 0 ? 255 : 0;
        int alpha = 255;
        return 0xFF000000 | (0xFF & sample) << 16 | (0xFF & sample) << 8 | (0xFF & sample) << 0;
    }

    @Override
    public int getBasicChannelsCount() {
        return 1;
    }
}

