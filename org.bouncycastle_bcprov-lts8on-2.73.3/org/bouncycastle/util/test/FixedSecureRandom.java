/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.encoders.Hex;

public class FixedSecureRandom
extends SecureRandom {
    private static java.math.BigInteger REGULAR = new java.math.BigInteger("01020304ffffffff0506070811111111", 16);
    private static java.math.BigInteger ANDROID = new java.math.BigInteger("1111111105060708ffffffff01020304", 16);
    private static java.math.BigInteger CLASSPATH = new java.math.BigInteger("3020104ffffffff05060708111111", 16);
    private static final boolean isAndroidStyle;
    private static final boolean isClasspathStyle;
    private static final boolean isRegularStyle;
    private byte[] _data;
    private int _index;

    public FixedSecureRandom(byte[] value) {
        this(new Source[]{new Data(value)});
    }

    public FixedSecureRandom(byte[][] values) {
        this(FixedSecureRandom.buildDataArray(values));
    }

    private static Data[] buildDataArray(byte[][] values) {
        Data[] res = new Data[values.length];
        for (int i = 0; i != values.length; ++i) {
            res[i] = new Data(values[i]);
        }
        return res;
    }

    public FixedSecureRandom(Source[] sources) {
        super(null, new DummyProvider());
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        if (isRegularStyle) {
            if (isClasspathStyle) {
                for (int i = 0; i != sources.length; ++i) {
                    try {
                        if (sources[i] instanceof BigInteger) {
                            int w;
                            byte[] data = sources[i].data;
                            int len = data.length - data.length % 4;
                            for (w = data.length - len - 1; w >= 0; --w) {
                                bOut.write(data[w]);
                            }
                            for (w = data.length - len; w < data.length; w += 4) {
                                bOut.write(data, w, 4);
                            }
                            continue;
                        }
                        bOut.write(sources[i].data);
                        continue;
                    }
                    catch (IOException e) {
                        throw new IllegalArgumentException("can't save value source.");
                    }
                }
            } else {
                for (int i = 0; i != sources.length; ++i) {
                    try {
                        bOut.write(sources[i].data);
                        continue;
                    }
                    catch (IOException e) {
                        throw new IllegalArgumentException("can't save value source.");
                    }
                }
            }
        } else if (isAndroidStyle) {
            for (int i = 0; i != sources.length; ++i) {
                try {
                    if (sources[i] instanceof BigInteger) {
                        int w;
                        byte[] data = sources[i].data;
                        int len = data.length - data.length % 4;
                        for (w = 0; w < len; w += 4) {
                            bOut.write(data, data.length - (w + 4), 4);
                        }
                        if (data.length - len != 0) {
                            for (w = 0; w != 4 - (data.length - len); ++w) {
                                bOut.write(0);
                            }
                        }
                        for (w = 0; w != data.length - len; ++w) {
                            bOut.write(data[len + w]);
                        }
                        continue;
                    }
                    bOut.write(sources[i].data);
                    continue;
                }
                catch (IOException e) {
                    throw new IllegalArgumentException("can't save value source.");
                }
            }
        } else {
            throw new IllegalStateException("Unrecognized BigInteger implementation");
        }
        this._data = bOut.toByteArray();
    }

    @Override
    public void nextBytes(byte[] bytes) {
        System.arraycopy(this._data, this._index, bytes, 0, bytes.length);
        this._index += bytes.length;
    }

    @Override
    public byte[] generateSeed(int numBytes) {
        byte[] bytes = new byte[numBytes];
        this.nextBytes(bytes);
        return bytes;
    }

    @Override
    public int nextInt() {
        int val = 0;
        val |= this.nextValue() << 24;
        val |= this.nextValue() << 16;
        val |= this.nextValue() << 8;
        return val |= this.nextValue();
    }

    @Override
    public long nextLong() {
        long val = 0L;
        val |= (long)this.nextValue() << 56;
        val |= (long)this.nextValue() << 48;
        val |= (long)this.nextValue() << 40;
        val |= (long)this.nextValue() << 32;
        val |= (long)this.nextValue() << 24;
        val |= (long)this.nextValue() << 16;
        val |= (long)this.nextValue() << 8;
        return val |= (long)this.nextValue();
    }

    public boolean isExhausted() {
        return this._index == this._data.length;
    }

    private int nextValue() {
        return this._data[this._index++] & 0xFF;
    }

    private static byte[] expandToBitLength(int bitLength, byte[] v) {
        if ((bitLength + 7) / 8 > v.length) {
            byte[] tmp = new byte[(bitLength + 7) / 8];
            System.arraycopy(v, 0, tmp, tmp.length - v.length, v.length);
            if (isAndroidStyle && bitLength % 8 != 0) {
                int i = Pack.bigEndianToInt(tmp, 0);
                Pack.intToBigEndian(i << 8 - bitLength % 8, tmp, 0);
            }
            return tmp;
        }
        if (isAndroidStyle && bitLength < v.length * 8 && bitLength % 8 != 0) {
            int i = Pack.bigEndianToInt(v, 0);
            Pack.intToBigEndian(i << 8 - bitLength % 8, v, 0);
        }
        return v;
    }

    static {
        java.math.BigInteger check1 = new java.math.BigInteger(128, new RandomChecker());
        java.math.BigInteger check2 = new java.math.BigInteger(120, new RandomChecker());
        isAndroidStyle = check1.equals(ANDROID);
        isRegularStyle = check1.equals(REGULAR);
        isClasspathStyle = check2.equals(CLASSPATH);
    }

    public static class BigInteger
    extends Source {
        public BigInteger(byte[] data) {
            super(data);
        }

        public BigInteger(int bitLength, byte[] data) {
            super(FixedSecureRandom.expandToBitLength(bitLength, data));
        }

        public BigInteger(String hexData) {
            this(Hex.decode(hexData));
        }

        public BigInteger(int bitLength, String hexData) {
            super(FixedSecureRandom.expandToBitLength(bitLength, Hex.decode(hexData)));
        }
    }

    public static class Data
    extends Source {
        public Data(byte[] data) {
            super(data);
        }
    }

    private static class DummyProvider
    extends Provider {
        DummyProvider() {
            super("BCFIPS_FIXED_RNG", 1.0, "BCFIPS Fixed Secure Random Provider");
        }
    }

    private static class RandomChecker
    extends SecureRandom {
        byte[] data = Hex.decode("01020304ffffffff0506070811111111");
        int index = 0;

        RandomChecker() {
            super(null, new DummyProvider());
        }

        @Override
        public void nextBytes(byte[] bytes) {
            System.arraycopy(this.data, this.index, bytes, 0, bytes.length);
            this.index += bytes.length;
        }
    }

    public static class Source {
        byte[] data;

        Source(byte[] data) {
            this.data = data;
        }
    }
}

