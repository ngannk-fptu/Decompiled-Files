/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.util.Hex;

final class ASCIIHexFilter
extends Filter {
    private static final Log LOG = LogFactory.getLog(ASCIIHexFilter.class);
    private static final int[] REVERSE_HEX = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    ASCIIHexFilter() {
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        int firstByte;
        while ((firstByte = encoded.read()) != -1) {
            while (this.isWhitespace(firstByte)) {
                firstByte = encoded.read();
            }
            if (firstByte == -1 || this.isEOD(firstByte)) break;
            if (REVERSE_HEX[firstByte] == -1) {
                LOG.error((Object)("Invalid hex, int: " + firstByte + " char: " + (char)firstByte));
            }
            int value = REVERSE_HEX[firstByte] * 16;
            int secondByte = encoded.read();
            if (secondByte == -1 || this.isEOD(secondByte)) {
                decoded.write(value);
                break;
            }
            if (REVERSE_HEX[secondByte] == -1) {
                LOG.error((Object)("Invalid hex, int: " + secondByte + " char: " + (char)secondByte));
            }
            decoded.write(value += REVERSE_HEX[secondByte]);
        }
        decoded.flush();
        return new DecodeResult(parameters);
    }

    private boolean isWhitespace(int c) {
        return c == 0 || c == 9 || c == 10 || c == 12 || c == 13 || c == 32;
    }

    private boolean isEOD(int c) {
        return c == 62;
    }

    @Override
    public void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        int byteRead;
        while ((byteRead = input.read()) != -1) {
            Hex.writeHexByte((byte)byteRead, encoded);
        }
        encoded.flush();
    }
}

