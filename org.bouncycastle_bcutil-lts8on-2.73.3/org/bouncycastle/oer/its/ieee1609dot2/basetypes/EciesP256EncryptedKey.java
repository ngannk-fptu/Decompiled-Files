/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.util.Arrays;

public class EciesP256EncryptedKey
extends ASN1Object {
    private final EccP256CurvePoint v;
    private final ASN1OctetString c;
    private final ASN1OctetString t;

    public EciesP256EncryptedKey(EccP256CurvePoint v, ASN1OctetString c, ASN1OctetString t) {
        this.v = v;
        this.c = c;
        this.t = t;
    }

    public static EciesP256EncryptedKey getInstance(Object o) {
        if (o instanceof EciesP256EncryptedKey) {
            return (EciesP256EncryptedKey)((Object)o);
        }
        if (o != null) {
            return new EciesP256EncryptedKey(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private EciesP256EncryptedKey(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.v = EccP256CurvePoint.getInstance(seq.getObjectAt(0));
        this.c = ASN1OctetString.getInstance((Object)seq.getObjectAt(1));
        this.t = ASN1OctetString.getInstance((Object)seq.getObjectAt(2));
    }

    public EccP256CurvePoint getV() {
        return this.v;
    }

    public ASN1OctetString getC() {
        return this.c;
    }

    public ASN1OctetString getT() {
        return this.t;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.v, this.c, this.t});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EccP256CurvePoint v;
        private ASN1OctetString c;
        private ASN1OctetString t;

        public Builder setV(EccP256CurvePoint v) {
            this.v = v;
            return this;
        }

        public Builder setC(ASN1OctetString c) {
            this.c = c;
            return this;
        }

        public Builder setC(byte[] c) {
            this.c = new DEROctetString(Arrays.clone((byte[])c));
            return this;
        }

        public Builder setT(ASN1OctetString t) {
            this.t = t;
            return this;
        }

        public Builder setT(byte[] t) {
            this.t = new DEROctetString(Arrays.clone((byte[])t));
            return this;
        }

        public EciesP256EncryptedKey createEciesP256EncryptedKey() {
            return new EciesP256EncryptedKey(this.v, this.c, this.t);
        }
    }
}

