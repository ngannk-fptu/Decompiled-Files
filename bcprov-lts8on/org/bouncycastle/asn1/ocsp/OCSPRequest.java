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
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.ocsp.TBSRequest;

public class OCSPRequest
extends ASN1Object {
    TBSRequest tbsRequest;
    Signature optionalSignature;

    public OCSPRequest(TBSRequest tbsRequest, Signature optionalSignature) {
        this.tbsRequest = tbsRequest;
        this.optionalSignature = optionalSignature;
    }

    private OCSPRequest(ASN1Sequence seq) {
        this.tbsRequest = TBSRequest.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.optionalSignature = Signature.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true);
        }
    }

    public static OCSPRequest getInstance(ASN1TaggedObject obj, boolean explicit) {
        return OCSPRequest.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static OCSPRequest getInstance(Object obj) {
        if (obj instanceof OCSPRequest) {
            return (OCSPRequest)obj;
        }
        if (obj != null) {
            return new OCSPRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TBSRequest getTbsRequest() {
        return this.tbsRequest;
    }

    public Signature getOptionalSignature() {
        return this.optionalSignature;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.tbsRequest);
        if (this.optionalSignature != null) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.optionalSignature));
        }
        return new DERSequence(v);
    }
}

