/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointPreCompInfo;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class FixedPointUtil {
    public static final String PRECOMP_NAME = "bc_fixed_point";

    public static int getCombSize(ECCurve c) {
        BigInteger order = c.getOrder();
        return order == null ? c.getFieldSize() + 1 : order.bitLength();
    }

    public static FixedPointPreCompInfo getFixedPointPreCompInfo(PreCompInfo preCompInfo) {
        return preCompInfo instanceof FixedPointPreCompInfo ? (FixedPointPreCompInfo)preCompInfo : null;
    }

    public static FixedPointPreCompInfo precompute(final ECPoint p) {
        final ECCurve c = p.getCurve();
        return (FixedPointPreCompInfo)c.precompute(p, PRECOMP_NAME, new PreCompCallback(){

            @Override
            public PreCompInfo precompute(PreCompInfo existing) {
                int bits;
                int minWidth;
                int n;
                FixedPointPreCompInfo existingFP = existing instanceof FixedPointPreCompInfo ? (FixedPointPreCompInfo)existing : null;
                if (this.checkExisting(existingFP, n = 1 << (minWidth = (bits = FixedPointUtil.getCombSize(c)) > 250 ? 6 : 5))) {
                    return existingFP;
                }
                int d = (bits + minWidth - 1) / minWidth;
                ECPoint[] pow2Table = new ECPoint[minWidth + 1];
                pow2Table[0] = p;
                for (int i = 1; i < minWidth; ++i) {
                    pow2Table[i] = pow2Table[i - 1].timesPow2(d);
                }
                pow2Table[minWidth] = pow2Table[0].subtract(pow2Table[1]);
                c.normalizeAll(pow2Table);
                ECPoint[] lookupTable = new ECPoint[n];
                lookupTable[0] = pow2Table[0];
                for (int bit = minWidth - 1; bit >= 0; --bit) {
                    int step;
                    ECPoint pow2 = pow2Table[bit];
                    for (int i = step = 1 << bit; i < n; i += step << 1) {
                        lookupTable[i] = lookupTable[i - step].add(pow2);
                    }
                }
                c.normalizeAll(lookupTable);
                FixedPointPreCompInfo result = new FixedPointPreCompInfo();
                result.setLookupTable(c.createCacheSafeLookupTable(lookupTable, 0, lookupTable.length));
                result.setOffset(pow2Table[minWidth]);
                result.setWidth(minWidth);
                return result;
            }

            private boolean checkExisting(FixedPointPreCompInfo existingFP, int n) {
                return existingFP != null && this.checkTable(existingFP.getLookupTable(), n);
            }

            private boolean checkTable(ECLookupTable table, int n) {
                return table != null && table.getSize() >= n;
            }
        });
    }
}

