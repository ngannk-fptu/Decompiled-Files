/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

public class TBSRequest
extends ASN1Object {
    private static final ASN1Integer V1 = new ASN1Integer(0L);
    ASN1Integer version;
    GeneralName requestorName;
    ASN1Sequence requestList;
    Extensions requestExtensions;
    boolean versionSet;

    public TBSRequest(GeneralName requestorName, ASN1Sequence requestList, Extensions requestExtensions) {
        this.version = V1;
        this.requestorName = requestorName;
        this.requestList = requestList;
        this.requestExtensions = requestExtensions;
    }

    private TBSRequest(ASN1Sequence seq) {
        int index = 0;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(0);
            if (o.getTagNo() == 0) {
                this.versionSet = true;
                this.version = ASN1Integer.getInstance((ASN1TaggedObject)seq.getObjectAt(0), true);
                ++index;
            } else {
                this.version = V1;
            }
        } else {
            this.version = V1;
        }
        if (seq.getObjectAt(index) instanceof ASN1TaggedObject) {
            this.requestorName = GeneralName.getInstance((ASN1TaggedObject)seq.getObjectAt(index++), true);
        }
        this.requestList = (ASN1Sequence)seq.getObjectAt(index++);
        if (seq.size() == index + 1) {
            this.requestExtensions = Extensions.getInstance((ASN1TaggedObject)seq.getObjectAt(index), true);
        }
    }

    public static TBSRequest getInstance(ASN1TaggedObject obj, boolean explicit) {
        return TBSRequest.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static TBSRequest getInstance(Object obj) {
        if (obj instanceof TBSRequest) {
            return (TBSRequest)obj;
        }
        if (obj != null) {
            return new TBSRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public GeneralName getRequestorName() {
        return this.requestorName;
    }

    public ASN1Sequence getRequestList() {
        return this.requestList;
    }

    public Extensions getRequestExtensions() {
        return this.requestExtensions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        if (!this.version.equals(V1) || this.versionSet) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.version));
        }
        if (this.requestorName != null) {
            v.add(new DERTaggedObject(true, 1, (ASN1Encodable)this.requestorName));
        }
        v.add(this.requestList);
        if (this.requestExtensions != null) {
            v.add(new DERTaggedObject(true, 2, (ASN1Encodable)this.requestExtensions));
        }
        return new DERSequence(v);
    }
}

