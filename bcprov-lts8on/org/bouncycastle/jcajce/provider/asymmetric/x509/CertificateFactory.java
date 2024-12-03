/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.jcajce.provider.asymmetric.x509.PEMUtil;
import org.bouncycastle.jcajce.provider.asymmetric.x509.PKIXCertPath;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLObject;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CertificateObject;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.io.Streams;

public class CertificateFactory
extends CertificateFactorySpi {
    private final JcaJceHelper bcHelper = new BCJcaJceHelper();
    private static final PEMUtil PEM_CERT_PARSER = new PEMUtil("CERTIFICATE");
    private static final PEMUtil PEM_CRL_PARSER = new PEMUtil("CRL");
    private static final PEMUtil PEM_PKCS7_PARSER = new PEMUtil("PKCS7");
    private ASN1Set sData = null;
    private int sDataObjectCount = 0;
    private InputStream currentStream = null;
    private ASN1Set sCrlData = null;
    private int sCrlDataObjectCount = 0;
    private InputStream currentCrlStream = null;

    private java.security.cert.Certificate readDERCertificate(ASN1InputStream dIn) throws IOException, CertificateParsingException {
        return this.getCertificate(ASN1Sequence.getInstance(dIn.readObject()));
    }

    private java.security.cert.Certificate readPEMCertificate(InputStream in, boolean isFirst) throws IOException, CertificateParsingException {
        return this.getCertificate(PEM_CERT_PARSER.readPEMObject(in, isFirst));
    }

    private java.security.cert.Certificate getCertificate(ASN1Sequence seq) throws CertificateParsingException {
        if (seq == null) {
            return null;
        }
        if (seq.size() > 1 && seq.getObjectAt(0) instanceof ASN1ObjectIdentifier && seq.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true)).getCertificates();
            return this.getCertificate();
        }
        return new X509CertificateObject(this.bcHelper, Certificate.getInstance(seq));
    }

    private java.security.cert.Certificate getCertificate() throws CertificateParsingException {
        if (this.sData != null) {
            while (this.sDataObjectCount < this.sData.size()) {
                ASN1Encodable obj;
                if (!((obj = this.sData.getObjectAt(this.sDataObjectCount++)) instanceof ASN1Sequence)) continue;
                return new X509CertificateObject(this.bcHelper, Certificate.getInstance(obj));
            }
        }
        return null;
    }

    protected CRL createCRL(CertificateList c) throws CRLException {
        return new X509CRLObject(this.bcHelper, c);
    }

    private CRL readPEMCRL(InputStream in, boolean isFirst) throws IOException, CRLException {
        return this.getCRL(PEM_CRL_PARSER.readPEMObject(in, isFirst));
    }

    private CRL readDERCRL(ASN1InputStream aIn) throws IOException, CRLException {
        return this.getCRL(ASN1Sequence.getInstance(aIn.readObject()));
    }

    private CRL getCRL(ASN1Sequence seq) throws CRLException {
        if (seq == null) {
            return null;
        }
        if (seq.size() > 1 && seq.getObjectAt(0) instanceof ASN1ObjectIdentifier && seq.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sCrlData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true)).getCRLs();
            return this.getCRL();
        }
        return this.createCRL(CertificateList.getInstance(seq));
    }

    private CRL getCRL() throws CRLException {
        if (this.sCrlData == null || this.sCrlDataObjectCount >= this.sCrlData.size()) {
            return null;
        }
        return this.createCRL(CertificateList.getInstance(this.sCrlData.getObjectAt(this.sCrlDataObjectCount++)));
    }

    @Override
    public java.security.cert.Certificate engineGenerateCertificate(InputStream in) throws CertificateException {
        return this.doGenerateCertificate(in, true);
    }

    private java.security.cert.Certificate doGenerateCertificate(InputStream in, boolean isFirst) throws CertificateException {
        if (this.currentStream == null) {
            this.currentStream = in;
            this.sData = null;
            this.sDataObjectCount = 0;
        } else if (this.currentStream != in) {
            this.currentStream = in;
            this.sData = null;
            this.sDataObjectCount = 0;
        }
        try {
            if (this.sData != null) {
                if (this.sDataObjectCount != this.sData.size()) {
                    return this.getCertificate();
                }
                this.sData = null;
                this.sDataObjectCount = 0;
                return null;
            }
            InputStream pis = in.markSupported() ? in : new ByteArrayInputStream(Streams.readAll(in));
            pis.mark(1);
            int tag = pis.read();
            if (tag == -1) {
                return null;
            }
            pis.reset();
            if (tag != 48) {
                return this.readPEMCertificate(pis, isFirst);
            }
            return this.readDERCertificate(new ASN1InputStream(pis));
        }
        catch (Exception e) {
            throw new ExCertificateException("parsing issue: " + e.getMessage(), e);
        }
    }

    public Collection engineGenerateCertificates(InputStream inStream) throws CertificateException {
        java.security.cert.Certificate cert;
        BufferedInputStream in = new BufferedInputStream(inStream);
        ArrayList<java.security.cert.Certificate> certs = new ArrayList<java.security.cert.Certificate>();
        while ((cert = this.doGenerateCertificate(in, certs.isEmpty())) != null) {
            certs.add(cert);
        }
        return certs;
    }

    @Override
    public CRL engineGenerateCRL(InputStream in) throws CRLException {
        return this.doGenerateCRL(in, true);
    }

    private CRL doGenerateCRL(InputStream in, boolean isFirst) throws CRLException {
        if (this.currentCrlStream == null) {
            this.currentCrlStream = in;
            this.sCrlData = null;
            this.sCrlDataObjectCount = 0;
        } else if (this.currentCrlStream != in) {
            this.currentCrlStream = in;
            this.sCrlData = null;
            this.sCrlDataObjectCount = 0;
        }
        try {
            if (this.sCrlData != null) {
                if (this.sCrlDataObjectCount != this.sCrlData.size()) {
                    return this.getCRL();
                }
                this.sCrlData = null;
                this.sCrlDataObjectCount = 0;
                return null;
            }
            InputStream pis = in.markSupported() ? in : new ByteArrayInputStream(Streams.readAll(in));
            pis.mark(1);
            int tag = pis.read();
            if (tag == -1) {
                return null;
            }
            pis.reset();
            if (tag != 48) {
                return this.readPEMCRL(pis, isFirst);
            }
            return this.readDERCRL(new ASN1InputStream(pis, true));
        }
        catch (CRLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CRLException(e.toString());
        }
    }

    public Collection engineGenerateCRLs(InputStream inStream) throws CRLException {
        CRL crl;
        ArrayList<CRL> crls = new ArrayList<CRL>();
        BufferedInputStream in = new BufferedInputStream(inStream);
        while ((crl = this.doGenerateCRL(in, crls.isEmpty())) != null) {
            crls.add(crl);
        }
        return crls;
    }

    public Iterator engineGetCertPathEncodings() {
        return PKIXCertPath.certPathEncodings.iterator();
    }

    @Override
    public CertPath engineGenerateCertPath(InputStream inStream) throws CertificateException {
        return this.engineGenerateCertPath(inStream, "PkiPath");
    }

    @Override
    public CertPath engineGenerateCertPath(InputStream inStream, String encoding) throws CertificateException {
        return new PKIXCertPath(inStream, encoding);
    }

    public CertPath engineGenerateCertPath(List certificates) throws CertificateException {
        for (Object obj : certificates) {
            if (obj == null || obj instanceof X509Certificate) continue;
            throw new CertificateException("list contains non X509Certificate object while creating CertPath\n" + obj.toString());
        }
        return new PKIXCertPath(certificates);
    }

    private static class ExCertificateException
    extends CertificateException {
        private Throwable cause;

        public ExCertificateException(Throwable cause) {
            this.cause = cause;
        }

        public ExCertificateException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

