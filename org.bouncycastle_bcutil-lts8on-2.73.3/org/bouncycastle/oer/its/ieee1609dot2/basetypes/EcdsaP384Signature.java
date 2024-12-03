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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.util.Arrays;

public class EcdsaP384Signature
extends ASN1Object {
    private final EccP384CurvePoint rSig;
    private final ASN1OctetString sSig;

    public EcdsaP384Signature(EccP384CurvePoint rSig, ASN1OctetString sSig) {
        this.rSig = rSig;
        this.sSig = sSig;
    }

    private EcdsaP384Signature(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.rSig = EccP384CurvePoint.getInstance(sequence.getObjectAt(0));
        this.sSig = ASN1OctetString.getInstance((Object)sequence.getObjectAt(1));
    }

    public static EcdsaP384Signature getInstance(Object object) {
        if (object instanceof EcdsaP384Signature) {
            return (EcdsaP384Signature)((Object)object);
        }
        if (object != null) {
            return new EcdsaP384Signature(ASN1Sequence.getInstance((Object)object));
        }
        return null;
    }

    public EccP384CurvePoint getRSig() {
        return this.rSig;
    }

    public ASN1OctetString getSSig() {
        return this.sSig;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.rSig, this.sSig});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EccP384CurvePoint rSig;
        private ASN1OctetString sSig;

        public Builder setRSig(EccP384CurvePoint rSig) {
            this.rSig = rSig;
            return this;
        }

        public Builder setSSig(ASN1OctetString sSig) {
            this.sSig = sSig;
            return this;
        }

        public Builder setSSig(byte[] sSig) {
            return this.setSSig((ASN1OctetString)new DEROctetString(Arrays.clone((byte[])sSig)));
        }

        public EcdsaP384Signature createEcdsaP384Signature() {
            return new EcdsaP384Signature(this.rSig, this.sSig);
        }
    }
}

