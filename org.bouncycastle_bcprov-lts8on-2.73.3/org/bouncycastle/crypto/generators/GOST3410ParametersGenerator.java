/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410ValidationParameters;
import org.bouncycastle.util.BigIntegers;

public class GOST3410ParametersGenerator {
    private int size;
    private int typeproc;
    private SecureRandom init_random;
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final BigInteger TWO = BigInteger.valueOf(2L);

    public void init(int size, int typeproc, SecureRandom random) {
        this.size = size;
        this.typeproc = typeproc;
        this.init_random = random;
    }

    private int procedure_A(int x0, int c, BigInteger[] pq, int size) {
        while (x0 < 0 || x0 > 65536) {
            x0 = this.init_random.nextInt() / 32768;
        }
        while (c < 0 || c > 65536 || c / 2 == 0) {
            c = this.init_random.nextInt() / 32768 + 1;
        }
        BigInteger C = new BigInteger(Integer.toString(c));
        BigInteger constA16 = new BigInteger("19381");
        BigInteger[] y = new BigInteger[]{new BigInteger(Integer.toString(x0))};
        int[] t = new int[]{size};
        int s = 0;
        int i = 0;
        while (t[i] >= 17) {
            int[] tmp_t = new int[t.length + 1];
            System.arraycopy(t, 0, tmp_t, 0, t.length);
            t = new int[tmp_t.length];
            System.arraycopy(tmp_t, 0, t, 0, tmp_t.length);
            t[i + 1] = t[i] / 2;
            s = i + 1;
            ++i;
        }
        BigInteger[] p = new BigInteger[s + 1];
        p[s] = new BigInteger("8003", 16);
        int m = s - 1;
        for (int i2 = 0; i2 < s; ++i2) {
            int rm = t[m] / 16;
            block4: while (true) {
                BigInteger[] tmp_y = new BigInteger[y.length];
                System.arraycopy(y, 0, tmp_y, 0, y.length);
                y = new BigInteger[rm + 1];
                System.arraycopy(tmp_y, 0, y, 0, tmp_y.length);
                for (int j = 0; j < rm; ++j) {
                    y[j + 1] = y[j].multiply(constA16).add(C).mod(TWO.pow(16));
                }
                BigInteger Ym = new BigInteger("0");
                for (int j = 0; j < rm; ++j) {
                    Ym = Ym.add(y[j].multiply(TWO.pow(16 * j)));
                }
                y[0] = y[rm];
                BigInteger N = TWO.pow(t[m] - 1).divide(p[m + 1]).add(TWO.pow(t[m] - 1).multiply(Ym).divide(p[m + 1].multiply(TWO.pow(16 * rm))));
                if (N.mod(TWO).compareTo(ONE) == 0) {
                    N = N.add(ONE);
                }
                int k = 0;
                while (true) {
                    p[m] = p[m + 1].multiply(N.add(BigInteger.valueOf(k))).add(ONE);
                    if (p[m].compareTo(TWO.pow(t[m])) == 1) continue block4;
                    if (TWO.modPow(p[m + 1].multiply(N.add(BigInteger.valueOf(k))), p[m]).compareTo(ONE) == 0 && TWO.modPow(N.add(BigInteger.valueOf(k)), p[m]).compareTo(ONE) != 0) break block4;
                    k += 2;
                }
                break;
            }
            if (--m >= 0) continue;
            pq[0] = p[0];
            pq[1] = p[1];
            return y[0].intValue();
        }
        return y[0].intValue();
    }

