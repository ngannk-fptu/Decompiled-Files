/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x9.X9Curve;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class X9ECParameters
extends ASN1Object
implements X9ObjectIdentifiers {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private X9FieldID fieldID;
    private ECCurve curve;
    private X9ECPoint g;
    private BigInteger n;
    private BigInteger h;
    private byte[] seed;

    private X9ECParameters(ASN1Sequence seq) {
        if (!(seq.getObjectAt(0) instanceof ASN1Integer) || !((ASN1Integer)seq.getObjectAt(0)).hasValue(1)) {
            throw new IllegalArgumentException("bad version in X9ECParameters");
        }
        this.n = ((ASN1Integer)seq.getObjectAt(4)).getValue();
        if (seq.size() == 6) {
            this.h = ((ASN1Integer)seq.getObjectAt(5)).getValue();
        }
        X9Curve x9c = new X9Curve(X9FieldID.getInstance(seq.getObjectAt(1)), this.n, this.h, ASN1Sequence.getInstance(seq.getObjectAt(2)));
        this.curve = x9c.getCurve();
        ASN1Encodable p = seq.getObjectAt(3);
        this.g = p instanceof X9ECPoint ? (X9ECPoint)p : new X9ECPoint(this.curve, (ASN1OctetString)p);
        this.seed = x9c.getSeed();
    }

    public static X9ECParameters getInstance(Object obj) {
        if (obj instanceof X9ECParameters) {
            return (X9ECParameters)obj;
        }
        if (obj != null) {
            return new X9ECParameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public X9ECParameters(ECCurve curve, X9ECPoint g, BigInteger n) {
        this(curve, g, n, null, null);
    }

    public X9ECParameters(ECCurve curve, X9ECPoint g, BigInteger n, BigInteger h) {
        this(curve, g, n, h, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public X9ECParameters(ECCurve curve, X9ECPoint g, BigInteger n, BigInteger h, byte[] seed) {
        this.curve = curve;
        this.g = g;
        this.n = n;
        this.h = h;
        this.seed = Arrays.clone(seed);
        if (ECAlgorithms.isFpCurve(curve)) {
            this.fieldID = new X9FieldID(curve.getField().getCharacteristic());
            return;
        } else {
            if (!ECAlgorithms.isF2mCurve(curve)) throw new IllegalArgumentException("'curve' is of an unsupported type");
            PolynomialExtensionField field = (PolynomialExtensionField)curve.getField();
            int[] exponents = field.getMinimalPolynomial().getExponentsPresent();
            if (exponents.length == 3) {
                this.fieldID = new X9FieldID(exponents[2], exponents[1]);
                return;
            } else {
                if (exponents.length != 5) throw new IllegalArgumentException("Only trinomial and pentomial curves are supported");
                this.fieldID = new X9FieldID(exponents[4], exponents[1], exponents[2], exponents[3]);
            }
        }
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public ECPoint getG() {
        return this.g.getPoint();
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getH() {
        return this.h;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    public boolean hasSeed() {
        return null != this.seed;
    }

    public X9Curve getCurveEntry() {
        return new X9Curve(this.curve, this.seed);
    }

    public X9FieldID getFieldIDEntry() {
        return this.fieldID;
    }

    public X9ECPoint getBaseEntry() {
        return this.g;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(6);
        v.add(new ASN1Integer(ONE));
        v.add(this.fieldID);
        v.add(new X9Curve(this.curve, this.seed));
        v.add(this.g);
        v.add(new ASN1Integer(this.n));
        if (this.h != null) {
            v.add(new ASN1Integer(this.h));
        }
        return new DERSequence(v);
    }
}

