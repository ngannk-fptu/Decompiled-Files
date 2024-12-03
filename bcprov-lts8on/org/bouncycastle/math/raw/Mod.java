/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

import java.util.Random;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Integers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class Mod {
    private static final int M30 = 0x3FFFFFFF;
    private static final long M32L = 0xFFFFFFFFL;

    public static void checkedModOddInverse(int[] m, int[] x, int[] z) {
        if (0 == Mod.modOddInverse(m, x, z)) {
            throw new ArithmeticException("Inverse does not exist.");
        }
    }

    public static void checkedModOddInverseVar(int[] m, int[] x, int[] z) {
        if (!Mod.modOddInverseVar(m, x, z)) {
            throw new ArithmeticException("Inverse does not exist.");
        }
    }

    public static int inverse32(int d) {
        int x = d;
        x *= 2 - d * x;
        x *= 2 - d * x;
        x *= 2 - d * x;
        x *= 2 - d * x;
        return x;
    }

    public static int modOddInverse(int[] m, int[] x, int[] z) {
        int len32 = m.length;
        int bits = (len32 << 5) - Integers.numberOfLeadingZeros(m[len32 - 1]);
        int len30 = (bits + 29) / 30;
        int[] t = new int[4];
        int[] D = new int[len30];
        int[] E = new int[len30];
        int[] F2 = new int[len30];
        int[] G = new int[len30];
        int[] M = new int[len30];
        E[0] = 1;
        Mod.encode30(bits, x, 0, G, 0);
        Mod.encode30(bits, m, 0, M, 0);
        System.arraycopy(M, 0, F2, 0, len30);
        int delta = 0;
        int m0Inv32 = Mod.inverse32(M[0]);
        int maxDivsteps = Mod.getMaximumDivsteps(bits);
        for (int divSteps = 0; divSteps < maxDivsteps; divSteps += 30) {
            delta = Mod.divsteps30(delta, F2[0], G[0], t);
            Mod.updateDE30(len30, D, E, t, m0Inv32, M);
            Mod.updateFG30(len30, F2, G, t);
        }
        int signF = F2[len30 - 1] >> 31;
        Mod.cnegate30(len30, signF, F2);
        Mod.cnormalize30(len30, signF, D, M);
        Mod.decode30(bits, D, 0, z, 0);
        return Nat.equalTo(len30, F2, 1) & Nat.equalToZero(len30, G);
    }

    public static boolean modOddInverseVar(int[] m, int[] x, int[] z) {
        int len32 = m.length;
        int bits = (len32 << 5) - Integers.numberOfLeadingZeros(m[len32 - 1]);
        int len30 = (bits + 29) / 30;
        int[] t = new int[4];
        int[] D = new int[len30];
        int[] E = new int[len30];
        int[] F2 = new int[len30];
        int[] G = new int[len30];
        int[] M = new int[len30];
        E[0] = 1;
        Mod.encode30(bits, x, 0, G, 0);
        Mod.encode30(bits, m, 0, M, 0);
        System.arraycopy(M, 0, F2, 0, len30);
        int clzG = Integers.numberOfLeadingZeros(G[len30 - 1] | 1) - (len30 * 30 + 2 - bits);
        int eta = -1 - clzG;
        int lenDE = len30;
        int lenFG = len30;
        int m0Inv32 = Mod.inverse32(M[0]);
        int maxDivsteps = Mod.getMaximumDivsteps(bits);
        int divsteps = 0;
        while (!Nat.isZero(lenFG, G)) {
            if (divsteps >= maxDivsteps) {
                return false;
            }
            divsteps += 30;
            eta = Mod.divsteps30Var(eta, F2[0], G[0], t);
            Mod.updateDE30(lenDE, D, E, t, m0Inv32, M);
            Mod.updateFG30(lenFG, F2, G, t);
            int fn = F2[lenFG - 1];
            int gn = G[lenFG - 1];
            int cond = lenFG - 2 >> 31;
            cond |= fn ^ fn >> 31;
            if ((cond |= gn ^ gn >> 31) != 0) continue;
            int n = lenFG - 2;
            F2[n] = F2[n] | fn << 30;
            int n2 = lenFG - 2;
            G[n2] = G[n2] | gn << 30;
            --lenFG;
        }
        int signF = F2[lenFG - 1] >> 31;
        int signD = D[lenDE - 1] >> 31;
        if (signD < 0) {
            signD = Mod.add30(lenDE, D, M);
        }
        if (signF < 0) {
            signD = Mod.negate30(lenDE, D);
            signF = Mod.negate30(lenFG, F2);
        }
        if (!Nat.isOne(lenFG, F2)) {
            return false;
        }
        if (signD < 0) {
            signD = Mod.add30(lenDE, D, M);
        }
        Mod.decode30(bits, D, 0, z, 0);
        return true;
    }

    public static int[] random(int[] p) {
        int len = p.length;
        Random rand = new Random();
        int[] s = Nat.create(len);
        int m = p[len - 1];
        m |= m >>> 1;
        m |= m >>> 2;
        m |= m >>> 4;
        m |= m >>> 8;
        m |= m >>> 16;
        do {
            for (int i = 0; i != len; ++i) {
                s[i] = rand.nextInt();
            }
            int n = len - 1;
            s[n] = s[n] & m;
        } while (Nat.gte(len, s, p));
        return s;
    }

    private static int add30(int len30, int[] D, int[] M) {
        int c = 0;
        int last = len30 - 1;
        for (int i = 0; i < last; ++i) {
            D[i] = (c += D[i] + M[i]) & 0x3FFFFFFF;
            c >>= 30;
        }
        D[last] = c += D[last] + M[last];
        return c >>= 30;
    }

    private static void cnegate30(int len30, int cond, int[] D) {
        int c = 0;
        int last = len30 - 1;
        for (int i = 0; i < last; ++i) {
            D[i] = (c += (D[i] ^ cond) - cond) & 0x3FFFFFFF;
            c >>= 30;
        }
        D[last] = c += (D[last] ^ cond) - cond;
    }

    private static void cnormalize30(int len30, int condNegate, int[] D, int[] M) {
        int di;
        int i;
        int last = len30 - 1;
        int c = 0;
        int condAdd = D[last] >> 31;
        for (i = 0; i < last; ++i) {
            di = D[i] + (M[i] & condAdd);
            di = (di ^ condNegate) - condNegate;
            D[i] = (c += di) & 0x3FFFFFFF;
            c >>= 30;
        }
        int di2 = D[last] + (M[last] & condAdd);
        di2 = (di2 ^ condNegate) - condNegate;
        D[last] = c += di2;
        c = 0;
        condAdd = D[last] >> 31;
        for (i = 0; i < last; ++i) {
            di = D[i] + (M[i] & condAdd);
            D[i] = (c += di) & 0x3FFFFFFF;
            c >>= 30;
        }
        di2 = D[last] + (M[last] & condAdd);
        D[last] = c += di2;
    }

    private static void decode30(int bits, int[] x, int xOff, int[] z, int zOff) {
        int avail = 0;
        long data = 0L;
        while (bits > 0) {
            while (avail < Math.min(32, bits)) {
                data |= (long)x[xOff++] << avail;
                avail += 30;
            }
            z[zOff++] = (int)data;
            data >>>= 32;
            avail -= 32;
            bits -= 32;
        }
    }

    private static int divsteps30(int delta, int f0, int g0, int[] t) {
        int u = 0x40000000;
        int v = 0;
        int q = 0;
        int r = 0x40000000;
        int f = f0;
        int g = g0;
        for (int i = 0; i < 30; ++i) {
            int c1 = delta >> 31;
            int c2 = -(g & 1);
            int x = f ^ c1;
            int y = u ^ c1;
            int z = v ^ c1;
            g -= x & c2;
            q -= y & c2;
            r -= z & c2;
            delta = (delta ^ (c2 &= ~c1)) - (c2 - 1);
            f += g & c2;
            u += q & c2;
            v += r & c2;
            g >>= 1;
            q >>= 1;
            r >>= 1;
        }
        t[0] = u;
        t[1] = v;
        t[2] = q;
        t[3] = r;
        return delta;
    }

    private static int divsteps30Var(int eta, int f0, int g0, int[] t) {
        int u = 1;
        int v = 0;
        int q = 0;
        int r = 1;
        int f = f0;
        int g = g0;
        int i = 30;
        while (true) {
            int w;
            int m;
            int limit;
            int zeros = Integers.numberOfTrailingZeros(g | -1 << i);
            g >>= zeros;
            u <<= zeros;
            v <<= zeros;
            eta -= zeros;
            if ((i -= zeros) <= 0) break;
            if (eta < 0) {
                eta = -eta;
                int x = f;
                f = g;
                g = -x;
                int y = u;
                u = q;
                q = -y;
                int z = v;
                v = r;
                r = -z;
                limit = eta + 1 > i ? i : eta + 1;
                m = -1 >>> 32 - limit & 0x3F;
                w = f * g * (f * f - 2) & m;
            } else {
                limit = eta + 1 > i ? i : eta + 1;
                m = -1 >>> 32 - limit & 0xF;
                w = f + ((f + 1 & 4) << 1);
                w = -w * g & m;
            }
            g += f * w;
            q += u * w;
            r += v * w;
        }
        t[0] = u;
        t[1] = v;
        t[2] = q;
        t[3] = r;
        return eta;
    }

    private static void encode30(int bits, int[] x, int xOff, int[] z, int zOff) {
        int avail = 0;
        long data = 0L;
        while (bits > 0) {
            if (avail < Math.min(30, bits)) {
                data |= ((long)x[xOff++] & 0xFFFFFFFFL) << avail;
                avail += 32;
            }
            z[zOff++] = (int)data & 0x3FFFFFFF;
            data >>>= 30;
            avail -= 30;
            bits -= 30;
        }
    }

    private static int getMaximumDivsteps(int bits) {
        return (49 * bits + (bits < 46 ? 80 : 47)) / 17;
    }

    private static int negate30(int len30, int[] D) {
        int c = 0;
        int last = len30 - 1;
        for (int i = 0; i < last; ++i) {
            D[i] = (c -= D[i]) & 0x3FFFFFFF;
            c >>= 30;
        }
        D[last] = c -= D[last];
        return c >>= 30;
    }

    private static void updateDE30(int len30, int[] D, int[] E, int[] t, int m0Inv32, int[] M) {
        int u = t[0];
        int v = t[1];
        int q = t[2];
        int r = t[3];
        int sd = D[len30 - 1] >> 31;
        int se = E[len30 - 1] >> 31;
        int md = (u & sd) + (v & se);
        int me = (q & sd) + (r & se);
        int mi = M[0];
        int di = D[0];
        int ei = E[0];
        long cd = (long)u * (long)di + (long)v * (long)ei;
        long ce = (long)q * (long)di + (long)r * (long)ei;
        md -= m0Inv32 * (int)cd + md & 0x3FFFFFFF;
        me -= m0Inv32 * (int)ce + me & 0x3FFFFFFF;
        cd += (long)mi * (long)md;
        ce += (long)mi * (long)me;
        cd >>= 30;
        ce >>= 30;
        for (int i = 1; i < len30; ++i) {
            mi = M[i];
            di = D[i];
            ei = E[i];
            D[i - 1] = (int)(cd += (long)u * (long)di + (long)v * (long)ei + (long)mi * (long)md) & 0x3FFFFFFF;
            cd >>= 30;
            E[i - 1] = (int)(ce += (long)q * (long)di + (long)r * (long)ei + (long)mi * (long)me) & 0x3FFFFFFF;
            ce >>= 30;
        }
        D[len30 - 1] = (int)cd;
        E[len30 - 1] = (int)ce;
    }

    private static void updateFG30(int len30, int[] F2, int[] G, int[] t) {
        int u = t[0];
        int v = t[1];
        int q = t[2];
        int r = t[3];
        int fi = F2[0];
        int gi = G[0];
        long cf = (long)u * (long)fi + (long)v * (long)gi;
        long cg = (long)q * (long)fi + (long)r * (long)gi;
        cf >>= 30;
        cg >>= 30;
        for (int i = 1; i < len30; ++i) {
            fi = F2[i];
            gi = G[i];
            F2[i - 1] = (int)(cf += (long)u * (long)fi + (long)v * (long)gi) & 0x3FFFFFFF;
            cf >>= 30;
            G[i - 1] = (int)(cg += (long)q * (long)fi + (long)r * (long)gi) & 0x3FFFFFFF;
            cg >>= 30;
        }
        F2[len30 - 1] = (int)cf;
        G[len30 - 1] = (int)cg;
    }
}

