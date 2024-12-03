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
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.util.Arrays;

public class EcdsaP256Signature
extends ASN1Object {
    private final EccP256CurvePoint rSig;
    private final ASN1OctetString sSig;

    public EcdsaP256Signature(EccP256CurvePoint rSig, ASN1OctetString sSig) {
        this.rSig = rSig;
        this.sSig = sSig;
    }

    private EcdsaP256Signature(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.rSig = EccP256CurvePoint.getInstance(sequence.getObjectAt(0));
        this.sSig = ASN1OctetString.getInstance((Object)sequence.getObjectAt(1));
    }

    public static EcdsaP256Signature getInstance(Object object) {
        if (object instanceof EcdsaP256Signature) {
            return (EcdsaP256Signature)((Object)object);
        }
        if (object != null) {
            return new EcdsaP256Signature(ASN1Sequence.getInstance((Object)object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public EccP256CurvePoint getRSig() {
        return this.rSig;
    }

    public ASN1OctetString getSSig() {
        return this.sSig;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.rSig, this.sSig});
    }

    public static class Builder {
        private EccP256CurvePoint rSig;
        private ASN1OctetString sSig;

        public Builder setRSig(EccP256CurvePoint rSig) {
            this.rSig = rSig;
            return this;
        }

        public Builder setSSig(byte[] sSig) {
            this.sSig = new DEROctetString(Arrays.clone((byte[])sSig));
            return this;
        }

        public Builder setSSig(ASN1OctetString sSig) {
            this.sSig = sSig;
            return this;
        }

        public EcdsaP256Signature createEcdsaP256Signature() {
            return new EcdsaP256Signature(this.rSig, this.sSig);
        }
    }
}

