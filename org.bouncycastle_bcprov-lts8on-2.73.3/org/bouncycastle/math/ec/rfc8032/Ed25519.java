/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.math.ec.rfc7748.X25519Field;
import org.bouncycastle.math.ec.rfc8032.Codec;
import org.bouncycastle.math.ec.rfc8032.Scalar25519;
import org.bouncycastle.math.ec.rfc8032.Wnaf;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat256;

public abstract class Ed25519 {
    private static final int COORD_INTS = 8;
    private static final int POINT_BYTES = 32;
    private static final int SCALAR_INTS = 8;
    private static final int SCALAR_BYTES = 32;
    public static final int PREHASH_SIZE = 64;
    public static final int PUBLIC_KEY_SIZE = 32;
    public static final int SECRET_KEY_SIZE = 32;
    public static final int SIGNATURE_SIZE = 64;
    private static final byte[] DOM2_PREFIX = new byte[]{83, 105, 103, 69, 100, 50, 53, 53, 49, 57, 32, 110, 111, 32, 69, 100, 50, 53, 53, 49, 57, 32, 99, 111, 108, 108, 105, 115, 105, 111, 110, 115};
    private static final int[] P = new int[]{-19, -1, -1, -1, -1, -1, -1, Integer.MAX_VALUE};
    private static final int[] ORDER8_y1 = new int[]{1886001095, 1339575613, 1980447930, 258412557, -95215574, -959694548, 2013120334, 2047061138};
    private static final int[] ORDER8_y2 = new int[]{-1886001114, -1339575614, -1980447931, -258412558, 95215573, 959694547, -2013120335, 100422509};
    private static final int[] B_x = new int[]{52811034, 25909283, 8072341, 50637101, 13785486, 30858332, 20483199, 20966410, 43936626, 4379245};
    private static final int[] B_y = new int[]{40265304, 0x1999999, 0x666666, 0x3333333, 0xCCCCCC, 0x2666666, 0x1999999, 0x666666, 0x3333333, 0xCCCCCC};
    private static final int[] B128_x = new int[]{12052516, 1174424, 4087752, 38672185, 20040971, 21899680, 55468344, 20105554, 66708015, 9981791};
    private static final int[] B128_y = new int[]{66430571, 45040722, 4842939, 15895846, 18981244, 46308410, 4697481, 8903007, 53646190, 12474675};
    private static final int[] C_d = new int[]{56195235, 47411844, 25868126, 40503822, 57364, 58321048, 30416477, 31930572, 57760639, 10749657};
    private static final int[] C_d2 = new int[]{45281625, 27714825, 18181821, 0xD4141D, 114729, 49533232, 60832955, 30306712, 48412415, 4722099};
    private static final int[] C_d4 = new int[]{23454386, 55429651, 2809210, 27797563, 229458, 31957600, 54557047, 27058993, 29715967, 9444199};
    private static final int WNAF_WIDTH_128 = 4;
    private static final int WNAF_WIDTH_BASE = 6;
    private static final int PRECOMP_BLOCKS = 8;
    private static final int PRECOMP_TEETH = 4;
    private static final int PRECOMP_SPACING = 8;
    private static final int PRECOMP_RANGE = 256;
    private static final int PRECOMP_POINTS = 8;
    private static final int PRECOMP_MASK = 7;
    private static final Object PRECOMP_LOCK = new Object();
    private static PointPrecomp[] PRECOMP_BASE_WNAF = null;
    private static PointPrecomp[] PRECOMP_BASE128_WNAF = null;
    private static int[] PRECOMP_BASE_COMB = null;

    private static byte[] calculateS(byte[] r, byte[] k, byte[] s) {
        int[] t = new int[16];
        Scalar25519.decode(r, t);
        int[] u = new int[8];
        Scalar25519.decode(k, u);
        int[] v = new int[8];
        Scalar25519.decode(s, v);
        Nat256.mulAddTo(u, v, t);
        byte[] result = new byte[64];
        Codec.encode32(t, 0, t.length, result, 0);
        return Scalar25519.reduce(result);
    }

    private static boolean checkContextVar(byte[] ctx, byte phflag) {
        return ctx == null && phflag == 0 || ctx != null && ctx.length < 256;
    }

    private static int checkPoint(PointAffine p) {
        int[] t = F.create();
        int[] u = F.create();
        int[] v = F.create();
        F.sqr(p.x, u);
        F.sqr(p.y, v);
        F.mul(u, v, t);
        F.sub(v, u, v);
        F.mul(t, C_d, t);
        F.addOne(t);
        F.sub(t, v, t);
        F.normalize(t);
        return F.isZero(t);
    }

    private static int checkPoint(PointAccum p) {
        int[] t = F.create();
        int[] u = F.create();
        int[] v = F.create();
        int[] w = F.create();
        F.sqr(p.x, u);
        F.sqr(p.y, v);
        F.sqr(p.z, w);
        F.mul(u, v, t);
        F.sub(v, u, v);
        F.mul(v, w, v);
        F.sqr(w, w);
        F.mul(t, C_d, t);
        F.add(t, w, t);
        F.sub(t, v, t);
        F.normalize(t);
        return F.isZero(t);
    }

