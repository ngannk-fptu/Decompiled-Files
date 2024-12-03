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
import org.bouncycastle.asn1.cmp.CertOrEncCert;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;

public class CertifiedKeyPair
extends ASN1Object {
    private final CertOrEncCert certOrEncCert;
    private EncryptedKey privateKey;
    private PKIPublicationInfo publicationInfo;

    private CertifiedKeyPair(ASN1Sequence seq) {
        this.certOrEncCert = CertOrEncCert.getInstance(seq.getObjectAt(0));
        if (seq.size() >= 2) {
            if (seq.size() == 2) {
                ASN1TaggedObject tagged = ASN1TaggedObject.getInstance((Object)seq.getObjectAt(1), (int)128);
                if (tagged.getTagNo() == 0) {
                    this.privateKey = EncryptedKey.getInstance(tagged.getExplicitBaseObject());
                } else {
                    this.publicationInfo = PKIPublicationInfo.getInstance(tagged.getExplicitBaseObject());
                }
            } else {
                this.privateKey = EncryptedKey.getInstance(ASN1TaggedObject.getInstance((Object)seq.getObjectAt(1), (int)128).getExplicitBaseObject());
                this.publicationInfo = PKIPublicationInfo.getInstance(ASN1TaggedObject.getInstance((Object)seq.getObjectAt(2), (int)128).getExplicitBaseObject());
            }
        }
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert) {
        this(certOrEncCert, (EncryptedKey)null, null);
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert, EncryptedKey privateKey, PKIPublicationInfo publicationInfo) {
        if (certOrEncCert == null) {
            throw new IllegalArgumentException("'certOrEncCert' cannot be null");
        }
        this.certOrEncCert = certOrEncCert;
        this.privateKey = privateKey;
        this.publicationInfo = publicationInfo;
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert, EncryptedValue privateKey, PKIPublicationInfo publicationInfo) {
        if (certOrEncCert == null) {
            throw new IllegalArgumentException("'certOrEncCert' cannot be null");
        }
        this.certOrEncCert = certOrEncCert;
        this.privateKey = privateKey != null ? new EncryptedKey(privateKey) : null;
        this.publicationInfo = publicationInfo;
    }

    public static CertifiedKeyPair getInstance(Object o) {
        if (o instanceof CertifiedKeyPair) {
            return (CertifiedKeyPair)((Object)o);
        }
        if (o != null) {
            return new CertifiedKeyPair(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertOrEncCert getCertOrEncCert() {
        return this.certOrEncCert;
    }

    public EncryptedKey getPrivateKey() {
        return this.privateKey;
    }

    public PKIPublicationInfo getPublicationInfo() {
        return this.publicationInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.certOrEncCert);
        if (this.privateKey != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.privateKey));
        }
        if (this.publicationInfo != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.publicationInfo));
        }
        return new DERSequence(v);
    }
}

