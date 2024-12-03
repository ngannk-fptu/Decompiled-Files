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

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;

public class KeyRecRepContent
extends ASN1Object {
    private final PKIStatusInfo status;
    private CMPCertificate newSigCert;
    private ASN1Sequence caCerts;
    private ASN1Sequence keyPairHist;

    private KeyRecRepContent(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.status = PKIStatusInfo.getInstance(en.nextElement());
        block5: while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(en.nextElement(), (int)128);
            switch (tObj.getTagNo()) {
                case 0: {
                    this.newSigCert = CMPCertificate.getInstance(tObj.getExplicitBaseObject());
                    continue block5;
                }
                case 1: {
                    this.caCerts = ASN1Sequence.getInstance((Object)tObj.getExplicitBaseObject());
                    continue block5;
                }
                case 2: {
                    this.keyPairHist = ASN1Sequence.getInstance((Object)tObj.getExplicitBaseObject());
                    continue block5;
                }
            }
            throw new IllegalArgumentException("unknown tag number: " + tObj.getTagNo());
        }
    }

    public static KeyRecRepContent getInstance(Object o) {
        if (o instanceof KeyRecRepContent) {
            return (KeyRecRepContent)((Object)o);
        }
        if (o != null) {
            return new KeyRecRepContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIStatusInfo getStatus() {
        return this.status;
    }

    public CMPCertificate getNewSigCert() {
        return this.newSigCert;
    }

    public CMPCertificate[] getCaCerts() {
        if (this.caCerts == null) {
            return null;
        }
        CMPCertificate[] results = new CMPCertificate[this.caCerts.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = CMPCertificate.getInstance(this.caCerts.getObjectAt(i));
        }
        return results;
    }

    public CertifiedKeyPair[] getKeyPairHist() {
        if (this.keyPairHist == null) {
            return null;
        }
        CertifiedKeyPair[] results = new CertifiedKeyPair[this.keyPairHist.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = CertifiedKeyPair.getInstance(this.keyPairHist.getObjectAt(i));
        }
        return results;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.status);
        this.addOptional(v, 0, (ASN1Encodable)this.newSigCert);
        this.addOptional(v, 1, (ASN1Encodable)this.caCerts);
        this.addOptional(v, 2, (ASN1Encodable)this.keyPairHist);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, tagNo, obj));
        }
    }
}

