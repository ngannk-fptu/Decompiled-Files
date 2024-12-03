/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.cert.CRLException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.jcajce.provider.asymmetric.x509.ExtCRLException;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLInternal;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509SignatureUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;

class X509CRLObject
extends X509CRLImpl {
    private final Object cacheLock = new Object();
    private X509CRLInternal internalCRLValue;
    private volatile boolean hashValueSet;
    private volatile int hashValue;

    X509CRLObject(JcaJceHelper bcHelper, CertificateList c) throws CRLException {
        super(bcHelper, c, X509CRLObject.createSigAlgName(c), X509CRLObject.createSigAlgParams(c), X509CRLObject.isIndirectCRL(c));
    }

    @Override
    public byte[] getEncoded() throws CRLException {
        return Arrays.clone(this.getInternalCRL().getEncoded());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof X509CRLObject) {
            ASN1BitString signature;
            X509CRLObject otherBC = (X509CRLObject)other;
            if (this.hashValueSet && otherBC.hashValueSet ? this.hashValue != otherBC.hashValue : (null == this.internalCRLValue || null == otherBC.internalCRLValue) && null != (signature = this.c.getSignature()) && !signature.equals(otherBC.c.getSignature())) {
                return false;
            }
            return this.getInternalCRL().equals(otherBC.getInternalCRL());
        }
        return this.getInternalCRL().equals(other);
    }

    @Override
    public int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = this.getInternalCRL().hashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private X509CRLInternal getInternalCRL() {
        Object object = this.cacheLock;
        synchronized (object) {
            if (null != this.internalCRLValue) {
                return this.internalCRLValue;
            }
        }
        byte[] encoding = null;
        X509CRLException exception = null;
        try {
            encoding = this.c.getEncoded("DER");
        }
        catch (IOException e) {
            exception = new X509CRLException(e);
        }
        X509CRLInternal temp = new X509CRLInternal(this.bcHelper, this.c, this.sigAlgName, this.sigAlgParams, this.isIndirect, encoding, exception);
        Object object2 = this.cacheLock;
        synchronized (object2) {
            if (null == this.internalCRLValue) {
                this.internalCRLValue = temp;
            }
            return this.internalCRLValue;
        }
    }

    private static String createSigAlgName(CertificateList c) throws CRLException {
        try {
            return X509SignatureUtil.getSignatureName(c.getSignatureAlgorithm());
        }
        catch (Exception e) {
            throw new X509CRLException("CRL contents invalid: " + e.getMessage(), e);
        }
    }

    private static byte[] createSigAlgParams(CertificateList c) throws CRLException {
        try {
            ASN1Encodable parameters = c.getSignatureAlgorithm().getParameters();
            if (null == parameters) {
                return null;
            }
            return parameters.toASN1Primitive().getEncoded("DER");
        }
        catch (Exception e) {
            throw new CRLException("CRL contents invalid: " + e);
        }
    }

    private static boolean isIndirectCRL(CertificateList c) throws CRLException {
        try {
            byte[] extOctets = X509CRLObject.getExtensionOctets(c, Extension.issuingDistributionPoint.getId());
            if (null == extOctets) {
                return false;
            }
            return IssuingDistributionPoint.getInstance(extOctets).isIndirectCRL();
        }
        catch (Exception e) {
            throw new ExtCRLException("Exception reading IssuingDistributionPoint", e);
        }
    }

    private static class X509CRLException
    extends CRLException {
        private final Throwable cause;

        X509CRLException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        X509CRLException(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

