/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.pkcs.Attribute
 *  org.bouncycastle.asn1.pkcs.CRLBag
 *  org.bouncycastle.asn1.pkcs.CertBag
 *  org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.pkcs.SafeBag
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.CertificateList
 */
package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CRLBag;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

public class PKCS12SafeBag {
    public static final ASN1ObjectIdentifier friendlyNameAttribute = PKCSObjectIdentifiers.pkcs_9_at_friendlyName;
    public static final ASN1ObjectIdentifier localKeyIdAttribute = PKCSObjectIdentifiers.pkcs_9_at_localKeyId;
    private SafeBag safeBag;

    public PKCS12SafeBag(SafeBag safeBag) {
        this.safeBag = safeBag;
    }

    public SafeBag toASN1Structure() {
        return this.safeBag;
    }

    public ASN1ObjectIdentifier getType() {
        return this.safeBag.getBagId();
    }

    public Attribute[] getAttributes() {
        ASN1Set attrs = this.safeBag.getBagAttributes();
        if (attrs == null) {
            return null;
        }
        Attribute[] attributes = new Attribute[attrs.size()];
        for (int i = 0; i != attrs.size(); ++i) {
            attributes[i] = Attribute.getInstance((Object)attrs.getObjectAt(i));
        }
        return attributes;
    }

    public Object getBagValue() {
        if (this.getType().equals((ASN1Primitive)PKCSObjectIdentifiers.pkcs8ShroudedKeyBag)) {
            return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance((Object)this.safeBag.getBagValue()));
        }
        if (this.getType().equals((ASN1Primitive)PKCSObjectIdentifiers.certBag)) {
            CertBag certBag = CertBag.getInstance((Object)this.safeBag.getBagValue());
            return new X509CertificateHolder(Certificate.getInstance((Object)ASN1OctetString.getInstance((Object)certBag.getCertValue()).getOctets()));
        }
        if (this.getType().equals((ASN1Primitive)PKCSObjectIdentifiers.keyBag)) {
            return PrivateKeyInfo.getInstance((Object)this.safeBag.getBagValue());
        }
        if (this.getType().equals((ASN1Primitive)PKCSObjectIdentifiers.crlBag)) {
            CRLBag crlBag = CRLBag.getInstance((Object)this.safeBag.getBagValue());
            return new X509CRLHolder(CertificateList.getInstance((Object)ASN1OctetString.getInstance((Object)crlBag.getCrlValue()).getOctets()));
        }
        return this.safeBag.getBagValue();
    }
}

