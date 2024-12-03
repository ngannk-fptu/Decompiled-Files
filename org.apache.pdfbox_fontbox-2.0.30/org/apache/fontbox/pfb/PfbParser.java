/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.pfb;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class PfbParser {
    private static final int PFB_HEADER_LENGTH = 18;
    private static final int START_MARKER = 128;
    private static final int ASCII_MARKER = 1;
    private static final int BINARY_MARKER = 2;
    private static final int[] PFB_RECORDS = new int[]{1, 2, 1};
    private static final int BUFFER_SIZE = 65535;
    private byte[] pfbdata;
    private int[] lengths;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PfbParser(String filename) throws IOException {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filename), 65535);
            byte[] pfb = this.readFully(in);
            this.parsePfb(pfb);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public PfbParser(InputStream in) throws IOException {
        byte[] pfb = this.readFully(in);
        this.parsePfb(pfb);
    }

    public PfbParser(byte[] bytes) throws IOException {
        this.parsePfb(bytes);
    }

    private void parsePfb(byte[] pfb) throws IOException {
        if (pfb.length < 18) {
            throw new IOException("PFB header missing");
        }
        ByteArrayInputStream in = new ByteArrayInputStream(pfb);
        this.pfbdata = new byte[pfb.length - 18];
        this.lengths = new int[PFB_RECORDS.length];
        int pointer = 0;
        for (int records = 0; records < PFB_RECORDS.length; ++records) {
            if (in.read() != 128) {
                throw new IOException("Start marker missing");
            }
            if (in.read() != PFB_RECORDS[records]) {
                throw new IOException("Incorrect record type");
            }
            int size = in.read();
            size += in.read() << 8;
            size += in.read() << 16;
            if ((size += in.read() << 24) < 0) {
                throw new IOException("PFB record size is negative: " + size);
            }
            this.lengths[records] = size;
            if (pointer >= this.pfbdata.length) {
                throw new EOFException("attempted to read past EOF");
            }
            if (size > this.pfbdata.length - pointer) {
                throw new EOFException("attempted to read " + size + " bytes at position " + pointer + " into array of size " + this.pfbdata.length + ", but only space for " + (this.pfbdata.length - pointer) + " bytes left");
            }
            int got = in.read(this.pfbdata, pointer, size);
            if (got < 0) {
                throw new EOFException();
            }
            pointer += got;
        }
    }

    private byte[] readFully(InputStream in) throws IOException {
        int amountRead;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] tmpbuf = new byte[65535];
        while ((amountRead = in.read(tmpbuf)) != -1) {
            out.write(tmpbuf, 0, amountRead);
        }
        return out.toByteArray();
    }

    public int[] getLengths() {
        return this.lengths;
    }

    public byte[] getPfbdata() {
        return this.pfbdata;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.pfbdata);
    }

    public int size() {
        return this.pfbdata.length;
    }

    public byte[] getSegment1() {
        return Arrays.copyOfRange(this.pfbdata, 0, this.lengths[0]);
    }

    public byte[] getSegment2() {
        return Arrays.copyOfRange(this.pfbdata, this.lengths[0], this.lengths[0] + this.lengths[1]);
    }
}

