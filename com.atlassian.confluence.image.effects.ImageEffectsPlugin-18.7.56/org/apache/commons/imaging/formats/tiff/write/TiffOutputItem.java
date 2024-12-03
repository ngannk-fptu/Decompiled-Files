/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import java.io.IOException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryOutputStream;

abstract class TiffOutputItem {
    public static final long UNDEFINED_VALUE = -1L;
    private long offset = -1L;

    TiffOutputItem() {
    }

    protected long getOffset() {
        return this.offset;
    }

    protected void setOffset(long offset) {
        this.offset = offset;
    }

    public abstract int getItemLength();

    public abstract String getItemDescription();

    public abstract void writeItem(BinaryOutputStream var1) throws IOException, ImageWriteException;

    public static class Value
    extends TiffOutputItem {
        private final byte[] bytes;
        private final String name;

        Value(String name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }

        @Override
        public int getItemLength() {
            return this.bytes.length;
        }

        @Override
        public String getItemDescription() {
            return this.name;
        }

        public void updateValue(byte[] bytes) throws ImageWriteException {
            if (this.bytes.length != bytes.length) {
                throw new ImageWriteException("Updated data size mismatch: " + this.bytes.length + " vs. " + bytes.length);
            }
            System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
        }

        @Override
        public void writeItem(BinaryOutputStream bos) throws IOException, ImageWriteException {
            bos.write(this.bytes);
        }
    }
}

