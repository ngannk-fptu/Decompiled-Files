/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.datareaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.mylzw.BitsToByteInputStream;
import org.apache.commons.imaging.common.mylzw.MyBitInputStream;
import org.apache.commons.imaging.formats.psd.PsdHeaderInfo;
import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;
import org.apache.commons.imaging.formats.psd.datareaders.DataReader;

public class UncompressedDataReader
implements DataReader {
    private final DataParser dataParser;

    public UncompressedDataReader(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    @Override
    public void readData(InputStream is, BufferedImage bi, PsdImageContents imageContents, BinaryFileParser bfp) throws ImageReadException, IOException {
        PsdHeaderInfo header = imageContents.header;
        int width = header.columns;
        int height = header.rows;
        int channelCount = this.dataParser.getBasicChannelsCount();
        int depth = header.depth;
        MyBitInputStream mbis = new MyBitInputStream(is, ByteOrder.BIG_ENDIAN);
        try (BitsToByteInputStream bbis = new BitsToByteInputStream(mbis, 8);){
            int[][][] data = new int[channelCount][height][width];
            for (int channel = 0; channel < channelCount; ++channel) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int b = bbis.readBits(depth);
                        data[channel][y][x] = (byte)b;
                    }
                }
            }
            this.dataParser.parseData(data, bi, imageContents);
        }
    }
}

