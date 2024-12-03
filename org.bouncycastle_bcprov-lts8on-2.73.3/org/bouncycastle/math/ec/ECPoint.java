/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Hashtable;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;
import org.bouncycastle.math.ec.ValidityPrecompInfo;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ECPoint {
    protected static final ECFieldElement[] EMPTY_ZS = new ECFieldElement[0];
    protected ECCurve curve;
    protected ECFieldElement x;
    protected ECFieldElement y;
    protected ECFieldElement[] zs;
    protected Hashtable preCompTable = null;

    protected static ECFieldElement[] getInitialZCoords(ECCurve curve) {
        int coord = null == curve ? 0 : curve.getCoordinateSystem();
        switch (coord) {
            case 0: 
            case 5: {
                return EMPTY_ZS;
            }
        }
        ECFieldElement one = curve.fromBigInteger(ECConstants.ONE);
        switch (coord) {
            case 1: 
            case 2: 
            case 6: {
                return new ECFieldElement[]{one};
            }
            case 3: {
                return new ECFieldElement[]{one, one, one};
            }
            case 4: {
                return new ECFieldElement[]{one, curve.getA()};
            }
        }
        throw new IllegalArgumentException("unknown coordinate system");
    }

    protected ECPoint(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        this(curve, x, y, ECPoint.getInitialZCoords(curve));
    }

    protected ECPoint(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        this.curve = curve;
        this.x = x;
        this.y = y;
        this.zs = zs;
    }

    protected abstract boolean satisfiesCurveEquation();

    protected boolean satisfiesOrder() {
        if (ECConstants.ONE.equals(this.curve.getCofactor())) {
            return true;
        }
        BigInteger n = this.curve.getOrder();
        return n == null || ECAlgorithms.referenceMultiply(this, n).isInfinity();
    }

    public final ECPoint getDetachedPoint() {
        return this.normalize().detach();
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    protected abstract ECPoint detach();

    protected int getCurveCoordinateSystem() {
        return null == this.curve ? 0 : this.curve.getCoordinateSystem();
    }

    public ECFieldElement getAffineXCoord() {
        this.checkNormalized();
        return this.getXCoord();
    }

    public ECFieldElement getAffineYCoord() {
        this.checkNormalized();
        return this.getYCoord();
    }

    public ECFieldElement getXCoord() {
        return this.x;
    }

    public ECFieldElement getYCoord() {
        return this.y;
    }

    public ECFieldElement getZCoord(int index) {
        return index < 0 || index >= this.zs.length ? null : this.zs[index];
    }

    public ECFieldElement[] getZCoords() {
        int zsLen = this.zs.length;
        if (zsLen == 0) {
            return EMPTY_ZS;
        }
        ECFieldElement[] copy = new ECFieldElement[zsLen];
        System.arraycopy(this.zs, 0, copy, 0, zsLen);
        return copy;
    }

    public final ECFieldElement getRawXCoord() {
        return this.x;
    }

    public final ECFieldElement getRawYCoord() {
        return this.y;
    }

    protected final ECFieldElement[] getRawZCoords() {
        return this.zs;
    }

    protected void checkNormalized() {
        if (!this.isNormalized()) {
            throw new IllegalStateException("point not in normal form");
        }
    }

    public boolean isNormalized() {
        int coord = this.getCurveCoordinateSystem();
        return coord == 0 || coord == 5 || this.isInfinity() || this.zs[0].isOne();
    }

    public ECPoint normalize() {
        if (this.isInfinity()) {
            return this;
        }
        switch (this.getCurveCoordinateSystem()) {
            case 0: 
            case 5: {
                return this;
            }
        }
        ECFieldElement z = this.getZCoord(0);
        if (z.isOne()) {
            return this;
        }
        if (null == this.curve) {
            throw new IllegalStateException("Detached points must be in affine coordinates");
        }
        SecureRandom r = CryptoServicesRegistrar.getSecureRandom();
        ECFieldElement b = this.curve.randomFieldElementMult(r);
        ECFieldElement zInv = z.multiply(b).invert().multiply(b);
        return this.normalize(zInv);
    }

    ECPoint normalize(ECFieldElement zInv) {
        switch (this.getCurveCoordinateSystem()) {
            case 1: 
            case 6: {
                return this.createScaledPoint(zInv, zInv);
            }
            case 2: 
            case 3: 
            case 4: {
                ECFieldElement zInv2 = zInv.square();
                ECFieldElement zInv3 = zInv2.multiply(zInv);
                return this.createScaledPoint(zInv2, zInv3);
            }
        }
        throw new IllegalStateException("not a projective coordinate system");
    }

    protected ECPoint createScaledPoint(ECFieldElement sx, ECFieldElement sy) {
        return this.getCurve().createRawPoint(this.getRawXCoord().multiply(sx), this.getRawYCoord().multiply(sy));
    }

    public boolean isInfinity() {
        return this.x == null || this.y == null || this.zs.length > 0 && this.zs[0].isZero();
    }

    public boolean isValid() {
        return this.implIsValid(false, true);
    }

    boolean isValidPartial() {
        return this.implIsValid(false, false);
    }

    boolean implIsValid(final boolean decompressed, final boolean checkOrder) {
        if (this.isInfinity()) {
            return true;
        }
        ValidityPrecompInfo validity = (ValidityPrecompInfo)this.getCurve().precompute(this, "bc_validity", new PreCompCallback(){

            @Override
            public PreCompInfo precompute(PreCompInfo existing) {
                ValidityPrecompInfo info;
                ValidityPrecompInfo validityPrecompInfo = info = existing instanceof ValidityPrecompInfo ? (ValidityPrecompInfo)existing : null;
                if (info == null) {
                    info = new ValidityPrecompInfo();
                }
                if (info.hasFailed()) {
                    return info;
                }
                if (!info.hasCurveEquationPassed()) {
                    if (!decompressed && !ECPoint.this.satisfiesCurveEquation()) {
                        info.reportFailed();
                        return info;
                    }
                    info.reportCurveEquationPassed();
                }
                if (checkOrder && !info.hasOrderPassed()) {
                    if (!ECPoint.this.satisfiesOrder()) {
                        info.reportFailed();
                        return info;
                    }
                    info.reportOrderPassed();
                }
                return info;
            }
        });
        return !validity.hasFailed();
    }

    public ECPoint scaleX(ECFieldElement scale) {
        return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord().multiply(scale), this.getRawYCoord(), this.getRawZCoords());
    }

    public ECPoint scaleXNegateY(ECFieldElement scale) {
        return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord().multiply(scale), this.getRawYCoord().negate(), this.getRawZCoords());
    }

    public ECPoint scaleY(ECFieldElement scale) {
        return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord(), this.getRawYCoord().multiply(scale), this.getRawZCoords());
    }

    public ECPoint scaleYNegateX(ECFieldElement scale) {
        return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord().negate(), this.getRawYCoord().multiply(scale), this.getRawZCoords());
    }

    public boolean equals(ECPoint other) {
        if (null == other) {
            return false;
        }
        ECCurve c1 = this.getCurve();
        ECCurve c2 = other.getCurve();
        boolean n1 = null == c1;
        boolean n2 = null == c2;
        boolean i1 = this.isInfinity();
        boolean i2 = other.isInfinity();
        if (i1 || i2) {
            return i1 && i2 && (n1 || n2 || c1.equals(c2));
        }
        ECPoint p1 = this;
        ECPoint p2 = other;
        if (!n1 || !n2) {
            if (n1) {
                p2 = p2.normalize();
            } else if (n2) {
                p1 = p1.normalize();
            } else {
                if (!c1.equals(c2)) {
                    return false;
                }
                ECPoint[] points = new ECPoint[]{this, c1.importPoint(p2)};
                c1.normalizeAll(points);
                p1 = points[0];
                p2 = points[1];
            }
        }
        return p1.getXCoord().equals(p2.getXCoord()) && p1.getYCoord().equals(p2.getYCoord());
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ECPoint)) {
            return false;
        }
        return this.equals((ECPoint)other);
    }

    public int hashCode() {
        int hc;
        ECCurve c = this.getCurve();
        int n = hc = null == c ? 0 : ~c.hashCode();
        if (!this.isInfinity()) {
            ECPoint p = this.normalize();
            hc ^= p.getXCoord().hashCode() * 17;
            hc ^= p.getYCoord().hashCode() * 257;
        }
        return hc;
    }

    public String toString() {
        if (this.isInfinity()) {
            return "INF";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        sb.append(this.getRawXCoord());
        sb.append(',');
        sb.append(this.getRawYCoord());
        for (int i = 0; i < this.zs.length; ++i) {
            sb.append(',');
            sb.append(this.zs[i]);
        }
        sb.append(')');
        return sb.toString();
    }

    public byte[] getEncoded(boolean compressed) {
        if (this.isInfinity()) {
            return new byte[1];
        }
        ECPoint normed = this.normalize();
        byte[] X = normed.getXCoord().getEncoded();
        if (compressed) {
            byte[] PO = new byte[X.length + 1];
            PO[0] = (byte)(normed.getCompressionYTilde() ? 3 : 2);
            System.arraycopy(X, 0, PO, 1, X.length);
            return PO;
        }
        byte[] Y = normed.getYCoord().getEncoded();
        byte[] PO = new byte[X.length + Y.length + 1];
        PO[0] = 4;
        System.arraycopy(X, 0, PO, 1, X.length);
        System.arraycopy(Y, 0, PO, X.length + 1, Y.length);
        return PO;
    }

    protected abstract boolean getCompressionYTilde();

    public abstract ECPoint add(ECPoint var1);

    public abstract ECPoint negate();

    public abstract ECPoint subtract(ECPoint var1);

    public ECPoint timesPow2(int e) {
        if (e < 0) {
            throw new IllegalArgumentException("'e' cannot be negative");
        }
        ECPoint p = this;
        while (--e >= 0) {
            p = p.twice();
        }
        return p;
    }

    public abstract ECPoint twice();

    public ECPoint twicePlus(ECPoint b) {
        return this.twice().add(b);
    }

    public ECPoint threeTimes() {
        return this.twicePlus(this);
    }

    public ECPoint multiply(BigInteger k) {
        return this.getCurve().getMultiplier().multiply(this, k);
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static abstract class AbstractF2m
    extends ECPoint {
        protected AbstractF2m(ECCurve curve, ECFieldElement x, ECFieldElement y) {
            super(curve, x, y);
        }

        protected AbstractF2m(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
            super(curve, x, y, zs);
        }

        @Override
        protected boolean satisfiesCurveEquation() {
            ECCurve curve = this.getCurve();
            ECFieldElement X = this.x;
            ECFieldElement A = curve.getA();
            ECFieldElement B = curve.getB();
            int coord = curve.getCoordinateSystem();
            if (coord == 6) {
                ECFieldElement rhs;
                ECFieldElement lhs;
                ECFieldElement Z = this.zs[0];
                boolean ZIsOne = Z.isOne();
                if (X.isZero()) {
                    ECFieldElement Y = this.y;
                    ECFieldElement lhs2 = Y.square();
                    ECFieldElement rhs2 = B;
                    if (!ZIsOne) {
                        rhs2 = rhs2.multiply(Z.square());
                    }
                    return lhs2.equals(rhs2);
                }
                ECFieldElement L = this.y;
                ECFieldElement X2 = X.square();
                if (ZIsOne) {
                    lhs = L.square().add(L).add(A);
                    rhs = X2.square().add(B);
                } else {
                    ECFieldElement Z2 = Z.square();
                    ECFieldElement Z4 = Z2.square();
                    lhs = L.add(Z).multiplyPlusProduct(L, A, Z2);
                    rhs = X2.squarePlusProduct(B, Z4);
                }
                lhs = lhs.multiply(X2);
                return lhs.equals(rhs);
            }
            ECFieldElement Y = this.y;
            ECFieldElement lhs = Y.add(X).multiply(Y);
            switch (coord) {
                case 0: {
                    break;
                }
                case 1: {
                    ECFieldElement Z = this.zs[0];
                    if (Z.isOne()) break;
                    ECFieldElement Z2 = Z.square();
                    ECFieldElement Z3 = Z.multiply(Z2);
                    lhs = lhs.multiply(Z);
                    A = A.multiply(Z);
                    B = B.multiply(Z3);
                    break;
                }
                default: {
                    throw new IllegalStateException("unsupported coordinate system");
                }
            }
            ECFieldElement rhs = X.add(A).multiply(X.square()).add(B);
            return lhs.equals(rhs);
        }

        @Override
        protected boolean satisfiesOrder() {
            BigInteger cofactor = this.curve.getCofactor();
            if (ECConstants.TWO.equals(cofactor)) {
                ECPoint N = this.normalize();
                ECFieldElement X = N.getAffineXCoord();
                return 0 != ((ECFieldElement.AbstractF2m)X).trace();
            }
            if (ECConstants.FOUR.equals(cofactor)) {
                ECPoint N = this.normalize();
                ECFieldElement X = N.getAffineXCoord();
                ECFieldElement L = ((ECCurve.AbstractF2m)this.curve).solveQuadraticEquation(X.add(this.curve.getA()));
                if (null == L) {
                    return false;
                }
                ECFieldElement Y = N.getAffineYCoord();
                ECFieldElement T = X.multiply(L).add(Y);
                return 0 == ((ECFieldElement.AbstractF2m)T).trace();
            }
            return super.satisfiesOrder();
        }

        @Override
        public ECPoint scaleX(ECFieldElement scale) {
            if (this.isInfinity()) {
                return this;
            }
            int coord = this.getCurveCoordinateSystem();
            switch (coord) {
                case 5: {
                    ECFieldElement X = this.getRawXCoord();
                    ECFieldElement L = this.getRawYCoord();
                    ECFieldElement X2 = X.multiply(scale);
                    ECFieldElement L2 = L.add(X).divide(scale).add(X2);
                    return this.getCurve().createRawPoint(X, L2, this.getRawZCoords());
                }
                case 6: {
                    ECFieldElement X = this.getRawXCoord();
                    ECFieldElement L = this.getRawYCoord();
                    ECFieldElement Z = this.getRawZCoords()[0];
                    ECFieldElement X2 = X.multiply(scale.square());
                    ECFieldElement L2 = L.add(X).add(X2);
                    ECFieldElement Z2 = Z.multiply(scale);
                    return this.getCurve().createRawPoint(X2, L2, new ECFieldElement[]{Z2});
                }
            }
            return super.scaleX(scale);
        }

        @Override
        public ECPoint scaleXNegateY(ECFieldElement scale) {
            return this.scaleX(scale);
        }

        @Override
        public ECPoint scaleY(ECFieldElement scale) {
            if (this.isInfinity()) {
                return this;
            }
            int coord = this.getCurveCoordinateSystem();
            switch (coord) {
                case 5: 
                case 6: {
                    ECFieldElement X = this.getRawXCoord();
                    ECFieldElement L = this.getRawYCoord();
                    ECFieldElement L2 = L.add(X).multiply(scale).add(X);
                    return this.getCurve().createRawPoint(X, L2, this.getRawZCoords());
                }
            }
            return super.scaleY(scale);
        }

        @Override
        public ECPoint scaleYNegateX(ECFieldElement scale) {
            return this.scaleY(scale);
        }

        @Override
        public ECPoint subtract(ECPoint b) {
            if (b.isInfinity()) {
                return this;
            }
            return this.add(b.negate());
        }

        public AbstractF2m tau() {
            if (this.isInfinity()) {
                return this;
            }
            ECCurve curve = this.getCurve();
            int coord = curve.getCoordinateSystem();
            ECFieldElement X1 = this.x;
            switch (coord) {
                case 0: 
                case 5: {
                    ECFieldElement Y1 = this.y;
                    return (AbstractF2m)curve.createRawPoint(X1.square(), Y1.square());
                }
                case 1: 
                case 6: {
                    ECFieldElement Y1 = this.y;
                    ECFieldElement Z1 = this.zs[0];
                    return (AbstractF2m)curve.createRawPoint(X1.square(), Y1.square(), new ECFieldElement[]{Z1.square()});
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }

        public AbstractF2m tauPow(int pow) {
            if (this.isInfinity()) {
                return this;
            }
            ECCurve curve = this.getCurve();
            int coord = curve.getCoordinateSystem();
            ECFieldElement X1 = this.x;
            switch (coord) {
                case 0: 
                case 5: {
                    ECFieldElement Y1 = this.y;
                    return (AbstractF2m)curve.createRawPoint(X1.squarePow(pow), Y1.squarePow(pow));
                }
                case 1: 
                case 6: {
                    ECFieldElement Y1 = this.y;
                    ECFieldElement Z1 = this.zs[0];
                    return (AbstractF2m)curve.createRawPoint(X1.squarePow(pow), Y1.squarePow(pow), new ECFieldElement[]{Z1.squarePow(pow)});
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static abstract class AbstractFp
    extends ECPoint {
        protected AbstractFp(ECCurve curve, ECFieldElement x, ECFieldElement y) {
            super(curve, x, y);
        }

        protected AbstractFp(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
            super(curve, x, y, zs);
        }

        @Override
        protected boolean getCompressionYTilde() {
            return this.getAffineYCoord().testBitZero();
        }

        @Override
        protected boolean satisfiesCurveEquation() {
            ECFieldElement X = this.x;
            ECFieldElement Y = this.y;
            ECFieldElement A = this.curve.getA();
            ECFieldElement B = this.curve.getB();
            ECFieldElement lhs = Y.square();
            switch (this.getCurveCoordinateSystem()) {
                case 0: {
                    break;
                }
                case 1: {
                    ECFieldElement Z = this.zs[0];
                    if (Z.isOne()) break;
                    ECFieldElement Z2 = Z.square();
                    ECFieldElement Z3 = Z.multiply(Z2);
                    lhs = lhs.multiply(Z);
                    A = A.multiply(Z2);
                    B = B.multiply(Z3);
                    break;
                }
                case 2: 
                case 3: 
                case 4: {
                    ECFieldElement Z = this.zs[0];
                    if (Z.isOne()) break;
                    ECFieldElement Z2 = Z.square();
                    ECFieldElement Z4 = Z2.square();
                    ECFieldElement Z6 = Z2.multiply(Z4);
                    A = A.multiply(Z4);
                    B = B.multiply(Z6);
                    break;
                }
                default: {
                    throw new IllegalStateException("unsupported coordinate system");
                }
            }
            ECFieldElement rhs = X.square().add(A).multiply(X).add(B);
            return lhs.equals(rhs);
        }

        @Override
        public ECPoint subtract(ECPoint b) {
            if (b.isInfinity()) {
                return this;
            }
            return this.add(b.negate());
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class F2m
    extends AbstractF2m {
        F2m(ECCurve curve, ECFieldElement x, ECFieldElement y) {
            super(curve, x, y);
        }

        F2m(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
            super(curve, x, y, zs);
        }

        @Override
        protected ECPoint detach() {
            return new F2m(null, this.getAffineXCoord(), this.getAffineYCoord());
        }

        @Override
        public ECFieldElement getYCoord() {
            int coord = this.getCurveCoordinateSystem();
            switch (coord) {
                case 5: 
                case 6: {
                    ECFieldElement Z;
                    ECFieldElement X = this.x;
                    ECFieldElement L = this.y;
                    if (this.isInfinity() || X.isZero()) {
                        return L;
                    }
                    ECFieldElement Y = L.add(X).multiply(X);
                    if (6 == coord && !(Z = this.zs[0]).isOne()) {
                        Y = Y.divide(Z);
                    }
                    return Y;
                }
            }
            return this.y;
        }

        @Override
        protected boolean getCompressionYTilde() {
            ECFieldElement X = this.getRawXCoord();
            if (X.isZero()) {
                return false;
            }
            ECFieldElement Y = this.getRawYCoord();
            switch (this.getCurveCoordinateSystem()) {
                case 5: 
                case 6: {
                    return Y.testBitZero() != X.testBitZero();
                }
            }
            return Y.divide(X).testBitZero();
        }

        @Override
        public ECPoint add(ECPoint b) {
            if (this.isInfinity()) {
                return b;
            }
            if (b.isInfinity()) {
                return this;
            }
            ECCurve curve = this.getCurve();
            int coord = curve.getCoordinateSystem();
            ECFieldElement X1 = this.x;
            ECFieldElement X2 = b.x;
            switch (coord) {
                case 0: {
                    ECFieldElement Y1 = this.y;
                    ECFieldElement Y2 = b.y;
                    ECFieldElement dx = X1.add(X2);
                    ECFieldElement dy = Y1.add(Y2);
                    if (dx.isZero()) {
                        if (dy.isZero()) {
                            return this.twice();
                        }
                        return curve.getInfinity();
                    }
                    ECFieldElement L = dy.divide(dx);
                    ECFieldElement X3 = L.square().add(L).add(dx).add(curve.getA());
                    ECFieldElement Y3 = L.multiply(X1.add(X3)).add(X3).add(Y1);
                    return new F2m(curve, X3, Y3);
                }
                case 1: {
                    ECFieldElement Y1 = this.y;
                    ECFieldElement Z1 = this.zs[0];
                    ECFieldElement Y2 = b.y;
                    ECFieldElement Z2 = b.zs[0];
                    boolean Z2IsOne = Z2.isOne();
                    ECFieldElement U1 = Z1.multiply(Y2);
                    ECFieldElement U2 = Z2IsOne ? Y1 : Y1.multiply(Z2);
                    ECFieldElement U = U1.add(U2);
                    ECFieldElement V1 = Z1.multiply(X2);
                    ECFieldElement V2 = Z2IsOne ? X1 : X1.multiply(Z2);
                    ECFieldElement V = V1.add(V2);
                    if (V.isZero()) {
                        if (U.isZero()) {
                            return this.twice();
                        }
                        return curve.getInfinity();
                    }
                    ECFieldElement VSq = V.square();
                    ECFieldElement VCu = VSq.multiply(V);
                    ECFieldElement W = Z2IsOne ? Z1 : Z1.multiply(Z2);
                    ECFieldElement uv = U.add(V);
                    ECFieldElement A = uv.multiplyPlusProduct(U, VSq, curve.getA()).multiply(W).add(VCu);
                    ECFieldElement X3 = V.multiply(A);
                    ECFieldElement VSqZ2 = Z2IsOne ? VSq : VSq.multiply(Z2);
                    ECFieldElement Y3 = U.multiplyPlusProduct(X1, V, Y1).multiplyPlusProduct(VSqZ2, uv, A);
                    ECFieldElement Z3 = VCu.multiply(W);
                    return new F2m(curve, X3, Y3, new ECFieldElement[]{Z3});
                }
                case 6: {
                    ECFieldElement Z3;
                    ECFieldElement L3;
                    ECFieldElement X3;
                    if (X1.isZero()) {
                        if (X2.isZero()) {
                            return curve.getInfinity();
                        }
                        return b.add(this);
                    }
                    ECFieldElement L1 = this.y;
                    ECFieldElement Z1 = this.zs[0];
                    ECFieldElement L2 = b.y;
                    ECFieldElement Z2 = b.zs[0];
                    boolean Z1IsOne = Z1.isOne();
                    ECFieldElement U2 = X2;
                    ECFieldElement S2 = L2;
                    if (!Z1IsOne) {
                        U2 = U2.multiply(Z1);
                        S2 = S2.multiply(Z1);
                    }
                    boolean Z2IsOne = Z2.isOne();
                    ECFieldElement U1 = X1;
                    ECFieldElement S1 = L1;
                    if (!Z2IsOne) {
                        U1 = U1.multiply(Z2);
                        S1 = S1.multiply(Z2);
                    }
                    ECFieldElement A = S1.add(S2);
                    ECFieldElement B = U1.add(U2);
                    if (B.isZero()) {
                        if (A.isZero()) {
                            return this.twice();
                        }
                        return curve.getInfinity();
                    }
                    if (X2.isZero()) {
                        ECFieldElement Y2;
                        ECPoint p = this.normalize();
                        X1 = p.getXCoord();
                        ECFieldElement Y1 = p.getYCoord();
                        ECFieldElement L = Y1.add(Y2 = L2).divide(X1);
                        X3 = L.square().add(L).add(X1).add(curve.getA());
                        if (X3.isZero()) {
                            return new F2m(curve, X3, curve.getB().sqrt());
                        }
                        ECFieldElement Y3 = L.multiply(X1.add(X3)).add(X3).add(Y1);
                        L3 = Y3.divide(X3).add(X3);
                        Z3 = curve.fromBigInteger(ECConstants.ONE);
                    } else {
                        ECFieldElement AU2;
                        B = B.square();
                        ECFieldElement AU1 = A.multiply(U1);
                        X3 = AU1.multiply(AU2 = A.multiply(U2));
                        if (X3.isZero()) {
                            return new F2m(curve, X3, curve.getB().sqrt());
                        }
                        ECFieldElement ABZ2 = A.multiply(B);
                        if (!Z2IsOne) {
                            ABZ2 = ABZ2.multiply(Z2);
                        }
                        L3 = AU2.add(B).squarePlusProduct(ABZ2, L1.add(Z1));
                        Z3 = ABZ2;
                        if (!Z1IsOne) {
                            Z3 = Z3.multiply(Z1);
                        }
                    }
                    return new F2m(curve, X3, L3, new ECFieldElement[]{Z3});
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }

        @Override
        public ECPoint twice() {
            if (this.isInfinity()) {
                return this;
            }
            ECCurve curve = this.getCurve();
            ECFieldElement X1 = this.x;
            if (X1.isZero()) {
                return curve.getInfinity();
            }
            int coord = curve.getCoordinateSystem();
            switch (coord) {
                case 0: {
                    ECFieldElement Y1 = this.y;
                    ECFieldElement L1 = Y1.divide(X1).add(X1);
                    ECFieldElement X3 = L1.square().add(L1).add(curve.getA());
                    ECFieldElement Y3 = X1.squarePlusProduct(X3, L1.addOne());
                    return new F2m(curve, X3, Y3);
                }
                case 1: {
                    ECFieldElement Y1 = this.y;
                    ECFieldElement Z1 = this.zs[0];
                    boolean Z1IsOne = Z1.isOne();
                    ECFieldElement X1Z1 = Z1IsOne ? X1 : X1.multiply(Z1);
                    ECFieldElement Y1Z1 = Z1IsOne ? Y1 : Y1.multiply(Z1);
                    ECFieldElement X1Sq = X1.square();
                    ECFieldElement S = X1Sq.add(Y1Z1);
                    ECFieldElement V = X1Z1;
                    ECFieldElement vSquared = V.square();
                    ECFieldElement sv = S.add(V);
                    ECFieldElement h = sv.multiplyPlusProduct(S, vSquared, curve.getA());
                    ECFieldElement X3 = V.multiply(h);
                    ECFieldElement Y3 = X1Sq.square().multiplyPlusProduct(V, h, sv);
                    ECFieldElement Z3 = V.multiply(vSquared);
                    return new F2m(curve, X3, Y3, new ECFieldElement[]{Z3});
                }
                case 6: {
                    ECFieldElement L3;
                    ECFieldElement L1 = this.y;
                    ECFieldElement Z1 = this.zs[0];
                    boolean Z1IsOne = Z1.isOne();
                    ECFieldElement L1Z1 = Z1IsOne ? L1 : L1.multiply(Z1);
                    ECFieldElement Z1Sq = Z1IsOne ? Z1 : Z1.square();
                    ECFieldElement a = curve.getA();
                    ECFieldElement aZ1Sq = Z1IsOne ? a : a.multiply(Z1Sq);
                    ECFieldElement T = L1.square().add(L1Z1).add(aZ1Sq);
                    if (T.isZero()) {
                        return new F2m(curve, T, curve.getB().sqrt());
                    }
                    ECFieldElement X3 = T.square();
                    ECFieldElement Z3 = Z1IsOne ? T : T.multiply(Z1Sq);
                    ECFieldElement b = curve.getB();
                    if (b.bitLength() < curve.getFieldSize() >> 1) {
                        ECFieldElement t1 = L1.add(X1).square();
                        ECFieldElement t2 = b.isOne() ? aZ1Sq.add(Z1Sq).square() : aZ1Sq.squarePlusProduct(b, Z1Sq.square());
                        L3 = t1.add(T).add(Z1Sq).multiply(t1).add(t2).add(X3);
                        if (a.isZero()) {
                            L3 = L3.add(Z3);
                        } else if (!a.isOne()) {
                            L3 = L3.add(a.addOne().multiply(Z3));
                        }
                    } else {
                        ECFieldElement X1Z1 = Z1IsOne ? X1 : X1.multiply(Z1);
                        L3 = X1Z1.squarePlusProduct(T, L1Z1).add(X3).add(Z3);
                    }
                    return new F2m(curve, X3, L3, new ECFieldElement[]{Z3});
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }

        @Override
        public ECPoint twicePlus(ECPoint b) {
            if (this.isInfinity()) {
                return b;
            }
            if (b.isInfinity()) {
                return this.twice();
            }
            ECCurve curve = this.getCurve();
            ECFieldElement X1 = this.x;
            if (X1.isZero()) {
                return b;
            }
            int coord = curve.getCoordinateSystem();
            switch (coord) {
                case 6: {
                    ECFieldElement X2 = b.x;
                    ECFieldElement Z2 = b.zs[0];
                    if (X2.isZero() || !Z2.isOne()) {
                        return this.twice().add(b);
                    }
                    ECFieldElement L1 = this.y;
                    ECFieldElement Z1 = this.zs[0];
                    ECFieldElement L2 = b.y;
                    ECFieldElement X1Sq = X1.square();
                    ECFieldElement L1Sq = L1.square();
                    ECFieldElement Z1Sq = Z1.square();
                    ECFieldElement L1Z1 = L1.multiply(Z1);
                    ECFieldElement T = curve.getA().multiply(Z1Sq).add(L1Sq).add(L1Z1);
                    ECFieldElement L2plus1 = L2.addOne();
                    ECFieldElement A = curve.getA().add(L2plus1).multiply(Z1Sq).add(L1Sq).multiplyPlusProduct(T, X1Sq, Z1Sq);
                    ECFieldElement X2Z1Sq = X2.multiply(Z1Sq);
                    ECFieldElement B = X2Z1Sq.add(T).square();
                    if (B.isZero()) {
                        if (A.isZero()) {
                            return b.twice();
                        }
                        return curve.getInfinity();
                    }
                    if (A.isZero()) {
                        return new F2m(curve, A, curve.getB().sqrt());
                    }
                    ECFieldElement X3 = A.square().multiply(X2Z1Sq);
                    ECFieldElement Z3 = A.multiply(B).multiply(Z1Sq);
                    ECFieldElement L3 = A.add(B).square().multiplyPlusProduct(T, L2plus1, Z3);
                    return new F2m(curve, X3, L3, new ECFieldElement[]{Z3});
                }
            }
            return this.twice().add(b);
        }

        @Override
        public ECPoint negate() {
            if (this.isInfinity()) {
                return this;
            }
            ECFieldElement X = this.x;
            if (X.isZero()) {
                return this;
            }
            switch (this.getCurveCoordinateSystem()) {
                case 0: {
                    ECFieldElement Y = this.y;
                    return new F2m(this.curve, X, Y.add(X));
                }
                case 1: {
                    ECFieldElement Y = this.y;
                    ECFieldElement Z = this.zs[0];
                    return new F2m(this.curve, X, Y.add(X), new ECFieldElement[]{Z});
                }
                case 5: {
                    ECFieldElement L = this.y;
                    return new F2m(this.curve, X, L.addOne());
                }
                case 6: {
                    ECFieldElement L = this.y;
                    ECFieldElement Z = this.zs[0];
                    return new F2m(this.curve, X, L.add(Z), new ECFieldElement[]{Z});
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class Fp
    extends AbstractFp {
        Fp(ECCurve curve, ECFieldElement x, ECFieldElement y) {
            super(curve, x, y);
        }

        Fp(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
            super(curve, x, y, zs);
        }

        @Override
        protected ECPoint detach() {
            return new Fp(null, this.getAffineXCoord(), this.getAffineYCoord());
        }

        @Override
        public ECFieldElement getZCoord(int index) {
            if (index == 1 && 4 == this.getCurveCoordinateSystem()) {
                return this.getJacobianModifiedW();
            }
            return super.getZCoord(index);
        }

        @Override
        public ECPoint add(ECPoint b) {
            if (this.isInfinity()) {
                return b;
            }
            if (b.isInfinity()) {
                return this;
            }
            if (this == b) {
                return this.twice();
            }
            ECCurve curve = this.getCurve();
            int coord = curve.getCoordinateSystem();
            ECFieldElement X1 = this.x;
            ECFieldElement Y1 = this.y;
            ECFieldElement X2 = b.x;
            ECFieldElement Y2 = b.y;
            switch (coord) {
                case 0: {
                    ECFieldElement dx = X2.subtract(X1);
                    ECFieldElement dy = Y2.subtract(Y1);
                    if (dx.isZero()) {
                        if (dy.isZero()) {
                            return this.twice();
                        }
                        return curve.getInfinity();
                    }
                    ECFieldElement gamma = dy.divide(dx);
                    ECFieldElement X3 = gamma.square().subtract(X1).subtract(X2);
                    ECFieldElement Y3 = gamma.multiply(X1.subtract(X3)).subtract(Y1);
                    return new Fp(curve, X3, Y3);
                }
                case 1: {
                    ECFieldElement Z1 = this.zs[0];
                    ECFieldElement Z2 = b.zs[0];
                    boolean Z1IsOne = Z1.isOne();
                    boolean Z2IsOne = Z2.isOne();
                    ECFieldElement u1 = Z1IsOne ? Y2 : Y2.multiply(Z1);
                    ECFieldElement u2 = Z2IsOne ? Y1 : Y1.multiply(Z2);
                    ECFieldElement u = u1.subtract(u2);
                    ECFieldElement v1 = Z1IsOne ? X2 : X2.multiply(Z1);
                    ECFieldElement v2 = Z2IsOne ? X1 : X1.multiply(Z2);
                    ECFieldElement v = v1.subtract(v2);
                    if (v.isZero()) {
                        if (u.isZero()) {
                            return this.twice();
                        }
                        return curve.getInfinity();
                    }
                    ECFieldElement w = Z1IsOne ? Z2 : (Z2IsOne ? Z1 : Z1.multiply(Z2));
                    ECFieldElement vSquared = v.square();
                    ECFieldElement vCubed = vSquared.multiply(v);
                    ECFieldElement vSquaredV2 = vSquared.multiply(v2);
                    ECFieldElement A = u.square().multiply(w).subtract(vCubed).subtract(this.two(vSquaredV2));
                    ECFieldElement X3 = v.multiply(A);
                    ECFieldElement Y3 = vSquaredV2.subtract(A).multiplyMinusProduct(u, u2, vCubed);
                    ECFieldElement Z3 = vCubed.multiply(w);
                    return new Fp(curve, X3, Y3, new ECFieldElement[]{Z3});
                }
                case 2: 
                case 4: {
                    ECFieldElement[] zs;
                    ECFieldElement Z3;
                    ECFieldElement Y3;
                    ECFieldElement X3;
                    ECFieldElement Z1 = this.zs[0];
                    ECFieldElement Z2 = b.zs[0];
                    boolean Z1IsOne = Z1.isOne();
                    ECFieldElement Z3Squared = null;
                    if (!Z1IsOne && Z1.equals(Z2)) {
                        ECFieldElement dx = X1.subtract(X2);
                        ECFieldElement dy = Y1.subtract(Y2);
                        if (dx.isZero()) {
                            if (dy.isZero()) {
                                return this.twice();
                            }
                            return curve.getInfinity();
                        }
                        ECFieldElement C = dx.square();
                        ECFieldElement W1 = X1.multiply(C);
                        ECFieldElement W2 = X2.multiply(C);
                        ECFieldElement A1 = W1.subtract(W2).multiply(Y1);
                        X3 = dy.square().subtract(W1).subtract(W2);
                        Y3 = W1.subtract(X3).multiply(dy).subtract(A1);
                        Z3 = dx;
                        Z3 = Z3.multiply(Z1);
                    } else {
                        ECFieldElement S1;
                        ECFieldElement U1;
                        ECFieldElement S2;
                        ECFieldElement U2;
                        ECFieldElement Z1Squared;
                        if (Z1IsOne) {
                            Z1Squared = Z1;
                            U2 = X2;
                            S2 = Y2;
                        } else {
                            Z1Squared = Z1.square();
                            U2 = Z1Squared.multiply(X2);
                            ECFieldElement Z1Cubed = Z1Squared.multiply(Z1);
                            S2 = Z1Cubed.multiply(Y2);
                        }
                        boolean Z2IsOne = Z2.isOne();
                        if (Z2IsOne) {
                            ECFieldElement Z2Squared = Z2;
                            U1 = X1;
                            S1 = Y1;
                        } else {
                            ECFieldElement Z2Squared = Z2.square();
                            U1 = Z2Squared.multiply(X1);
                            ECFieldElement Z2Cubed = Z2Squared.multiply(Z2);
                            S1 = Z2Cubed.multiply(Y1);
                        }
                        ECFieldElement H = U1.subtract(U2);
                        ECFieldElement R = S1.subtract(S2);
                        if (H.isZero()) {
                            if (R.isZero()) {
                                return this.twice();
                            }
                            return curve.getInfinity();
                        }
                        ECFieldElement HSquared = H.square();
                        ECFieldElement G = HSquared.multiply(H);
                        ECFieldElement V = HSquared.multiply(U1);
                        X3 = R.square().add(G).subtract(this.two(V));
                        Y3 = V.subtract(X3).multiplyMinusProduct(R, G, S1);
                        Z3 = H;
                        if (!Z1IsOne) {
                            Z3 = Z3.multiply(Z1);
                        }
                        if (!Z2IsOne) {
                            Z3 = Z3.multiply(Z2);
                        }
                        if (Z3 == H) {
                            Z3Squared = HSquared;
                        }
                    }
                    if (coord == 4) {
                        ECFieldElement W3 = this.calculateJacobianModifiedW(Z3, Z3Squared);
                        zs = new ECFieldElement[]{Z3, W3};
                    } else {
                        zs = new ECFieldElement[]{Z3};
                    }
                    return new Fp(curve, X3, Y3, zs);
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }

        @Override
        public ECPoint twice() {
            if (this.isInfinity()) {
                return this;
            }
            ECCurve curve = this.getCurve();
            ECFieldElement Y1 = this.y;
            if (Y1.isZero()) {
                return curve.getInfinity();
            }
            int coord = curve.getCoordinateSystem();
            ECFieldElement X1 = this.x;
            switch (coord) {
                case 0: {
                    ECFieldElement X1Squared = X1.square();
                    ECFieldElement gamma = this.three(X1Squared).add(this.getCurve().getA()).divide(this.two(Y1));
                    ECFieldElement X3 = gamma.square().subtract(this.two(X1));
                    ECFieldElement Y3 = gamma.multiply(X1.subtract(X3)).subtract(Y1);
                    return new Fp(curve, X3, Y3);
                }
                case 1: {
                    ECFieldElement Z1 = this.zs[0];
                    boolean Z1IsOne = Z1.isOne();
                    ECFieldElement w = curve.getA();
                    if (!w.isZero() && !Z1IsOne) {
                        w = w.multiply(Z1.square());
                    }
                    w = w.add(this.three(X1.square()));
                    ECFieldElement s = Z1IsOne ? Y1 : Y1.multiply(Z1);
                    ECFieldElement t = Z1IsOne ? Y1.square() : s.multiply(Y1);
                    ECFieldElement B = X1.multiply(t);
                    ECFieldElement _4B = this.four(B);
                    ECFieldElement h = w.square().subtract(this.two(_4B));
                    ECFieldElement _2s = this.two(s);
                    ECFieldElement X3 = h.multiply(_2s);
                    ECFieldElement _2t = this.two(t);
                    ECFieldElement Y3 = _4B.subtract(h).multiply(w).subtract(this.two(_2t.square()));
                    ECFieldElement _4sSquared = Z1IsOne ? this.two(_2t) : _2s.square();
                    ECFieldElement Z3 = this.two(_4sSquared).multiply(s);
                    return new Fp(curve, X3, Y3, new ECFieldElement[]{Z3});
                }
                case 2: {
                    ECFieldElement S;
                    ECFieldElement M;
                    ECFieldElement Z1 = this.zs[0];
                    boolean Z1IsOne = Z1.isOne();
                    ECFieldElement Y1Squared = Y1.square();
                    ECFieldElement T = Y1Squared.square();
                    ECFieldElement a4 = curve.getA();
                    ECFieldElement a4Neg = a4.negate();
                    if (a4Neg.toBigInteger().equals(BigInteger.valueOf(3L))) {
                        ECFieldElement Z1Squared = Z1IsOne ? Z1 : Z1.square();
                        M = this.three(X1.add(Z1Squared).multiply(X1.subtract(Z1Squared)));
                        S = this.four(Y1Squared.multiply(X1));
                    } else {
                        ECFieldElement X1Squared = X1.square();
                        M = this.three(X1Squared);
                        if (Z1IsOne) {
                            M = M.add(a4);
                        } else if (!a4.isZero()) {
                            ECFieldElement Z1Squared = Z1.square();
                            ECFieldElement Z1Pow4 = Z1Squared.square();
                            M = a4Neg.bitLength() < a4.bitLength() ? M.subtract(Z1Pow4.multiply(a4Neg)) : M.add(Z1Pow4.multiply(a4));
                        }
                        S = this.four(X1.multiply(Y1Squared));
                    }
                    ECFieldElement X3 = M.square().subtract(this.two(S));
                    ECFieldElement Y3 = S.subtract(X3).multiply(M).subtract(this.eight(T));
                    ECFieldElement Z3 = this.two(Y1);
                    if (!Z1IsOne) {
                        Z3 = Z3.multiply(Z1);
                    }
                    return new Fp(curve, X3, Y3, new ECFieldElement[]{Z3});
                }
                case 4: {
                    return this.twiceJacobianModified(true);
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }

        @Override
        public ECPoint twicePlus(ECPoint b) {
            if (this == b) {
                return this.threeTimes();
            }
            if (this.isInfinity()) {
                return b;
            }
            if (b.isInfinity()) {
                return this.twice();
            }
            ECFieldElement Y1 = this.y;
            if (Y1.isZero()) {
                return b;
            }
            ECCurve curve = this.getCurve();
            int coord = curve.getCoordinateSystem();
            switch (coord) {
                case 0: {
                    ECFieldElement X1 = this.x;
                    ECFieldElement X2 = b.x;
                    ECFieldElement Y2 = b.y;
                    ECFieldElement dx = X2.subtract(X1);
                    ECFieldElement dy = Y2.subtract(Y1);
                    if (dx.isZero()) {
                        if (dy.isZero()) {
                            return this.threeTimes();
                        }
                        return this;
                    }
                    ECFieldElement X = dx.square();
                    ECFieldElement Y = dy.square();
                    ECFieldElement d = X.multiply(this.two(X1).add(X2)).subtract(Y);
                    if (d.isZero()) {
                        return curve.getInfinity();
                    }
                    ECFieldElement D = d.multiply(dx);
                    ECFieldElement I = D.invert();
                    ECFieldElement L1 = d.multiply(I).multiply(dy);
                    ECFieldElement L2 = this.two(Y1).multiply(X).multiply(dx).multiply(I).subtract(L1);
                    ECFieldElement X4 = L2.subtract(L1).multiply(L1.add(L2)).add(X2);
                    ECFieldElement Y4 = X1.subtract(X4).multiply(L2).subtract(Y1);
                    return new Fp(curve, X4, Y4);
                }
                case 4: {
                    return this.twiceJacobianModified(false).add(b);
                }
            }
            return this.twice().add(b);
        }

        @Override
        public ECPoint threeTimes() {
            if (this.isInfinity()) {
                return this;
            }
            ECFieldElement Y1 = this.y;
            if (Y1.isZero()) {
                return this;
            }
            ECCurve curve = this.getCurve();
            int coord = curve.getCoordinateSystem();
            switch (coord) {
                case 0: {
                    ECFieldElement X1 = this.x;
                    ECFieldElement _2Y1 = this.two(Y1);
                    ECFieldElement X = _2Y1.square();
                    ECFieldElement Z = this.three(X1.square()).add(this.getCurve().getA());
                    ECFieldElement Y = Z.square();
                    ECFieldElement d = this.three(X1).multiply(X).subtract(Y);
                    if (d.isZero()) {
                        return this.getCurve().getInfinity();
                    }
                    ECFieldElement D = d.multiply(_2Y1);
                    ECFieldElement I = D.invert();
                    ECFieldElement L1 = d.multiply(I).multiply(Z);
                    ECFieldElement L2 = X.square().multiply(I).subtract(L1);
                    ECFieldElement X4 = L2.subtract(L1).multiply(L1.add(L2)).add(X1);
                    ECFieldElement Y4 = X1.subtract(X4).multiply(L2).subtract(Y1);
                    return new Fp(curve, X4, Y4);
                }
                case 4: {
                    return this.twiceJacobianModified(false).add(this);
                }
            }
            return this.twice().add(this);
        }

        @Override
        public ECPoint timesPow2(int e) {
            ECFieldElement Z1;
            if (e < 0) {
                throw new IllegalArgumentException("'e' cannot be negative");
            }
            if (e == 0 || this.isInfinity()) {
                return this;
            }
            if (e == 1) {
                return this.twice();
            }
            ECCurve curve = this.getCurve();
            ECFieldElement Y1 = this.y;
            if (Y1.isZero()) {
                return curve.getInfinity();
            }
            int coord = curve.getCoordinateSystem();
            ECFieldElement W1 = curve.getA();
            ECFieldElement X1 = this.x;
            ECFieldElement eCFieldElement = Z1 = this.zs.length < 1 ? curve.fromBigInteger(ECConstants.ONE) : this.zs[0];
            if (!Z1.isOne()) {
                switch (coord) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        ECFieldElement Z1Sq = Z1.square();
                        X1 = X1.multiply(Z1);
                        Y1 = Y1.multiply(Z1Sq);
                        W1 = this.calculateJacobianModifiedW(Z1, Z1Sq);
                        break;
                    }
                    case 2: {
                        W1 = this.calculateJacobianModifiedW(Z1, null);
                        break;
                    }
                    case 4: {
                        W1 = this.getJacobianModifiedW();
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unsupported coordinate system");
                    }
                }
            }
            for (int i = 0; i < e; ++i) {
                if (Y1.isZero()) {
                    return curve.getInfinity();
                }
                ECFieldElement X1Squared = X1.square();
                ECFieldElement M = this.three(X1Squared);
                ECFieldElement _2Y1 = this.two(Y1);
                ECFieldElement _2Y1Squared = _2Y1.multiply(Y1);
                ECFieldElement S = this.two(X1.multiply(_2Y1Squared));
                ECFieldElement _4T = _2Y1Squared.square();
                ECFieldElement _8T = this.two(_4T);
                if (!W1.isZero()) {
                    M = M.add(W1);
                    W1 = this.two(_8T.multiply(W1));
                }
                X1 = M.square().subtract(this.two(S));
                Y1 = M.multiply(S.subtract(X1)).subtract(_8T);
                Z1 = Z1.isOne() ? _2Y1 : _2Y1.multiply(Z1);
            }
            switch (coord) {
                case 0: {
                    ECFieldElement zInv = Z1.invert();
                    ECFieldElement zInv2 = zInv.square();
                    ECFieldElement zInv3 = zInv2.multiply(zInv);
                    return new Fp(curve, X1.multiply(zInv2), Y1.multiply(zInv3));
                }
                case 1: {
                    X1 = X1.multiply(Z1);
                    Z1 = Z1.multiply(Z1.square());
                    return new Fp(curve, X1, Y1, new ECFieldElement[]{Z1});
                }
                case 2: {
                    return new Fp(curve, X1, Y1, new ECFieldElement[]{Z1});
                }
                case 4: {
                    return new Fp(curve, X1, Y1, new ECFieldElement[]{Z1, W1});
                }
            }
            throw new IllegalStateException("unsupported coordinate system");
        }

        protected ECFieldElement two(ECFieldElement x) {
            return x.add(x);
        }

        protected ECFieldElement three(ECFieldElement x) {
            return this.two(x).add(x);
        }

        protected ECFieldElement four(ECFieldElement x) {
            return this.two(this.two(x));
        }

        protected ECFieldElement eight(ECFieldElement x) {
            return this.four(this.two(x));
        }

        protected ECFieldElement doubleProductFromSquares(ECFieldElement a, ECFieldElement b, ECFieldElement aSquared, ECFieldElement bSquared) {
            return a.add(b).square().subtract(aSquared).subtract(bSquared);
        }

        @Override
        public ECPoint negate() {
            if (this.isInfinity()) {
                return this;
            }
            ECCurve curve = this.getCurve();
            int coord = curve.getCoordinateSystem();
            if (0 != coord) {
                return new Fp(curve, this.x, this.y.negate(), this.zs);
            }
            return new Fp(curve, this.x, this.y.negate());
        }

        protected ECFieldElement calculateJacobianModifiedW(ECFieldElement Z, ECFieldElement ZSquared) {
            ECFieldElement a4 = this.getCurve().getA();
            if (a4.isZero() || Z.isOne()) {
                return a4;
            }
            if (ZSquared == null) {
                ZSquared = Z.square();
            }
            ECFieldElement W = ZSquared.square();
            ECFieldElement a4Neg = a4.negate();
            W = a4Neg.bitLength() < a4.bitLength() ? W.multiply(a4Neg).negate() : W.multiply(a4);
            return W;
        }

        protected ECFieldElement getJacobianModifiedW() {
            ECFieldElement W = this.zs[1];
            if (W == null) {
                this.zs[1] = W = this.calculateJacobianModifiedW(this.zs[0], null);
            }
            return W;
        }

        protected Fp twiceJacobianModified(boolean calculateW) {
            ECFieldElement X1 = this.x;
            ECFieldElement Y1 = this.y;
            ECFieldElement Z1 = this.zs[0];
            ECFieldElement W1 = this.getJacobianModifiedW();
            ECFieldElement X1Squared = X1.square();
            ECFieldElement M = this.three(X1Squared).add(W1);
            ECFieldElement _2Y1 = this.two(Y1);
            ECFieldElement _2Y1Squared = _2Y1.multiply(Y1);
            ECFieldElement S = this.two(X1.multiply(_2Y1Squared));
            ECFieldElement X3 = M.square().subtract(this.two(S));
            ECFieldElement _4T = _2Y1Squared.square();
            ECFieldElement _8T = this.two(_4T);
            ECFieldElement Y3 = M.multiply(S.subtract(X3)).subtract(_8T);
            ECFieldElement W3 = calculateW ? this.two(_8T.multiply(W1)) : null;
            ECFieldElement Z3 = Z1.isOne() ? _2Y1 : _2Y1.multiply(Z1);
            return new Fp(this.getCurve(), X3, Y3, new ECFieldElement[]{Z3, W3});
        }
    }
}

