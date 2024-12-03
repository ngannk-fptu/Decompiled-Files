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
 *  org.bouncycastle.asn1.x509.CertificateList
 */
package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevRepContent
extends ASN1Object {
    private final ASN1Sequence status;
    private ASN1Sequence revCerts;
    private ASN1Sequence crls;

    private RevRepContent(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.status = ASN1Sequence.getInstance(en.nextElement());
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(en.nextElement());
            if (tObj.hasContextTag(0)) {
                this.revCerts = ASN1Sequence.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                continue;
            }
            if (!tObj.hasContextTag(1)) continue;
            this.crls = ASN1Sequence.getInstance((ASN1TaggedObject)tObj, (boolean)true);
        }
    }

    public static RevRepContent getInstance(Object o) {
        if (o instanceof RevRepContent) {
            return (RevRepContent)((Object)o);
        }
        if (o != null) {
            return new RevRepContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIStatusInfo[] getStatus() {
        PKIStatusInfo[] results = new PKIStatusInfo[this.status.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = PKIStatusInfo.getInstance(this.status.getObjectAt(i));
        }
        return results;
    }

    public CertId[] getRevCerts() {
        if (this.revCerts == null) {
            return null;
        }
        CertId[] results = new CertId[this.revCerts.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = CertId.getInstance(this.revCerts.getObjectAt(i));
        }
        return results;
    }

    public CertificateList[] getCrls() {
        if (this.crls == null) {
            return null;
        }
        CertificateList[] results = new CertificateList[this.crls.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = CertificateList.getInstance((Object)this.crls.getObjectAt(i));
        }
        return results;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.status);
        this.addOptional(v, 0, (ASN1Encodable)this.revCerts);
        this.addOptional(v, 1, (ASN1Encodable)this.crls);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, tagNo, obj));
        }
    }
}

