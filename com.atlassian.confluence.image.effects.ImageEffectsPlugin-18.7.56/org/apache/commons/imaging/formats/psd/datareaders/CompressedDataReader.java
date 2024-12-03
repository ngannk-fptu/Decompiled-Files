/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.datareaders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.PackBits;
import org.apache.commons.imaging.common.mylzw.BitsToByteInputStream;
import org.apache.commons.imaging.common.mylzw.MyBitInputStream;
import org.apache.commons.imaging.formats.psd.PsdHeaderInfo;
import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;
import org.apache.commons.imaging.formats.psd.datareaders.DataReader;

public class CompressedDataReader
implements DataReader {
    private final DataParser dataParser;

    public CompressedDataReader(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    @Override
    public void readData(InputStream is, BufferedImage bi, PsdImageContents imageContents, BinaryFileParser bfp) throws ImageReadException, IOException {
        PsdHeaderInfo header = imageContents.header;
        int width = header.columns;
        int height = header.rows;
        int scanlineCount = height * header.channels;
        int[] scanlineBytecounts = new int[scanlineCount];
        for (int i = 0; i < scanlineCount; ++i) {
            scanlineBytecounts[i] = BinaryFunctions.read2Bytes("scanline_bytecount[" + i + "]", is, "PSD: bad Image Data", bfp.getByteOrder());
        }
        int depth = header.depth;
        int channelCount = this.dataParser.getBasicChannelsCount();
        int[][][] data = new int[channelCount][height][];
        for (int channel = 0; channel < channelCount; ++channel) {
            for (int y = 0; y < height; ++y) {
                int index = channel * height + y;
                byte[] packed = BinaryFunctions.readBytes("scanline", is, scanlineBytecounts[index], "PSD: Missing Image Data");
                byte[] unpacked = new PackBits().decompress(packed, width);
                ByteArrayInputStream bais = new ByteArrayInputStream(unpacked);
                MyBitInputStream mbis = new MyBitInputStream(bais, ByteOrder.BIG_ENDIAN);
                try (BitsToByteInputStream bbis = new BitsToByteInputStream(mbis, 8);){
                    int[] scanline = bbis.readBitsArray(depth, width);
                    data[channel][y] = scanline;
                    continue;
                }
            }
        }
        this.dataParser.parseData(data, bi, imageContents);
    }
}

