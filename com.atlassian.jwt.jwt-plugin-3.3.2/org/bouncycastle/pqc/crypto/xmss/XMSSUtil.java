/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xmss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class XMSSUtil {
    public static int log2(int n) {
        int n2 = 0;
        while ((n >>= 1) != 0) {
            ++n2;
        }
        return n2;
    }

    public static byte[] toBytesBigEndian(long l, int n) {
        byte[] byArray = new byte[n];
        for (int i = n - 1; i >= 0; --i) {
            byArray[i] = (byte)l;
            l >>>= 8;
        }
        return byArray;
    }

    public static void longToBigEndian(long l, byte[] byArray, int n) {
        if (byArray == null) {
            throw new NullPointerException("in == null");
        }
        if (byArray.length - n < 8) {
            throw new IllegalArgumentException("not enough space in array");
        }
        byArray[n] = (byte)(l >> 56 & 0xFFL);
        byArray[n + 1] = (byte)(l >> 48 & 0xFFL);
        byArray[n + 2] = (byte)(l >> 40 & 0xFFL);
        byArray[n + 3] = (byte)(l >> 32 & 0xFFL);
        byArray[n + 4] = (byte)(l >> 24 & 0xFFL);
        byArray[n + 5] = (byte)(l >> 16 & 0xFFL);
        byArray[n + 6] = (byte)(l >> 8 & 0xFFL);
        byArray[n + 7] = (byte)(l & 0xFFL);
    }

    public static long bytesToXBigEndian(byte[] byArray, int n, int n2) {
        if (byArray == null) {
            throw new NullPointerException("in == null");
        }
        long l = 0L;
        for (int i = n; i < n + n2; ++i) {
            l = l << 8 | (long)(byArray[i] & 0xFF);
        }
        return l;
    }

    public static byte[] cloneArray(byte[] byArray) {
        if (byArray == null) {
            throw new NullPointerException("in == null");
        }
        byte[] byArray2 = new byte[byArray.length];
        System.arraycopy(byArray, 0, byArray2, 0, byArray.length);
        return byArray2;
    }

    public static byte[][] cloneArray(byte[][] byArray) {
        if (XMSSUtil.hasNullPointer(byArray)) {
            throw new NullPointerException("in has null pointers");
        }
        byte[][] byArrayArray = new byte[byArray.length][];
        for (int i = 0; i < byArray.length; ++i) {
            byArrayArray[i] = new byte[byArray[i].length];
            System.arraycopy(byArray[i], 0, byArrayArray[i], 0, byArray[i].length);
        }
        return byArrayArray;
    }

    public static boolean areEqual(byte[][] byArray, byte[][] byArray2) {
        if (XMSSUtil.hasNullPointer(byArray) || XMSSUtil.hasNullPointer(byArray2)) {
            throw new NullPointerException("a or b == null");
        }
        for (int i = 0; i < byArray.length; ++i) {
            if (Arrays.areEqual(byArray[i], byArray2[i])) continue;
            return false;
        }
        return true;
    }

    public static void dumpByteArray(byte[][] byArray) {
        if (XMSSUtil.hasNullPointer(byArray)) {
            throw new NullPointerException("x has null pointers");
        }
        for (int i = 0; i < byArray.length; ++i) {
            System.out.println(Hex.toHexString(byArray[i]));
        }
    }

    public static boolean hasNullPointer(byte[][] byArray) {
        if (byArray == null) {
            return true;
        }
        for (int i = 0; i < byArray.length; ++i) {
            if (byArray[i] != null) continue;
            return true;
        }
        return false;
    }

    public static void copyBytesAtOffset(byte[] byArray, byte[] byArray2, int n) {
        if (byArray == null) {
            throw new NullPointerException("dst == null");
        }
        if (byArray2 == null) {
            throw new NullPointerException("src == null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("offset hast to be >= 0");
        }
        if (byArray2.length + n > byArray.length) {
            throw new IllegalArgumentException("src length + offset must not be greater than size of destination");
        }
        for (int i = 0; i < byArray2.length; ++i) {
            byArray[n + i] = byArray2[i];
        }
    }

    public static byte[] extractBytesAtOffset(byte[] byArray, int n, int n2) {
        if (byArray == null) {
            throw new NullPointerException("src == null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("offset hast to be >= 0");
        }
        if (n2 < 0) {
            throw new IllegalArgumentException("length hast to be >= 0");
        }
        if (n + n2 > byArray.length) {
            throw new IllegalArgumentException("offset + length must not be greater then size of source array");
        }
        byte[] byArray2 = new byte[n2];
        for (int i = 0; i < byArray2.length; ++i) {
            byArray2[i] = byArray[n + i];
        }
        return byArray2;
    }

    public static boolean isIndexValid(int n, long l) {
        if (l < 0L) {
            throw new IllegalStateException("index must not be negative");
        }
        return l < 1L << n;
    }

    public static int getDigestSize(Digest digest) {
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        String string = digest.getAlgorithmName();
        if (string.equals("SHAKE128")) {
            return 32;
        }
        if (string.equals("SHAKE256")) {
            return 64;
        }
        return digest.getDigestSize();
    }

    public static long getTreeIndex(long l, int n) {
        return l >> n;
    }

    public static int getLeafIndex(long l, int n) {
        return (int)(l & (1L << n) - 1L);
    }

    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] byArray, Class clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byArray);
        CheckingStream checkingStream = new CheckingStream(clazz, byteArrayInputStream);
        Object object = checkingStream.readObject();
        if (checkingStream.available() != 0) {
            throw new IOException("unexpected data found at end of ObjectInputStream");
        }
        if (clazz.isInstance(object)) {
            return object;
        }
        throw new IOException("unexpected class found in ObjectInputStream");
    }

    public static int calculateTau(int n, int n2) {
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            if ((n >> i & 1) != 0) continue;
            n3 = i;
            break;
        }
        return n3;
    }

    public static boolean isNewBDSInitNeeded(long l, int n, int n2) {
        if (l == 0L) {
            return false;
        }
        return l % (long)Math.pow(1 << n, n2 + 1) == 0L;
    }

    public static boolean isNewAuthenticationPathNeeded(long l, int n, int n2) {
        if (l == 0L) {
            return false;
        }
        return (l + 1L) % (long)Math.pow(1 << n, n2) == 0L;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class CheckingStream
    extends ObjectInputStream {
        private static final Set components = new HashSet();
        private final Class mainClass;
        private boolean found = false;

        CheckingStream(Class clazz, InputStream inputStream) throws IOException {
            super(inputStream);
            this.mainClass = clazz;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
            if (!this.found) {
                if (!objectStreamClass.getName().equals(this.mainClass.getName())) {
                    throw new InvalidClassException("unexpected class: ", objectStreamClass.getName());
                }
                this.found = true;
            } else if (!components.contains(objectStreamClass.getName())) {
                throw new InvalidClassException("unexpected class: ", objectStreamClass.getName());
            }
            return super.resolveClass(objectStreamClass);
        }

        static {
            components.add("java.util.TreeMap");
            components.add("java.lang.Integer");
            components.add("java.lang.Number");
            components.add("org.bouncycastle.pqc.crypto.xmss.BDS");
            components.add("java.util.ArrayList");
            components.add("org.bouncycastle.pqc.crypto.xmss.XMSSNode");
            components.add("[B");
            components.add("java.util.LinkedList");
            components.add("java.util.Stack");
            components.add("java.util.Vector");
            components.add("[Ljava.lang.Object;");
            components.add("org.bouncycastle.pqc.crypto.xmss.BDSTreeHash");
        }
    }
}