    private long procedure_Aa(long x0, long c, BigInteger[] pq, int size) {
        while (x0 < 0L || x0 > 0x100000000L) {
            x0 = this.init_random.nextInt() * 2;
        }
        while (c < 0L || c > 0x100000000L || c / 2L == 0L) {
            c = this.init_random.nextInt() * 2 + 1;
        }
        BigInteger C = new BigInteger(Long.toString(c));
        BigInteger constA32 = new BigInteger("97781173");
        BigInteger[] y = new BigInteger[]{new BigInteger(Long.toString(x0))};
        int[] t = new int[]{size};
        int s = 0;
        int i = 0;
        while (t[i] >= 33) {
            int[] tmp_t = new int[t.length + 1];
            System.arraycopy(t, 0, tmp_t, 0, t.length);
            t = new int[tmp_t.length];
            System.arraycopy(tmp_t, 0, t, 0, tmp_t.length);
            t[i + 1] = t[i] / 2;
            s = i + 1;
            ++i;
        }
        BigInteger[] p = new BigInteger[s + 1];
        p[s] = new BigInteger("8000000B", 16);
        int m = s - 1;
        for (int i2 = 0; i2 < s; ++i2) {
            int rm = t[m] / 32;
            block4: while (true) {
                BigInteger[] tmp_y = new BigInteger[y.length];
                System.arraycopy(y, 0, tmp_y, 0, y.length);
                y = new BigInteger[rm + 1];
                System.arraycopy(tmp_y, 0, y, 0, tmp_y.length);
                for (int j = 0; j < rm; ++j) {
                    y[j + 1] = y[j].multiply(constA32).add(C).mod(TWO.pow(32));
                }
                BigInteger Ym = new BigInteger("0");
                for (int j = 0; j < rm; ++j) {
                    Ym = Ym.add(y[j].multiply(TWO.pow(32 * j)));
                }
                y[0] = y[rm];
                BigInteger N = TWO.pow(t[m] - 1).divide(p[m + 1]).add(TWO.pow(t[m] - 1).multiply(Ym).divide(p[m + 1].multiply(TWO.pow(32 * rm))));
                if (N.mod(TWO).compareTo(ONE) == 0) {
                    N = N.add(ONE);
                }
                int k = 0;
                while (true) {
                    p[m] = p[m + 1].multiply(N.add(BigInteger.valueOf(k))).add(ONE);
                    if (p[m].compareTo(TWO.pow(t[m])) == 1) continue block4;
                    if (TWO.modPow(p[m + 1].multiply(N.add(BigInteger.valueOf(k))), p[m]).compareTo(ONE) == 0 && TWO.modPow(N.add(BigInteger.valueOf(k)), p[m]).compareTo(ONE) != 0) break block4;
                    k += 2;
                }
                break;
            }
            if (--m >= 0) continue;
            pq[0] = p[0];
            pq[1] = p[1];
            return y[0].longValue();
        }
        return y[0].longValue();
    }

