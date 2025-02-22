/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class ResponseBytes
extends ASN1Object {
    ASN1ObjectIdentifier responseType;
    ASN1OctetString response;

    public ResponseBytes(ASN1ObjectIdentifier responseType, ASN1OctetString response) {
        this.responseType = responseType;
        this.response = response;
    }

    private ResponseBytes(ASN1Sequence seq) {
        this.responseType = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        this.response = (ASN1OctetString)seq.getObjectAt(1);
    }

    public static ResponseBytes getInstance(ASN1TaggedObject obj, boolean explicit) {
        return ResponseBytes.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static ResponseBytes getInstance(Object obj) {
        if (obj instanceof ResponseBytes) {
            return (ResponseBytes)obj;
        }
        if (obj != null) {
            return new ResponseBytes(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getResponseType() {
        return this.responseType;
    }

    public ASN1OctetString getResponse() {
        return this.response;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.responseType);
        v.add(this.response);
        return new DERSequence(v);
    }
}

