/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.EncryptedValue;

public class CertOrEncCert
extends ASN1Object
implements ASN1Choice {
    private CMPCertificate certificate;
    private EncryptedKey encryptedCert;

    private CertOrEncCert(ASN1TaggedObject tagged) {
        if (tagged.hasContextTag(0)) {
            this.certificate = CMPCertificate.getInstance(tagged.getExplicitBaseObject());
        } else if (tagged.hasContextTag(1)) {
            this.encryptedCert = EncryptedKey.getInstance(tagged.getExplicitBaseObject());
        } else {
            throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText((ASN1TaggedObject)tagged));
        }
    }

    public CertOrEncCert(CMPCertificate certificate) {
        if (certificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        this.certificate = certificate;
    }

    public CertOrEncCert(EncryptedValue encryptedValue) {
        if (encryptedValue == null) {
            throw new IllegalArgumentException("'encryptedCert' cannot be null");
        }
        this.encryptedCert = new EncryptedKey(encryptedValue);
    }

    public CertOrEncCert(EncryptedKey encryptedKey) {
        if (encryptedKey == null) {
            throw new IllegalArgumentException("'encryptedCert' cannot be null");
        }
        this.encryptedCert = encryptedKey;
    }

    public static CertOrEncCert getInstance(Object o) {
        if (o instanceof CertOrEncCert) {
            return (CertOrEncCert)((Object)o);
        }
        if (o instanceof ASN1TaggedObject) {
            return new CertOrEncCert(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public boolean hasEncryptedCertificate() {
        return this.encryptedCert != null;
    }

    public CMPCertificate getCertificate() {
        return this.certificate;
    }

    public EncryptedKey getEncryptedCert() {
        return this.encryptedCert;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.certificate != null) {
            return new DERTaggedObject(true, 0, (ASN1Encodable)this.certificate);
        }
        return new DERTaggedObject(true, 1, (ASN1Encodable)this.encryptedCert);
    }
}

