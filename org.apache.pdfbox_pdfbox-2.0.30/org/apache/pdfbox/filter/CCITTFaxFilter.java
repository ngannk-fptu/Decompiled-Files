/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.CCITTFaxDecoderStream;
import org.apache.pdfbox.filter.CCITTFaxEncoderStream;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.IOUtils;

final class CCITTFaxFilter
extends Filter {
    CCITTFaxFilter() {
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        int type;
        COSDictionary decodeParms = this.getDecodeParams(parameters, index);
        int cols = decodeParms.getInt(COSName.COLUMNS, 1728);
        int rows = decodeParms.getInt(COSName.ROWS, 0);
        int height = parameters.getInt(COSName.HEIGHT, COSName.H, 0);
        rows = rows > 0 && height > 0 ? height : Math.max(rows, height);
        int k = decodeParms.getInt(COSName.K, 0);
        boolean encodedByteAlign = decodeParms.getBoolean(COSName.ENCODED_BYTE_ALIGN, false);
        int arraySize = (cols + 7) / 8 * rows;
        byte[] decompressed = new byte[arraySize];
        long tiffOptions = 0L;
        if (k == 0) {
            type = 3;
            byte[] streamData = new byte[20];
            int bytesRead = encoded.read(streamData);
            PushbackInputStream pushbackInputStream = new PushbackInputStream(encoded, streamData.length);
            pushbackInputStream.unread(streamData, 0, bytesRead);
            encoded = pushbackInputStream;
            if (streamData[0] != 0 || streamData[1] >> 4 != 1 && streamData[1] != 1) {
                type = 2;
                short b = (short)((streamData[0] << 8) + (streamData[1] & 0xFF) >> 4);
                for (int i = 12; i < bytesRead * 8; ++i) {
                    if (((b = (short)((b << 1) + (streamData[i / 8] >> 7 - i % 8 & 1))) & 0xFFF) != 1) continue;
                    type = 3;
                    break;
                }
            }
        } else if (k > 0) {
            type = 3;
            tiffOptions = 1L;
        } else {
            type = 4;
        }
        CCITTFaxDecoderStream s = new CCITTFaxDecoderStream(encoded, cols, type, tiffOptions, encodedByteAlign);
        this.readFromDecoderStream(s, decompressed);
        boolean blackIsOne = decodeParms.getBoolean(COSName.BLACK_IS_1, false);
        if (!blackIsOne) {
            this.invertBitmap(decompressed);
        }
        decoded.write(decompressed);
        return new DecodeResult(parameters);
    }

    void readFromDecoderStream(CCITTFaxDecoderStream decoderStream, byte[] result) throws IOException {
        int read;
        int pos = 0;
        while ((read = decoderStream.read(result, pos, result.length - pos)) > -1 && (pos += read) < result.length) {
        }
    }

    private void invertBitmap(byte[] bufferData) {
        int c = bufferData.length;
        for (int i = 0; i < c; ++i) {
            bufferData[i] = (byte)(~bufferData[i] & 0xFF);
        }
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        int cols = parameters.getInt(COSName.COLUMNS);
        int rows = parameters.getInt(COSName.ROWS);
        CCITTFaxEncoderStream ccittFaxEncoderStream = new CCITTFaxEncoderStream(encoded, cols, rows, 1);
        IOUtils.copy(input, ccittFaxEncoderStream);
    }
}

