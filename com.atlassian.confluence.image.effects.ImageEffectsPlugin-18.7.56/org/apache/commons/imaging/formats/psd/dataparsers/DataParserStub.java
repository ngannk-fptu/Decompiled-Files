/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.dataparsers;

import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;

public class DataParserStub
extends DataParser {
    @Override
    protected int getRGB(int[][][] data, int x, int y, PsdImageContents imageContents) {
        return 0;
    }

    @Override
    public int getBasicChannelsCount() {
        return 1;
    }
}

