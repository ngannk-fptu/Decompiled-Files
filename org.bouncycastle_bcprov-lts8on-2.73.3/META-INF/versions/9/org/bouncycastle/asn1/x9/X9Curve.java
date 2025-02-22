/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x9.X9FieldElement;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class X9Curve
extends ASN1Object
implements X9ObjectIdentifiers {
    private ECCurve curve;
    private byte[] seed;
    private ASN1ObjectIdentifier fieldIdentifier = null;

    public X9Curve(ECCurve curve) {
        this(curve, null);
    }

    public X9Curve(ECCurve curve, byte[] seed) {
        this.curve = curve;
        this.seed = Arrays.clone(seed);
        this.setFieldIdentifier();
    }

    public X9Curve(X9FieldID fieldID, BigInteger order, BigInteger cofactor, ASN1Sequence seq) {
        this.fieldIdentifier = fieldID.getIdentifier();
        if (this.fieldIdentifier.equals(prime_field)) {
            BigInteger p = ((ASN1Integer)fieldID.getParameters()).getValue();
            BigInteger A = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
            BigInteger B = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets());
            this.curve = new ECCurve.Fp(p, A, B, order, cofactor);
        } else if (this.fieldIdentifier.equals(characteristic_two_field)) {
            ASN1Sequence parameters = ASN1Sequence.getInstance(fieldID.getParameters());
            int m = ((ASN1Integer)parameters.getObjectAt(0)).intValueExact();
            ASN1ObjectIdentifier representation = (ASN1ObjectIdentifier)parameters.getObjectAt(1);
            int k1 = 0;
            int k2 = 0;
            int k3 = 0;
            if (representation.equals(tpBasis)) {
                k1 = ASN1Integer.getInstance(parameters.getObjectAt(2)).intValueExact();
            } else if (representation.equals(ppBasis)) {
                ASN1Sequence pentanomial = ASN1Sequence.getInstance(parameters.getObjectAt(2));
                k1 = ASN1Integer.getInstance(pentanomial.getObjectAt(0)).intValueExact();
                k2 = ASN1Integer.getInstance(pentanomial.getObjectAt(1)).intValueExact();
                k3 = ASN1Integer.getInstance(pentanomial.getObjectAt(2)).intValueExact();
            } else {
                throw new IllegalArgumentException("This type of EC basis is not implemented");
            }
            BigInteger A = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
            BigInteger B = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets());
            this.curve = new ECCurve.F2m(m, k1, k2, k3, A, B, order, cofactor);
        } else {
            throw new IllegalArgumentException("This type of ECCurve is not implemented");
        }
        if (seq.size() == 3) {
            this.seed = ((DERBitString)seq.getObjectAt(2)).getBytes();
        }
    }

    private void setFieldIdentifier() {
        if (ECAlgorithms.isFpCurve(this.curve)) {
            this.fieldIdentifier = prime_field;
        } else if (ECAlgorithms.isF2mCurve(this.curve)) {
            this.fieldIdentifier = characteristic_two_field;
        } else {
            throw new IllegalArgumentException("This type of ECCurve is not implemented");
        }
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.fieldIdentifier.equals(prime_field)) {
            v.add(new X9FieldElement(this.curve.getA()).toASN1Primitive());
            v.add(new X9FieldElement(this.curve.getB()).toASN1Primitive());
        } else if (this.fieldIdentifier.equals(characteristic_two_field)) {
            v.add(new X9FieldElement(this.curve.getA()).toASN1Primitive());
            v.add(new X9FieldElement(this.curve.getB()).toASN1Primitive());
        }
        if (this.seed != null) {
            v.add(new DERBitString(this.seed));
        }
        return new DERSequence(v);
    }
}

