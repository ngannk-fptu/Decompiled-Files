/*
 * Decompiled with CFR 0.152.
 */
package org.monte.media.jpeg;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class JFIFInputStream
extends FilterInputStream {
    private final HashSet<Integer> standaloneMarkers = new HashSet();
    private final HashSet<Integer> doubleSegMarkers = new HashSet();
    private Segment segment;
    private boolean markerFound;
    private int marker = -1;
    private long offset = 0L;
    private boolean isStuffed0xff = false;
    public static final int JUNK_MARKER = -1;
    public static final int SOI_MARKER = 65496;
    public static final int EOI_MARKER = 65497;
    public static final int TEM_MARKER = 65281;
    public static final int SOS_MARKER = 65498;
    public static final int APP1_MARKER = 65505;
    public static final int APP2_MARKER = 65506;
    public static final int JPG0_MARKER = 65520;
    public static final int JPG1_MARKER = 65521;
    public static final int JPG2_MARKER = 65522;
    public static final int JPG3_MARKER = 65523;
    public static final int JPG4_MARKER = 65524;
    public static final int JPG5_MARKER = 65525;
    public static final int JPG6_MARKER = 65526;
    public static final int JPG7_MARKER = 65527;
    public static final int JPG8_MARKER = 65528;
    public static final int JPG9_MARKER = 65529;
    public static final int JPGA_MARKER = 65530;
    public static final int JPGB_MARKER = 65531;
    public static final int JPGC_MARKER = 65532;
    public static final int JPGD_MARKER = 65533;
    public static final int SOF0_MARKER = 65472;
    public static final int SOF1_MARKER = 65473;
    public static final int SOF2_MARKER = 65474;
    public static final int SOF3_MARKER = 65475;
    public static final int SOF5_MARKER = 65477;
    public static final int SOF6_MARKER = 65478;
    public static final int SOF7_MARKER = 65479;
    public static final int SOF9_MARKER = 65481;
    public static final int SOFA_MARKER = 65482;
    public static final int SOFB_MARKER = 65483;
    public static final int SOFD_MARKER = 65485;
    public static final int SOFE_MARKER = 65486;
    public static final int SOFF_MARKER = 65487;
    public static final int RST0_MARKER = 65488;
    public static final int RST1_MARKER = 65489;
    public static final int RST2_MARKER = 65490;
    public static final int RST3_MARKER = 65491;
    public static final int RST4_MARKER = 65492;
    public static final int RST5_MARKER = 65493;
    public static final int RST6_MARKER = 65494;
    public static final int RST7_MARKER = 65495;

    public JFIFInputStream(File f) throws IOException {
        this(new BufferedInputStream(new FileInputStream(f)));
    }

    public JFIFInputStream(InputStream in) {
        super(in);
        for (int i = 65488; i <= 65495; ++i) {
            this.standaloneMarkers.add(i);
        }
        this.standaloneMarkers.add(65496);
        this.standaloneMarkers.add(65497);
        this.standaloneMarkers.add(65281);
        this.standaloneMarkers.add(65520);
        this.standaloneMarkers.add(65521);
        this.standaloneMarkers.add(65522);
        this.standaloneMarkers.add(65523);
        this.standaloneMarkers.add(65524);
        this.standaloneMarkers.add(65525);
        this.standaloneMarkers.add(65526);
        this.standaloneMarkers.add(65527);
        this.standaloneMarkers.add(65528);
        this.standaloneMarkers.add(65529);
        this.standaloneMarkers.add(65530);
        this.standaloneMarkers.add(65531);
        this.standaloneMarkers.add(65532);
        this.standaloneMarkers.add(65533);
        this.standaloneMarkers.add(65535);
        this.doubleSegMarkers.add(65498);
        this.segment = new Segment(-1, 0L, -1);
    }

    public Segment getSegment() throws IOException {
        return this.segment;
    }

    public Segment getNextSegment() throws IOException {
        if (!this.segment.isEntropyCoded()) {
            this.markerFound = false;
            do {
                long skipped;
                if ((skipped = this.in.skip((long)this.segment.length - this.offset + this.segment.offset)) == -1L) {
                    this.segment = new Segment(0, this.offset, -1);
                    return null;
                }
                this.offset += skipped;
            } while (this.offset < (long)this.segment.length + this.segment.offset);
            if (this.doubleSegMarkers.contains(this.segment.marker)) {
                this.segment = new Segment(0, this.offset, -1);
                return this.segment;
            }
        }
        while (!this.markerFound) {
            int b;
            do {
                if (this.isStuffed0xff) {
                    b = 255;
                    this.isStuffed0xff = false;
                } else {
                    b = this.read0();
                }
                if (b != -1) continue;
                return null;
            } while (b != 255);
            this.markerFound = true;
            b = this.read0();
            if (b == -1) {
                return null;
            }
            if (b == 0) {
                this.markerFound = false;
                continue;
            }
            if (b == 255) {
                this.isStuffed0xff = true;
                this.markerFound = false;
                continue;
            }
            this.marker = 0xFF00 | b;
        }
        this.markerFound = false;
        if (this.standaloneMarkers.contains(this.marker)) {
            this.segment = new Segment(0xFF00 | this.marker, this.offset, -1);
        } else {
            int length = this.read0() << 8 | this.read0();
            if (length < 2) {
                throw new IOException("JFIFInputStream found illegal segment length " + length + " after marker " + Integer.toHexString(this.marker) + " at offset " + this.offset + ".");
            }
            this.segment = new Segment(0xFF00 | this.marker, this.offset, length - 2);
        }
        return this.segment;
    }

    public long getStreamPosition() {
        return this.offset;
    }

    private int read0() throws IOException {
        int b = this.in.read();
        if (b != -1) {
            ++this.offset;
        }
        return b;
    }

    @Override
    public int read() throws IOException {
        int b;
        if (this.markerFound) {
            return -1;
        }
        if (this.isStuffed0xff) {
            this.isStuffed0xff = false;
            b = 255;
        } else {
            b = this.read0();
        }
        if (this.segment.isEntropyCoded() && b == 255) {
            b = this.read0();
            if (b == 0) {
                return 255;
            }
            if (b == 255) {
                this.isStuffed0xff = true;
                return 255;
            }
            this.markerFound = true;
            this.marker = 0xFF00 | b;
            return -1;
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count;
        if (this.markerFound) {
            return -1;
        }
        if (this.segment.isEntropyCoded()) {
            for (count = 0; count < len; ++count) {
                int data = this.read();
                if (data == -1) {
                    if (count == 0) {
                        return -1;
                    }
                    break;
                }
                b[off + count] = (byte)data;
            }
        } else {
            long available = (long)this.segment.length - this.offset + this.segment.offset;
            if (available <= 0L) {
                return -1;
            }
            if (available < (long)len) {
                len = (int)available;
            }
            if ((count = this.in.read(b, off, len)) != -1) {
                this.offset += (long)count;
            }
        }
        return count;
    }

    public final void skipFully(long n) throws IOException {
        long total;
        long cur = 0L;
        for (total = 0L; total < n && (cur = (long)((int)this.in.skip(n - total))) > 0L; total += cur) {
        }
        this.offset += total;
        if (total < n) {
            throw new EOFException();
        }
    }

    @Override
    public long skip(long n) throws IOException {
        long count;
        if (this.markerFound) {
            return -1L;
        }
        if (this.segment.isEntropyCoded()) {
            int data;
            for (count = 0L; count < n && (data = this.read()) != -1; ++count) {
            }
        } else {
            long available = (long)this.segment.length - this.offset + this.segment.offset;
            if (available < n) {
                n = (int)available;
            }
            if ((count = this.in.skip(n)) != -1L) {
                this.offset += count;
            }
        }
        return count;
    }

    @Override
    public synchronized void mark(int readlimit) {
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("Reset not supported");
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    public static class Segment {
        public final int marker;
        public final long offset;
        public final int length;

        public Segment(int marker, long offset, int length) {
            this.marker = marker;
            this.offset = offset;
            this.length = length;
        }

        public boolean isEntropyCoded() {
            return this.length == -1;
        }

        public String toString() {
            return "Segment marker=0x" + Integer.toHexString(this.marker) + " offset=" + this.offset + "=0x" + Long.toHexString(this.offset);
        }
    }
}

