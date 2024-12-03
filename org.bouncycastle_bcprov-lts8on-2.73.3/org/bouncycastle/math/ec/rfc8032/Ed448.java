/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.math.ec.rfc7748.X448;
import org.bouncycastle.math.ec.rfc7748.X448Field;
import org.bouncycastle.math.ec.rfc8032.Codec;
import org.bouncycastle.math.ec.rfc8032.Scalar448;
import org.bouncycastle.math.ec.rfc8032.Wnaf;
import org.bouncycastle.math.raw.Nat;

public abstract class Ed448 {
    private static final int COORD_INTS = 14;
    private static final int POINT_BYTES = 57;
    private static final int SCALAR_INTS = 14;
    private static final int SCALAR_BYTES = 57;
    public static final int PREHASH_SIZE = 64;
    public static final int PUBLIC_KEY_SIZE = 57;
    public static final int SECRET_KEY_SIZE = 57;
    public static final int SIGNATURE_SIZE = 114;
    private static final byte[] DOM4_PREFIX = new byte[]{83, 105, 103, 69, 100, 52, 52, 56};
    private static final int[] P = new int[]{-1, -1, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1};
    private static final int[] B_x = new int[]{118276190, 40534716, 9670182, 135141552, 85017403, 259173222, 68333082, 171784774, 174973732, 15824510, 73756743, 57518561, 94773951, 248652241, 107736333, 82941708};
    private static final int[] B_y = new int[]{36764180, 8885695, 130592152, 20104429, 163904957, 30304195, 121295871, 5901357, 125344798, 171541512, 175338348, 209069246, 3626697, 38307682, 24032956, 110359655};
    private static final int[] B225_x = new int[]{110141154, 30892124, 160820362, 264558960, 217232225, 47722141, 19029845, 8326902, 183409749, 170134547, 90340180, 222600478, 61097333, 7431335, 198491505, 102372861};
    private static final int[] B225_y = new int[]{221945828, 50763449, 132637478, 109250759, 216053960, 61612587, 50649998, 138339097, 98949899, 248139835, 186410297, 126520782, 47339196, 78164062, 198835543, 169622712};
    private static final int C_d = -39081;
    private static final int WNAF_WIDTH_225 = 5;
    private static final int WNAF_WIDTH_BASE = 7;
    private static final int PRECOMP_BLOCKS = 5;
    private static final int PRECOMP_TEETH = 5;
    private static final int PRECOMP_SPACING = 18;
    private static final int PRECOMP_RANGE = 450;
    private static final int PRECOMP_POINTS = 16;
    private static final int PRECOMP_MASK = 15;
    private static final Object PRECOMP_LOCK = new Object();
    private static PointAffine[] PRECOMP_BASE_WNAF = null;
    private static PointAffine[] PRECOMP_BASE225_WNAF = null;
    private static int[] PRECOMP_BASE_COMB = null;

    private static byte[] calculateS(byte[] r, byte[] k, byte[] s) {
        int[] t = new int[28];
        Scalar448.decode(r, t);
        int[] u = new int[14];
        Scalar448.decode(k, u);
        int[] v = new int[14];
        Scalar448.decode(s, v);
        Nat.mulAddTo(14, u, v, t);
        byte[] result = new byte[114];
        Codec.encode32(t, 0, t.length, result, 0);
        return Scalar448.reduce(result);
    }

    private static boolean checkContextVar(byte[] ctx) {
        return ctx != null && ctx.length < 256;
    }

    private static int checkPoint(PointAffine p) {
        int[] t = F.create();
        int[] u = F.create();
        int[] v = F.create();
        F.sqr(p.x, u);
        F.sqr(p.y, v);
        F.mul(u, v, t);
        F.add(u, v, u);
        F.mul(t, 39081, t);
        F.subOne(t);
        F.add(t, u, t);
        F.normalize(t);
        return F.isZero(t);
    }

    private static int checkPoint(PointProjective p) {
        int[] t = F.create();
        int[] u = F.create();
        int[] v = F.create();
        int[] w = F.create();
        F.sqr(p.x, u);
        F.sqr(p.y, v);
        F.sqr(p.z, w);
        F.mul(u, v, t);
        F.add(u, v, u);
        F.mul(u, w, u);
        F.sqr(w, w);
        F.mul(t, 39081, t);
        F.sub(t, w, t);
        F.add(t, u, t);
        F.normalize(t);
        return F.isZero(t);
    }

