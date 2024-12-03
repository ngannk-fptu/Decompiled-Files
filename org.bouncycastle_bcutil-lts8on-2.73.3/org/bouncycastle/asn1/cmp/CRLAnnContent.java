/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.CertificateList
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertificateList;

public class CRLAnnContent
extends ASN1Object {
    private final ASN1Sequence content;

    private CRLAnnContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public CRLAnnContent(CertificateList crl) {
        this.content = new DERSequence((ASN1Encodable)crl);
    }

    public static CRLAnnContent getInstance(Object o) {
        if (o instanceof CRLAnnContent) {
            return (CRLAnnContent)((Object)o);
        }
        if (o != null) {
            return new CRLAnnContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertificateList[] getCertificateLists() {
        CertificateList[] result = new CertificateList[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = CertificateList.getInstance((Object)this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

