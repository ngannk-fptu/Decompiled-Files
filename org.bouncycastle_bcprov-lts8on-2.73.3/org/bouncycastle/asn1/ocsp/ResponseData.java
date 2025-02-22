/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.x509.Extensions;

public class ResponseData
extends ASN1Object {
    private static final ASN1Integer V1 = new ASN1Integer(0L);
    private boolean versionPresent;
    private ASN1Integer version;
    private ResponderID responderID;
    private ASN1GeneralizedTime producedAt;
    private ASN1Sequence responses;
    private Extensions responseExtensions;

    public ResponseData(ASN1Integer version, ResponderID responderID, ASN1GeneralizedTime producedAt, ASN1Sequence responses, Extensions responseExtensions) {
        this.version = version;
        this.responderID = responderID;
        this.producedAt = producedAt;
        this.responses = responses;
        this.responseExtensions = responseExtensions;
    }

    public ResponseData(ResponderID responderID, ASN1GeneralizedTime producedAt, ASN1Sequence responses, Extensions responseExtensions) {
        this(V1, responderID, producedAt, responses, responseExtensions);
    }

    private ResponseData(ASN1Sequence seq) {
        int index = 0;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(0);
            if (o.getTagNo() == 0) {
                this.versionPresent = true;
                this.version = ASN1Integer.getInstance((ASN1TaggedObject)seq.getObjectAt(0), true);
            } else {
                this.version = V1;
            }
        } else {
            this.version = V1;
        }
        int n = ++index;
        this.responderID = ResponderID.getInstance(seq.getObjectAt(n));
        int n2 = ++index;
        this.producedAt = ASN1GeneralizedTime.getInstance(seq.getObjectAt(n2));
        int n3 = ++index;
        this.responses = (ASN1Sequence)seq.getObjectAt(n3);
        if (seq.size() > ++index) {
            this.responseExtensions = Extensions.getInstance((ASN1TaggedObject)seq.getObjectAt(index), true);
        }
    }

    public static ResponseData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return ResponseData.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static ResponseData getInstance(Object obj) {
        if (obj instanceof ResponseData) {
            return (ResponseData)obj;
        }
        if (obj != null) {
            return new ResponseData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ResponderID getResponderID() {
        return this.responderID;
    }

    public ASN1GeneralizedTime getProducedAt() {
        return this.producedAt;
    }

    public ASN1Sequence getResponses() {
        return this.responses;
    }

    public Extensions getResponseExtensions() {
        return this.responseExtensions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        if (this.versionPresent || !this.version.equals(V1)) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.version));
        }
        v.add(this.responderID);
        v.add(this.producedAt);
        v.add(this.responses);
        if (this.responseExtensions != null) {
            v.add(new DERTaggedObject(true, 1, (ASN1Encodable)this.responseExtensions));
        }
        return new DERSequence(v);
    }
}

