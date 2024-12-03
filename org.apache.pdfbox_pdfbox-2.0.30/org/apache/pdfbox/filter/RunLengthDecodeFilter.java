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

final class RunLengthDecodeFilter
extends Filter {
    private static final Log LOG = LogFactory.getLog(RunLengthDecodeFilter.class);
    private static final int RUN_LENGTH_EOD = 128;

    RunLengthDecodeFilter() {
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        int dupAmount;
        byte[] buffer = new byte[128];
        while ((dupAmount = encoded.read()) != -1 && dupAmount != 128) {
            if (dupAmount <= 127) {
                int compressedRead;
                for (int amountToCopy = dupAmount + 1; amountToCopy > 0 && (compressedRead = encoded.read(buffer, 0, amountToCopy)) != -1; amountToCopy -= compressedRead) {
                    decoded.write(buffer, 0, compressedRead);
                }
                continue;
            }
            int dupByte = encoded.read();
            if (dupByte == -1) break;
            for (int i = 0; i < 257 - dupAmount; ++i) {
                decoded.write(dupByte);
            }
        }
        return new DecodeResult(parameters);
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        int byt;
        int lastVal = -1;
        int count = 0;
        boolean equality = false;
        byte[] buf = new byte[128];
        while ((byt = input.read()) != -1) {
            if (lastVal == -1) {
                lastVal = byt;
                count = 1;
                continue;
            }
            if (count == 128) {
                if (equality) {
                    encoded.write(129);
                    encoded.write(lastVal);
                } else {
                    encoded.write(127);
                    encoded.write(buf, 0, 128);
                }
                equality = false;
                lastVal = byt;
                count = 1;
                continue;
            }
            if (count == 1) {
                if (byt == lastVal) {
                    equality = true;
                } else {
                    buf[0] = (byte)lastVal;
                    buf[1] = (byte)byt;
                    lastVal = byt;
                }
                count = 2;
                continue;
            }
            if (byt == lastVal) {
                if (equality) {
                    ++count;
                    continue;
                }
                encoded.write(count - 2);
                encoded.write(buf, 0, count - 1);
                count = 2;
                equality = true;
                continue;
            }
            if (equality) {
                encoded.write(257 - count);
                encoded.write(lastVal);
                equality = false;
                count = 1;
            } else {
                buf[count] = (byte)byt;
                ++count;
            }
            lastVal = byt;
        }
        if (count > 0) {
            if (count == 1) {
                encoded.write(0);
                encoded.write(lastVal);
            } else if (equality) {
                encoded.write(257 - count);
                encoded.write(lastVal);
            } else {
                encoded.write(count - 1);
                encoded.write(buf, 0, count);
            }
        }
        encoded.write(128);
    }
}

