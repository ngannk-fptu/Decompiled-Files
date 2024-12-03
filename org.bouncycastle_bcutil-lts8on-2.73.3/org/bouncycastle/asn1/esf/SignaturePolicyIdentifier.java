/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERNull
 */
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.esf.SignaturePolicyId;

public class SignaturePolicyIdentifier
extends ASN1Object {
    private SignaturePolicyId signaturePolicyId;
    private boolean isSignaturePolicyImplied;

    public static SignaturePolicyIdentifier getInstance(Object obj) {
        if (obj instanceof SignaturePolicyIdentifier) {
            return (SignaturePolicyIdentifier)((Object)obj);
        }
        if (obj instanceof ASN1Null || SignaturePolicyIdentifier.hasEncodedTagValue((Object)obj, (int)5)) {
            return new SignaturePolicyIdentifier();
        }
        if (obj != null) {
            return new SignaturePolicyIdentifier(SignaturePolicyId.getInstance(obj));
        }
        return null;
    }

    public SignaturePolicyIdentifier() {
        this.isSignaturePolicyImplied = true;
    }

    public SignaturePolicyIdentifier(SignaturePolicyId signaturePolicyId) {
        this.signaturePolicyId = signaturePolicyId;
        this.isSignaturePolicyImplied = false;
    }

    public SignaturePolicyId getSignaturePolicyId() {
        return this.signaturePolicyId;
    }

    public boolean isSignaturePolicyImplied() {
        return this.isSignaturePolicyImplied;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.isSignaturePolicyImplied) {
            return DERNull.INSTANCE;
        }
        return this.signaturePolicyId.toASN1Primitive();
    }
}

