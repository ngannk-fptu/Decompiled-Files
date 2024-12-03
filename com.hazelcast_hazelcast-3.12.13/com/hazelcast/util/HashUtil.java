/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util;

import com.hazelcast.internal.memory.ByteAccessStrategy;
import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.impl.EndiannessUtil;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

@SuppressFBWarnings(value={"SF_SWITCH_FALLTHROUGH", "SF_SWITCH_NO_DEFAULT"})
public final class HashUtil {
    private static final int MURMUR32_BLOCK_SIZE = 4;
    private static final int MURMUR64_BLOCK_SIZE = 16;
    private static final int DEFAULT_MURMUR_SEED = 16777619;
    private static final int[] PERTURBATIONS = new int[32];
    private static final LoadStrategy<byte[]> BYTE_ARRAY_LOADER = new ByteArrayLoadStrategy();
    private static final LoadStrategy<MemoryAccessor> WIDE_DIRECT_LOADER = new WideDirectLoadStrategy();
    private static final LoadStrategy<MemoryAccessor> NARROW_DIRECT_LOADER = new NarrowDirectLoadStrategy();

    private HashUtil() {
    }

    public static int MurmurHash3_x86_32(byte[] data, int offset, int len) {
        long endIndex = (long)offset + (long)len - 1L;
        assert (endIndex >= Integer.MIN_VALUE && endIndex <= Integer.MAX_VALUE) : String.format("offset %,d len %,d would cause int overflow", offset, len);
        return HashUtil.MurmurHash3_x86_32(BYTE_ARRAY_LOADER, data, offset, len, 16777619);
    }

    public static int MurmurHash3_x86_32_direct(long base, int offset, int len) {
        return HashUtil.MurmurHash3_x86_32_direct(GlobalMemoryAccessorRegistry.MEM, base, offset, len);
    }

    public static int MurmurHash3_x86_32_direct(MemoryAccessor mem, long base, int offset, int len) {
        return HashUtil.MurmurHash3_x86_32(mem.isBigEndian() ? NARROW_DIRECT_LOADER : WIDE_DIRECT_LOADER, mem, base + (long)offset, len, 16777619);
    }

    private static <R> int MurmurHash3_x86_32(LoadStrategy<R> loader, R resource, long offset, int len, int seed) {
        long tailStart = offset + (long)(len & 0xFFFFFFFC);
        int c1 = -862048943;
        int c2 = 461845907;
        int h1 = seed;
        for (long blockAddr = offset; blockAddr < tailStart; blockAddr += 4L) {
            int k1 = loader.getInt(resource, blockAddr);
            k1 *= c1;
            k1 = k1 << 15 | k1 >>> 17;
            h1 ^= (k1 *= c2);
            h1 = h1 << 13 | h1 >>> 19;
            h1 = h1 * 5 + -430675100;
        }
        int k1 = 0;
        switch (len & 3) {
            case 3: {
                k1 = (loader.getByte(resource, tailStart + 2L) & 0xFF) << 16;
            }
            case 2: {
                k1 |= (loader.getByte(resource, tailStart + 1L) & 0xFF) << 8;
            }
            case 1: {
                k1 |= loader.getByte(resource, tailStart) & 0xFF;
                k1 *= c1;
                k1 = k1 << 15 | k1 >>> 17;
                h1 ^= (k1 *= c2);
            }
        }
        h1 ^= len;
        h1 = HashUtil.MurmurHash3_fmix(h1);
        return h1;
    }

    public static long MurmurHash3_x64_64(byte[] data, int offset, int len) {
        return HashUtil.MurmurHash3_x64_64(BYTE_ARRAY_LOADER, data, offset, len, 16777619);
    }

    public static long MurmurHash3_x64_64_direct(long base, int offset, int len) {
        return HashUtil.MurmurHash3_x64_64_direct(GlobalMemoryAccessorRegistry.MEM, base, offset, len);
    }

    public static long MurmurHash3_x64_64_direct(MemoryAccessor mem, long base, int offset, int len) {
        return HashUtil.MurmurHash3_x64_64(mem.isBigEndian() ? NARROW_DIRECT_LOADER : WIDE_DIRECT_LOADER, mem, base + (long)offset, len, 16777619);
    }

    static <R> long MurmurHash3_x64_64(LoadStrategy<R> loader, R resource, long offset, int len) {
        return HashUtil.MurmurHash3_x64_64(loader, resource, offset, len, 16777619);
    }

