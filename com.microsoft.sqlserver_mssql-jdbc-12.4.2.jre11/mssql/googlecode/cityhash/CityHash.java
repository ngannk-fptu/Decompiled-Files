/*
 * Decompiled with CFR 0.152.
 */
package mssql.googlecode.cityhash;

public final class CityHash {
    private static final long k0 = -4348849565147123417L;
    private static final long k1 = -5435081209227447693L;
    private static final long k2 = -7286425919675154353L;
    private static final long k3 = -3942382747735136937L;
    private static final long kMul = -7070675565921424023L;

    private static long toLongLE(byte[] b, int i) {
        return ((long)b[i + 7] << 56) + ((long)(b[i + 6] & 0xFF) << 48) + ((long)(b[i + 5] & 0xFF) << 40) + ((long)(b[i + 4] & 0xFF) << 32) + ((long)(b[i + 3] & 0xFF) << 24) + (long)((b[i + 2] & 0xFF) << 16) + (long)((b[i + 1] & 0xFF) << 8) + (long)((b[i + 0] & 0xFF) << 0);
    }

    private static int toIntLE(byte[] b, int i) {
        return ((b[i + 3] & 0xFF) << 24) + ((b[i + 2] & 0xFF) << 16) + ((b[i + 1] & 0xFF) << 8) + ((b[i + 0] & 0xFF) << 0);
    }

    private static long fetch64(byte[] s, int pos) {
        return CityHash.toLongLE(s, pos);
    }

    private static int fetch32(byte[] s, int pos) {
        return CityHash.toIntLE(s, pos);
    }

    private static long rotate(long val, int shift) {
        return shift == 0 ? val : val >>> shift | val << 64 - shift;
    }

    private static long rotateByAtLeast1(long val, int shift) {
        return val >>> shift | val << 64 - shift;
    }

    private static long shiftMix(long val) {
        return val ^ val >>> 47;
    }

    private static long hash128to64(long u, long v) {
        long a = (u ^ v) * -7070675565921424023L;
        a ^= a >>> 47;
        long b = (v ^ a) * -7070675565921424023L;
        b ^= b >>> 47;
        return b *= -7070675565921424023L;
    }

    private static long hashLen16(long u, long v) {
        return CityHash.hash128to64(u, v);
    }

    private static long hashLen0to16(byte[] s, int pos, int len) {
        if (len > 8) {
            long a = CityHash.fetch64(s, pos + 0);
            long b = CityHash.fetch64(s, pos + len - 8);
            return CityHash.hashLen16(a, CityHash.rotateByAtLeast1(b + (long)len, len)) ^ b;
        }
        if (len >= 4) {
            long a = 0xFFFFFFFFL & (long)CityHash.fetch32(s, pos + 0);
            return CityHash.hashLen16((a << 3) + (long)len, 0xFFFFFFFFL & (long)CityHash.fetch32(s, pos + len - 4));
        }
        if (len > 0) {
            int a = s[pos + 0] & 0xFF;
            int b = s[pos + (len >>> 1)] & 0xFF;
            int c = s[pos + len - 1] & 0xFF;
            int y = a + (b << 8);
            int z = len + (c << 2);
            return CityHash.shiftMix((long)y * -7286425919675154353L ^ (long)z * -3942382747735136937L) * -7286425919675154353L;
        }
        return -7286425919675154353L;
    }

    private static long hashLen17to32(byte[] s, int pos, int len) {
        long a = CityHash.fetch64(s, pos + 0) * -5435081209227447693L;
        long b = CityHash.fetch64(s, pos + 8);
        long c = CityHash.fetch64(s, pos + len - 8) * -7286425919675154353L;
        long d = CityHash.fetch64(s, pos + len - 16) * -4348849565147123417L;
        return CityHash.hashLen16(CityHash.rotate(a - b, 43) + CityHash.rotate(c, 30) + d, a + CityHash.rotate(b ^ 0xC949D7C7509E6557L, 20) - c + (long)len);
    }

    private static long[] weakHashLen32WithSeeds(long w, long x, long y, long z, long a, long b) {
        b = CityHash.rotate(b + (a += w) + z, 21);
        long c = a;
        a += x;
        return new long[]{a + z, (b += CityHash.rotate(a += y, 44)) + c};
    }

    private static long[] weakHashLen32WithSeeds(byte[] s, int pos, long a, long b) {
        return CityHash.weakHashLen32WithSeeds(CityHash.fetch64(s, pos + 0), CityHash.fetch64(s, pos + 8), CityHash.fetch64(s, pos + 16), CityHash.fetch64(s, pos + 24), a, b);
    }