    /*
     * Unable to fully structure code
     */
    private void procedure_B(int x0, int c, BigInteger[] pq) {
        while (x0 < 0 || x0 > 65536) {
            x0 = this.init_random.nextInt() / 32768;
        }
        while (c < 0 || c > 65536 || c / 2 == 0) {
            c = this.init_random.nextInt() / 32768 + 1;
        }
        qp = new BigInteger[2];
        q = null;
        Q = null;
        p = null;
        C = new BigInteger(Integer.toString(c));
        constA16 = new BigInteger("19381");
        x0 = this.procedure_A(x0, c, qp, 256);
        q = qp[0];
        x0 = this.procedure_A(x0, c, qp, 512);
        Q = qp[0];
        y = new BigInteger[65];
        y[0] = new BigInteger(Integer.toString(x0));
        tp = 1024;
        while (true) {
            for (j = 0; j < 64; ++j) {
                y[j + 1] = y[j].multiply(constA16).add(C).mod(GOST3410ParametersGenerator.TWO.pow(16));
            }
            Y = new BigInteger("0");
            for (j = 0; j < 64; ++j) {
                Y = Y.add(y[j].multiply(GOST3410ParametersGenerator.TWO.pow(16 * j)));
            }
            y[0] = y[64];
            N = GOST3410ParametersGenerator.TWO.pow(tp - 1).divide(q.multiply(Q)).add(GOST3410ParametersGenerator.TWO.pow(tp - 1).multiply(Y).divide(q.multiply(Q).multiply(GOST3410ParametersGenerator.TWO.pow(1024))));
            if (N.mod(GOST3410ParametersGenerator.TWO).compareTo(GOST3410ParametersGenerator.ONE) == 0) {
                N = N.add(GOST3410ParametersGenerator.ONE);
            }
            k = 0;
            while (true) {
                if ((p = q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))).add(GOST3410ParametersGenerator.ONE)).compareTo(GOST3410ParametersGenerator.TWO.pow(tp)) == 1) ** continue;
                if (GOST3410ParametersGenerator.TWO.modPow(q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))), p).compareTo(GOST3410ParametersGenerator.ONE) == 0 && GOST3410ParametersGenerator.TWO.modPow(q.multiply(N.add(BigInteger.valueOf(k))), p).compareTo(GOST3410ParametersGenerator.ONE) != 0) {
                    pq[0] = p;
                    pq[1] = q;
                    return;
                }
                k += 2;
            }
            break;
        }
    }

    /*
     * Unable to fully structure code
     */
    private void procedure_Bb(long x0, long c, BigInteger[] pq) {
        while (x0 < 0L || x0 > 0x100000000L) {
            x0 = this.init_random.nextInt() * 2;
        }
        while (c < 0L || c > 0x100000000L || c / 2L == 0L) {
            c = this.init_random.nextInt() * 2 + 1;
        }
        qp = new BigInteger[2];
        q = null;
        Q = null;
        p = null;
        C = new BigInteger(Long.toString(c));
        constA32 = new BigInteger("97781173");
        x0 = this.procedure_Aa(x0, c, qp, 256);
        q = qp[0];
        x0 = this.procedure_Aa(x0, c, qp, 512);
        Q = qp[0];
        y = new BigInteger[33];
        y[0] = new BigInteger(Long.toString(x0));
        tp = 1024;
        while (true) {
            for (j = 0; j < 32; ++j) {
                y[j + 1] = y[j].multiply(constA32).add(C).mod(GOST3410ParametersGenerator.TWO.pow(32));
            }
            Y = new BigInteger("0");
            for (j = 0; j < 32; ++j) {
                Y = Y.add(y[j].multiply(GOST3410ParametersGenerator.TWO.pow(32 * j)));
            }
            y[0] = y[32];
            N = GOST3410ParametersGenerator.TWO.pow(tp - 1).divide(q.multiply(Q)).add(GOST3410ParametersGenerator.TWO.pow(tp - 1).multiply(Y).divide(q.multiply(Q).multiply(GOST3410ParametersGenerator.TWO.pow(1024))));
            if (N.mod(GOST3410ParametersGenerator.TWO).compareTo(GOST3410ParametersGenerator.ONE) == 0) {
                N = N.add(GOST3410ParametersGenerator.ONE);
            }
            k = 0;
            while (true) {
                if ((p = q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))).add(GOST3410ParametersGenerator.ONE)).compareTo(GOST3410ParametersGenerator.TWO.pow(tp)) == 1) ** continue;
                if (GOST3410ParametersGenerator.TWO.modPow(q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))), p).compareTo(GOST3410ParametersGenerator.ONE) == 0 && GOST3410ParametersGenerator.TWO.modPow(q.multiply(N.add(BigInteger.valueOf(k))), p).compareTo(GOST3410ParametersGenerator.ONE) != 0) {
                    pq[0] = p;
                    pq[1] = q;
                    return;
                }
                k += 2;
            }
            break;
        }
    }

    private BigInteger procedure_C(BigInteger p, BigInteger q) {
        BigInteger a;
        BigInteger d;
        BigInteger pSub1 = p.subtract(ONE);
        BigInteger pSub1DivQ = pSub1.divide(q);
        int length = p.bitLength();
        while ((d = BigIntegers.createRandomBigInteger(length, this.init_random)).compareTo(ONE) <= 0 || d.compareTo(pSub1) >= 0 || (a = d.modPow(pSub1DivQ, p)).compareTo(ONE) == 0) {
        }
        return a;
    }

    public GOST3410Parameters generateParameters() {
        BigInteger[] pq = new BigInteger[2];
        BigInteger q = null;
        BigInteger p = null;
        BigInteger a = null;
        if (this.typeproc == 1) {
            int x0 = this.init_random.nextInt();
            int c = this.init_random.nextInt();
            switch (this.size) {
                case 512: {
                    this.procedure_A(x0, c, pq, 512);
                    break;
                }
                case 1024: {
                    this.procedure_B(x0, c, pq);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Ooops! key size 512 or 1024 bit.");
                }
            }
            p = pq[0];
            q = pq[1];
            a = this.procedure_C(p, q);
            return new GOST3410Parameters(p, q, a, new GOST3410ValidationParameters(x0, c));
        }
        long x0L = this.init_random.nextLong();
        long cL = this.init_random.nextLong();
        switch (this.size) {
            case 512: {
                this.procedure_Aa(x0L, cL, pq, 512);
                break;
            }
            case 1024: {
                this.procedure_Bb(x0L, cL, pq);
                break;
            }
            default: {
                throw new IllegalStateException("Ooops! key size 512 or 1024 bit.");
            }
        }
        p = pq[0];
        q = pq[1];
        a = this.procedure_C(p, q);
        return new GOST3410Parameters(p, q, a, new GOST3410ValidationParameters(x0L, cL));
    }
}

