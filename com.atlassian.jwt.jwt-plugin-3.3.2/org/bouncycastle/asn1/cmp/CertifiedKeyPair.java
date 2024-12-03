/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

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
    private CertOrEncCert certOrEncCert;
    private EncryptedKey privateKey;
    private PKIPublicationInfo publicationInfo;

    private CertifiedKeyPair(ASN1Sequence aSN1Sequence) {
        this.certOrEncCert = CertOrEncCert.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() >= 2) {
            if (aSN1Sequence.size() == 2) {
                ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(1));
                if (aSN1TaggedObject.getTagNo() == 0) {
                    this.privateKey = EncryptedKey.getInstance(aSN1TaggedObject.getObject());
                } else {
                    this.publicationInfo = PKIPublicationInfo.getInstance(aSN1TaggedObject.getObject());
                }
            } else {
                this.privateKey = EncryptedKey.getInstance(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(1)).getObject());
                this.publicationInfo = PKIPublicationInfo.getInstance(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(2)).getObject());
            }
        }
    }

    public static CertifiedKeyPair getInstance(Object object) {
        if (object instanceof CertifiedKeyPair) {
            return (CertifiedKeyPair)object;
        }
        if (object != null) {
            return new CertifiedKeyPair(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert) {
        this(certOrEncCert, (EncryptedKey)null, null);
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert, EncryptedKey encryptedKey, PKIPublicationInfo pKIPublicationInfo) {
        if (certOrEncCert == null) {
            throw new IllegalArgumentException("'certOrEncCert' cannot be null");
        }
        this.certOrEncCert = certOrEncCert;
        this.privateKey = encryptedKey;
        this.publicationInfo = pKIPublicationInfo;
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert, EncryptedValue encryptedValue, PKIPublicationInfo pKIPublicationInfo) {
        if (certOrEncCert == null) {
            throw new IllegalArgumentException("'certOrEncCert' cannot be null");
        }
        this.certOrEncCert = certOrEncCert;
        this.privateKey = encryptedValue != null ? new EncryptedKey(encryptedValue) : (EncryptedKey)null;
        this.publicationInfo = pKIPublicationInfo;
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
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.certOrEncCert);
        if (this.privateKey != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, this.privateKey));
        }
        if (this.publicationInfo != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, this.publicationInfo));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

