/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.xml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class BOMZipFileInputStream
extends InputStream {
    public static final byte[] UTF32BEBOMBYTES = new byte[]{0, 0, -2, -1};
    public static final byte[] UTF32LEBOMBYTES = new byte[]{-1, -2, 0, 0};
    public static final byte[] UTF16BEBOMBYTES = new byte[]{-2, -1};
    public static final byte[] UTF16LEBOMBYTES = new byte[]{-1, -2};
    public static final byte[] UTF8BOMBYTES = new byte[]{-17, -69, -65};
    public static final byte[][] BOMBYTES = new byte[][]{UTF32BEBOMBYTES, UTF32LEBOMBYTES, UTF16BEBOMBYTES, UTF16LEBOMBYTES, UTF8BOMBYTES};
    public static final int NONE = -1;
    public static final int MAXBOMBYTES = 4;
    private InputStream daStream;

    public BOMZipFileInputStream(String fileName) throws IOException, FileNotFoundException {
        int BOMType = this.getBOMType(fileName);
        int skipBytes = this.getSkipBytes(BOMType);
        InputStream fIn = this.getFileInputStream(fileName);
        if (skipBytes > 0) {
            fIn.skip(skipBytes);
        }
        this.daStream = fIn;
    }

    @Override
    public int read() throws IOException {
        return this.daStream.read();
    }

    private InputStream getFileInputStream(String filename) throws IOException {
        FilterInputStream is = null;
        FileInputStream fileInputStream = new FileInputStream(filename);
        if (filename != null && filename.trim().endsWith(".zip")) {
            ZipInputStream input = new ZipInputStream(new BufferedInputStream(fileInputStream));
            input.getNextEntry();
            is = input;
        } else {
            is = new BufferedInputStream(fileInputStream);
        }
        return is;
    }

    private int getBOMType(String _f) throws IOException {
        InputStream fileInputStream = this.getFileInputStream(_f);
        byte[] buff = new byte[4];
        int read = fileInputStream.read(buff);
        int bomType = this.getBOMType(buff, read);
        fileInputStream.close();
        return bomType;
    }

    private int getSkipBytes(int bomType) {
        if (bomType < 0 || bomType >= BOMBYTES.length) {
            return 0;
        }
        return BOMBYTES[bomType].length;
    }

    private int getBOMType(byte[] _bomBytes, int _length) {
        for (int i = 0; i < BOMBYTES.length; ++i) {
            for (int j = 0; j < _length && j < BOMBYTES[i].length && _bomBytes[j] == BOMBYTES[i][j]; ++j) {
                if (_bomBytes[j] != BOMBYTES[i][j] || j != BOMBYTES[i].length - 1) continue;
                return i;
            }
        }
        return -1;
    }
}