    private static long hashLen33to64(byte[] s, int pos, int len) {
        long z = CityHash.fetch64(s, pos + 24);
        long a = CityHash.fetch64(s, pos + 0) + (CityHash.fetch64(s, pos + len - 16) + (long)len) * -4348849565147123417L;
        long b = CityHash.rotate(a + z, 52);
        long c = CityHash.rotate(a, 37);
        long vf = (a += CityHash.fetch64(s, pos + 16)) + z;
        long vs = b + CityHash.rotate(a, 31) + (c += CityHash.rotate(a += CityHash.fetch64(s, pos + 8), 7));
        a = CityHash.fetch64(s, pos + 16) + CityHash.fetch64(s, pos + len - 32);
        z = CityHash.fetch64(s, pos + len - 8);
        b = CityHash.rotate(a + z, 52);
        c = CityHash.rotate(a, 37);
        long wf = (a += CityHash.fetch64(s, pos + len - 16)) + z;
        long ws = b + CityHash.rotate(a, 31) + (c += CityHash.rotate(a += CityHash.fetch64(s, pos + len - 24), 7));
        long r = CityHash.shiftMix((vf + ws) * -7286425919675154353L + (wf + vs) * -4348849565147123417L);
        return CityHash.shiftMix(r * -4348849565147123417L + vs) * -7286425919675154353L;
    }

    static long cityHash64(byte[] s, int pos, int len) {
        if (len <= 32) {
            if (len <= 16) {
                return CityHash.hashLen0to16(s, pos, len);
            }
            return CityHash.hashLen17to32(s, pos, len);
        }
        if (len <= 64) {
            return CityHash.hashLen33to64(s, pos, len);
        }
        long x = CityHash.fetch64(s, pos + len - 40);
        long y = CityHash.fetch64(s, pos + len - 16) + CityHash.fetch64(s, pos + len - 56);
        long z = CityHash.hashLen16(CityHash.fetch64(s, pos + len - 48) + (long)len, CityHash.fetch64(s, pos + len - 24));
        long[] v = CityHash.weakHashLen32WithSeeds(s, pos + len - 64, len, z);
        long[] w = CityHash.weakHashLen32WithSeeds(s, pos + len - 32, y + -5435081209227447693L, x);
        x = x * -5435081209227447693L + CityHash.fetch64(s, pos + 0);
        len = len - 1 & 0xFFFFFFC0;
        do {
            x = CityHash.rotate(x + y + v[0] + CityHash.fetch64(s, pos + 8), 37) * -5435081209227447693L;
            y = CityHash.rotate(y + v[1] + CityHash.fetch64(s, pos + 48), 42) * -5435081209227447693L;
            z = CityHash.rotate(z + w[0], 33) * -5435081209227447693L;
            v = CityHash.weakHashLen32WithSeeds(s, pos + 0, v[1] * -5435081209227447693L, (x ^= w[1]) + w[0]);
            w = CityHash.weakHashLen32WithSeeds(s, pos + 32, z + w[1], (y += v[0] + CityHash.fetch64(s, pos + 40)) + CityHash.fetch64(s, pos + 16));
            long swap = z;
            z = x;
            x = swap;
            pos += 64;
        } while ((len -= 64) != 0);
        return CityHash.hashLen16(CityHash.hashLen16(v[0], w[0]) + CityHash.shiftMix(y) * -5435081209227447693L + z, CityHash.hashLen16(v[1], w[1]) + x);
    }

    static long cityHash64WithSeed(byte[] s, int pos, int len, long seed) {
        return CityHash.cityHash64WithSeeds(s, pos, len, -7286425919675154353L, seed);
    }

    static long cityHash64WithSeeds(byte[] s, int pos, int len, long seed0, long seed1) {
        return CityHash.hashLen16(CityHash.cityHash64(s, pos, len) - seed0, seed1);
    }

    static long[] cityMurmur(byte[] s, int pos, int len, long seed0, long seed1) {
        long a = seed0;
        long b = seed1;
        long c = 0L;
        long d = 0L;
        int l = len - 16;
        if (l <= 0) {
            a = CityHash.shiftMix(a * -5435081209227447693L) * -5435081209227447693L;
            c = b * -5435081209227447693L + CityHash.hashLen0to16(s, pos, len);
            d = CityHash.shiftMix(a + (len >= 8 ? CityHash.fetch64(s, pos + 0) : c));
        } else {
            c = CityHash.hashLen16(CityHash.fetch64(s, pos + len - 8) + -5435081209227447693L, a);
            d = CityHash.hashLen16(b + (long)len, c + CityHash.fetch64(s, pos + len - 16));
            a += d;
            do {
                a ^= CityHash.shiftMix(CityHash.fetch64(s, pos + 0) * -5435081209227447693L) * -5435081209227447693L;
                b ^= (a *= -5435081209227447693L);
                c ^= CityHash.shiftMix(CityHash.fetch64(s, pos + 8) * -5435081209227447693L) * -5435081209227447693L;
                d ^= (c *= -5435081209227447693L);
                pos += 16;
            } while ((l -= 16) > 0);
        }
        a = CityHash.hashLen16(a, c);
        b = CityHash.hashLen16(d, b);
        return new long[]{a ^ b, CityHash.hashLen16(b, a)};
    }

