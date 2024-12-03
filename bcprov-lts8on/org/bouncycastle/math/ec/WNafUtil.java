/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;
import org.bouncycastle.math.ec.WNafPreCompInfo;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class WNafUtil {
    public static final String PRECOMP_NAME = "bc_wnaf";
    private static final int[] DEFAULT_WINDOW_SIZE_CUTOFFS = new int[]{13, 41, 121, 337, 897, 2305};
    private static final int MAX_WIDTH = 16;
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final int[] EMPTY_INTS = new int[0];
    private static final ECPoint[] EMPTY_POINTS = new ECPoint[0];

    public static void configureBasepoint(ECPoint p) {
        ECCurve c = p.getCurve();
        if (null == c) {
            return;
        }
        BigInteger n = c.getOrder();
        int bits = null == n ? c.getFieldSize() + 1 : n.bitLength();
        final int confWidth = Math.min(16, WNafUtil.getWindowSize(bits) + 3);
        c.precompute(p, PRECOMP_NAME, new PreCompCallback(){

            @Override
            public PreCompInfo precompute(PreCompInfo existing) {
                WNafPreCompInfo existingWNaf;
                WNafPreCompInfo wNafPreCompInfo = existingWNaf = existing instanceof WNafPreCompInfo ? (WNafPreCompInfo)existing : null;
                if (null != existingWNaf && existingWNaf.getConfWidth() == confWidth) {
                    existingWNaf.setPromotionCountdown(0);
                    return existingWNaf;
                }
                WNafPreCompInfo result = new WNafPreCompInfo();
                result.setPromotionCountdown(0);
                result.setConfWidth(confWidth);
                if (null != existingWNaf) {
                    result.setPreComp(existingWNaf.getPreComp());
                    result.setPreCompNeg(existingWNaf.getPreCompNeg());
                    result.setTwice(existingWNaf.getTwice());
                    result.setWidth(existingWNaf.getWidth());
                }
                return result;
            }
        });
    }

    public static int[] generateCompactNaf(BigInteger k) {
        if (k.bitLength() >>> 16 != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (k.signum() == 0) {
            return EMPTY_INTS;
        }
        BigInteger _3k = k.shiftLeft(1).add(k);
        int bits = _3k.bitLength();
        int[] naf = new int[bits >> 1];
        BigInteger diff = _3k.xor(k);
        int highBit = bits - 1;
        int length = 0;
        int zeroes = 0;
        for (int i = 1; i < highBit; ++i) {
            if (!diff.testBit(i)) {
                ++zeroes;
                continue;
            }
            int digit = k.testBit(i) ? -1 : 1;
            naf[length++] = digit << 16 | zeroes;
            zeroes = 1;
            ++i;
        }
        naf[length++] = 0x10000 | zeroes;
        if (naf.length > length) {
            naf = WNafUtil.trim(naf, length);
        }
        return naf;
    }

    public static int[] generateCompactWindowNaf(int width, BigInteger k) {
        if (width == 2) {
            return WNafUtil.generateCompactNaf(k);
        }
        if (width < 2 || width > 16) {
            throw new IllegalArgumentException("'width' must be in the range [2, 16]");
        }
        if (k.bitLength() >>> 16 != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (k.signum() == 0) {
            return EMPTY_INTS;
        }
        int[] wnaf = new int[k.bitLength() / width + 1];
        int pow2 = 1 << width;
        int mask = pow2 - 1;
        int sign = pow2 >>> 1;
        boolean carry = false;
        int length = 0;
        int pos = 0;
        while (pos <= k.bitLength()) {
            if (k.testBit(pos) == carry) {
                ++pos;
                continue;
            }
            k = k.shiftRight(pos);
            int digit = k.intValue() & mask;
            if (carry) {
                ++digit;
            }
            boolean bl = carry = (digit & sign) != 0;
            if (carry) {
                digit -= pow2;
            }
            int zeroes = length > 0 ? pos - 1 : pos;
            wnaf[length++] = digit << 16 | zeroes;
            pos = width;
        }
        if (wnaf.length > length) {
            wnaf = WNafUtil.trim(wnaf, length);
        }
        return wnaf;
    }

    public static byte[] generateJSF(BigInteger g, BigInteger h) {
        int digits = Math.max(g.bitLength(), h.bitLength()) + 1;
        byte[] jsf = new byte[digits];
        BigInteger k0 = g;
        BigInteger k1 = h;
        int j = 0;
        int d0 = 0;
        int d1 = 0;
        int offset = 0;
        while (d0 | d1 || k0.bitLength() > offset || k1.bitLength() > offset) {
            int u1;
            int n0 = (k0.intValue() >>> offset) + d0 & 7;
            int n1 = (k1.intValue() >>> offset) + d1 & 7;
            int u0 = n0 & 1;
            if (u0 != 0 && n0 + (u0 -= n0 & 2) == 4 && (n1 & 3) == 2) {
                u0 = -u0;
            }
            if ((u1 = n1 & 1) != 0 && n1 + (u1 -= n1 & 2) == 4 && (n0 & 3) == 2) {
                u1 = -u1;
            }
            if (d0 << 1 == 1 + u0) {
                d0 ^= 1;
            }
            if (d1 << 1 == 1 + u1) {
                d1 ^= 1;
            }
            if (++offset == 30) {
                offset = 0;
                k0 = k0.shiftRight(30);
                k1 = k1.shiftRight(30);
            }
            jsf[j++] = (byte)(u0 << 4 | u1 & 0xF);
        }
        if (jsf.length > j) {
            jsf = WNafUtil.trim(jsf, j);
        }
        return jsf;
    }

    public static byte[] generateNaf(BigInteger k) {
        if (k.signum() == 0) {
            return EMPTY_BYTES;
        }
        BigInteger _3k = k.shiftLeft(1).add(k);
        int digits = _3k.bitLength() - 1;
        byte[] naf = new byte[digits];
        BigInteger diff = _3k.xor(k);
        for (int i = 1; i < digits; ++i) {
            if (!diff.testBit(i)) continue;
            naf[i - 1] = (byte)(k.testBit(i) ? -1 : 1);
            ++i;
        }
        naf[digits - 1] = 1;
        return naf;
    }

    public static byte[] generateWindowNaf(int width, BigInteger k) {
        if (width == 2) {
            return WNafUtil.generateNaf(k);
        }
        if (width < 2 || width > 8) {
            throw new IllegalArgumentException("'width' must be in the range [2, 8]");
        }
        if (k.signum() == 0) {
            return EMPTY_BYTES;
        }
        byte[] wnaf = new byte[k.bitLength() + 1];
        int pow2 = 1 << width;
        int mask = pow2 - 1;
        int sign = pow2 >>> 1;
        boolean carry = false;
        int length = 0;
        int pos = 0;
        while (pos <= k.bitLength()) {
            if (k.testBit(pos) == carry) {
                ++pos;
                continue;
            }
            k = k.shiftRight(pos);
            int digit = k.intValue() & mask;
            if (carry) {
                ++digit;
            }
            boolean bl = carry = (digit & sign) != 0;
            if (carry) {
                digit -= pow2;
            }
            length += length > 0 ? pos - 1 : pos;
            wnaf[length++] = (byte)digit;
            pos = width;
        }
        if (wnaf.length > length) {
            wnaf = WNafUtil.trim(wnaf, length);
        }
        return wnaf;
    }

    public static int getNafWeight(BigInteger k) {
        if (k.signum() == 0) {
            return 0;
        }
        BigInteger _3k = k.shiftLeft(1).add(k);
        BigInteger diff = _3k.xor(k);
        return diff.bitCount();
    }

    public static WNafPreCompInfo getWNafPreCompInfo(ECPoint p) {
        return WNafUtil.getWNafPreCompInfo(p.getCurve().getPreCompInfo(p, PRECOMP_NAME));
    }

    public static WNafPreCompInfo getWNafPreCompInfo(PreCompInfo preCompInfo) {
        return preCompInfo instanceof WNafPreCompInfo ? (WNafPreCompInfo)preCompInfo : null;
    }

    public static int getWindowSize(int bits) {
        return WNafUtil.getWindowSize(bits, DEFAULT_WINDOW_SIZE_CUTOFFS, 16);
    }

    public static int getWindowSize(int bits, int maxWidth) {
        return WNafUtil.getWindowSize(bits, DEFAULT_WINDOW_SIZE_CUTOFFS, maxWidth);
    }

    public static int getWindowSize(int bits, int[] windowSizeCutoffs) {
        return WNafUtil.getWindowSize(bits, windowSizeCutoffs, 16);
    }

    public static int getWindowSize(int bits, int[] windowSizeCutoffs, int maxWidth) {
        int w;
        for (w = 0; w < windowSizeCutoffs.length && bits >= windowSizeCutoffs[w]; ++w) {
        }
        return Math.max(2, Math.min(maxWidth, w + 2));
    }

    public static WNafPreCompInfo precompute(final ECPoint p, final int minWidth, final boolean includeNegated) {
        final ECCurve c = p.getCurve();
        return (WNafPreCompInfo)c.precompute(p, PRECOMP_NAME, new PreCompCallback(){

            @Override
            public PreCompInfo precompute(PreCompInfo existing) {
                int reqPreCompLen;
                int width;
                WNafPreCompInfo existingWNaf = existing instanceof WNafPreCompInfo ? (WNafPreCompInfo)existing : null;
                if (this.checkExisting(existingWNaf, width = Math.max(2, Math.min(16, minWidth)), reqPreCompLen = 1 << width - 2, includeNegated)) {
                    existingWNaf.decrementPromotionCountdown();
                    return existingWNaf;
                }
                WNafPreCompInfo result = new WNafPreCompInfo();
                ECPoint[] preComp = null;
                ECPoint[] preCompNeg = null;
                ECPoint twiceP = null;
                if (null != existingWNaf) {
                    int promotionCountdown = existingWNaf.decrementPromotionCountdown();
                    result.setPromotionCountdown(promotionCountdown);
                    int confWidth = existingWNaf.getConfWidth();
                    result.setConfWidth(confWidth);
                    preComp = existingWNaf.getPreComp();
                    preCompNeg = existingWNaf.getPreCompNeg();
                    twiceP = existingWNaf.getTwice();
                }
                width = Math.min(16, Math.max(result.getConfWidth(), width));
                reqPreCompLen = 1 << width - 2;
                int iniPreCompLen = 0;
                if (null == preComp) {
                    preComp = EMPTY_POINTS;
                } else {
                    iniPreCompLen = preComp.length;
                }
                if (iniPreCompLen < reqPreCompLen) {
                    preComp = WNafUtil.resizeTable(preComp, reqPreCompLen);
                    if (reqPreCompLen == 1) {
                        preComp[0] = p.normalize();
                    } else {
                        int curPreCompLen = iniPreCompLen;
                        if (curPreCompLen == 0) {
                            preComp[0] = p;
                            curPreCompLen = 1;
                        }
                        ECFieldElement iso = null;
                        if (reqPreCompLen == 2) {
                            preComp[1] = p.threeTimes();
                        } else {
                            ECPoint isoTwiceP = twiceP;
                            ECPoint last = preComp[curPreCompLen - 1];
                            if (null == isoTwiceP && !(twiceP = (isoTwiceP = preComp[0].twice())).isInfinity() && ECAlgorithms.isFpCurve(c) && c.getFieldSize() >= 64) {
                                switch (c.getCoordinateSystem()) {
                                    case 2: 
                                    case 3: 
                                    case 4: {
                                        iso = twiceP.getZCoord(0);
                                        isoTwiceP = c.createPoint(twiceP.getXCoord().toBigInteger(), twiceP.getYCoord().toBigInteger());
                                        ECFieldElement iso2 = iso.square();
                                        ECFieldElement iso3 = iso2.multiply(iso);
                                        last = last.scaleX(iso2).scaleY(iso3);
                                        if (iniPreCompLen != 0) break;
                                        preComp[0] = last;
                                        break;
                                    }
                                }
                            }
                            while (curPreCompLen < reqPreCompLen) {
                                preComp[curPreCompLen++] = last = last.add(isoTwiceP);
                            }
                        }
                        c.normalizeAll(preComp, iniPreCompLen, reqPreCompLen - iniPreCompLen, iso);
                    }
                }
                if (includeNegated) {
                    int pos;
                    if (null == preCompNeg) {
                        pos = 0;
                        preCompNeg = new ECPoint[reqPreCompLen];
                    } else {
                        pos = preCompNeg.length;
                        if (pos < reqPreCompLen) {
                            preCompNeg = WNafUtil.resizeTable(preCompNeg, reqPreCompLen);
                        }
                    }
                    while (pos < reqPreCompLen) {
                        preCompNeg[pos] = preComp[pos].negate();
                        ++pos;
                    }
                }
                result.setPreComp(preComp);
                result.setPreCompNeg(preCompNeg);
                result.setTwice(twiceP);
                result.setWidth(width);
                return result;
            }

            private boolean checkExisting(WNafPreCompInfo existingWNaf, int width, int reqPreCompLen, boolean includeNegated2) {
                return null != existingWNaf && existingWNaf.getWidth() >= Math.max(existingWNaf.getConfWidth(), width) && this.checkTable(existingWNaf.getPreComp(), reqPreCompLen) && (!includeNegated2 || this.checkTable(existingWNaf.getPreCompNeg(), reqPreCompLen));
            }

            private boolean checkTable(ECPoint[] table, int reqLen) {
                return null != table && table.length >= reqLen;
            }
        });
    }

    public static WNafPreCompInfo precomputeWithPointMap(ECPoint p, final ECPointMap pointMap, final WNafPreCompInfo fromWNaf, final boolean includeNegated) {
        ECCurve c = p.getCurve();
        return (WNafPreCompInfo)c.precompute(p, PRECOMP_NAME, new PreCompCallback(){

            @Override
            public PreCompInfo precompute(PreCompInfo existing) {
                int reqPreCompLen;
                int width;
                WNafPreCompInfo existingWNaf = existing instanceof WNafPreCompInfo ? (WNafPreCompInfo)existing : null;
                if (this.checkExisting(existingWNaf, width = fromWNaf.getWidth(), reqPreCompLen = fromWNaf.getPreComp().length, includeNegated)) {
                    existingWNaf.decrementPromotionCountdown();
                    return existingWNaf;
                }
                WNafPreCompInfo result = new WNafPreCompInfo();
                result.setPromotionCountdown(fromWNaf.getPromotionCountdown());
                ECPoint twiceFrom = fromWNaf.getTwice();
                if (null != twiceFrom) {
                    ECPoint twice = pointMap.map(twiceFrom);
                    result.setTwice(twice);
                }
                ECPoint[] preCompFrom = fromWNaf.getPreComp();
                ECPoint[] preComp = new ECPoint[preCompFrom.length];
                for (int i = 0; i < preCompFrom.length; ++i) {
                    preComp[i] = pointMap.map(preCompFrom[i]);
                }
                result.setPreComp(preComp);
                result.setWidth(width);
                if (includeNegated) {
                    ECPoint[] preCompNeg = new ECPoint[preComp.length];
                    for (int i = 0; i < preCompNeg.length; ++i) {
                        preCompNeg[i] = preComp[i].negate();
                    }
                    result.setPreCompNeg(preCompNeg);
                }
                return result;
            }

            private boolean checkExisting(WNafPreCompInfo existingWNaf, int width, int reqPreCompLen, boolean includeNegated2) {
                return null != existingWNaf && existingWNaf.getWidth() >= width && this.checkTable(existingWNaf.getPreComp(), reqPreCompLen) && (!includeNegated2 || this.checkTable(existingWNaf.getPreCompNeg(), reqPreCompLen));
            }

            private boolean checkTable(ECPoint[] table, int reqLen) {
                return null != table && table.length >= reqLen;
            }
        });
    }

    private static byte[] trim(byte[] a, int length) {
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, result.length);
        return result;
    }

    private static int[] trim(int[] a, int length) {
        int[] result = new int[length];
        System.arraycopy(a, 0, result, 0, result.length);
        return result;
    }

    private static ECPoint[] resizeTable(ECPoint[] a, int length) {
        ECPoint[] result = new ECPoint[length];
        System.arraycopy(a, 0, result, 0, a.length);
        return result;
    }
}

