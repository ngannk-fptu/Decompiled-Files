/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.pdfbox.io.IOUtils;

public class COSFilterInputStream
extends FilterInputStream {
    private int[][] ranges;
    private int range;
    private long position = 0L;

    public COSFilterInputStream(InputStream in, int[] byteRange) {
        super(in);
        this.calculateRanges(byteRange);
    }

    public COSFilterInputStream(byte[] in, int[] byteRange) {
        this(new ByteArrayInputStream(in), byteRange);
    }

    @Override
    public int read() throws IOException {
        if (!(this.range != -1 && this.getRemaining() > 0L || this.nextRange())) {
            return -1;
        }
        int result = super.read();
        ++this.position;
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (!(this.range != -1 && this.getRemaining() > 0L || this.nextRange())) {
            return -1;
        }
        int bytesRead = super.read(b, off, (int)Math.min((long)len, this.getRemaining()));
        this.position += (long)bytesRead;
        return bytesRead;
    }

    public byte[] toByteArray() throws IOException {
        return IOUtils.toByteArray(this);
    }

    private void calculateRanges(int[] byteRange) {
        this.ranges = new int[byteRange.length / 2][];
        for (int i = 0; i < byteRange.length / 2; ++i) {
            this.ranges[i] = new int[]{byteRange[i * 2], byteRange[i * 2] + byteRange[i * 2 + 1]};
        }
        this.range = -1;
    }

    private long getRemaining() {
        return (long)this.ranges[this.range][1] - this.position;
    }

    private boolean nextRange() throws IOException {
        if (this.range + 1 < this.ranges.length) {
            ++this.range;
            while (this.position < (long)this.ranges[this.range][0]) {
                long skipped = super.skip((long)this.ranges[this.range][0] - this.position);
                if (skipped == 0L) {
                    throw new IOException("FilterInputStream.skip() returns 0, range: " + Arrays.toString(this.ranges[this.range]));
                }
                this.position += skipped;
            }
            return true;
        }
        return false;
    }
}

