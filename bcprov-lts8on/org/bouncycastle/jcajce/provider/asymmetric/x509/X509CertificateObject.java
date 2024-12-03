/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.util.Date;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CertificateImpl;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CertificateInternal;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509SignatureUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.util.Arrays;

class X509CertificateObject
extends X509CertificateImpl
implements PKCS12BagAttributeCarrier {
    private final Object cacheLock = new Object();
    private X509CertificateInternal internalCertificateValue;
    private X500Principal issuerValue;
    private PublicKey publicKeyValue;
    private X500Principal subjectValue;
    private long[] validityValues;
    private volatile boolean hashValueSet;
    private volatile int hashValue;
    private PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

    X509CertificateObject(JcaJceHelper bcHelper, Certificate c) throws CertificateParsingException {
        super(bcHelper, c, X509CertificateObject.createBasicConstraints(c), X509CertificateObject.createKeyUsage(c), X509CertificateObject.createSigAlgName(c), X509CertificateObject.createSigAlgParams(c));
    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        long[] validityValues;
        long checkTime = date.getTime();
        if (checkTime > (validityValues = this.getValidityValues())[1]) {
            throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime());
        }
        if (checkTime < validityValues[0]) {
            throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public X500Principal getIssuerX500Principal() {
        Object object = this.cacheLock;
        synchronized (object) {
            if (null != this.issuerValue) {
                return this.issuerValue;
            }
        }
        X500Principal temp = super.getIssuerX500Principal();
        Object object2 = this.cacheLock;
        synchronized (object2) {
            if (null == this.issuerValue) {
                this.issuerValue = temp;
            }
            return this.issuerValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PublicKey getPublicKey() {
        Object object = this.cacheLock;
        synchronized (object) {
            if (null != this.publicKeyValue) {
                return this.publicKeyValue;
            }
        }
        PublicKey temp = super.getPublicKey();
        if (null == temp) {
            return null;
        }
        Object object2 = this.cacheLock;
        synchronized (object2) {
            if (null == this.publicKeyValue) {
                this.publicKeyValue = temp;
            }
            return this.publicKeyValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public X500Principal getSubjectX500Principal() {
        Object object = this.cacheLock;
        synchronized (object) {
            if (null != this.subjectValue) {
                return this.subjectValue;
            }
        }
        X500Principal temp = super.getSubjectX500Principal();
        Object object2 = this.cacheLock;
        synchronized (object2) {
            if (null == this.subjectValue) {
                this.subjectValue = temp;
            }
            return this.subjectValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long[] getValidityValues() {
        Object object = this.cacheLock;
        synchronized (object) {
            if (null != this.validityValues) {
                return this.validityValues;
            }
        }
        long[] temp = new long[]{super.getNotBefore().getTime(), super.getNotAfter().getTime()};
        Object object2 = this.cacheLock;
        synchronized (object2) {
            if (null == this.validityValues) {
                this.validityValues = temp;
            }
            return this.validityValues;
        }
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        return Arrays.clone(this.getInternalCertificate().getEncoded());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof X509CertificateObject) {
            ASN1BitString signature;
            X509CertificateObject otherBC = (X509CertificateObject)other;
            if (this.hashValueSet && otherBC.hashValueSet ? this.hashValue != otherBC.hashValue : (null == this.internalCertificateValue || null == otherBC.internalCertificateValue) && null != (signature = this.c.getSignature()) && !signature.equals(otherBC.c.getSignature())) {
                return false;
            }
            return this.getInternalCertificate().equals(otherBC.getInternalCertificate());
        }
        return this.getInternalCertificate().equals(other);
    }

    @Override
    public int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = this.getInternalCertificate().hashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    public int originalHashCode() {
        try {
            int hashCode = 0;
            byte[] certData = this.getInternalCertificate().getEncoded();
            for (int i = 1; i < certData.length; ++i) {
                hashCode += certData[i] * i;
            }
            return hashCode;
        }
        catch (CertificateEncodingException e) {
            return 0;
        }
    }

    @Override
    public void setBagAttribute(ASN1ObjectIdentifier oid, ASN1Encodable attribute) {
        this.attrCarrier.setBagAttribute(oid, attribute);
    }

    @Override
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier oid) {
        return this.attrCarrier.getBagAttribute(oid);
    }

    @Override
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private X509CertificateInternal getInternalCertificate() {
        Object object = this.cacheLock;
        synchronized (object) {
            if (null != this.internalCertificateValue) {
                return this.internalCertificateValue;
            }
        }
        byte[] encoding = null;
        X509CertificateEncodingException exception = null;
        try {
            encoding = this.c.getEncoded("DER");
        }
        catch (IOException e) {
            exception = new X509CertificateEncodingException(e);
        }
        X509CertificateInternal temp = new X509CertificateInternal(this.bcHelper, this.c, this.basicConstraints, this.keyUsage, this.sigAlgName, this.sigAlgParams, encoding, exception);
        Object object2 = this.cacheLock;
        synchronized (object2) {
            if (null == this.internalCertificateValue) {
                this.internalCertificateValue = temp;
            }
            return this.internalCertificateValue;
        }
    }

    private static BasicConstraints createBasicConstraints(Certificate c) throws CertificateParsingException {
        try {
            byte[] extOctets = X509CertificateObject.getExtensionOctets(c, "2.5.29.19");
            if (null == extOctets) {
                return null;
            }
            return BasicConstraints.getInstance(ASN1Primitive.fromByteArray(extOctets));
        }
        catch (Exception e) {
            throw new CertificateParsingException("cannot construct BasicConstraints: " + e);
        }
    }

    private static boolean[] createKeyUsage(Certificate c) throws CertificateParsingException {
        try {
            byte[] extOctets = X509CertificateObject.getExtensionOctets(c, "2.5.29.15");
            if (null == extOctets) {
                return null;
            }
            ASN1BitString bits = ASN1BitString.getInstance(ASN1Primitive.fromByteArray(extOctets));
            byte[] bytes = bits.getBytes();
            int length = bytes.length * 8 - bits.getPadBits();
            boolean[] keyUsage = new boolean[length < 9 ? 9 : length];
            for (int i = 0; i != length; ++i) {
                keyUsage[i] = (bytes[i / 8] & 128 >>> i % 8) != 0;
            }
            return keyUsage;
        }
        catch (Exception e) {
            throw new CertificateParsingException("cannot construct KeyUsage: " + e);
        }
    }

    private static String createSigAlgName(Certificate c) throws CertificateParsingException {
        try {
            return X509SignatureUtil.getSignatureName(c.getSignatureAlgorithm());
        }
        catch (Exception e) {
            throw new CertificateParsingException("cannot construct SigAlgName: " + e);
        }
    }

    private static byte[] createSigAlgParams(Certificate c) throws CertificateParsingException {
        try {
            ASN1Encodable parameters = c.getSignatureAlgorithm().getParameters();
            if (null == parameters) {
                return null;
            }
            return parameters.toASN1Primitive().getEncoded("DER");
        }
        catch (Exception e) {
            throw new CertificateParsingException("cannot construct SigAlgParams: " + e);
        }
    }

    private static class X509CertificateEncodingException
    extends CertificateEncodingException {
        private final Throwable cause;

        X509CertificateEncodingException(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