    private static boolean checkPointFullVar(byte[] p) {
        int y13;
        if ((p[56] & 0x7F) != 0) {
            return false;
        }
        int t0 = y13 = Codec.decode32(p, 52);
        int t1 = y13 ^ P[13];
        for (int i = 12; i > 0; --i) {
            int yi = Codec.decode32(p, i * 4);
            if (t1 == 0 && yi + Integer.MIN_VALUE > P[i] + Integer.MIN_VALUE) {
                return false;
            }
            t0 |= yi;
            t1 |= yi ^ P[i];
        }
        int y0 = Codec.decode32(p, 0);
        if (t0 == 0 && y0 + Integer.MIN_VALUE <= -2147483647) {
            return false;
        }
        return t1 != 0 || y0 + Integer.MIN_VALUE < P[0] - 1 + Integer.MIN_VALUE;
    }

    private static boolean checkPointOrderVar(PointAffine p) {
        PointProjective r = new PointProjective();
        Ed448.scalarMultOrderVar(p, r);
        return Ed448.normalizeToNeutralElementVar(r);
    }

    private static boolean checkPointVar(byte[] p) {
        if ((p[56] & 0x7F) != 0) {
            return false;
        }
        if (Codec.decode32(p, 52) != P[13]) {
            return true;
        }
        int[] t = new int[14];
        Codec.decode32(p, 0, t, 0, 14);
        return !Nat.gte(14, t, P);
    }

    private static byte[] copy(byte[] buf, int off, int len) {
        byte[] result = new byte[len];
        System.arraycopy(buf, off, result, 0, len);
        return result;
    }

    public static Xof createPrehash() {
        return Ed448.createXof();
    }

    private static Xof createXof() {
        return new SHAKEDigest(256);
    }

