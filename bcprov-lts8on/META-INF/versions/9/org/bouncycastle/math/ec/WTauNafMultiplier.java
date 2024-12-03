/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECMultiplier;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;
import org.bouncycastle.math.ec.Tnaf;
import org.bouncycastle.math.ec.WTauNafPreCompInfo;
import org.bouncycastle.math.ec.ZTauElement;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class WTauNafMultiplier
extends AbstractECMultiplier {
    static final String PRECOMP_NAME = "bc_wtnaf";

    @Override
    protected ECPoint multiplyPositive(ECPoint point, BigInteger k) {
        if (!(point instanceof ECPoint.AbstractF2m)) {
            throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier");
        }
        ECPoint.AbstractF2m p = (ECPoint.AbstractF2m)point;
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m)p.getCurve();
        byte a = curve.getA().toBigInteger().byteValue();
        byte mu = Tnaf.getMu(a);
        ZTauElement rho = Tnaf.partModReduction(curve, k, a, mu, (byte)10);
        return this.multiplyWTnaf(p, rho, a, mu);
    }

    private ECPoint.AbstractF2m multiplyWTnaf(ECPoint.AbstractF2m p, ZTauElement lambda, byte a, byte mu) {
        ZTauElement[] alpha = a == 0 ? Tnaf.alpha0 : Tnaf.alpha1;
        BigInteger tw = Tnaf.getTw(mu, 4);
        byte[] u = Tnaf.tauAdicWNaf(mu, lambda, 4, tw.intValue(), alpha);
        return WTauNafMultiplier.multiplyFromWTnaf(p, u);
    }

    private static ECPoint.AbstractF2m multiplyFromWTnaf(final ECPoint.AbstractF2m p, byte[] u) {
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m)p.getCurve();
        final byte a = curve.getA().toBigInteger().byteValue();
        WTauNafPreCompInfo preCompInfo = (WTauNafPreCompInfo)curve.precompute(p, PRECOMP_NAME, new PreCompCallback(){

            @Override
            public PreCompInfo precompute(PreCompInfo existing) {
                if (existing instanceof WTauNafPreCompInfo) {
                    return existing;
                }
                WTauNafPreCompInfo result = new WTauNafPreCompInfo();
                result.setPreComp(Tnaf.getPreComp(p, a));
                return result;
            }
        });
        ECPoint.AbstractF2m[] pu = preCompInfo.getPreComp();
        ECPoint.AbstractF2m[] puNeg = new ECPoint.AbstractF2m[pu.length];
        for (int i = 0; i < pu.length; ++i) {
            puNeg[i] = (ECPoint.AbstractF2m)pu[i].negate();
        }
        ECPoint.AbstractF2m q = (ECPoint.AbstractF2m)p.getCurve().getInfinity();
        int tauCount = 0;
        for (int i = u.length - 1; i >= 0; --i) {
            ++tauCount;
            byte ui = u[i];
            if (ui == 0) continue;
            q = q.tauPow(tauCount);
            tauCount = 0;
            ECPoint.AbstractF2m x = ui > 0 ? pu[ui >>> 1] : puNeg[-ui >>> 1];
            q = (ECPoint.AbstractF2m)q.add(x);
        }
        if (tauCount > 0) {
            q = q.tauPow(tauCount);
        }
        return q;
    }
}

