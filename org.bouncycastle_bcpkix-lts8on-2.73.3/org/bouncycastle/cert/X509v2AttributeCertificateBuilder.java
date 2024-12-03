/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.x509.AttCertIssuer
 *  org.bouncycastle.asn1.x509.Attribute
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.V2AttributeCertificateInfoGenerator
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.V2AttributeCertificateInfoGenerator;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.operator.ContentSigner;

public class X509v2AttributeCertificateBuilder {
    private V2AttributeCertificateInfoGenerator acInfoGen = new V2AttributeCertificateInfoGenerator();
    private ExtensionsGenerator extGenerator;

    public X509v2AttributeCertificateBuilder(AttributeCertificateHolder holder, AttributeCertificateIssuer issuer, BigInteger serialNumber, Date notBefore, Date notAfter) {
        this.extGenerator = new ExtensionsGenerator();
        this.acInfoGen.setHolder(holder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance((Object)issuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(serialNumber));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(notBefore));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(notAfter));
    }

    public X509v2AttributeCertificateBuilder(AttributeCertificateHolder holder, AttributeCertificateIssuer issuer, BigInteger serialNumber, Date notBefore, Date notAfter, Locale dateLocale) {
        this.extGenerator = new ExtensionsGenerator();
        this.acInfoGen.setHolder(holder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance((Object)issuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(serialNumber));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(notBefore, dateLocale));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(notAfter, dateLocale));
    }

    public X509v2AttributeCertificateBuilder(X509AttributeCertificateHolder template) {
        this.acInfoGen.setSerialNumber(new ASN1Integer(template.getSerialNumber()));
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance((Object)template.getIssuer().form));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(template.getNotBefore()));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(template.getNotAfter()));
        this.acInfoGen.setHolder(template.getHolder().holder);
        boolean[] uniqueID = template.getIssuerUniqueID();
        if (uniqueID != null) {
            this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(uniqueID));
        }
        Attribute[] attr = template.getAttributes();
        for (int i = 0; i != attr.length; ++i) {
            this.acInfoGen.addAttribute(attr[i]);
        }
        this.extGenerator = new ExtensionsGenerator();
        Extensions exts = template.getExtensions();
        Enumeration en = exts.oids();
        while (en.hasMoreElements()) {
            this.extGenerator.addExtension(exts.getExtension((ASN1ObjectIdentifier)en.nextElement()));
        }
    }

    public boolean hasExtension(ASN1ObjectIdentifier oid) {
        return this.doGetExtension(oid) != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        return this.doGetExtension(oid);
    }

    private Extension doGetExtension(ASN1ObjectIdentifier oid) {
        Extensions exts = this.extGenerator.generate();
        return exts.getExtension(oid);
    }

    public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        this.acInfoGen.addAttribute(new Attribute(attrType, (ASN1Set)new DERSet(attrValue)));
        return this;
    }

    public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable[] attrValues) {
        this.acInfoGen.addAttribute(new Attribute(attrType, (ASN1Set)new DERSet(attrValues)));
        return this;
    }

    public void setIssuerUniqueId(boolean[] iui) {
        this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(iui));
    }

    public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, oid, isCritical, value);
        return this;
    }

    public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator.addExtension(oid, isCritical, encodedValue);
        return this;
    }

    public X509v2AttributeCertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v2AttributeCertificateBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, value.toASN1Primitive().getEncoded("DER")));
        }
        catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
        return this;
    }

    public X509v2AttributeCertificateBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v2AttributeCertificateBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, encodedValue));
        return this;
    }

    public X509v2AttributeCertificateBuilder removeExtension(ASN1ObjectIdentifier oid) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, oid);
        return this;
    }

    public X509AttributeCertificateHolder build(ContentSigner signer) {
        this.acInfoGen.setSignature(signer.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.acInfoGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullAttrCert(signer, this.acInfoGen.generateAttributeCertificateInfo());
    }
}

