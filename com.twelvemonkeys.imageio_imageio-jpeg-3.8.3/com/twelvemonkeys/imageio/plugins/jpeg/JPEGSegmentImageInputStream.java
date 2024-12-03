/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentWarningListener;
import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

final class JPEGSegmentImageInputStream
extends ImageInputStreamImpl {
    private final ImageInputStream stream;
    private final JPEGSegmentWarningListener warningListener;
    private final ComponentIdSet componentIds = new ComponentIdSet();
    private final List<Segment> segments = new ArrayList<Segment>(64);
    private int currentSegment = -1;
    private Segment segment;

    JPEGSegmentImageInputStream(ImageInputStream imageInputStream, JPEGSegmentWarningListener jPEGSegmentWarningListener) {
        this.stream = (ImageInputStream)Validate.notNull((Object)imageInputStream, (String)"stream");
        this.warningListener = (JPEGSegmentWarningListener)Validate.notNull((Object)jPEGSegmentWarningListener, (String)"warningListener");
    }

    JPEGSegmentImageInputStream(ImageInputStream imageInputStream) {
        this(imageInputStream, JPEGSegmentWarningListener.NULL_LISTENER);
    }

    private void processWarningOccured(String string) {
        this.warningListener.warningOccurred(string);
    }

    private Segment fetchSegment() throws IOException {
        block27: {
            if (this.currentSegment == -1) {
                this.streamInit();
            } else {
                this.segment = this.segments.get(this.currentSegment);
            }
            if (this.streamPos >= this.segment.end()) {
                int n = this.currentSegment;
                while (++n < this.segments.size()) {
                    this.currentSegment = n;
                    this.segment = this.segments.get(this.currentSegment);
                    if (this.streamPos < this.segment.start || this.streamPos >= this.segment.end()) continue;
                    this.segment.seek(this.stream, this.streamPos);
                    return this.segment;
                }
                this.stream.seek(this.segment.realEnd());
                while (true) {
                    boolean bl;
                    int n2 = 0;
                    int n3 = this.stream.readUnsignedByte();
                    while (!JPEGSegmentUtil.isKnownJPEGMarker((int)n3)) {
                        n3 &= 0xFF;
                        while (n3 != 255) {
                            n3 = this.stream.readUnsignedByte();
                            ++n2;
                        }
                        n3 = 0xFF00 | this.stream.readUnsignedByte();
                        while (n3 == 65535) {
                            n3 = 0xFF00 | this.stream.readUnsignedByte();
                            ++n2;
                        }
                    }
                    if (n2 != 0) {
                        this.processWarningOccured(String.format("Corrupt JPEG data: %d extraneous bytes before marker 0x%02x", n2, n3 & 0xFF));
                    }
                    long l = this.stream.getStreamPosition() - 2L;
                    boolean bl2 = JPEGSegmentImageInputStream.isAppSegmentMarker(n3);
                    boolean bl3 = n3 == 65518 && JPEGSegmentImageInputStream.isAppSegmentWithId("Adobe", this.stream);
                    boolean bl4 = bl = n3 == 65505 && JPEGSegmentImageInputStream.isAppSegmentWithId("Exif", this.stream);
                    if (bl2 && !bl && !bl3) {
                        int n4 = this.stream.readUnsignedShort();
                        this.stream.seek(l + 2L + (long)n4);
                        continue;
                    }
                    if (n3 == 65497) {
                        this.segment = new Segment(n3, l, this.segment.end(), 2L);
                    } else {
                        long l2 = 2 + this.stream.readUnsignedShort();
                        if (bl3 && l2 != 16L) {
                            this.segment = new AdobeAPP14Replacement(l, this.segment.end(), l2, this.stream);
                        } else if (n3 == 65499) {
                            int n5 = this.stream.read();
                            if ((n5 & 0x10) == 16) {
                                this.processWarningOccured("16 bit DQT encountered");
                                this.segment = new DownsampledDQTReplacement(l, this.segment.end(), l2, n5, this.stream);
                            } else {
                                this.segment = new Segment(n3, l, this.segment.end(), l2);
                            }
                        } else if (JPEGSegmentImageInputStream.isSOFMarker(n3)) {
                            byte[] byArray = this.readReplaceDuplicateSOFnComponentIds(n3, l2);
                            this.segment = new ReplacementSegment(n3, l, this.segment.end(), l2, byArray);
                        } else if (n3 == 65498) {
                            byte[] byArray = this.readReplaceDuplicateSOSComponentSelectors(l2);
                            this.segment = new ReplacementSegment(n3, l, this.segment.end(), l2, byArray);
                        } else {
                            this.segment = new Segment(n3, l, this.segment.end(), l2);
                        }
                    }
                    this.segments.add(this.segment);
                    this.currentSegment = this.segments.size() - 1;
                    if (n3 == 65498) {
                        this.segments.add(new Segment(-1, this.segment.realEnd(), this.segment.end(), Long.MAX_VALUE - this.segment.realEnd()));
                    }
                    if (this.streamPos >= this.segment.start && this.streamPos < this.segment.end()) {
                        this.segment.seek(this.stream, this.streamPos);
                        break block27;
                    }
                    this.stream.seek(this.segment.realEnd());
                }
            }
            if (this.streamPos < this.segment.start) {
                int n = this.currentSegment;
                while (--n >= 0) {
                    this.currentSegment = n;
                    this.segment = this.segments.get(this.currentSegment);
                    if (this.streamPos < this.segment.start || this.streamPos >= this.segment.end()) continue;
                    this.segment.seek(this.stream, this.streamPos);
                    break;
                }
            } else {
                this.segment.seek(this.stream, this.streamPos);
            }
        }
        return this.segment;
    }

    private byte[] readReplaceDuplicateSOSComponentSelectors(long l) throws IOException {
        int n;
        byte[] byArray = JPEGSegmentImageInputStream.readSegment(65498, (int)l, this.stream);
        ComponentIdSet componentIdSet = new ComponentIdSet();
        boolean bl = false;
        int n2 = 5;
        while ((long)n2 < l - 3L) {
            n = byArray[n2] & 0xFF;
            if (!componentIdSet.add(n)) {
                this.processWarningOccured(String.format("Duplicate component ID %d in SOS", n));
                bl = true;
            }
            n2 += 2;
        }
        if (bl) {
            n2 = 5;
            n = 0;
            while (n < this.componentIds.size() && (long)n2 < l - 3L) {
                byArray[n2] = (byte)this.componentIds.get(n);
                ++n;
                n2 += 2;
            }
        }
        return byArray;
    }

    private byte[] readReplaceDuplicateSOFnComponentIds(int n, long l) throws IOException {
        byte[] byArray = JPEGSegmentImageInputStream.readSegment(n, (int)l, this.stream);
        int n2 = 10;
        while ((long)n2 < l) {
            int n3 = byArray[n2] & 0xFF;
            if (!this.componentIds.add(n3)) {
                this.processWarningOccured(String.format("Duplicate component ID %d in SOF", n3));
                ++n3;
                while (this.componentIds.size() < 4 && !this.componentIds.add(n3) && n3 < 255) {
                    ++n3;
                }
                byArray[n2] = (byte)n3;
            }
            n2 += 3;
        }
        return byArray;
    }

    private static byte[] readSegment(int n, int n2, ImageInputStream imageInputStream) throws IOException {
        byte[] byArray = new byte[n2];
        byArray[0] = (byte)(n >> 8 & 0xFF);
        byArray[1] = (byte)(n & 0xFF);
        byArray[2] = (byte)(n2 - 2 >> 8 & 0xFF);
        byArray[3] = (byte)(n2 - 2 & 0xFF);
        imageInputStream.readFully(byArray, 4, n2 - 4);
        return byArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean isAppSegmentWithId(String string, ImageInputStream imageInputStream) throws IOException {
        Validate.notNull((Object)string, (String)"segmentId");
        imageInputStream.mark();
        try {
            int n = imageInputStream.readUnsignedShort();
            byte[] byArray = new byte[Math.min(string.length() + 1, n - 2)];
            imageInputStream.readFully(byArray);
            boolean bl = string.equals(JPEGSegmentImageInputStream.asNullTerminatedAsciiString(byArray, 0));
            return bl;
        }
        finally {
            imageInputStream.reset();
        }
    }

    static String asNullTerminatedAsciiString(byte[] byArray, int n) {
        for (int i = 0; i < byArray.length - n; ++i) {
            if (byArray[n + i] != 0 && i <= 255) continue;
            return JPEGSegmentImageInputStream.asAsciiString(byArray, n, n + i);
        }
        return null;
    }

    static String asAsciiString(byte[] byArray, int n, int n2) {
        return new String(byArray, n, n2, StandardCharsets.US_ASCII);
    }

    private void streamInit() throws IOException {
        long l = this.stream.getStreamPosition();
        try {
            int n = this.stream.readUnsignedShort();
            if (n != 65496) {
                throw new IIOException(String.format("Not a JPEG stream (starts with: 0x%04x, expected SOI: 0x%04x)", n, 65496));
            }
            this.segment = new Segment(n, l, 0L, 2L);
            this.segments.add(this.segment);
            this.currentSegment = this.segments.size() - 1;
        }
        catch (EOFException eOFException) {
            throw new IIOException(String.format("Not a JPEG stream (short stream. expected SOI: 0x%04x)", 65496), eOFException);
        }
    }

    static boolean isAppSegmentMarker(int n) {
        return n >= 65504 && n <= 65519;
    }

    static boolean isSOFMarker(int n) {
        switch (n) {
            case 65472: 
            case 65473: 
            case 65474: 
            case 65475: 
            case 65477: 
            case 65478: 
            case 65479: 
            case 65481: 
            case 65482: 
            case 65483: 
            case 65485: 
            case 65486: 
            case 65487: {
                return true;
            }
        }
        return false;
    }

    private void repositionAsNecessary() throws IOException {
        if (this.segment == null || this.streamPos < this.segment.start || this.streamPos >= this.segment.end()) {
            try {
                this.fetchSegment();
            }
            catch (EOFException eOFException) {
                this.segments.add(new Segment(0, this.segment.realEnd(), this.segment.end(), 0xFFFFFFFEL - this.segment.realEnd()));
            }
        }
    }

    @Override
    public int read() throws IOException {
        this.bitOffset = 0;
        this.repositionAsNecessary();
        int n = this.segment.read(this.stream);
        if (n != -1) {
            ++this.streamPos;
        }
        return n;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        int n4;
        this.bitOffset = 0;
        for (n3 = 0; n3 < n2; n3 += n4) {
            this.repositionAsNecessary();
            long l = this.segment.end() - this.streamPos;
            int n5 = n4 = l <= 0L ? -1 : this.segment.read(this.stream, byArray, n + n3, (int)Math.min((long)(n2 - n3), l));
            if (n4 == -1) {
                if (n3 != 0) break;
                return -1;
            }
            this.streamPos += (long)n4;
        }
        return n3;
    }

    @Override
    @Deprecated
    protected void finalize() {
    }

    static final class ComponentIdSet {
        final int[] values = new int[4];
        int size;

        ComponentIdSet() {
        }

        boolean add(int n) {
            if (this.contains(n) || this.size >= this.values.length) {
                return false;
            }
            this.values[this.size++] = n;
            return true;
        }

        boolean contains(int n) {
            for (int i = 0; i < this.size; ++i) {
                if (this.values[i] != n) continue;
                return true;
            }
            return false;
        }

        int size() {
            return this.size;
        }

        int get(int n) {
            return this.values[n];
        }

        public String toString() {
            return Arrays.toString(Arrays.copyOf(this.values, this.size));
        }
    }

    static class ReplacementSegment
    extends Segment {
        final long realLength;
        final byte[] data;
        int pos;

        ReplacementSegment(int n, long l, long l2, long l3, byte[] byArray) {
            super(n, l, l2, byArray.length);
            this.realLength = l3;
            this.data = byArray;
        }

        @Override
        long realEnd() {
            return this.realStart + this.realLength;
        }

        @Override
        public void seek(ImageInputStream imageInputStream, long l) throws IOException {
            this.pos = (int)(l - this.start);
            super.seek(imageInputStream, l);
        }

        @Override
        public int read(ImageInputStream imageInputStream) {
            return this.data.length > this.pos ? this.data[this.pos++] & 0xFF : -1;
        }

        @Override
        public int read(ImageInputStream imageInputStream, byte[] byArray, int n, int n2) {
            int n3 = this.data.length - this.pos;
            if (n3 <= 0) {
                return -1;
            }
            int n4 = Math.min(n3, n2);
            System.arraycopy(this.data, this.pos, byArray, n, n4);
            this.pos += n4;
            return n4;
        }
    }

    static final class DownsampledDQTReplacement
    extends ReplacementSegment {
        DownsampledDQTReplacement(long l, long l2, long l3, int n, ImageInputStream imageInputStream) throws IOException {
            super(65499, l, l2, l3, DownsampledDQTReplacement.createMarkerFixedLength((int)l3, n, imageInputStream));
        }

        private static byte[] createMarkerFixedLength(int n, int n2, ImageInputStream imageInputStream) throws IOException {
            int n3 = n / 128;
            int n4 = 2 + 65 * n3;
            byte[] byArray = new byte[n];
            byArray[0] = -1;
            byArray[1] = -37;
            byArray[2] = (byte)(n4 >> 8 & 0xFF);
            byArray[3] = (byte)(n4 & 0xFF);
            byArray[4] = (byte)(n2 & 0xF);
            imageInputStream.readFully(byArray, 5, byArray.length - 5);
            int n5 = 4;
            int n6 = 4;
            for (int i = 0; i < n3; ++i) {
                byArray[n5++] = (byte)(byArray[n6++] & 0xF);
                for (int j = 0; j < 64; ++j) {
                    byArray[n5 + j] = byArray[n6 + 1 + j * 2];
                }
                n5 += 64;
                n6 += 128;
            }
            return Arrays.copyOfRange(byArray, 0, n4 + 2);
        }
    }

    static final class AdobeAPP14Replacement
    extends ReplacementSegment {
        AdobeAPP14Replacement(long l, long l2, long l3, ImageInputStream imageInputStream) throws IOException {
            super(65518, l, l2, l3, AdobeAPP14Replacement.createMarkerFixedLength(imageInputStream));
        }

        private static byte[] createMarkerFixedLength(ImageInputStream imageInputStream) throws IOException {
            return JPEGSegmentImageInputStream.readSegment(65518, 16, imageInputStream);
        }
    }

    static class Segment {
        final int marker;
        final long realStart;
        final long start;
        final long length;

        Segment(int n, long l, long l2, long l3) {
            this.marker = n;
            this.realStart = l;
            this.start = l2;
            this.length = l3;
        }

        long realEnd() {
            return this.realStart + this.length;
        }

        long end() {
            return this.start + this.length;
        }

        public void seek(ImageInputStream imageInputStream, long l) throws IOException {
            imageInputStream.seek(this.realStart + l - this.start);
        }

        public int read(ImageInputStream imageInputStream) throws IOException {
            return imageInputStream.read();
        }

        public int read(ImageInputStream imageInputStream, byte[] byArray, int n, int n2) throws IOException {
            return imageInputStream.read(byArray, n, n2);
        }

        public String toString() {
            return String.format("0x%04x[%d-%d]", this.marker, this.realStart, this.realEnd());
        }
    }
}