    private static boolean checkPointFullVar(byte[] p) {
        int y7;
        int t0 = y7 = Codec.decode32(p, 28) & Integer.MAX_VALUE;
        int t1 = y7 ^ P[7];
        int t2 = y7 ^ ORDER8_y1[7];
        int t3 = y7 ^ ORDER8_y2[7];
        for (int i = 6; i > 0; --i) {
            int yi = Codec.decode32(p, i * 4);
            t0 |= yi;
            t1 |= yi ^ P[i];
            t2 |= yi ^ ORDER8_y1[i];
            t3 |= yi ^ ORDER8_y2[i];
        }
        int y0 = Codec.decode32(p, 0);
        if (t0 == 0 && y0 + Integer.MIN_VALUE <= -2147483647) {
            return false;
        }
        if (t1 == 0 && y0 + Integer.MIN_VALUE >= P[0] - 1 + Integer.MIN_VALUE) {
            return false;
        }
        return (t2 |= y0 ^ ORDER8_y1[0]) != 0 & (t3 |= y0 ^ ORDER8_y2[0]) != 0;
    }

    private static boolean checkPointOrderVar(PointAffine p) {
        PointAccum r = new PointAccum();
        Ed25519.scalarMultOrderVar(p, r);
        return Ed25519.normalizeToNeutralElementVar(r);
    }

    private static boolean checkPointVar(byte[] p) {
        if ((Codec.decode32(p, 28) & Integer.MAX_VALUE) < P[7]) {
            return true;
        }
        int[] t = new int[8];
        Codec.decode32(p, 0, t, 0, 8);
        t[7] = t[7] & Integer.MAX_VALUE;
        return !Nat256.gte(t, P);
    }

    private static byte[] copy(byte[] buf, int off, int len) {
        byte[] result = new byte[len];
        System.arraycopy(buf, off, result, 0, len);
        return result;
    }

    private static Digest createDigest() {
        SHA512Digest d = new SHA512Digest();
        if (d.getDigestSize() != 64) {
            throw new IllegalStateException();
        }
        return d;
    }

    public static Digest createPrehash() {
        return Ed25519.createDigest();
    }

    private static boolean decodePointVar(byte[] p, boolean negate, PointAffine r) {
        int x_0 = (p[31] & 0x80) >>> 7;
        F.decode(p, r.y);
        int[] u = F.create();
        int[] v = F.create();
        F.sqr(r.y, u);
        F.mul(C_d, u, v);
        F.subOne(u);
        F.addOne(v);
        if (!F.sqrtRatioVar(u, v, r.x)) {
            return false;
        }
        F.normalize(r.x);
        if (x_0 == 1 && F.isZeroVar(r.x)) {
            return false;
        }
        if (negate ^ x_0 != (r.x[0] & 1)) {
            F.negate(r.x, r.x);
            F.normalize(r.x);
        }
        return true;
    }

    private static void dom2(Digest d, byte phflag, byte[] ctx) {
        int n = DOM2_PREFIX.length;
        byte[] t = new byte[n + 2 + ctx.length];
        System.arraycopy(DOM2_PREFIX, 0, t, 0, n);
        t[n] = phflag;
        t[n + 1] = (byte)ctx.length;
        System.arraycopy(ctx, 0, t, n + 2, ctx.length);
        d.update(t, 0, t.length);
    }

    private static void encodePoint(PointAffine p, byte[] r, int rOff) {
        F.encode(p.y, r, rOff);
        int n = rOff + 32 - 1;
        r[n] = (byte)(r[n] | (p.x[0] & 1) << 7);
    }

    public static void encodePublicPoint(PublicPoint publicPoint, byte[] pk, int pkOff) {
        F.encode(publicPoint.data, 10, pk, pkOff);
        int n = pkOff + 32 - 1;
        pk[n] = (byte)(pk[n] | (publicPoint.data[0] & 1) << 7);
    }

    private static int encodeResult(PointAccum p, byte[] r, int rOff) {
        PointAffine q = new PointAffine();
        Ed25519.normalizeToAffine(p, q);
        int result = Ed25519.checkPoint(q);
        Ed25519.encodePoint(q, r, rOff);
        return result;
    }

    private static PublicPoint exportPoint(PointAffine p) {
        int[] data = new int[20];
        F.copy(p.x, 0, data, 0);
        F.copy(p.y, 0, data, 10);
        return new PublicPoint(data);
    }

    public static void generatePrivateKey(SecureRandom random, byte[] k) {
        if (k.length != 32) {
            throw new IllegalArgumentException("k");
        }
        random.nextBytes(k);
    }

    public static void generatePublicKey(byte[] sk, int skOff, byte[] pk, int pkOff) {
        Digest d = Ed25519.createDigest();
        byte[] h = new byte[64];
        d.update(sk, skOff, 32);
        d.doFinal(h, 0);
        byte[] s = new byte[32];
        Ed25519.pruneScalar(h, 0, s);
        Ed25519.scalarMultBaseEncoded(s, pk, pkOff);
    }

    public static PublicPoint generatePublicKey(byte[] sk, int skOff) {
        Digest d = Ed25519.createDigest();
        byte[] h = new byte[64];
        d.update(sk, skOff, 32);
        d.doFinal(h, 0);
        byte[] s = new byte[32];
        Ed25519.pruneScalar(h, 0, s);
        PointAccum p = new PointAccum();
        Ed25519.scalarMultBase(s, p);
        PointAffine q = new PointAffine();
        Ed25519.normalizeToAffine(p, q);
        if (0 == Ed25519.checkPoint(q)) {
            throw new IllegalStateException();
        }
        return Ed25519.exportPoint(q);
    }

