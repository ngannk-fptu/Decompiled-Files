/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class AltSignatureValue
extends ASN1Object {
    private final ASN1BitString signature;

    public static AltSignatureValue getInstance(ASN1TaggedObject obj, boolean explicit) {
        return AltSignatureValue.getInstance(ASN1BitString.getInstance(obj, explicit));
    }

    public static AltSignatureValue getInstance(Object obj) {
        if (obj instanceof AltSignatureValue) {
            return (AltSignatureValue)obj;
        }
        if (obj != null) {
            return new AltSignatureValue(ASN1BitString.getInstance(obj));
        }
        return null;
    }

    public static AltSignatureValue fromExtensions(Extensions extensions) {
        return AltSignatureValue.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.altSignatureValue));
    }

    private AltSignatureValue(ASN1BitString signature) {
        this.signature = signature;
    }

    public AltSignatureValue(byte[] signature) {
        this.signature = new DERBitString(signature);
    }

    public ASN1BitString getSignature() {
        return this.signature;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.signature;
    }
}

