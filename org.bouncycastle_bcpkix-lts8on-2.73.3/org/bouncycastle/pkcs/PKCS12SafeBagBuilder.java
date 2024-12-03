/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.pkcs.Attribute
 *  org.bouncycastle.asn1.pkcs.CertBag
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.pkcs.SafeBag
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.CertificateList
 */
package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;

public class PKCS12SafeBagBuilder {
    private ASN1ObjectIdentifier bagType;
    private ASN1Encodable bagValue;
    private ASN1EncodableVector bagAttrs = new ASN1EncodableVector();

    public PKCS12SafeBagBuilder(PrivateKeyInfo privateKeyInfo, OutputEncryptor encryptor) {
        this.bagType = PKCSObjectIdentifiers.pkcs8ShroudedKeyBag;
        this.bagValue = new PKCS8EncryptedPrivateKeyInfoBuilder(privateKeyInfo).build(encryptor).toASN1Structure();
    }

    public PKCS12SafeBagBuilder(PrivateKeyInfo privateKeyInfo) {
        this.bagType = PKCSObjectIdentifiers.keyBag;
        this.bagValue = privateKeyInfo;
    }

    public PKCS12SafeBagBuilder(X509CertificateHolder certificate) throws IOException {
        this(certificate.toASN1Structure());
    }

    public PKCS12SafeBagBuilder(X509CRLHolder crl) throws IOException {
        this(crl.toASN1Structure());
    }

    public PKCS12SafeBagBuilder(Certificate certificate) throws IOException {
        this.bagType = PKCSObjectIdentifiers.certBag;
        this.bagValue = new CertBag(PKCSObjectIdentifiers.x509Certificate, (ASN1Encodable)new DEROctetString(certificate.getEncoded()));
    }

    public PKCS12SafeBagBuilder(CertificateList crl) throws IOException {
        this.bagType = PKCSObjectIdentifiers.crlBag;
        this.bagValue = new CertBag(PKCSObjectIdentifiers.x509Crl, (ASN1Encodable)new DEROctetString(crl.getEncoded()));
    }

    public PKCS12SafeBagBuilder addBagAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        this.bagAttrs.add((ASN1Encodable)new Attribute(attrType, (ASN1Set)new DERSet(attrValue)));
        return this;
    }

    public PKCS12SafeBag build() {
        return new PKCS12SafeBag(new SafeBag(this.bagType, this.bagValue, (ASN1Set)new DERSet(this.bagAttrs)));
    }
}

