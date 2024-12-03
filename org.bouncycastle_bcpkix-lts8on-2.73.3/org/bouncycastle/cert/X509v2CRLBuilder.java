/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.CertificateList
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.TBSCertList
 *  org.bouncycastle.asn1.x509.Time
 *  org.bouncycastle.asn1.x509.V2TBSCertListGenerator
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
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Exceptions;

public class X509v2CRLBuilder {
    private V2TBSCertListGenerator tbsGen = new V2TBSCertListGenerator();
    private ExtensionsGenerator extGenerator;

    public X509v2CRLBuilder(X500Name issuer, Date thisUpdate) {
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(new Time(thisUpdate));
    }

    public X509v2CRLBuilder(X500Name issuer, Date thisUpdate, Locale dateLocale) {
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(new Time(thisUpdate, dateLocale));
    }

    public X509v2CRLBuilder(X500Name issuer, Time thisUpdate) {
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(thisUpdate);
    }

    public X509v2CRLBuilder(X509CRLHolder template) {
        this.tbsGen.setIssuer(template.getIssuer());
        this.tbsGen.setThisUpdate(new Time(template.getThisUpdate()));
        Date nextUpdate = template.getNextUpdate();
        if (nextUpdate != null) {
            this.tbsGen.setNextUpdate(new Time(nextUpdate));
        }
        this.addCRL(template);
        this.extGenerator = new ExtensionsGenerator();
        Extensions exts = template.getExtensions();
        if (exts != null) {
            Enumeration en = exts.oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)en.nextElement();
                if (Extension.altSignatureAlgorithm.equals((ASN1Primitive)oid) || Extension.altSignatureValue.equals((ASN1Primitive)oid)) continue;
                this.extGenerator.addExtension(exts.getExtension(oid));
            }
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

    public X509v2CRLBuilder setNextUpdate(Date date) {
        return this.setNextUpdate(new Time(date));
    }

    public X509v2CRLBuilder setNextUpdate(Date date, Locale dateLocale) {
        return this.setNextUpdate(new Time(date, dateLocale));
    }

    public X509v2CRLBuilder setNextUpdate(Time date) {
        this.tbsGen.setNextUpdate(date);
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger userCertificateSerial, Date revocationDate, int reason) {
        this.tbsGen.addCRLEntry(new ASN1Integer(userCertificateSerial), new Time(revocationDate), reason);
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger userCertificateSerial, Date revocationDate, int reason, Date invalidityDate) {
        this.tbsGen.addCRLEntry(new ASN1Integer(userCertificateSerial), new Time(revocationDate), reason, new ASN1GeneralizedTime(invalidityDate));
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger userCertificateSerial, Date revocationDate, Extensions extensions) {
        this.tbsGen.addCRLEntry(new ASN1Integer(userCertificateSerial), new Time(revocationDate), extensions);
        return this;
    }

    public X509v2CRLBuilder addCRL(X509CRLHolder other) {
        TBSCertList revocations = other.toASN1Structure().getTBSCertList();
        if (revocations != null) {
            Enumeration en = revocations.getRevokedCertificateEnumeration();
            while (en.hasMoreElements()) {
                this.tbsGen.addCRLEntry(ASN1Sequence.getInstance((Object)((ASN1Encodable)en.nextElement()).toASN1Primitive()));
            }
        }
        return this;
    }

    public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, oid, isCritical, value);
        return this;
    }

    public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator.addExtension(oid, isCritical, encodedValue);
        return this;
    }

    public X509v2CRLBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v2CRLBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, value.toASN1Primitive().getEncoded("DER")));
        }
        catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
        return this;
    }

    public X509v2CRLBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v2CRLBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, encodedValue));
        return this;
    }

    public X509v2CRLBuilder removeExtension(ASN1ObjectIdentifier oid) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, oid);
        return this;
    }

    public X509CRLHolder build(ContentSigner signer) {
        this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        return X509v2CRLBuilder.generateFullCRL(signer, this.tbsGen.generateTBSCertList());
    }

    public X509CRLHolder build(ContentSigner signer, boolean isCritical, ContentSigner altSigner) {
        this.tbsGen.setSignature(null);
        try {
            this.extGenerator.addExtension(Extension.altSignatureAlgorithm, isCritical, (ASN1Encodable)altSigner.getAlgorithmIdentifier());
        }
        catch (IOException e) {
            throw Exceptions.illegalStateException((String)"cannot add altSignatureAlgorithm extension", (Throwable)e);
        }
        this.tbsGen.setExtensions(this.extGenerator.generate());
        try {
            this.extGenerator.addExtension(Extension.altSignatureValue, isCritical, (ASN1Encodable)new DERBitString(X509v2CRLBuilder.generateSig(altSigner, (ASN1Object)this.tbsGen.generatePreTBSCertList())));
            this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
            this.tbsGen.setExtensions(this.extGenerator.generate());
            TBSCertList tbsCert = this.tbsGen.generateTBSCertList();
            return new X509CRLHolder(X509v2CRLBuilder.generateCRLStructure(tbsCert, signer.getAlgorithmIdentifier(), X509v2CRLBuilder.generateSig(signer, (ASN1Object)tbsCert)));
        }
        catch (IOException e) {
            throw Exceptions.illegalArgumentException((String)"cannot produce certificate signature", (Throwable)e);
        }
    }

    private static X509CRLHolder generateFullCRL(ContentSigner signer, TBSCertList tbsCertList) {
        try {
            return new X509CRLHolder(X509v2CRLBuilder.generateCRLStructure(tbsCertList, signer.getAlgorithmIdentifier(), X509v2CRLBuilder.generateSig(signer, (ASN1Object)tbsCertList)));
        }
        catch (IOException e) {
            throw Exceptions.illegalStateException((String)"cannot produce certificate signature", (Throwable)e);
        }
    }

    private static CertificateList generateCRLStructure(TBSCertList tbsCertList, AlgorithmIdentifier sigAlgId, byte[] signature) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)tbsCertList);
        v.add((ASN1Encodable)sigAlgId);
        v.add((ASN1Encodable)new DERBitString(signature));
        return CertificateList.getInstance((Object)new DERSequence(v));
    }

    private static byte[] generateSig(ContentSigner signer, ASN1Object tbsObj) throws IOException {
        OutputStream sOut = signer.getOutputStream();
        tbsObj.encodeTo(sOut, "DER");
        sOut.close();
        return signer.getSignature();
    }
}

