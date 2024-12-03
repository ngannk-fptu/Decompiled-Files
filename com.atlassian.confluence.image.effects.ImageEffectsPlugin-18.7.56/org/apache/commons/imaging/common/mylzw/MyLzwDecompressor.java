/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.mylzw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import org.apache.commons.imaging.common.mylzw.MyBitInputStream;

public final class MyLzwDecompressor {
    private static final int MAX_TABLE_SIZE = 4096;
    private final byte[][] table;
    private int codeSize;
    private final int initialCodeSize;
    private int codes = -1;
    private final ByteOrder byteOrder;
    private final Listener listener;
    private final int clearCode;
    private final int eoiCode;
    private int written;
    private boolean tiffLZWMode;

    public MyLzwDecompressor(int initialCodeSize, ByteOrder byteOrder) {
        this(initialCodeSize, byteOrder, null);
    }

    public MyLzwDecompressor(int initialCodeSize, ByteOrder byteOrder, Listener listener) {
        this.listener = listener;
        this.byteOrder = byteOrder;
        this.initialCodeSize = initialCodeSize;
        this.table = new byte[4096][];
        this.clearCode = 1 << initialCodeSize;
        this.eoiCode = this.clearCode + 1;
        if (null != listener) {
            listener.init(this.clearCode, this.eoiCode);
        }
        this.initializeTable();
    }

    private void initializeTable() {
        this.codeSize = this.initialCodeSize;
        int intialEntriesCount = 1 << this.codeSize + 2;
        for (int i = 0; i < intialEntriesCount; ++i) {
            this.table[i] = new byte[]{(byte)i};
        }
    }

    private void clearTable() {
        this.codes = (1 << this.initialCodeSize) + 2;
        this.codeSize = this.initialCodeSize;
        this.incrementCodeSize();
    }

    private int getNextCode(MyBitInputStream is) throws IOException {
        int code = is.readBits(this.codeSize);
        if (null != this.listener) {
            this.listener.code(code);
        }
        return code;
    }

    private byte[] stringFromCode(int code) throws IOException {
        if (code >= this.codes || code < 0) {
            throw new IOException("Bad Code: " + code + " codes: " + this.codes + " code_size: " + this.codeSize + ", table: " + this.table.length);
        }
        return this.table[code];
    }

    private boolean isInTable(int code) {
        return code < this.codes;
    }

    private byte firstChar(byte[] bytes) {
        return bytes[0];
    }

    private void addStringToTable(byte[] bytes) throws IOException {
        if (this.codes < 1 << this.codeSize) {
            this.table[this.codes] = bytes;
            ++this.codes;
        }
        this.checkCodeSize();
    }

    private byte[] appendBytes(byte[] bytes, byte b) {
        byte[] result = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        result[result.length - 1] = b;
        return result;
    }

    private void writeToResult(OutputStream os, byte[] bytes) throws IOException {
        os.write(bytes);
        this.written += bytes.length;
    }

    public void setTiffLZWMode() {
        this.tiffLZWMode = true;
    }

    public byte[] decompress(InputStream is, int expectedLength) throws IOException {
        int code;
        int oldCode = -1;
        MyBitInputStream mbis = new MyBitInputStream(is, this.byteOrder);
        if (this.tiffLZWMode) {
            mbis.setTiffLZWMode();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedLength);
        this.clearTable();
        while ((code = this.getNextCode(mbis)) != this.eoiCode) {
            if (code == this.clearCode) {
                this.clearTable();
                if (this.written >= expectedLength || (code = this.getNextCode(mbis)) == this.eoiCode) break;
                this.writeToResult(baos, this.stringFromCode(code));
                oldCode = code;
            } else if (this.isInTable(code)) {
                this.writeToResult(baos, this.stringFromCode(code));
                this.addStringToTable(this.appendBytes(this.stringFromCode(oldCode), this.firstChar(this.stringFromCode(code))));
                oldCode = code;
            } else {
                byte[] outString = this.appendBytes(this.stringFromCode(oldCode), this.firstChar(this.stringFromCode(oldCode)));
                this.writeToResult(baos, outString);
                this.addStringToTable(outString);
                oldCode = code;
            }
            if (this.written < expectedLength) continue;
            break;
        }
        return baos.toByteArray();
    }

    private void checkCodeSize() {
        int limit = 1 << this.codeSize;
        if (this.tiffLZWMode) {
            --limit;
        }
        if (this.codes == limit) {
            this.incrementCodeSize();
        }
    }

    private void incrementCodeSize() {
        if (this.codeSize != 12) {
            ++this.codeSize;
        }
    }

    public static interface Listener {
        public void code(int var1);

        public void init(int var1, int var2);
    }
}