    static <R> long MurmurHash3_x64_64(LoadStrategy<R> loader, R resource, long offset, int len, int seed) {
        long k2;
        long k1;
        long tailStart = offset + (long)(len & 0xFFFFFFF0);
        long h1 = 0x9368E53C2F6AF274L ^ (long)seed;
        long h2 = 0x586DCD208F7CD3FDL ^ (long)seed;
        long c1 = -8663945395140668459L;
        long c2 = 5545529020109919103L;
        for (long blockAddr = offset; blockAddr < tailStart; blockAddr += 16L) {
            k1 = loader.getLong(resource, blockAddr);
            k2 = loader.getLong(resource, blockAddr + 8L);
            k1 *= c1;
            k1 = k1 << 23 | k1 >>> 41;
            h1 ^= (k1 *= c2);
            h1 += h2;
            h2 = h2 << 41 | h2 >>> 23;
            k2 *= c2;
            k2 = k2 << 23 | k2 >>> 41;
            h2 ^= (k2 *= c1);
            h2 += h1;
            h1 = h1 * 3L + 1390208809L;
            h2 = h2 * 3L + 944331445L;
            c1 = c1 * 5L + 2071795100L;
            c2 = c2 * 5L + 1808688022L;
        }
        k1 = 0L;
        k2 = 0L;
        switch (len & 0xF) {
            case 15: {
                k2 ^= (long)loader.getByte(resource, tailStart + 14L) << 48;
            }
            case 14: {
                k2 ^= (long)loader.getByte(resource, tailStart + 13L) << 40;
            }
            case 13: {
                k2 ^= (long)loader.getByte(resource, tailStart + 12L) << 32;
            }
            case 12: {
                k2 ^= (long)loader.getByte(resource, tailStart + 11L) << 24;
            }
            case 11: {
                k2 ^= (long)loader.getByte(resource, tailStart + 10L) << 16;
            }
            case 10: {
                k2 ^= (long)loader.getByte(resource, tailStart + 9L) << 8;
            }
            case 9: {
                k2 ^= (long)loader.getByte(resource, tailStart + 8L);
            }
            case 8: {
                k1 ^= (long)loader.getByte(resource, tailStart + 7L) << 56;
            }
            case 7: {
                k1 ^= (long)loader.getByte(resource, tailStart + 6L) << 48;
            }
            case 6: {
                k1 ^= (long)loader.getByte(resource, tailStart + 5L) << 40;
            }
            case 5: {
                k1 ^= (long)loader.getByte(resource, tailStart + 4L) << 32;
            }
            case 4: {
                k1 ^= (long)loader.getByte(resource, tailStart + 3L) << 24;
            }
            case 3: {
                k1 ^= (long)loader.getByte(resource, tailStart + 2L) << 16;
            }
            case 2: {
                k1 ^= (long)loader.getByte(resource, tailStart + 1L) << 8;
            }
            case 1: {
                k1 ^= (long)loader.getByte(resource, tailStart);
                k1 *= c1;
                k1 = k1 << 23 | k1 >>> 41;
                h1 ^= (k1 *= c2);
                h1 += h2;
                h2 = h2 << 41 | h2 >>> 23;
                k2 *= c2;
                k2 = k2 << 23 | k2 >>> 41;
                h2 ^= (k2 *= c1);
                h2 += h1;
                h1 = h1 * 3L + 1390208809L;
                h2 = h2 * 3L + 944331445L;
            }
        }
        h1 += (h2 ^= (long)len);
        h2 += h1;
        h1 = HashUtil.MurmurHash3_fmix(h1);
        h2 = HashUtil.MurmurHash3_fmix(h2);
        return h1 + h2;
    }

    public static int MurmurHash3_fmix(int k) {
        k ^= k >>> 16;
        k *= -2048144789;
        k ^= k >>> 13;
        k *= -1028477387;
        k ^= k >>> 16;
        return k;
    }

    public static long MurmurHash3_fmix(long k) {
        k ^= k >>> 33;
        k *= -49064778989728563L;
        k ^= k >>> 33;
        k *= -4265267296055464877L;
        k ^= k >>> 33;
        return k;
    }

    public static long fastLongMix(long k) {
        long phi = -7046029254386353131L;
        long h = k * -7046029254386353131L;
        h ^= h >>> 32;
        return h ^ h >>> 16;
    }

    public static int fastIntMix(int k) {
        int phi = -1640531527;
        int h = k * -1640531527;
        return h ^ h >>> 16;
    }

    public static int hashCode(Object ... objects) {
        return Arrays.hashCode(objects);
    }

    public static int hashToIndex(int hash, int length) {
        Preconditions.checkPositive(length, "length must be larger than 0");
        if (hash == Integer.MIN_VALUE) {
            return 0;
        }
        return Math.abs(hash) % length;
    }

    public static int computePerturbationValue(int capacity) {
        return PERTURBATIONS[Integer.numberOfLeadingZeros(capacity)];
    }

    static {
        int primeDisplacement = 17;
        for (int i = 0; i < PERTURBATIONS.length; ++i) {
            HashUtil.PERTURBATIONS[i] = HashUtil.MurmurHash3_fmix(17 + i);
        }
    }

    private static final class NarrowDirectLoadStrategy
    extends LoadStrategy<MemoryAccessor> {
        private NarrowDirectLoadStrategy() {
        }

        @Override
        public int getInt(MemoryAccessor mem, long offset) {
            return EndiannessUtil.readIntL(this, mem, offset);
        }

        @Override
        public long getLong(MemoryAccessor mem, long offset) {
            return EndiannessUtil.readLongL(this, mem, offset);
        }

        @Override
        public byte getByte(MemoryAccessor mem, long offset) {
            return mem.getByte(offset);
        }
    }

    private static final class WideDirectLoadStrategy
    extends LoadStrategy<MemoryAccessor> {
        private WideDirectLoadStrategy() {
        }

        @Override
        public int getInt(MemoryAccessor mem, long offset) {
            return mem.getInt(offset);
        }

        @Override
        public long getLong(MemoryAccessor mem, long offset) {
            return mem.getLong(offset);
        }

        @Override
        public byte getByte(MemoryAccessor mem, long offset) {
            return mem.getByte(offset);
        }
    }

    private static final class ByteArrayLoadStrategy
    extends LoadStrategy<byte[]> {
        private ByteArrayLoadStrategy() {
        }

        @Override
        public int getInt(byte[] buf, long offset) {
            return EndiannessUtil.readIntL(this, buf, offset);
        }

        @Override
        public long getLong(byte[] buf, long offset) {
            return EndiannessUtil.readLongL(this, buf, offset);
        }

        @Override
        public byte getByte(byte[] buf, long offset) {
            return buf[(int)offset];
        }
    }

    static abstract class LoadStrategy<R>
    implements ByteAccessStrategy<R> {
        LoadStrategy() {
        }

        abstract int getInt(R var1, long var2);

        abstract long getLong(R var1, long var2);

        @Override
        public final void putByte(R resource, long offset, byte value) {
        }
    }
}

