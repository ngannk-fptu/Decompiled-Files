/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x509.TBSCertificate
 *  org.bouncycastle.asn1.x509.Time
 *  org.bouncycastle.asn1.x509.V3TBSCertificateGenerator
 *  org.bouncycastle.util.Exceptions
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Exceptions;

public class X509v3CertificateBuilder {
    private V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
    private ExtensionsGenerator extGenerator;

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore), new Time(notAfter), subject, publicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, Locale dateLocale, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore, dateLocale), new Time(notAfter, dateLocale), subject, publicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Time notBefore, Time notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this.tbsGen.setSerialNumber(new ASN1Integer(serial));
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setStartDate(notBefore);
        this.tbsGen.setEndDate(notAfter);
        this.tbsGen.setSubject(subject);
        this.tbsGen.setSubjectPublicKeyInfo(publicKeyInfo);
        this.extGenerator = new ExtensionsGenerator();
    }

    public X509v3CertificateBuilder(X509CertificateHolder template) {
        this.tbsGen.setSerialNumber(new ASN1Integer(template.getSerialNumber()));
        this.tbsGen.setIssuer(template.getIssuer());
        this.tbsGen.setStartDate(new Time(template.getNotBefore()));
        this.tbsGen.setEndDate(new Time(template.getNotAfter()));
        this.tbsGen.setSubject(template.getSubject());
        this.tbsGen.setSubjectPublicKeyInfo(template.getSubjectPublicKeyInfo());
        this.extGenerator = new ExtensionsGenerator();
        Extensions exts = template.getExtensions();
        Enumeration en = exts.oids();
        while (en.hasMoreElements()) {
            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)en.nextElement();
            if (Extension.subjectAltPublicKeyInfo.equals((ASN1Primitive)oid) || Extension.altSignatureAlgorithm.equals((ASN1Primitive)oid) || Extension.altSignatureValue.equals((ASN1Primitive)oid)) continue;
            this.extGenerator.addExtension(exts.getExtension(oid));
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

    public X509v3CertificateBuilder setSubjectUniqueID(boolean[] uniqueID) {
        this.tbsGen.setSubjectUniqueID(X509v3CertificateBuilder.booleanToBitString(uniqueID));
        return this;
    }

    public X509v3CertificateBuilder setIssuerUniqueID(boolean[] uniqueID) {
        this.tbsGen.setIssuerUniqueID(X509v3CertificateBuilder.booleanToBitString(uniqueID));
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            this.extGenerator.addExtension(oid, isCritical, value);
        }
        catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
        return this;
    }

    public X509v3CertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator.addExtension(oid, isCritical, encodedValue);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, value.toASN1Primitive().getEncoded("DER")));
        }
        catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, encodedValue));
        return this;
    }

    public X509v3CertificateBuilder removeExtension(ASN1ObjectIdentifier oid) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, oid);
        return this;
    }

    public X509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier oid, boolean isCritical, X509CertificateHolder certHolder) {
        Certificate cert = certHolder.toASN1Structure();
        Extension extension = cert.getTBSCertificate().getExtensions().getExtension(oid);
        if (extension == null) {
            throw new NullPointerException("extension " + oid + " not present");
        }
        this.extGenerator.addExtension(oid, isCritical, extension.getExtnValue().getOctets());
        return this;
    }

    public X509CertificateHolder build(ContentSigner signer) {
        this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        try {
            TBSCertificate tbsCert = this.tbsGen.generateTBSCertificate();
            return new X509CertificateHolder(X509v3CertificateBuilder.generateStructure(tbsCert, signer.getAlgorithmIdentifier(), X509v3CertificateBuilder.generateSig(signer, (ASN1Object)tbsCert)));
        }
        catch (IOException e) {
            throw Exceptions.illegalArgumentException((String)"cannot produce certificate signature", (Throwable)e);
        }
    }

    public X509CertificateHolder build(ContentSigner signer, boolean isCritical, ContentSigner altSigner) {
        this.tbsGen.setSignature(null);
        try {
            this.extGenerator.addExtension(Extension.altSignatureAlgorithm, isCritical, (ASN1Encodable)altSigner.getAlgorithmIdentifier());
        }
        catch (IOException e) {
            throw Exceptions.illegalStateException((String)"cannot add altSignatureAlgorithm extension", (Throwable)e);
        }
        this.tbsGen.setExtensions(this.extGenerator.generate());
        try {
            this.extGenerator.addExtension(Extension.altSignatureValue, isCritical, (ASN1Encodable)new DERBitString(X509v3CertificateBuilder.generateSig(altSigner, (ASN1Object)this.tbsGen.generatePreTBSCertificate())));
            this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
            this.tbsGen.setExtensions(this.extGenerator.generate());
            TBSCertificate tbsCert = this.tbsGen.generateTBSCertificate();
            return new X509CertificateHolder(X509v3CertificateBuilder.generateStructure(tbsCert, signer.getAlgorithmIdentifier(), X509v3CertificateBuilder.generateSig(signer, (ASN1Object)tbsCert)));
        }
        catch (IOException e) {
            throw Exceptions.illegalArgumentException((String)"cannot produce certificate signature", (Throwable)e);
        }
    }

    private static byte[] generateSig(ContentSigner signer, ASN1Object tbsObj) throws IOException {
        OutputStream sOut = signer.getOutputStream();
        tbsObj.encodeTo(sOut, "DER");
        sOut.close();
        return signer.getSignature();
    }

    private static Certificate generateStructure(TBSCertificate tbsCert, AlgorithmIdentifier sigAlgId, byte[] signature) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)tbsCert);
        v.add((ASN1Encodable)sigAlgId);
        v.add((ASN1Encodable)new DERBitString(signature));
        return Certificate.getInstance((Object)new DERSequence(v));
    }

    static DERBitString booleanToBitString(boolean[] id) {
        byte[] bytes = new byte[(id.length + 7) / 8];
        for (int i = 0; i != id.length; ++i) {
            int n = i / 8;
            bytes[n] = (byte)(bytes[n] | (id[i] ? 1 << 7 - i % 8 : 0));
        }
        int pad = id.length % 8;
        if (pad == 0) {
            return new DERBitString(bytes);
        }
        return new DERBitString(bytes, 8 - pad);
    }
}

