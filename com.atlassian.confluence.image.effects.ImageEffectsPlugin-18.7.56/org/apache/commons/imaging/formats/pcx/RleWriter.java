/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pcx;

import java.io.IOException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryOutputStream;

class RleWriter {
    private final boolean isCompressed;
    private int previousByte = -1;
    private int repeatCount = 0;

    RleWriter(boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    void write(BinaryOutputStream bos, byte[] samples) throws IOException, ImageWriteException {
        if (this.isCompressed) {
            for (byte element : samples) {
                if ((element & 0xFF) == this.previousByte && this.repeatCount < 63) {
                    ++this.repeatCount;
                    continue;
                }
                if (this.repeatCount > 0) {
                    if (this.repeatCount == 1 && (this.previousByte & 0xC0) != 192) {
                        bos.write(this.previousByte);
                    } else {
                        bos.write(0xC0 | this.repeatCount);
                        bos.write(this.previousByte);
                    }
                }
                this.previousByte = 0xFF & element;
                this.repeatCount = 1;
            }
        } else {
            bos.write(samples);
        }
    }

    void flush(BinaryOutputStream bos) throws IOException {
        if (this.repeatCount > 0) {
            if (this.repeatCount == 1 && (this.previousByte & 0xC0) != 192) {
                bos.write(this.previousByte);
            } else {
                bos.write(0xC0 | this.repeatCount);
                bos.write(this.previousByte);
            }
        }
    }
}

