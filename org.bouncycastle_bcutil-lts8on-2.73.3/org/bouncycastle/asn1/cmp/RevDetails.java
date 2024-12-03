/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.Extensions
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.x509.Extensions;

public class RevDetails
extends ASN1Object {
    private final CertTemplate certDetails;
    private Extensions crlEntryDetails;

    private RevDetails(ASN1Sequence seq) {
        this.certDetails = CertTemplate.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.crlEntryDetails = Extensions.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public RevDetails(CertTemplate certDetails) {
        this.certDetails = certDetails;
    }

    public RevDetails(CertTemplate certDetails, Extensions crlEntryDetails) {
        this.certDetails = certDetails;
        this.crlEntryDetails = crlEntryDetails;
    }

    public static RevDetails getInstance(Object o) {
        if (o instanceof RevDetails) {
            return (RevDetails)((Object)o);
        }
        if (o != null) {
            return new RevDetails(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertTemplate getCertDetails() {
        return this.certDetails;
    }

    public Extensions getCrlEntryDetails() {
        return this.crlEntryDetails;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.certDetails);
        if (this.crlEntryDetails != null) {
            v.add((ASN1Encodable)this.crlEntryDetails);
        }
        return new DERSequence(v);
    }
}