    private static int getWindow4(int[] x, int n) {
        int w = n >>> 3;
        int b = (n & 7) << 2;
        return x[w] >>> b & 0xF;
    }

    private static void groupCombBits(int[] n) {
        for (int i = 0; i < n.length; ++i) {
            n[i] = Interleave.shuffle2(n[i]);
        }
    }

    private static void implSign(Digest d, byte[] h, byte[] s, byte[] pk, int pkOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        if (ctx != null) {
            Ed25519.dom2(d, phflag, ctx);
        }
        d.update(h, 32, 32);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0);
        byte[] r = Scalar25519.reduce(h);
        byte[] R = new byte[32];
        Ed25519.scalarMultBaseEncoded(r, R, 0);
        if (ctx != null) {
            Ed25519.dom2(d, phflag, ctx);
        }
        d.update(R, 0, 32);
        d.update(pk, pkOff, 32);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0);
        byte[] k = Scalar25519.reduce(h);
        byte[] S = Ed25519.calculateS(r, k, s);
        System.arraycopy(R, 0, sig, sigOff, 32);
        System.arraycopy(S, 0, sig, sigOff + 32, 32);
    }

    private static void implSign(byte[] sk, int skOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        if (!Ed25519.checkContextVar(ctx, phflag)) {
            throw new IllegalArgumentException("ctx");
        }
        Digest d = Ed25519.createDigest();
        byte[] h = new byte[64];
        d.update(sk, skOff, 32);
        d.doFinal(h, 0);
        byte[] s = new byte[32];
        Ed25519.pruneScalar(h, 0, s);
        byte[] pk = new byte[32];
        Ed25519.scalarMultBaseEncoded(s, pk, 0);
        Ed25519.implSign(d, h, s, pk, 0, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    private static void implSign(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        if (!Ed25519.checkContextVar(ctx, phflag)) {
            throw new IllegalArgumentException("ctx");
        }
        Digest d = Ed25519.createDigest();
        byte[] h = new byte[64];
        d.update(sk, skOff, 32);
        d.doFinal(h, 0);
        byte[] s = new byte[32];
        Ed25519.pruneScalar(h, 0, s);
        Ed25519.implSign(d, h, s, pk, pkOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    private static boolean implVerify(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen) {
        if (!Ed25519.checkContextVar(ctx, phflag)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] R = Ed25519.copy(sig, sigOff, 32);
        byte[] S = Ed25519.copy(sig, sigOff + 32, 32);
        byte[] A = Ed25519.copy(pk, pkOff, 32);
        if (!Ed25519.checkPointVar(R)) {
            return false;
        }
        int[] nS = new int[8];
        if (!Scalar25519.checkVar(S, nS)) {
            return false;
        }
        if (!Ed25519.checkPointFullVar(A)) {
            return false;
        }
        PointAffine pR = new PointAffine();
        if (!Ed25519.decodePointVar(R, true, pR)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        if (!Ed25519.decodePointVar(A, true, pA)) {
            return false;
        }
        Digest d = Ed25519.createDigest();
        byte[] h = new byte[64];
        if (ctx != null) {
            Ed25519.dom2(d, phflag, ctx);
        }
        d.update(R, 0, 32);
        d.update(A, 0, 32);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0);
        byte[] k = Scalar25519.reduce(h);
        int[] nA = new int[8];
        Scalar25519.decode(k, nA);
        int[] v0 = new int[4];
        int[] v1 = new int[4];
        Scalar25519.reduceBasisVar(nA, v0, v1);
        Scalar25519.multiply128Var(nS, v1, nS);
        PointAccum pZ = new PointAccum();
        Ed25519.scalarMultStraus128Var(nS, v0, pA, v1, pR, pZ);
        return Ed25519.normalizeToNeutralElementVar(pZ);
    }

    private static boolean implVerify(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen) {
        if (!Ed25519.checkContextVar(ctx, phflag)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] R = Ed25519.copy(sig, sigOff, 32);
        byte[] S = Ed25519.copy(sig, sigOff + 32, 32);
        if (!Ed25519.checkPointVar(R)) {
            return false;
        }
        int[] nS = new int[8];
        if (!Scalar25519.checkVar(S, nS)) {
            return false;
        }
        PointAffine pR = new PointAffine();
        if (!Ed25519.decodePointVar(R, true, pR)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        F.negate(publicPoint.data, pA.x);
        F.copy(publicPoint.data, 10, pA.y, 0);
        byte[] A = new byte[32];
        Ed25519.encodePublicPoint(publicPoint, A, 0);
        Digest d = Ed25519.createDigest();
        byte[] h = new byte[64];
        if (ctx != null) {
            Ed25519.dom2(d, phflag, ctx);
        }
        d.update(R, 0, 32);
        d.update(A, 0, 32);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0);
        byte[] k = Scalar25519.reduce(h);
        int[] nA = new int[8];
        Scalar25519.decode(k, nA);
        int[] v0 = new int[4];
        int[] v1 = new int[4];
        Scalar25519.reduceBasisVar(nA, v0, v1);
        Scalar25519.multiply128Var(nS, v1, nS);
        PointAccum pZ = new PointAccum();
        Ed25519.scalarMultStraus128Var(nS, v0, pA, v1, pR, pZ);
        return Ed25519.normalizeToNeutralElementVar(pZ);
    }

    private static void invertDoubleZs(PointExtended[] points) {
        int count = points.length;
        int[] cs = F.createTable(count);
        int[] u = F.create();
        F.copy(points[0].z, 0, u, 0);
        F.copy(u, 0, cs, 0);
        int i = 0;
        while (++i < count) {
            F.mul(u, points[i].z, u);
            F.copy(u, 0, cs, i * 10);
        }
        F.add(u, u, u);
        F.invVar(u, u);
        --i;
        int[] t = F.create();
        while (i > 0) {
            int j = i--;
            F.copy(cs, i * 10, t, 0);
            F.mul(t, u, t);
            F.mul(u, points[j].z, u);
            F.copy(t, 0, points[j].z, 0);
        }
        F.copy(u, 0, points[0].z, 0);
    }

    private static void normalizeToAffine(PointAccum p, PointAffine r) {
        F.inv(p.z, r.y);
        F.mul(r.y, p.x, r.x);
        F.mul(r.y, p.y, r.y);
        F.normalize(r.x);
        F.normalize(r.y);
    }

    private static boolean normalizeToNeutralElementVar(PointAccum p) {
        F.normalize(p.x);
        F.normalize(p.y);
        F.normalize(p.z);
        return F.isZeroVar(p.x) && F.areEqualVar(p.y, p.z);
    }

    private static void pointAdd(PointExtended p, PointExtended q, PointExtended r, PointTemp t) {
        int[] a = r.x;
        int[] b = r.y;
        int[] c = t.r0;
        int[] d = t.r1;
        int[] e = a;
        int[] f = c;
        int[] g = d;
        int[] h = b;
        F.apm(p.y, p.x, b, a);
        F.apm(q.y, q.x, d, c);
        F.mul(a, c, a);
        F.mul(b, d, b);
        F.mul(p.t, q.t, c);
        F.mul(c, C_d2, c);
        F.add(p.z, p.z, d);
        F.mul(d, q.z, d);
        F.apm(b, a, h, e);
        F.apm(d, c, g, f);
        F.mul(e, h, r.t);
        F.mul(f, g, r.z);
        F.mul(e, f, r.x);
        F.mul(h, g, r.y);
    }

    private static void pointAdd(PointPrecomp p, PointAccum r, PointTemp t) {
        int[] a = r.x;
        int[] b = r.y;
        int[] c = t.r0;
        int[] e = r.u;
        int[] f = a;
        int[] g = b;
        int[] h = r.v;
        F.apm(r.y, r.x, b, a);
        F.mul(a, p.ymx_h, a);
        F.mul(b, p.ypx_h, b);
        F.mul(r.u, r.v, c);
        F.mul(c, p.xyd, c);
        F.apm(b, a, h, e);
        F.apm(r.z, c, g, f);
        F.mul(f, g, r.z);
        F.mul(f, e, r.x);
        F.mul(g, h, r.y);
    }

    private static void pointAdd(PointPrecompZ p, PointAccum r, PointTemp t) {
        int[] a = r.x;
        int[] b = r.y;
        int[] c = t.r0;
        int[] d = r.z;
        int[] e = r.u;
        int[] f = a;
        int[] g = b;
        int[] h = r.v;
        F.apm(r.y, r.x, b, a);
        F.mul(a, p.ymx_h, a);
        F.mul(b, p.ypx_h, b);
        F.mul(r.u, r.v, c);
        F.mul(c, p.xyd, c);
        F.mul(r.z, p.z, d);
        F.apm(b, a, h, e);
        F.apm(d, c, g, f);
        F.mul(f, g, r.z);
        F.mul(f, e, r.x);
        F.mul(g, h, r.y);
    }

    private static void pointAddVar(boolean negate, PointPrecomp p, PointAccum r, PointTemp t) {
        int[] nb;
        int[] na;
        int[] a = r.x;
        int[] b = r.y;
        int[] c = t.r0;
        int[] e = r.u;
        int[] f = a;
        int[] g = b;
        int[] h = r.v;
        if (negate) {
            na = b;
            nb = a;
        } else {
            na = a;
            nb = b;
        }
        int[] nf = na;
        int[] ng = nb;
        F.apm(r.y, r.x, b, a);
        F.mul(na, p.ymx_h, na);
        F.mul(nb, p.ypx_h, nb);
        F.mul(r.u, r.v, c);
        F.mul(c, p.xyd, c);
        F.apm(b, a, h, e);
        F.apm(r.z, c, ng, nf);
        F.mul(f, g, r.z);
        F.mul(f, e, r.x);
        F.mul(g, h, r.y);
    }

    private static void pointAddVar(boolean negate, PointPrecompZ p, PointAccum r, PointTemp t) {
        int[] nb;
        int[] na;
        int[] a = r.x;
        int[] b = r.y;
        int[] c = t.r0;
        int[] d = r.z;
        int[] e = r.u;
        int[] f = a;
        int[] g = b;
        int[] h = r.v;
        if (negate) {
            na = b;
            nb = a;
        } else {
            na = a;
            nb = b;
        }
        int[] nf = na;
        int[] ng = nb;
        F.apm(r.y, r.x, b, a);
        F.mul(na, p.ymx_h, na);
        F.mul(nb, p.ypx_h, nb);
        F.mul(r.u, r.v, c);
        F.mul(c, p.xyd, c);
        F.mul(r.z, p.z, d);
        F.apm(b, a, h, e);
        F.apm(d, c, ng, nf);
        F.mul(f, g, r.z);
        F.mul(f, e, r.x);
        F.mul(g, h, r.y);
    }

    private static void pointCopy(PointAccum p, PointExtended r) {
        F.copy(p.x, 0, r.x, 0);
        F.copy(p.y, 0, r.y, 0);
        F.copy(p.z, 0, r.z, 0);
        F.mul(p.u, p.v, r.t);
    }

    private static void pointCopy(PointAffine p, PointExtended r) {
        F.copy(p.x, 0, r.x, 0);
        F.copy(p.y, 0, r.y, 0);
        F.one(r.z);
        F.mul(p.x, p.y, r.t);
    }

    private static void pointCopy(PointExtended p, PointPrecompZ r) {
        F.apm(p.y, p.x, r.ypx_h, r.ymx_h);
        F.mul(p.t, C_d2, r.xyd);
        F.add(p.z, p.z, r.z);
    }

    private static void pointDouble(PointAccum r) {
        int[] a = r.x;
        int[] b = r.y;
        int[] c = r.z;
        int[] e = r.u;
        int[] f = a;
        int[] g = b;
        int[] h = r.v;
        F.add(r.x, r.y, e);
        F.sqr(r.x, a);
        F.sqr(r.y, b);
        F.sqr(r.z, c);
        F.add(c, c, c);
        F.apm(a, b, h, g);
        F.sqr(e, e);
        F.sub(h, e, e);
        F.add(c, g, f);
        F.carry(f);
        F.mul(f, g, r.z);
        F.mul(f, e, r.x);
        F.mul(g, h, r.y);
    }

    private static void pointLookup(int block, int index, PointPrecomp p) {
        int off = block * 8 * 3 * 10;
        for (int i = 0; i < 8; ++i) {
            int cond = (i ^ index) - 1 >> 31;
            F.cmov(cond, PRECOMP_BASE_COMB, off, p.ymx_h, 0);
            F.cmov(cond, PRECOMP_BASE_COMB, off += 10, p.ypx_h, 0);
            F.cmov(cond, PRECOMP_BASE_COMB, off += 10, p.xyd, 0);
            off += 10;
        }
    }

    private static void pointLookupZ(int[] x, int n, int[] table, PointPrecompZ r) {
        int w = Ed25519.getWindow4(x, n);
        int sign = w >>> 3 ^ 1;
        int abs = (w ^ -sign) & 7;
        int off = 0;
        for (int i = 0; i < 8; ++i) {
            int cond = (i ^ abs) - 1 >> 31;
            F.cmov(cond, table, off, r.ymx_h, 0);
            F.cmov(cond, table, off += 10, r.ypx_h, 0);
            F.cmov(cond, table, off += 10, r.xyd, 0);
            F.cmov(cond, table, off += 10, r.z, 0);
            off += 10;
        }
        F.cswap(sign, r.ymx_h, r.ypx_h);
        F.cnegate(sign, r.xyd);
    }

    private static void pointPrecompute(PointAffine p, PointExtended[] points, int pointsOff, int pointsLen, PointTemp t) {
        points[pointsOff] = new PointExtended();
        Ed25519.pointCopy(p, points[pointsOff]);
        PointExtended d = new PointExtended();
        Ed25519.pointAdd(points[pointsOff], points[pointsOff], d, t);
        for (int i = 1; i < pointsLen; ++i) {
            PointExtended pointExtended = new PointExtended();
            points[pointsOff + i] = pointExtended;
            Ed25519.pointAdd(points[pointsOff + i - 1], d, pointExtended, t);
        }
    }

    private static int[] pointPrecomputeZ(PointAffine p, int count, PointTemp t) {
        PointExtended q = new PointExtended();
        Ed25519.pointCopy(p, q);
        PointExtended d = new PointExtended();
        Ed25519.pointAdd(q, q, d, t);
        PointPrecompZ r = new PointPrecompZ();
        int[] table = F.createTable(count * 4);
        int off = 0;
        int i = 0;
        while (true) {
            Ed25519.pointCopy(q, r);
            F.copy(r.ymx_h, 0, table, off);
            F.copy(r.ypx_h, 0, table, off += 10);
            F.copy(r.xyd, 0, table, off += 10);
            F.copy(r.z, 0, table, off += 10);
            off += 10;
            if (++i == count) break;
            Ed25519.pointAdd(q, d, q, t);
        }
        return table;
    }

    private static void pointPrecomputeZ(PointAffine p, PointPrecompZ[] points, int count, PointTemp t) {
        PointExtended q = new PointExtended();
        Ed25519.pointCopy(p, q);
        PointExtended d = new PointExtended();
        Ed25519.pointAdd(q, q, d, t);
        int i = 0;
        while (true) {
            PointPrecompZ r = points[i] = new PointPrecompZ();
            Ed25519.pointCopy(q, r);
            if (++i == count) break;
            Ed25519.pointAdd(q, d, q, t);
        }
    }

    private static void pointSetNeutral(PointAccum p) {
        F.zero(p.x);
        F.one(p.y);
        F.one(p.z);
        F.zero(p.u);
        F.one(p.v);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void precompute() {
        Object object = PRECOMP_LOCK;
        synchronized (object) {
            PointExtended q;
            int i;
            if (PRECOMP_BASE_COMB != null) {
                return;
            }
            int wnafPoints = 16;
            int combPoints = 64;
            int totalPoints = wnafPoints * 2 + combPoints;
            PointExtended[] points = new PointExtended[totalPoints];
            PointTemp t = new PointTemp();
            PointAffine B = new PointAffine();
            F.copy(B_x, 0, B.x, 0);
            F.copy(B_y, 0, B.y, 0);
            Ed25519.pointPrecompute(B, points, 0, wnafPoints, t);
            PointAffine B128 = new PointAffine();
            F.copy(B128_x, 0, B128.x, 0);
            F.copy(B128_y, 0, B128.y, 0);
            Ed25519.pointPrecompute(B128, points, wnafPoints, wnafPoints, t);
            PointAccum p = new PointAccum();
            F.copy(B_x, 0, p.x, 0);
            F.copy(B_y, 0, p.y, 0);
            F.one(p.z);
            F.copy(p.x, 0, p.u, 0);
            F.copy(p.y, 0, p.v, 0);
            int pointsIndex = wnafPoints * 2;
            PointExtended[] toothPowers = new PointExtended[4];
            for (int tooth = 0; tooth < 4; ++tooth) {
                toothPowers[tooth] = new PointExtended();
            }
            PointExtended u = new PointExtended();
            for (int block = 0; block < 8; ++block) {
                int tooth;
                int n = pointsIndex++;
                PointExtended pointExtended = new PointExtended();
                points[n] = pointExtended;
                PointExtended sum = pointExtended;
                for (tooth = 0; tooth < 4; ++tooth) {
                    if (tooth == 0) {
                        Ed25519.pointCopy(p, sum);
                    } else {
                        Ed25519.pointCopy(p, u);
                        Ed25519.pointAdd(sum, u, sum, t);
                    }
                    Ed25519.pointDouble(p);
                    Ed25519.pointCopy(p, toothPowers[tooth]);
                    if (block + tooth == 10) continue;
                    for (int spacing = 1; spacing < 8; ++spacing) {
                        Ed25519.pointDouble(p);
                    }
                }
                F.negate(sum.x, sum.x);
                F.negate(sum.t, sum.t);
                for (tooth = 0; tooth < 3; ++tooth) {
                    int size = 1 << tooth;
                    int j = 0;
                    while (j < size) {
                        points[pointsIndex] = new PointExtended();
                        Ed25519.pointAdd(points[pointsIndex - size], toothPowers[tooth], points[pointsIndex], t);
                        ++j;
                        ++pointsIndex;
                    }
                }
            }
            Ed25519.invertDoubleZs(points);
            PRECOMP_BASE_WNAF = new PointPrecomp[wnafPoints];
            for (i = 0; i < wnafPoints; ++i) {
                q = points[i];
                PointPrecomp r = Ed25519.PRECOMP_BASE_WNAF[i] = new PointPrecomp();
                F.mul(q.x, q.z, q.x);
                F.mul(q.y, q.z, q.y);
                F.apm(q.y, q.x, r.ypx_h, r.ymx_h);
                F.mul(q.x, q.y, r.xyd);
                F.mul(r.xyd, C_d4, r.xyd);
                F.normalize(r.ymx_h);
                F.normalize(r.ypx_h);
                F.normalize(r.xyd);
            }
            PRECOMP_BASE128_WNAF = new PointPrecomp[wnafPoints];
            for (i = 0; i < wnafPoints; ++i) {
                q = points[wnafPoints + i];
                PointPrecomp r = Ed25519.PRECOMP_BASE128_WNAF[i] = new PointPrecomp();
                F.mul(q.x, q.z, q.x);
                F.mul(q.y, q.z, q.y);
                F.apm(q.y, q.x, r.ypx_h, r.ymx_h);
                F.mul(q.x, q.y, r.xyd);
                F.mul(r.xyd, C_d4, r.xyd);
                F.normalize(r.ymx_h);
                F.normalize(r.ypx_h);
                F.normalize(r.xyd);
            }
            PRECOMP_BASE_COMB = F.createTable(combPoints * 3);
            PointPrecomp s = new PointPrecomp();
            int off = 0;
            for (int i2 = wnafPoints * 2; i2 < totalPoints; ++i2) {
                PointExtended q2 = points[i2];
                F.mul(q2.x, q2.z, q2.x);
                F.mul(q2.y, q2.z, q2.y);
                F.apm(q2.y, q2.x, s.ypx_h, s.ymx_h);
                F.mul(q2.x, q2.y, s.xyd);
                F.mul(s.xyd, C_d4, s.xyd);
                F.normalize(s.ymx_h);
                F.normalize(s.ypx_h);
                F.normalize(s.xyd);
                F.copy(s.ymx_h, 0, PRECOMP_BASE_COMB, off);
                F.copy(s.ypx_h, 0, PRECOMP_BASE_COMB, off += 10);
                F.copy(s.xyd, 0, PRECOMP_BASE_COMB, off += 10);
                off += 10;
            }
        }
    }

    private static void pruneScalar(byte[] n, int nOff, byte[] r) {
        System.arraycopy(n, nOff, r, 0, 32);
        r[0] = (byte)(r[0] & 0xF8);
        r[31] = (byte)(r[31] & 0x7F);
        r[31] = (byte)(r[31] | 0x40);
    }

    private static void scalarMult(byte[] k, PointAffine p, PointAccum r) {
        int[] n = new int[8];
        Scalar25519.decode(k, n);
        Scalar25519.toSignedDigits(256, n, n);
        PointPrecompZ q = new PointPrecompZ();
        PointTemp t = new PointTemp();
        int[] table = Ed25519.pointPrecomputeZ(p, 8, t);
        Ed25519.pointSetNeutral(r);
        int w = 63;
        block0: while (true) {
            Ed25519.pointLookupZ(n, w, table, q);
            Ed25519.pointAdd(q, r, t);
            if (--w < 0) break;
            int i = 0;
            while (true) {
                if (i >= 4) continue block0;
                Ed25519.pointDouble(r);
                ++i;
            }
            break;
        }
    }

    private static void scalarMultBase(byte[] k, PointAccum r) {
        Ed25519.precompute();
        int[] n = new int[8];
        Scalar25519.decode(k, n);
        Scalar25519.toSignedDigits(256, n, n);
        Ed25519.groupCombBits(n);
        PointPrecomp p = new PointPrecomp();
        PointTemp t = new PointTemp();
        Ed25519.pointSetNeutral(r);
        int resultSign = 0;
        int cOff = 28;
        while (true) {
            for (int block = 0; block < 8; ++block) {
                int w = n[block] >>> cOff;
                int sign = w >>> 3 & 1;
                int abs = (w ^ -sign) & 7;
                Ed25519.pointLookup(block, abs, p);
                F.cnegate(resultSign ^ sign, r.x);
                F.cnegate(resultSign ^ sign, r.u);
                resultSign = sign;
                Ed25519.pointAdd(p, r, t);
            }
            if ((cOff -= 4) < 0) break;
            Ed25519.pointDouble(r);
        }
        F.cnegate(resultSign, r.x);
        F.cnegate(resultSign, r.u);
    }

    private static void scalarMultBaseEncoded(byte[] k, byte[] r, int rOff) {
        PointAccum p = new PointAccum();
        Ed25519.scalarMultBase(k, p);
        if (0 == Ed25519.encodeResult(p, r, rOff)) {
            throw new IllegalStateException();
        }
    }

    public static void scalarMultBaseYZ(X25519.Friend friend, byte[] k, int kOff, int[] y, int[] z) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by X25519");
        }
        byte[] n = new byte[32];
        Ed25519.pruneScalar(k, kOff, n);
        PointAccum p = new PointAccum();
        Ed25519.scalarMultBase(n, p);
        if (0 == Ed25519.checkPoint(p)) {
            throw new IllegalStateException();
        }
        F.copy(p.y, 0, y, 0);
        F.copy(p.z, 0, z, 0);
    }

    private static void scalarMultOrderVar(PointAffine p, PointAccum r) {
        byte[] ws_p = new byte[253];
        Scalar25519.getOrderWnafVar(4, ws_p);
        int count = 4;
        PointPrecompZ[] tp = new PointPrecompZ[count];
        PointTemp t = new PointTemp();
        Ed25519.pointPrecomputeZ(p, tp, count, t);
        Ed25519.pointSetNeutral(r);
        int bit = 252;
        while (true) {
            byte wp;
            if ((wp = ws_p[bit]) != 0) {
                int index = wp >> 1 ^ wp >> 31;
                Ed25519.pointAddVar(wp < 0, tp[index], r, t);
            }
            if (--bit < 0) break;
            Ed25519.pointDouble(r);
        }
    }

    private static void scalarMultStraus128Var(int[] nb, int[] np, PointAffine p, int[] nq, PointAffine q, PointAccum r) {
        Ed25519.precompute();
        byte[] ws_b = new byte[256];
        byte[] ws_p = new byte[128];
        byte[] ws_q = new byte[128];
        Wnaf.getSignedVar(nb, 6, ws_b);
        Wnaf.getSignedVar(np, 4, ws_p);
        Wnaf.getSignedVar(nq, 4, ws_q);
        int count = 4;
        PointPrecompZ[] tp = new PointPrecompZ[count];
        PointPrecompZ[] tq = new PointPrecompZ[count];
        PointTemp t = new PointTemp();
        Ed25519.pointPrecomputeZ(p, tp, count, t);
        Ed25519.pointPrecomputeZ(q, tq, count, t);
        Ed25519.pointSetNeutral(r);
        int bit = 128;
        while (--bit >= 0) {
            byte wq;
            byte wp;
            byte wb128;
            byte wb = ws_b[bit];
            if (wb != 0) {
                int index = wb >> 1 ^ wb >> 31;
                Ed25519.pointAddVar(wb < 0, PRECOMP_BASE_WNAF[index], r, t);
            }
            if ((wb128 = ws_b[128 + bit]) != 0) {
                int index = wb128 >> 1 ^ wb128 >> 31;
                Ed25519.pointAddVar(wb128 < 0, PRECOMP_BASE128_WNAF[index], r, t);
            }
            if ((wp = ws_p[bit]) != 0) {
                int index = wp >> 1 ^ wp >> 31;
                Ed25519.pointAddVar(wp < 0, tp[index], r, t);
            }
            if ((wq = ws_q[bit]) != 0) {
                int index = wq >> 1 ^ wq >> 31;
                Ed25519.pointAddVar(wq < 0, tq[index], r, t);
            }
            Ed25519.pointDouble(r);
        }
        Ed25519.pointDouble(r);
        Ed25519.pointDouble(r);
    }

    public static void sign(byte[] sk, int skOff, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        byte[] ctx = null;
        byte phflag = 0;
        Ed25519.implSign(sk, skOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    public static void sign(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        byte[] ctx = null;
        byte phflag = 0;
        Ed25519.implSign(sk, skOff, pk, pkOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    public static void sign(byte[] sk, int skOff, byte[] ctx, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        byte phflag = 0;
        Ed25519.implSign(sk, skOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    public static void sign(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        byte phflag = 0;
        Ed25519.implSign(sk, skOff, pk, pkOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] ctx, byte[] ph, int phOff, byte[] sig, int sigOff) {
        byte phflag = 1;
        Ed25519.implSign(sk, skOff, ctx, phflag, ph, phOff, 64, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, byte[] ph, int phOff, byte[] sig, int sigOff) {
        byte phflag = 1;
        Ed25519.implSign(sk, skOff, pk, pkOff, ctx, phflag, ph, phOff, 64, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] ctx, Digest ph, byte[] sig, int sigOff) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        Ed25519.implSign(sk, skOff, ctx, phflag, m, 0, m.length, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, Digest ph, byte[] sig, int sigOff) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        Ed25519.implSign(sk, skOff, pk, pkOff, ctx, phflag, m, 0, m.length, sig, sigOff);
    }

    public static boolean validatePublicKeyFull(byte[] pk, int pkOff) {
        byte[] A = Ed25519.copy(pk, pkOff, 32);
        if (!Ed25519.checkPointFullVar(A)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        if (!Ed25519.decodePointVar(A, false, pA)) {
            return false;
        }
        return Ed25519.checkPointOrderVar(pA);
    }

    public static PublicPoint validatePublicKeyFullExport(byte[] pk, int pkOff) {
        byte[] A = Ed25519.copy(pk, pkOff, 32);
        if (!Ed25519.checkPointFullVar(A)) {
            return null;
        }
        PointAffine pA = new PointAffine();
        if (!Ed25519.decodePointVar(A, false, pA)) {
            return null;
        }
        if (!Ed25519.checkPointOrderVar(pA)) {
            return null;
        }
        return Ed25519.exportPoint(pA);
    }

    public static boolean validatePublicKeyPartial(byte[] pk, int pkOff) {
        byte[] A = Ed25519.copy(pk, pkOff, 32);
        if (!Ed25519.checkPointFullVar(A)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        return Ed25519.decodePointVar(A, false, pA);
    }

    public static PublicPoint validatePublicKeyPartialExport(byte[] pk, int pkOff) {
        byte[] A = Ed25519.copy(pk, pkOff, 32);
        if (!Ed25519.checkPointFullVar(A)) {
            return null;
        }
        PointAffine pA = new PointAffine();
        if (!Ed25519.decodePointVar(A, false, pA)) {
            return null;
        }
        return Ed25519.exportPoint(pA);
    }

    public static boolean verify(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] m, int mOff, int mLen) {
        byte[] ctx = null;
        byte phflag = 0;
        return Ed25519.implVerify(sig, sigOff, pk, pkOff, ctx, phflag, m, mOff, mLen);
    }

    public static boolean verify(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] m, int mOff, int mLen) {
        byte[] ctx = null;
        byte phflag = 0;
        return Ed25519.implVerify(sig, sigOff, publicPoint, ctx, phflag, m, mOff, mLen);
    }

    public static boolean verify(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, byte[] m, int mOff, int mLen) {
        byte phflag = 0;
        return Ed25519.implVerify(sig, sigOff, pk, pkOff, ctx, phflag, m, mOff, mLen);
    }

    public static boolean verify(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, byte[] m, int mOff, int mLen) {
        byte phflag = 0;
        return Ed25519.implVerify(sig, sigOff, publicPoint, ctx, phflag, m, mOff, mLen);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, byte[] ph, int phOff) {
        byte phflag = 1;
        return Ed25519.implVerify(sig, sigOff, pk, pkOff, ctx, phflag, ph, phOff, 64);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, byte[] ph, int phOff) {
        byte phflag = 1;
        return Ed25519.implVerify(sig, sigOff, publicPoint, ctx, phflag, ph, phOff, 64);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, Digest ph) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        return Ed25519.implVerify(sig, sigOff, pk, pkOff, ctx, phflag, m, 0, m.length);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, Digest ph) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        return Ed25519.implVerify(sig, sigOff, publicPoint, ctx, phflag, m, 0, m.length);
    }

    public static final class Algorithm {
        public static final int Ed25519 = 0;
        public static final int Ed25519ctx = 1;
        public static final int Ed25519ph = 2;
    }

    private static class F
    extends X25519Field {
        private F() {
        }
    }

    private static class PointAccum {
        int[] x = F.create();
        int[] y = F.create();
        int[] z = F.create();
        int[] u = F.create();
        int[] v = F.create();

        private PointAccum() {
        }
    }

    private static class PointAffine {
        int[] x = F.create();
        int[] y = F.create();

        private PointAffine() {
        }
    }

    private static class PointExtended {
        int[] x = F.create();
        int[] y = F.create();
        int[] z = F.create();
        int[] t = F.create();

        private PointExtended() {
        }
    }

    private static class PointPrecomp {
        int[] ymx_h = F.create();
        int[] ypx_h = F.create();
        int[] xyd = F.create();

        private PointPrecomp() {
        }
    }

    private static class PointPrecompZ {
        int[] ymx_h = F.create();
        int[] ypx_h = F.create();
        int[] xyd = F.create();
        int[] z = F.create();

        private PointPrecompZ() {
        }
    }

    private static class PointTemp {
        int[] r0 = F.create();
        int[] r1 = F.create();

        private PointTemp() {
        }
    }

    public static final class PublicPoint {
        final int[] data;

        PublicPoint(int[] data) {
            this.data = data;
        }
    }
}

