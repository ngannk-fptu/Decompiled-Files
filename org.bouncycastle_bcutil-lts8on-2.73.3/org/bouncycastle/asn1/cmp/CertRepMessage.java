/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertResponse;

public class CertRepMessage
extends ASN1Object {
    private final ASN1Sequence caPubs;
    private final ASN1Sequence response;

    private CertRepMessage(ASN1Sequence seq) {
        int index = 0;
        this.caPubs = seq.size() > 1 ? ASN1Sequence.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(index++)), (boolean)true) : null;
        this.response = ASN1Sequence.getInstance((Object)seq.getObjectAt(index));
    }

    public CertRepMessage(CMPCertificate[] caPubs, CertResponse[] response) {
        if (response == null) {
            throw new IllegalArgumentException("'response' cannot be null");
        }
        this.caPubs = caPubs != null && caPubs.length != 0 ? new DERSequence((ASN1Encodable[])caPubs) : null;
        this.response = new DERSequence((ASN1Encodable[])response);
    }

    public static CertRepMessage getInstance(Object o) {
        if (o instanceof CertRepMessage) {
            return (CertRepMessage)((Object)o);
        }
        if (o != null) {
            return new CertRepMessage(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CMPCertificate[] getCaPubs() {
        if (this.caPubs == null) {
            return null;
        }
        CMPCertificate[] results = new CMPCertificate[this.caPubs.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = CMPCertificate.getInstance(this.caPubs.getObjectAt(i));
        }
        return results;
    }

    public CertResponse[] getResponse() {
        CertResponse[] results = new CertResponse[this.response.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = CertResponse.getInstance(this.response.getObjectAt(i));
        }
        return results;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.caPubs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.caPubs));
        }
        v.add((ASN1Encodable)this.response);
        return new DERSequence(v);
    }
}

