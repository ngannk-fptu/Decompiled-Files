/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pcx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;

class RleReader {
    private final boolean isCompressed;
    private int count;
    private byte sample;

    RleReader(boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    void read(InputStream is, byte[] samples) throws IOException, ImageReadException {
        if (this.isCompressed) {
            int prefill = Math.min(this.count, samples.length);
            Arrays.fill(samples, 0, prefill, this.sample);
            this.count -= prefill;
            int bytesRead = prefill;
            while (bytesRead < samples.length) {
                byte b = BinaryFunctions.readByte("RleByte", is, "Error reading image data");
                if ((b & 0xC0) == 192) {
                    this.count = b & 0x3F;
                    this.sample = BinaryFunctions.readByte("RleValue", is, "Error reading image data");
                } else {
                    this.count = 1;
                    this.sample = b;
                }
                int samplesToAdd = Math.min(this.count, samples.length - bytesRead);
                Arrays.fill(samples, bytesRead, bytesRead + samplesToAdd, this.sample);
                bytesRead += samplesToAdd;
                this.count -= samplesToAdd;
            }
        } else {
            int r;
            for (int bytesRead = 0; bytesRead < samples.length; bytesRead += r) {
                r = is.read(samples, bytesRead, samples.length - bytesRead);
                if (r >= 0) continue;
                throw new ImageReadException("Premature end of file reading image data");
            }
        }
    }
}

