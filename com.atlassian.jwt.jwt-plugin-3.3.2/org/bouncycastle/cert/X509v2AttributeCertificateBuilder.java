/*
 * Decompiled with CFR 0.152.
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

    public X509v2AttributeCertificateBuilder(AttributeCertificateHolder attributeCertificateHolder, AttributeCertificateIssuer attributeCertificateIssuer, BigInteger bigInteger, Date date, Date date2) {
        this.extGenerator = new ExtensionsGenerator();
        this.acInfoGen.setHolder(attributeCertificateHolder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance(attributeCertificateIssuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(bigInteger));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(date));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(date2));
    }

    public X509v2AttributeCertificateBuilder(AttributeCertificateHolder attributeCertificateHolder, AttributeCertificateIssuer attributeCertificateIssuer, BigInteger bigInteger, Date date, Date date2, Locale locale) {
        this.extGenerator = new ExtensionsGenerator();
        this.acInfoGen.setHolder(attributeCertificateHolder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance(attributeCertificateIssuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(bigInteger));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(date, locale));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(date2, locale));
    }

    public X509v2AttributeCertificateBuilder(X509AttributeCertificateHolder x509AttributeCertificateHolder) {
        this.acInfoGen.setSerialNumber(new ASN1Integer(x509AttributeCertificateHolder.getSerialNumber()));
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance(x509AttributeCertificateHolder.getIssuer().form));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(x509AttributeCertificateHolder.getNotBefore()));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(x509AttributeCertificateHolder.getNotAfter()));
        this.acInfoGen.setHolder(x509AttributeCertificateHolder.getHolder().holder);
        boolean[] blArray = x509AttributeCertificateHolder.getIssuerUniqueID();
        if (blArray != null) {
            this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(blArray));
        }
        Attribute[] attributeArray = x509AttributeCertificateHolder.getAttributes();
        for (int i = 0; i != attributeArray.length; ++i) {
            this.acInfoGen.addAttribute(attributeArray[i]);
        }
        this.extGenerator = new ExtensionsGenerator();
        Extensions extensions = x509AttributeCertificateHolder.getExtensions();
        Enumeration enumeration = extensions.oids();
        while (enumeration.hasMoreElements()) {
            this.extGenerator.addExtension(extensions.getExtension((ASN1ObjectIdentifier)enumeration.nextElement()));
        }
    }

    public boolean hasExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.doGetExtension(aSN1ObjectIdentifier) != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.doGetExtension(aSN1ObjectIdentifier);
    }

    private Extension doGetExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Extensions extensions = this.extGenerator.generate();
        return extensions.getExtension(aSN1ObjectIdentifier);
    }

    public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.acInfoGen.addAttribute(new Attribute(aSN1ObjectIdentifier, new DERSet(aSN1Encodable)));
        return this;
    }

    public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable[] aSN1EncodableArray) {
        this.acInfoGen.addAttribute(new Attribute(aSN1ObjectIdentifier, new DERSet(aSN1EncodableArray)));
        return this;
    }

    public void setIssuerUniqueId(boolean[] blArray) {
        this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(blArray));
    }

    public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, aSN1ObjectIdentifier, bl, aSN1Encodable);
        return this;
    }

    public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) throws CertIOException {
        this.extGenerator.addExtension(aSN1ObjectIdentifier, bl, byArray);
        return this;
    }

    public X509v2AttributeCertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v2AttributeCertificateBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, bl, aSN1Encodable.toASN1Primitive().getEncoded("DER")));
        }
        catch (IOException iOException) {
            throw new CertIOException("cannot encode extension: " + iOException.getMessage(), iOException);
        }
        return this;
    }

    public X509v2AttributeCertificateBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v2AttributeCertificateBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, bl, byArray));
        return this;
    }

    public X509v2AttributeCertificateBuilder removeExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, aSN1ObjectIdentifier);
        return this;
    }

    public X509AttributeCertificateHolder build(ContentSigner contentSigner) {
        this.acInfoGen.setSignature(contentSigner.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.acInfoGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullAttrCert(contentSigner, this.acInfoGen.generateAttributeCertificateInfo());
    }
}

