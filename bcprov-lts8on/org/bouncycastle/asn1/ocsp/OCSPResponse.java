/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.ocsp.ResponseBytes;

public class OCSPResponse
extends ASN1Object {
    OCSPResponseStatus responseStatus;
    ResponseBytes responseBytes;

    public OCSPResponse(OCSPResponseStatus responseStatus, ResponseBytes responseBytes) {
        this.responseStatus = responseStatus;
        this.responseBytes = responseBytes;
    }

    private OCSPResponse(ASN1Sequence seq) {
        this.responseStatus = OCSPResponseStatus.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.responseBytes = ResponseBytes.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true);
        }
    }

    public static OCSPResponse getInstance(ASN1TaggedObject obj, boolean explicit) {
        return OCSPResponse.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static OCSPResponse getInstance(Object obj) {
        if (obj instanceof OCSPResponse) {
            return (OCSPResponse)obj;
        }
        if (obj != null) {
            return new OCSPResponse(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public OCSPResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public ResponseBytes getResponseBytes() {
        return this.responseBytes;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.responseStatus);
        if (this.responseBytes != null) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.responseBytes));
        }
        return new DERSequence(v);
    }
}