    private static boolean decodePointVar(byte[] p, boolean negate, PointAffine r) {
        int x_0 = (p[56] & 0x80) >>> 7;
        F.decode(p, r.y);
        int[] u = F.create();
        int[] v = F.create();
        F.sqr(r.y, u);
        F.mul(u, 39081, v);
        F.negate(u, u);
        F.addOne(u);
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

    private static void dom4(Xof d, byte phflag, byte[] ctx) {
        int n = DOM4_PREFIX.length;
        byte[] t = new byte[n + 2 + ctx.length];
        System.arraycopy(DOM4_PREFIX, 0, t, 0, n);
        t[n] = phflag;
        t[n + 1] = (byte)ctx.length;
        System.arraycopy(ctx, 0, t, n + 2, ctx.length);
        d.update(t, 0, t.length);
    }

    private static void encodePoint(PointAffine p, byte[] r, int rOff) {
        F.encode(p.y, r, rOff);
        r[rOff + 57 - 1] = (byte)((p.x[0] & 1) << 7);
    }

    public static void encodePublicPoint(PublicPoint publicPoint, byte[] pk, int pkOff) {
        F.encode(publicPoint.data, 16, pk, pkOff);
        pk[pkOff + 57 - 1] = (byte)((publicPoint.data[0] & 1) << 7);
    }

    private static int encodeResult(PointProjective p, byte[] r, int rOff) {
        PointAffine q = new PointAffine();
        Ed448.normalizeToAffine(p, q);
        int result = Ed448.checkPoint(q);
        Ed448.encodePoint(q, r, rOff);
        return result;
    }

    private static PublicPoint exportPoint(PointAffine p) {
        int[] data = new int[32];
        F.copy(p.x, 0, data, 0);
        F.copy(p.y, 0, data, 16);
        return new PublicPoint(data);
    }

    public static void generatePrivateKey(SecureRandom random, byte[] k) {
        if (k.length != 57) {
            throw new IllegalArgumentException("k");
        }
        random.nextBytes(k);
    }

    public static void generatePublicKey(byte[] sk, int skOff, byte[] pk, int pkOff) {
        Xof d = Ed448.createXof();
        byte[] h = new byte[114];
        d.update(sk, skOff, 57);
        d.doFinal(h, 0, h.length);
        byte[] s = new byte[57];
        Ed448.pruneScalar(h, 0, s);
        Ed448.scalarMultBaseEncoded(s, pk, pkOff);
    }

    public static PublicPoint generatePublicKey(byte[] sk, int skOff) {
        Xof d = Ed448.createXof();
        byte[] h = new byte[114];
        d.update(sk, skOff, 57);
        d.doFinal(h, 0, h.length);
        byte[] s = new byte[57];
        Ed448.pruneScalar(h, 0, s);
        PointProjective p = new PointProjective();
        Ed448.scalarMultBase(s, p);
        PointAffine q = new PointAffine();
        Ed448.normalizeToAffine(p, q);
        if (0 == Ed448.checkPoint(q)) {
            throw new IllegalStateException();
        }
        return Ed448.exportPoint(q);
    }

    private static int getWindow4(int[] x, int n) {
        int w = n >>> 3;
        int b = (n & 7) << 2;
        return x[w] >>> b & 0xF;
    }

    private static void implSign(Xof d, byte[] h, byte[] s, byte[] pk, int pkOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        Ed448.dom4(d, phflag, ctx);
        d.update(h, 57, 57);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0, h.length);
        byte[] r = Scalar448.reduce(h);
        byte[] R = new byte[57];
        Ed448.scalarMultBaseEncoded(r, R, 0);
        Ed448.dom4(d, phflag, ctx);
        d.update(R, 0, 57);
        d.update(pk, pkOff, 57);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0, h.length);
        byte[] k = Scalar448.reduce(h);
        byte[] S = Ed448.calculateS(r, k, s);
        System.arraycopy(R, 0, sig, sigOff, 57);
        System.arraycopy(S, 0, sig, sigOff + 57, 57);
    }

    private static void implSign(byte[] sk, int skOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        if (!Ed448.checkContextVar(ctx)) {
            throw new IllegalArgumentException("ctx");
        }
        Xof d = Ed448.createXof();
        byte[] h = new byte[114];
        d.update(sk, skOff, 57);
        d.doFinal(h, 0, h.length);
        byte[] s = new byte[57];
        Ed448.pruneScalar(h, 0, s);
        byte[] pk = new byte[57];
        Ed448.scalarMultBaseEncoded(s, pk, 0);
        Ed448.implSign(d, h, s, pk, 0, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    private static void implSign(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        if (!Ed448.checkContextVar(ctx)) {
            throw new IllegalArgumentException("ctx");
        }
        Xof d = Ed448.createXof();
        byte[] h = new byte[114];
        d.update(sk, skOff, 57);
        d.doFinal(h, 0, h.length);
        byte[] s = new byte[57];
        Ed448.pruneScalar(h, 0, s);
        Ed448.implSign(d, h, s, pk, pkOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    private static boolean implVerify(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen) {
        if (!Ed448.checkContextVar(ctx)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] R = Ed448.copy(sig, sigOff, 57);
        byte[] S = Ed448.copy(sig, sigOff + 57, 57);
        byte[] A = Ed448.copy(pk, pkOff, 57);
        if (!Ed448.checkPointVar(R)) {
            return false;
        }
        int[] nS = new int[14];
        if (!Scalar448.checkVar(S, nS)) {
            return false;
        }
        if (!Ed448.checkPointFullVar(A)) {
            return false;
        }
        PointAffine pR = new PointAffine();
        if (!Ed448.decodePointVar(R, true, pR)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        if (!Ed448.decodePointVar(A, true, pA)) {
            return false;
        }
        Xof d = Ed448.createXof();
        byte[] h = new byte[114];
        Ed448.dom4(d, phflag, ctx);
        d.update(R, 0, 57);
        d.update(A, 0, 57);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0, h.length);
        byte[] k = Scalar448.reduce(h);
        int[] nA = new int[14];
        Scalar448.decode(k, nA);
        int[] v0 = new int[8];
        int[] v1 = new int[8];
        Scalar448.reduceBasisVar(nA, v0, v1);
        Scalar448.multiply225Var(nS, v1, nS);
        PointProjective pZ = new PointProjective();
        Ed448.scalarMultStraus225Var(nS, v0, pA, v1, pR, pZ);
        return Ed448.normalizeToNeutralElementVar(pZ);
    }

    private static boolean implVerify(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, byte phflag, byte[] m, int mOff, int mLen) {
        if (!Ed448.checkContextVar(ctx)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] R = Ed448.copy(sig, sigOff, 57);
        byte[] S = Ed448.copy(sig, sigOff + 57, 57);
        if (!Ed448.checkPointVar(R)) {
            return false;
        }
        int[] nS = new int[14];
        if (!Scalar448.checkVar(S, nS)) {
            return false;
        }
        PointAffine pR = new PointAffine();
        if (!Ed448.decodePointVar(R, true, pR)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        F.negate(publicPoint.data, pA.x);
        F.copy(publicPoint.data, 16, pA.y, 0);
        byte[] A = new byte[57];
        Ed448.encodePublicPoint(publicPoint, A, 0);
        Xof d = Ed448.createXof();
        byte[] h = new byte[114];
        Ed448.dom4(d, phflag, ctx);
        d.update(R, 0, 57);
        d.update(A, 0, 57);
        d.update(m, mOff, mLen);
        d.doFinal(h, 0, h.length);
        byte[] k = Scalar448.reduce(h);
        int[] nA = new int[14];
        Scalar448.decode(k, nA);
        int[] v0 = new int[8];
        int[] v1 = new int[8];
        Scalar448.reduceBasisVar(nA, v0, v1);
        Scalar448.multiply225Var(nS, v1, nS);
        PointProjective pZ = new PointProjective();
        Ed448.scalarMultStraus225Var(nS, v0, pA, v1, pR, pZ);
        return Ed448.normalizeToNeutralElementVar(pZ);
    }

    private static void invertZs(PointProjective[] points) {
        int count = points.length;
        int[] cs = F.createTable(count);
        int[] u = F.create();
        F.copy(points[0].z, 0, u, 0);
        F.copy(u, 0, cs, 0);
        int i = 0;
        while (++i < count) {
            F.mul(u, points[i].z, u);
            F.copy(u, 0, cs, i * 16);
        }
        F.invVar(u, u);
        --i;
        int[] t = F.create();
        while (i > 0) {
            int j = i--;
            F.copy(cs, i * 16, t, 0);
            F.mul(t, u, t);
            F.mul(u, points[j].z, u);
            F.copy(t, 0, points[j].z, 0);
        }
        F.copy(u, 0, points[0].z, 0);
    }

    private static void normalizeToAffine(PointProjective p, PointAffine r) {
        F.inv(p.z, r.y);
        F.mul(r.y, p.x, r.x);
        F.mul(r.y, p.y, r.y);
        F.normalize(r.x);
        F.normalize(r.y);
    }

    private static boolean normalizeToNeutralElementVar(PointProjective p) {
        F.normalize(p.x);
        F.normalize(p.y);
        F.normalize(p.z);
        return F.isZeroVar(p.x) && F.areEqualVar(p.y, p.z);
    }

    private static void pointAdd(PointAffine p, PointProjective r, PointTemp t) {
        int[] b = t.r1;
        int[] c = t.r2;
        int[] d = t.r3;
        int[] e = t.r4;
        int[] f = t.r5;
        int[] g = t.r6;
        int[] h = t.r7;
        F.sqr(r.z, b);
        F.mul(p.x, r.x, c);
        F.mul(p.y, r.y, d);
        F.mul(c, d, e);
        F.mul(e, 39081, e);
        F.add(b, e, f);
        F.sub(b, e, g);
        F.add(p.y, p.x, h);
        F.add(r.y, r.x, e);
        F.mul(h, e, h);
        F.add(d, c, b);
        F.sub(d, c, e);
        F.carry(b);
        F.sub(h, b, h);
        F.mul(h, r.z, h);
        F.mul(e, r.z, e);
        F.mul(f, h, r.x);
        F.mul(e, g, r.y);
        F.mul(f, g, r.z);
    }

    private static void pointAdd(PointProjective p, PointProjective r, PointTemp t) {
        int[] a = t.r0;
        int[] b = t.r1;
        int[] c = t.r2;
        int[] d = t.r3;
        int[] e = t.r4;
        int[] f = t.r5;
        int[] g = t.r6;
        int[] h = t.r7;
        F.mul(p.z, r.z, a);
        F.sqr(a, b);
        F.mul(p.x, r.x, c);
        F.mul(p.y, r.y, d);
        F.mul(c, d, e);
        F.mul(e, 39081, e);
        F.add(b, e, f);
        F.sub(b, e, g);
        F.add(p.y, p.x, h);
        F.add(r.y, r.x, e);
        F.mul(h, e, h);
        F.add(d, c, b);
        F.sub(d, c, e);
        F.carry(b);
        F.sub(h, b, h);
        F.mul(h, a, h);
        F.mul(e, a, e);
        F.mul(f, h, r.x);
        F.mul(e, g, r.y);
        F.mul(f, g, r.z);
    }

    private static void pointAddVar(boolean negate, PointAffine p, PointProjective r, PointTemp t) {
        int[] ng;
        int[] nf;
        int[] ne;
        int[] nb;
        int[] b = t.r1;
        int[] c = t.r2;
        int[] d = t.r3;
        int[] e = t.r4;
        int[] f = t.r5;
        int[] g = t.r6;
        int[] h = t.r7;
        if (negate) {
            nb = e;
            ne = b;
            nf = g;
            ng = f;
            F.sub(p.y, p.x, h);
        } else {
            nb = b;
            ne = e;
            nf = f;
            ng = g;
            F.add(p.y, p.x, h);
        }
        F.sqr(r.z, b);
        F.mul(p.x, r.x, c);
        F.mul(p.y, r.y, d);
        F.mul(c, d, e);
        F.mul(e, 39081, e);
        F.add(b, e, nf);
        F.sub(b, e, ng);
        F.add(r.y, r.x, e);
        F.mul(h, e, h);
        F.add(d, c, nb);
        F.sub(d, c, ne);
        F.carry(nb);
        F.sub(h, b, h);
        F.mul(h, r.z, h);
        F.mul(e, r.z, e);
        F.mul(f, h, r.x);
        F.mul(e, g, r.y);
        F.mul(f, g, r.z);
    }

    private static void pointAddVar(boolean negate, PointProjective p, PointProjective r, PointTemp t) {
        int[] ng;
        int[] nf;
        int[] ne;
        int[] nb;
        int[] a = t.r0;
        int[] b = t.r1;
        int[] c = t.r2;
        int[] d = t.r3;
        int[] e = t.r4;
        int[] f = t.r5;
        int[] g = t.r6;
        int[] h = t.r7;
        if (negate) {
            nb = e;
            ne = b;
            nf = g;
            ng = f;
            F.sub(p.y, p.x, h);
        } else {
            nb = b;
            ne = e;
            nf = f;
            ng = g;
            F.add(p.y, p.x, h);
        }
        F.mul(p.z, r.z, a);
        F.sqr(a, b);
        F.mul(p.x, r.x, c);
        F.mul(p.y, r.y, d);
        F.mul(c, d, e);
        F.mul(e, 39081, e);
        F.add(b, e, nf);
        F.sub(b, e, ng);
        F.add(r.y, r.x, e);
        F.mul(h, e, h);
        F.add(d, c, nb);
        F.sub(d, c, ne);
        F.carry(nb);
        F.sub(h, b, h);
        F.mul(h, a, h);
        F.mul(e, a, e);
        F.mul(f, h, r.x);
        F.mul(e, g, r.y);
        F.mul(f, g, r.z);
    }

    private static void pointCopy(PointAffine p, PointProjective r) {
        F.copy(p.x, 0, r.x, 0);
        F.copy(p.y, 0, r.y, 0);
        F.one(r.z);
    }

    private static void pointCopy(PointProjective p, PointProjective r) {
        F.copy(p.x, 0, r.x, 0);
        F.copy(p.y, 0, r.y, 0);
        F.copy(p.z, 0, r.z, 0);
    }

    private static void pointDouble(PointProjective r, PointTemp t) {
        int[] b = t.r1;
        int[] c = t.r2;
        int[] d = t.r3;
        int[] e = t.r4;
        int[] h = t.r7;
        int[] j = t.r0;
        F.add(r.x, r.y, b);
        F.sqr(b, b);
        F.sqr(r.x, c);
        F.sqr(r.y, d);
        F.add(c, d, e);
        F.carry(e);
        F.sqr(r.z, h);
        F.add(h, h, h);
        F.carry(h);
        F.sub(e, h, j);
        F.sub(b, e, b);
        F.sub(c, d, c);
        F.mul(b, j, r.x);
        F.mul(e, c, r.y);
        F.mul(e, j, r.z);
    }

    private static void pointLookup(int block, int index, PointAffine p) {
        int off = block * 16 * 2 * 16;
        for (int i = 0; i < 16; ++i) {
            int cond = (i ^ index) - 1 >> 31;
            F.cmov(cond, PRECOMP_BASE_COMB, off, p.x, 0);
            F.cmov(cond, PRECOMP_BASE_COMB, off += 16, p.y, 0);
            off += 16;
        }
    }

    private static void pointLookup(int[] x, int n, int[] table, PointProjective r) {
        int w = Ed448.getWindow4(x, n);
        int sign = w >>> 3 ^ 1;
        int abs = (w ^ -sign) & 7;
        int off = 0;
        for (int i = 0; i < 8; ++i) {
            int cond = (i ^ abs) - 1 >> 31;
            F.cmov(cond, table, off, r.x, 0);
            F.cmov(cond, table, off += 16, r.y, 0);
            F.cmov(cond, table, off += 16, r.z, 0);
            off += 16;
        }
        F.cnegate(sign, r.x);
    }

    private static void pointLookup15(int[] table, PointProjective r) {
        int off = 336;
        F.copy(table, off, r.x, 0);
        F.copy(table, off += 16, r.y, 0);
        F.copy(table, off += 16, r.z, 0);
    }

    private static int[] pointPrecompute(PointProjective p, int count, PointTemp t) {
        PointProjective q = new PointProjective();
        Ed448.pointCopy(p, q);
        PointProjective d = new PointProjective();
        Ed448.pointCopy(q, d);
        Ed448.pointDouble(d, t);
        int[] table = F.createTable(count * 3);
        int off = 0;
        int i = 0;
        while (true) {
            F.copy(q.x, 0, table, off);
            F.copy(q.y, 0, table, off += 16);
            F.copy(q.z, 0, table, off += 16);
            off += 16;
            if (++i == count) break;
            Ed448.pointAdd(d, q, t);
        }
        return table;
    }

    private static void pointPrecompute(PointAffine p, PointProjective[] points, int pointsOff, int pointsLen, PointTemp t) {
        PointProjective d = new PointProjective();
        Ed448.pointCopy(p, d);
        Ed448.pointDouble(d, t);
        points[pointsOff] = new PointProjective();
        Ed448.pointCopy(p, points[pointsOff]);
        for (int i = 1; i < pointsLen; ++i) {
            points[pointsOff + i] = new PointProjective();
            Ed448.pointCopy(points[pointsOff + i - 1], points[pointsOff + i]);
            Ed448.pointAdd(d, points[pointsOff + i], t);
        }
    }

    private static void pointSetNeutral(PointProjective p) {
        F.zero(p.x);
        F.one(p.y);
        F.one(p.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void precompute() {
        Object object = PRECOMP_LOCK;
        synchronized (object) {
            PointProjective q;
            int i;
            if (PRECOMP_BASE_COMB != null) {
                return;
            }
            int wnafPoints = 32;
            int combPoints = 80;
            int totalPoints = wnafPoints * 2 + combPoints;
            PointProjective[] points = new PointProjective[totalPoints];
            PointTemp t = new PointTemp();
            PointAffine B = new PointAffine();
            F.copy(B_x, 0, B.x, 0);
            F.copy(B_y, 0, B.y, 0);
            Ed448.pointPrecompute(B, points, 0, wnafPoints, t);
            PointAffine B225 = new PointAffine();
            F.copy(B225_x, 0, B225.x, 0);
            F.copy(B225_y, 0, B225.y, 0);
            Ed448.pointPrecompute(B225, points, wnafPoints, wnafPoints, t);
            PointProjective p = new PointProjective();
            Ed448.pointCopy(B, p);
            int pointsIndex = wnafPoints * 2;
            PointProjective[] toothPowers = new PointProjective[5];
            for (int tooth = 0; tooth < 5; ++tooth) {
                toothPowers[tooth] = new PointProjective();
            }
            for (int block = 0; block < 5; ++block) {
                int tooth;
                int n = pointsIndex++;
                PointProjective pointProjective = new PointProjective();
                points[n] = pointProjective;
                PointProjective sum = pointProjective;
                for (tooth = 0; tooth < 5; ++tooth) {
                    if (tooth == 0) {
                        Ed448.pointCopy(p, sum);
                    } else {
                        Ed448.pointAdd(p, sum, t);
                    }
                    Ed448.pointDouble(p, t);
                    Ed448.pointCopy(p, toothPowers[tooth]);
                    if (block + tooth == 8) continue;
                    for (int spacing = 1; spacing < 18; ++spacing) {
                        Ed448.pointDouble(p, t);
                    }
                }
                F.negate(sum.x, sum.x);
                for (tooth = 0; tooth < 4; ++tooth) {
                    int size = 1 << tooth;
                    int j = 0;
                    while (j < size) {
                        points[pointsIndex] = new PointProjective();
                        Ed448.pointCopy(points[pointsIndex - size], points[pointsIndex]);
                        Ed448.pointAdd(toothPowers[tooth], points[pointsIndex], t);
                        ++j;
                        ++pointsIndex;
                    }
                }
            }
            Ed448.invertZs(points);
            PRECOMP_BASE_WNAF = new PointAffine[wnafPoints];
            for (i = 0; i < wnafPoints; ++i) {
                q = points[i];
                PointAffine r = Ed448.PRECOMP_BASE_WNAF[i] = new PointAffine();
                F.mul(q.x, q.z, r.x);
                F.normalize(r.x);
                F.mul(q.y, q.z, r.y);
                F.normalize(r.y);
            }
            PRECOMP_BASE225_WNAF = new PointAffine[wnafPoints];
            for (i = 0; i < wnafPoints; ++i) {
                q = points[wnafPoints + i];
                PointAffine r = Ed448.PRECOMP_BASE225_WNAF[i] = new PointAffine();
                F.mul(q.x, q.z, r.x);
                F.normalize(r.x);
                F.mul(q.y, q.z, r.y);
                F.normalize(r.y);
            }
            PRECOMP_BASE_COMB = F.createTable(combPoints * 2);
            int off = 0;
            for (int i2 = wnafPoints * 2; i2 < totalPoints; ++i2) {
                PointProjective q2 = points[i2];
                F.mul(q2.x, q2.z, q2.x);
                F.normalize(q2.x);
                F.mul(q2.y, q2.z, q2.y);
                F.normalize(q2.y);
                F.copy(q2.x, 0, PRECOMP_BASE_COMB, off);
                F.copy(q2.y, 0, PRECOMP_BASE_COMB, off += 16);
                off += 16;
            }
        }
    }

    private static void pruneScalar(byte[] n, int nOff, byte[] r) {
        System.arraycopy(n, nOff, r, 0, 56);
        r[0] = (byte)(r[0] & 0xFC);
        r[55] = (byte)(r[55] | 0x80);
        r[56] = 0;
    }

    private static void scalarMult(byte[] k, PointProjective p, PointProjective r) {
        int[] n = new int[15];
        Scalar448.decode(k, n);
        Scalar448.toSignedDigits(449, n, n);
        PointProjective q = new PointProjective();
        PointTemp t = new PointTemp();
        int[] table = Ed448.pointPrecompute(p, 8, t);
        Ed448.pointLookup15(table, r);
        Ed448.pointAdd(p, r, t);
        int w = 111;
        block0: while (true) {
            Ed448.pointLookup(n, w, table, q);
            Ed448.pointAdd(q, r, t);
            if (--w < 0) break;
            int i = 0;
            while (true) {
                if (i >= 4) continue block0;
                Ed448.pointDouble(r, t);
                ++i;
            }
            break;
        }
    }

    private static void scalarMultBase(byte[] k, PointProjective r) {
        Ed448.precompute();
        int[] n = new int[15];
        Scalar448.decode(k, n);
        Scalar448.toSignedDigits(450, n, n);
        PointAffine p = new PointAffine();
        PointTemp t = new PointTemp();
        Ed448.pointSetNeutral(r);
        int cOff = 17;
        while (true) {
            int tPos = cOff;
            for (int block = 0; block < 5; ++block) {
                int w = 0;
                for (int tooth = 0; tooth < 5; ++tooth) {
                    int tBit = n[tPos >>> 5] >>> (tPos & 0x1F);
                    w &= ~(1 << tooth);
                    w ^= tBit << tooth;
                    tPos += 18;
                }
                int sign = w >>> 4 & 1;
                int abs = (w ^ -sign) & 0xF;
                Ed448.pointLookup(block, abs, p);
                F.cnegate(sign, p.x);
                Ed448.pointAdd(p, r, t);
            }
            if (--cOff < 0) break;
            Ed448.pointDouble(r, t);
        }
    }

    private static void scalarMultBaseEncoded(byte[] k, byte[] r, int rOff) {
        PointProjective p = new PointProjective();
        Ed448.scalarMultBase(k, p);
        if (0 == Ed448.encodeResult(p, r, rOff)) {
            throw new IllegalStateException();
        }
    }

    public static void scalarMultBaseXY(X448.Friend friend, byte[] k, int kOff, int[] x, int[] y) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by X448");
        }
        byte[] n = new byte[57];
        Ed448.pruneScalar(k, kOff, n);
        PointProjective p = new PointProjective();
        Ed448.scalarMultBase(n, p);
        if (0 == Ed448.checkPoint(p)) {
            throw new IllegalStateException();
        }
        F.copy(p.x, 0, x, 0);
        F.copy(p.y, 0, y, 0);
    }

    private static void scalarMultOrderVar(PointAffine p, PointProjective r) {
        byte[] ws_p = new byte[447];
        Scalar448.getOrderWnafVar(5, ws_p);
        int count = 8;
        PointProjective[] tp = new PointProjective[count];
        PointTemp t = new PointTemp();
        Ed448.pointPrecompute(p, tp, 0, count, t);
        Ed448.pointSetNeutral(r);
        int bit = 446;
        while (true) {
            byte wp;
            if ((wp = ws_p[bit]) != 0) {
                int index = wp >> 1 ^ wp >> 31;
                Ed448.pointAddVar(wp < 0, tp[index], r, t);
            }
            if (--bit < 0) break;
            Ed448.pointDouble(r, t);
        }
    }

    private static void scalarMultStraus225Var(int[] nb, int[] np, PointAffine p, int[] nq, PointAffine q, PointProjective r) {
        Ed448.precompute();
        byte[] ws_b = new byte[450];
        byte[] ws_p = new byte[225];
        byte[] ws_q = new byte[225];
        Wnaf.getSignedVar(nb, 7, ws_b);
        Wnaf.getSignedVar(np, 5, ws_p);
        Wnaf.getSignedVar(nq, 5, ws_q);
        int count = 8;
        PointProjective[] tp = new PointProjective[count];
        PointProjective[] tq = new PointProjective[count];
        PointTemp t = new PointTemp();
        Ed448.pointPrecompute(p, tp, 0, count, t);
        Ed448.pointPrecompute(q, tq, 0, count, t);
        Ed448.pointSetNeutral(r);
        int bit = 225;
        while (--bit >= 0) {
            byte wq;
            byte wp;
            byte wb225;
            byte wb = ws_b[bit];
            if (wb != 0) {
                int index = wb >> 1 ^ wb >> 31;
                Ed448.pointAddVar(wb < 0, PRECOMP_BASE_WNAF[index], r, t);
            }
            if ((wb225 = ws_b[225 + bit]) != 0) {
                int index = wb225 >> 1 ^ wb225 >> 31;
                Ed448.pointAddVar(wb225 < 0, PRECOMP_BASE225_WNAF[index], r, t);
            }
            if ((wp = ws_p[bit]) != 0) {
                int index = wp >> 1 ^ wp >> 31;
                Ed448.pointAddVar(wp < 0, tp[index], r, t);
            }
            if ((wq = ws_q[bit]) != 0) {
                int index = wq >> 1 ^ wq >> 31;
                Ed448.pointAddVar(wq < 0, tq[index], r, t);
            }
            Ed448.pointDouble(r, t);
        }
        Ed448.pointDouble(r, t);
    }

    public static void sign(byte[] sk, int skOff, byte[] ctx, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        byte phflag = 0;
        Ed448.implSign(sk, skOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    public static void sign(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, byte[] m, int mOff, int mLen, byte[] sig, int sigOff) {
        byte phflag = 0;
        Ed448.implSign(sk, skOff, pk, pkOff, ctx, phflag, m, mOff, mLen, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] ctx, byte[] ph, int phOff, byte[] sig, int sigOff) {
        byte phflag = 1;
        Ed448.implSign(sk, skOff, ctx, phflag, ph, phOff, 64, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, byte[] ph, int phOff, byte[] sig, int sigOff) {
        byte phflag = 1;
        Ed448.implSign(sk, skOff, pk, pkOff, ctx, phflag, ph, phOff, 64, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] ctx, Xof ph, byte[] sig, int sigOff) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        Ed448.implSign(sk, skOff, ctx, phflag, m, 0, m.length, sig, sigOff);
    }

    public static void signPrehash(byte[] sk, int skOff, byte[] pk, int pkOff, byte[] ctx, Xof ph, byte[] sig, int sigOff) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        Ed448.implSign(sk, skOff, pk, pkOff, ctx, phflag, m, 0, m.length, sig, sigOff);
    }

    public static boolean validatePublicKeyFull(byte[] pk, int pkOff) {
        byte[] A = Ed448.copy(pk, pkOff, 57);
        if (!Ed448.checkPointFullVar(A)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        if (!Ed448.decodePointVar(A, false, pA)) {
            return false;
        }
        return Ed448.checkPointOrderVar(pA);
    }

    public static PublicPoint validatePublicKeyFullExport(byte[] pk, int pkOff) {
        byte[] A = Ed448.copy(pk, pkOff, 57);
        if (!Ed448.checkPointFullVar(A)) {
            return null;
        }
        PointAffine pA = new PointAffine();
        if (!Ed448.decodePointVar(A, false, pA)) {
            return null;
        }
        if (!Ed448.checkPointOrderVar(pA)) {
            return null;
        }
        return Ed448.exportPoint(pA);
    }

    public static boolean validatePublicKeyPartial(byte[] pk, int pkOff) {
        byte[] A = Ed448.copy(pk, pkOff, 57);
        if (!Ed448.checkPointFullVar(A)) {
            return false;
        }
        PointAffine pA = new PointAffine();
        return Ed448.decodePointVar(A, false, pA);
    }

    public static PublicPoint validatePublicKeyPartialExport(byte[] pk, int pkOff) {
        byte[] A = Ed448.copy(pk, pkOff, 57);
        if (!Ed448.checkPointFullVar(A)) {
            return null;
        }
        PointAffine pA = new PointAffine();
        if (!Ed448.decodePointVar(A, false, pA)) {
            return null;
        }
        return Ed448.exportPoint(pA);
    }

    public static boolean verify(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, byte[] m, int mOff, int mLen) {
        byte phflag = 0;
        return Ed448.implVerify(sig, sigOff, pk, pkOff, ctx, phflag, m, mOff, mLen);
    }

    public static boolean verify(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, byte[] m, int mOff, int mLen) {
        byte phflag = 0;
        return Ed448.implVerify(sig, sigOff, publicPoint, ctx, phflag, m, mOff, mLen);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, byte[] ph, int phOff) {
        byte phflag = 1;
        return Ed448.implVerify(sig, sigOff, pk, pkOff, ctx, phflag, ph, phOff, 64);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, byte[] ph, int phOff) {
        byte phflag = 1;
        return Ed448.implVerify(sig, sigOff, publicPoint, ctx, phflag, ph, phOff, 64);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, byte[] pk, int pkOff, byte[] ctx, Xof ph) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        return Ed448.implVerify(sig, sigOff, pk, pkOff, ctx, phflag, m, 0, m.length);
    }

    public static boolean verifyPrehash(byte[] sig, int sigOff, PublicPoint publicPoint, byte[] ctx, Xof ph) {
        byte[] m = new byte[64];
        if (64 != ph.doFinal(m, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte phflag = 1;
        return Ed448.implVerify(sig, sigOff, publicPoint, ctx, phflag, m, 0, m.length);
    }

    public static final class Algorithm {
        public static final int Ed448 = 0;
        public static final int Ed448ph = 1;
    }

    private static class F
    extends X448Field {
        private F() {
        }
    }

    private static class PointAffine {
        int[] x = F.create();
        int[] y = F.create();

        private PointAffine() {
        }
    }

    private static class PointProjective {
        int[] x = F.create();
        int[] y = F.create();
        int[] z = F.create();

        private PointProjective() {
        }
    }

    private static class PointTemp {
        int[] r0 = F.create();
        int[] r1 = F.create();
        int[] r2 = F.create();
        int[] r3 = F.create();
        int[] r4 = F.create();
        int[] r5 = F.create();
        int[] r6 = F.create();
        int[] r7 = F.create();

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

