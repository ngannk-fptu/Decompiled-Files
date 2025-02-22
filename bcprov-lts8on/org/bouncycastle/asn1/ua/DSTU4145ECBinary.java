/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ua;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class DSTU4145ECBinary
extends ASN1Object {
    BigInteger version = BigInteger.valueOf(0L);
    DSTU4145BinaryField f;
    ASN1Integer a;
    ASN1OctetString b;
    ASN1Integer n;
    ASN1OctetString bp;

    public DSTU4145ECBinary(ECDomainParameters params) {
        ECCurve curve = params.getCurve();
        if (!ECAlgorithms.isF2mCurve(curve)) {
            throw new IllegalArgumentException("only binary domain is possible");
        }
        PolynomialExtensionField field = (PolynomialExtensionField)curve.getField();
        int[] exponents = field.getMinimalPolynomial().getExponentsPresent();
        if (exponents.length == 3) {
            this.f = new DSTU4145BinaryField(exponents[2], exponents[1]);
        } else if (exponents.length == 5) {
            this.f = new DSTU4145BinaryField(exponents[4], exponents[1], exponents[2], exponents[3]);
        } else {
            throw new IllegalArgumentException("curve must have a trinomial or pentanomial basis");
        }
        this.a = new ASN1Integer(curve.getA().toBigInteger());
        this.b = new DEROctetString(curve.getB().getEncoded());
        this.n = new ASN1Integer(params.getN());
        this.bp = new DEROctetString(DSTU4145PointEncoder.encodePoint(params.getG()));
    }

    private DSTU4145ECBinary(ASN1Sequence seq) {
        int index = 0;
        if (seq.getObjectAt(index) instanceof ASN1TaggedObject) {
            ASN1TaggedObject taggedVersion = (ASN1TaggedObject)seq.getObjectAt(index);
            if (taggedVersion.isExplicit() && 0 == taggedVersion.getTagNo()) {
                this.version = ASN1Integer.getInstance(taggedVersion.getLoadedObject()).getValue();
                ++index;
            } else {
                throw new IllegalArgumentException("object parse error");
            }
        }
        this.f = DSTU4145BinaryField.getInstance(seq.getObjectAt(index));
        this.a = ASN1Integer.getInstance(seq.getObjectAt(++index));
        this.b = ASN1OctetString.getInstance(seq.getObjectAt(++index));
        this.n = ASN1Integer.getInstance(seq.getObjectAt(++index));
        this.bp = ASN1OctetString.getInstance(seq.getObjectAt(++index));
    }

    public static DSTU4145ECBinary getInstance(Object obj) {
        if (obj instanceof DSTU4145ECBinary) {
            return (DSTU4145ECBinary)obj;
        }
        if (obj != null) {
            return new DSTU4145ECBinary(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DSTU4145BinaryField getField() {
        return this.f;
    }

    public BigInteger getA() {
        return this.a.getValue();
    }

    public byte[] getB() {
        return Arrays.clone(this.b.getOctets());
    }

    public BigInteger getN() {
        return this.n.getValue();
    }

    public byte[] getG() {
        return Arrays.clone(this.bp.getOctets());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(6);
        if (0 != this.version.compareTo(BigInteger.valueOf(0L))) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)new ASN1Integer(this.version)));
        }
        v.add(this.f);
        v.add(this.a);
        v.add(this.b);
        v.add(this.n);
        v.add(this.bp);
        return new DERSequence(v);
    }
}