    static long[] cityHash128WithSeed(byte[] s, int pos, int len, long seed0, long seed1) {
        if (len < 128) {
            return CityHash.cityMurmur(s, pos, len, seed0, seed1);
        }
        long[] v = new long[2];
        long[] w = new long[2];
        long x = seed0;
        long y = seed1;
        long z = -5435081209227447693L * (long)len;
        v[0] = CityHash.rotate(y ^ 0xB492B66FBE98F273L, 49) * -5435081209227447693L + CityHash.fetch64(s, pos);
        v[1] = CityHash.rotate(v[0], 42) * -5435081209227447693L + CityHash.fetch64(s, pos + 8);
        w[0] = CityHash.rotate(y + z, 35) * -5435081209227447693L + x;
        w[1] = CityHash.rotate(x + CityHash.fetch64(s, pos + 88), 53) * -5435081209227447693L;
        do {
            x = CityHash.rotate(x + y + v[0] + CityHash.fetch64(s, pos + 8), 37) * -5435081209227447693L;
            y = CityHash.rotate(y + v[1] + CityHash.fetch64(s, pos + 48), 42) * -5435081209227447693L;
            y += v[0] + CityHash.fetch64(s, pos + 40);
            z = CityHash.rotate(z + w[0], 33) * -5435081209227447693L;
            v = CityHash.weakHashLen32WithSeeds(s, pos + 0, v[1] * -5435081209227447693L, (x ^= w[1]) + w[0]);
            w = CityHash.weakHashLen32WithSeeds(s, pos + 32, z + w[1], y + CityHash.fetch64(s, pos + 16));
            long swap = z;
            z = x;
            x = swap;
            x = CityHash.rotate(x + y + v[0] + CityHash.fetch64(s, (pos += 64) + 8), 37) * -5435081209227447693L;
            y = CityHash.rotate(y + v[1] + CityHash.fetch64(s, pos + 48), 42) * -5435081209227447693L;
            y += v[0] + CityHash.fetch64(s, pos + 40);
            z = CityHash.rotate(z + w[0], 33) * -5435081209227447693L;
            v = CityHash.weakHashLen32WithSeeds(s, pos, v[1] * -5435081209227447693L, (x ^= w[1]) + w[0]);
            w = CityHash.weakHashLen32WithSeeds(s, pos + 32, z + w[1], y + CityHash.fetch64(s, pos + 16));
            swap = z;
            z = x;
            x = swap;
            pos += 64;
        } while ((len -= 128) >= 128);
        x += CityHash.rotate(v[0] + z, 49) * -4348849565147123417L;
        z += CityHash.rotate(w[0], 37) * -4348849565147123417L;
        int tail_done = 0;
        while (tail_done < len) {
            y = CityHash.rotate(x + y, 42) * -4348849565147123417L + v[1];
            w[0] = w[0] + CityHash.fetch64(s, pos + len - (tail_done += 32) + 16);
            x = x * -4348849565147123417L + w[0];
            w[1] = w[1] + v[0];
            v = CityHash.weakHashLen32WithSeeds(s, pos + len - tail_done, v[0] + (z += w[1] + CityHash.fetch64(s, pos + len - tail_done)), v[1]);
        }
        x = CityHash.hashLen16(x, v[0]);
        y = CityHash.hashLen16(y + z, w[0]);
        return new long[]{CityHash.hashLen16(x + v[1], w[1]) + y, CityHash.hashLen16(x + w[1], y + v[1])};
    }

    public static long[] cityHash128(byte[] s, int pos, int len) {
        if (len >= 16) {
            return CityHash.cityHash128WithSeed(s, pos + 16, len - 16, CityHash.fetch64(s, pos + 0) ^ 0xC949D7C7509E6557L, CityHash.fetch64(s, pos + 8));
        }
        if (len >= 8) {
            return CityHash.cityHash128WithSeed(new byte[0], 0, 0, CityHash.fetch64(s, pos + 0) ^ (long)len * -4348849565147123417L, CityHash.fetch64(s, pos + len - 8) ^ 0xB492B66FBE98F273L);
        }
        return CityHash.cityHash128WithSeed(s, pos, len, -4348849565147123417L, -5435081209227447693L);
    }
}

