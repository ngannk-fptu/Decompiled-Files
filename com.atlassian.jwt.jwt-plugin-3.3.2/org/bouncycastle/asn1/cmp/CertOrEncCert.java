/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.EncryptedValue;

public class CertOrEncCert
extends ASN1Object
implements ASN1Choice {
    private CMPCertificate certificate;
    private EncryptedKey encryptedKey;

    private CertOrEncCert(ASN1TaggedObject aSN1TaggedObject) {
        if (aSN1TaggedObject.getTagNo() == 0) {
            this.certificate = CMPCertificate.getInstance(aSN1TaggedObject.getObject());
        } else if (aSN1TaggedObject.getTagNo() == 1) {
            this.encryptedKey = EncryptedKey.getInstance(aSN1TaggedObject.getObject());
        } else {
            throw new IllegalArgumentException("unknown tag: " + aSN1TaggedObject.getTagNo());
        }
    }

    public static CertOrEncCert getInstance(Object object) {
        if (object instanceof CertOrEncCert) {
            return (CertOrEncCert)object;
        }
        if (object instanceof ASN1TaggedObject) {
            return new CertOrEncCert((ASN1TaggedObject)object);
        }
        return null;
    }

    public CertOrEncCert(CMPCertificate cMPCertificate) {
        if (cMPCertificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        this.certificate = cMPCertificate;
    }

    public CertOrEncCert(EncryptedValue encryptedValue) {
        if (encryptedValue == null) {
            throw new IllegalArgumentException("'encryptedCert' cannot be null");
        }
        this.encryptedKey = new EncryptedKey(encryptedValue);
    }

    public CertOrEncCert(EncryptedKey encryptedKey) {
        if (encryptedKey == null) {
            throw new IllegalArgumentException("'encryptedKey' cannot be null");
        }
        this.encryptedKey = encryptedKey;
    }

    public CMPCertificate getCertificate() {
        return this.certificate;
    }

    public EncryptedKey getEncryptedCert() {
        return this.encryptedKey;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.certificate != null) {
            return new DERTaggedObject(true, 0, this.certificate);
        }
        return new DERTaggedObject(true, 1, this.encryptedKey);
    }
}

