/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.util.Comparator;

public abstract class TiffElement {
    public final long offset;
    public final int length;
    public static final Comparator<TiffElement> COMPARATOR = (e1, e2) -> {
        if (e1.offset < e2.offset) {
            return -1;
        }
        if (e1.offset > e2.offset) {
            return 1;
        }
        return 0;
    };

    public TiffElement(long offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public abstract String getElementDescription();

    public static final class Stub
    extends TiffElement {
        public Stub(long offset, int length) {
            super(offset, length);
        }

        @Override
        public String getElementDescription() {
            return "Element, offset: " + this.offset + ", length: " + this.length + ", last: " + (this.offset + (long)this.length);
        }
    }

    public static abstract class DataElement
    extends TiffElement {
        private final byte[] data;

        public DataElement(long offset, int length, byte[] data) {
            super(offset, length);
            this.data = data;
        }

        public byte[] getData() {
            return (byte[])this.data.clone();
        }

        public int getDataLength() {
            return this.data.length;
        }
    }
}

