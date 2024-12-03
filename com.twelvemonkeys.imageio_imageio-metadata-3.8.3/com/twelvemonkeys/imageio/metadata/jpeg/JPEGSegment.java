/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

public final class JPEGSegment
implements Serializable {
    final int marker;
    final byte[] data;
    private final int length;
    private transient String id;

    JPEGSegment(int n, byte[] byArray, int n2) {
        this.marker = n;
        this.data = byArray;
        this.length = n2;
    }

    public int segmentLength() {
        return this.length;
    }

    public InputStream segmentData() {
        return this.data != null ? new ByteArrayInputStream(this.data) : null;
    }

    public int marker() {
        return this.marker;
    }

    public String identifier() {
        if (this.id == null && JPEGSegment.isAppSegmentMarker(this.marker)) {
            this.id = JPEGSegmentUtil.asNullTerminatedAsciiString(this.data, 0);
        }
        return this.id;
    }

    static boolean isAppSegmentMarker(int n) {
        return n >= 65504 && n <= 65519;
    }

    public InputStream data() {
        return this.data != null ? new ByteArrayInputStream(this.data, this.offset(), this.length()) : null;
    }

    public int length() {
        return this.data != null ? this.data.length - this.offset() : 0;
    }

    int offset() {
        String string = this.identifier();
        return string == null ? 0 : string.length() + 1;
    }

    public String toString() {
        String string = this.identifier();
        if (string != null) {
            return String.format("JPEGSegment[%04x/%s size: %d]", this.marker, string, this.segmentLength());
        }
        return String.format("JPEGSegment[%04x size: %d]", this.marker, this.segmentLength());
    }

    public int hashCode() {
        String string = this.identifier();
        return this.marker() << 16 | (string != null ? string.hashCode() : 0) & 0xFFFF;
    }

    public boolean equals(Object object) {
        return object instanceof JPEGSegment && ((JPEGSegment)object).marker == this.marker && Arrays.equals(((JPEGSegment)object).data, this.data);
    }
}

