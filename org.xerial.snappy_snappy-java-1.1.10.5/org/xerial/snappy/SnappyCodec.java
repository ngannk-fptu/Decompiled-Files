/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.xerial.snappy.SnappyOutputStream;

public class SnappyCodec {
    static final byte[] MAGIC_HEADER = new byte[]{-126, 83, 78, 65, 80, 80, 89, 0};
    public static final int MAGIC_LEN = MAGIC_HEADER.length;
    public static final int HEADER_SIZE = MAGIC_LEN + 8;
    public static final int MAGIC_HEADER_HEAD = SnappyOutputStream.readInt(MAGIC_HEADER, 0);
    public static final int DEFAULT_VERSION = 1;
    public static final int MINIMUM_COMPATIBLE_VERSION = 1;
    public static final SnappyCodec currentHeader;
    public final byte[] magic;
    public final int version;
    public final int compatibleVersion;
    private final byte[] headerArray;

    private SnappyCodec(byte[] byArray, int n, int n2) {
        this.magic = byArray;
        this.version = n;
        this.compatibleVersion = n2;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(HEADER_SIZE);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.write(byArray, 0, MAGIC_LEN);
            dataOutputStream.writeInt(n);
            dataOutputStream.writeInt(n2);
            dataOutputStream.close();
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        this.headerArray = byteArrayOutputStream.toByteArray();
    }

    public static byte[] getMagicHeader() {
        return (byte[])MAGIC_HEADER.clone();
    }

    public String toString() {
        return String.format("version:%d, compatible version:%d", this.version, this.compatibleVersion);
    }

    public static int headerSize() {
        return HEADER_SIZE;
    }

    public int writeHeader(byte[] byArray, int n) {
        System.arraycopy(this.headerArray, 0, byArray, n, this.headerArray.length);
        return this.headerArray.length;
    }

    public int writeHeader(OutputStream outputStream) throws IOException {
        outputStream.write(this.headerArray, 0, this.headerArray.length);
        return this.headerArray.length;
    }

    public boolean isValidMagicHeader() {
        return Arrays.equals(MAGIC_HEADER, this.magic);
    }

    public static boolean hasMagicHeaderPrefix(byte[] byArray) {
        int n = Math.min(MAGIC_LEN, byArray.length);
        for (int i = 0; i < n; ++i) {
            if (byArray[i] == MAGIC_HEADER[i]) continue;
            return false;
        }
        return true;
    }

    public static SnappyCodec readHeader(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] byArray = new byte[MAGIC_LEN];
        dataInputStream.readFully(byArray, 0, MAGIC_LEN);
        int n = dataInputStream.readInt();
        int n2 = dataInputStream.readInt();
        return new SnappyCodec(byArray, n, n2);
    }

    static {
        assert (MAGIC_HEADER_HEAD < 0);
        currentHeader = new SnappyCodec(MAGIC_HEADER, 1, 1);
    }
}

