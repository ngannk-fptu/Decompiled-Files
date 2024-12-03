/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.util.Arrays;

public class DSTU4145Params
extends ASN1Object {
    private static final byte[] DEFAULT_DKE = new byte[]{-87, -42, -21, 69, -15, 60, 112, -126, -128, -60, -106, 123, 35, 31, 94, -83, -10, 88, -21, -92, -64, 55, 41, 29, 56, -39, 107, -16, 37, -54, 78, 23, -8, -23, 114, 13, -58, 21, -76, 58, 40, -105, 95, 11, -63, -34, -93, 100, 56, -75, 100, -22, 44, 23, -97, -48, 18, 62, 109, -72, -6, -59, 121, 4};
    private ASN1ObjectIdentifier namedCurve;
    private DSTU4145ECBinary ecbinary;
    private byte[] dke = DEFAULT_DKE;

    public DSTU4145Params(ASN1ObjectIdentifier namedCurve) {
        this.namedCurve = namedCurve;
    }

    public DSTU4145Params(ASN1ObjectIdentifier namedCurve, byte[] dke) {
        this.namedCurve = namedCurve;
        this.dke = Arrays.clone(dke);
    }

    public DSTU4145Params(DSTU4145ECBinary ecbinary) {
        this.ecbinary = ecbinary;
    }

    public boolean isNamedCurve() {
        return this.namedCurve != null;
    }

    public DSTU4145ECBinary getECBinary() {
        return this.ecbinary;
    }

    public byte[] getDKE() {
        return Arrays.clone(this.dke);
    }

    public static byte[] getDefaultDKE() {
        return Arrays.clone(DEFAULT_DKE);
    }

    public ASN1ObjectIdentifier getNamedCurve() {
        return this.namedCurve;
    }

    public static DSTU4145Params getInstance(Object obj) {
        if (obj instanceof DSTU4145Params) {
            return (DSTU4145Params)obj;
        }
        if (obj != null) {
            ASN1Sequence seq = ASN1Sequence.getInstance(obj);
            DSTU4145Params params = seq.getObjectAt(0) instanceof ASN1ObjectIdentifier ? new DSTU4145Params(ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0))) : new DSTU4145Params(DSTU4145ECBinary.getInstance(seq.getObjectAt(0)));
            if (seq.size() == 2) {
                params.dke = ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets();
                if (params.dke.length != DEFAULT_DKE.length) {
                    throw new IllegalArgumentException("object parse error");
                }
            }
            return params;
        }
        throw new IllegalArgumentException("object parse error");
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.namedCurve != null) {
            v.add(this.namedCurve);
        } else {
            v.add(this.ecbinary);
        }
        if (!Arrays.areEqual(this.dke, DEFAULT_DKE)) {
            v.add(new DEROctetString(this.dke));
        }
        return new DERSequence(v);
    }
}

