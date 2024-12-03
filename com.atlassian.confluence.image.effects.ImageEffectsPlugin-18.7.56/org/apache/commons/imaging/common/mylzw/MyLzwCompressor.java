/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.mylzw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.imaging.common.mylzw.MyBitOutputStream;

public class MyLzwCompressor {
    private int codeSize;
    private final int initialCodeSize;
    private int codes = -1;
    private final ByteOrder byteOrder;
    private final boolean earlyLimit;
    private final int clearCode;
    private final int eoiCode;
    private final Listener listener;
    private final Map<ByteArray, Integer> map = new HashMap<ByteArray, Integer>();

    public MyLzwCompressor(int initialCodeSize, ByteOrder byteOrder, boolean earlyLimit) {
        this(initialCodeSize, byteOrder, earlyLimit, null);
    }

    public MyLzwCompressor(int initialCodeSize, ByteOrder byteOrder, boolean earlyLimit, Listener listener) {
        this.listener = listener;
        this.byteOrder = byteOrder;
        this.earlyLimit = earlyLimit;
        this.initialCodeSize = initialCodeSize;
        this.clearCode = 1 << initialCodeSize;
        this.eoiCode = this.clearCode + 1;
        if (null != listener) {
            listener.init(this.clearCode, this.eoiCode);
        }
        this.initializeStringTable();
    }

    private void initializeStringTable() {
        this.codeSize = this.initialCodeSize;
        int intialEntriesCount = (1 << this.codeSize) + 2;
        this.map.clear();
        this.codes = 0;
        while (this.codes < intialEntriesCount) {
            if (this.codes != this.clearCode && this.codes != this.eoiCode) {
                ByteArray key = this.arrayToKey((byte)this.codes);
                this.map.put(key, this.codes);
            }
            ++this.codes;
        }
    }

    private void clearTable() {
        this.initializeStringTable();
        this.incrementCodeSize();
    }

    private void incrementCodeSize() {
        if (this.codeSize != 12) {
            ++this.codeSize;
        }
    }

    private ByteArray arrayToKey(byte b) {
        return this.arrayToKey(new byte[]{b}, 0, 1);
    }

    private ByteArray arrayToKey(byte[] bytes, int start, int length) {
        return new ByteArray(bytes, start, length);
    }

    private void writeDataCode(MyBitOutputStream bos, int code) throws IOException {
        if (null != this.listener) {
            this.listener.dataCode(code);
        }
        this.writeCode(bos, code);
    }

    private void writeClearCode(MyBitOutputStream bos) throws IOException {
        if (null != this.listener) {
            this.listener.dataCode(this.clearCode);
        }
        this.writeCode(bos, this.clearCode);
    }

    private void writeEoiCode(MyBitOutputStream bos) throws IOException {
        if (null != this.listener) {
            this.listener.eoiCode(this.eoiCode);
        }
        this.writeCode(bos, this.eoiCode);
    }

    private void writeCode(MyBitOutputStream bos, int code) throws IOException {
        bos.writeBits(code, this.codeSize);
    }

    private boolean isInTable(byte[] bytes, int start, int length) {
        ByteArray key = this.arrayToKey(bytes, start, length);
        return this.map.containsKey(key);
    }

    private int codeFromString(byte[] bytes, int start, int length) throws IOException {
        ByteArray key = this.arrayToKey(bytes, start, length);
        Integer code = this.map.get(key);
        if (code == null) {
            throw new IOException("CodeFromString");
        }
        return code;
    }

    private boolean addTableEntry(MyBitOutputStream bos, byte[] bytes, int start, int length) throws IOException {
        ByteArray key = this.arrayToKey(bytes, start, length);
        return this.addTableEntry(bos, key);
    }

    private boolean addTableEntry(MyBitOutputStream bos, ByteArray key) throws IOException {
        boolean cleared = false;
        int limit = 1 << this.codeSize;
        if (this.earlyLimit) {
            --limit;
        }
        if (this.codes == limit) {
            if (this.codeSize < 12) {
                this.incrementCodeSize();
            } else {
                this.writeClearCode(bos);
                this.clearTable();
                cleared = true;
            }
        }
        if (!cleared) {
            this.map.put(key, this.codes);
            ++this.codes;
        }
        return cleared;
    }

    public byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        MyBitOutputStream bos = new MyBitOutputStream(baos, this.byteOrder);
        this.initializeStringTable();
        this.clearTable();
        this.writeClearCode(bos);
        int wStart = 0;
        int wLength = 0;
        for (int i = 0; i < bytes.length; ++i) {
            if (this.isInTable(bytes, wStart, wLength + 1)) {
                ++wLength;
                continue;
            }
            int code = this.codeFromString(bytes, wStart, wLength);
            this.writeDataCode(bos, code);
            this.addTableEntry(bos, bytes, wStart, wLength + 1);
            wStart = i;
            wLength = 1;
        }
        int code = this.codeFromString(bytes, wStart, wLength);
        this.writeDataCode(bos, code);
        this.writeEoiCode(bos);
        bos.flushCache();
        return baos.toByteArray();
    }

    public static interface Listener {
        public void dataCode(int var1);

        public void eoiCode(int var1);

        public void clearCode(int var1);

        public void init(int var1, int var2);
    }

    private static final class ByteArray {
        private final byte[] bytes;
        private final int start;
        private final int length;
        private final int hash;

        ByteArray(byte[] bytes, int start, int length) {
            this.bytes = bytes;
            this.start = start;
            this.length = length;
            int tempHash = length;
            for (int i = 0; i < length; ++i) {
                int b = 0xFF & bytes[i + start];
                tempHash = tempHash + (tempHash << 8) ^ b ^ i;
            }
            this.hash = tempHash;
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object o) {
            if (o instanceof ByteArray) {
                ByteArray other = (ByteArray)o;
                if (other.hash != this.hash) {
                    return false;
                }
                if (other.length != this.length) {
                    return false;
                }
                for (int i = 0; i < this.length; ++i) {
                    if (other.bytes[i + other.start] == this.bytes[i + this.start]) continue;
                    return false;
                }
                return true;
            }
            return false;
        }
    }
}

