/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

public class PKIXCertPath
extends CertPath {
    private final JcaJceHelper helper;
    static final List certPathEncodings;
    private List certificates;

    private List sortCerts(List certs) {
        int i;
        if (certs.size() < 2) {
            return certs;
        }
        X500Principal issuer = ((X509Certificate)certs.get(0)).getIssuerX500Principal();
        boolean okay = true;
        for (int i2 = 1; i2 != certs.size(); ++i2) {
            X509Certificate cert = (X509Certificate)certs.get(i2);
            if (!issuer.equals(cert.getSubjectX500Principal())) {
                okay = false;
                break;
            }
            issuer = ((X509Certificate)certs.get(i2)).getIssuerX500Principal();
        }
        if (okay) {
            return certs;
        }
        ArrayList<X509Certificate> retList = new ArrayList<X509Certificate>(certs.size());
        ArrayList orig = new ArrayList(certs);
        for (i = 0; i < certs.size(); ++i) {
            X509Certificate cert = (X509Certificate)certs.get(i);
            boolean found = false;
            X500Principal subject = cert.getSubjectX500Principal();
            for (int j = 0; j != certs.size(); ++j) {
                X509Certificate c = (X509Certificate)certs.get(j);
                if (!c.getIssuerX500Principal().equals(subject)) continue;
                found = true;
                break;
            }
            if (found) continue;
            retList.add(cert);
            certs.remove(i);
        }
        if (retList.size() > 1) {
            return orig;
        }
        block3: for (i = 0; i != retList.size(); ++i) {
            issuer = ((X509Certificate)retList.get(i)).getIssuerX500Principal();
            for (int j = 0; j < certs.size(); ++j) {
                X509Certificate c = (X509Certificate)certs.get(j);
                if (!issuer.equals(c.getSubjectX500Principal())) continue;
                retList.add(c);
                certs.remove(j);
                continue block3;
            }
        }
        if (certs.size() > 0) {
            return orig;
        }
        return retList;
    }

    PKIXCertPath(List certificates) {
        super("X.509");
        this.helper = new BCJcaJceHelper();
        this.certificates = this.sortCerts(new ArrayList(certificates));
    }

    PKIXCertPath(InputStream inStream, String encoding) throws CertificateException {
        block8: {
            super("X.509");
            this.helper = new BCJcaJceHelper();
            try {
                if (encoding.equalsIgnoreCase("PkiPath")) {
                    ASN1InputStream derInStream = new ASN1InputStream(inStream);
                    ASN1Primitive derObject = derInStream.readObject();
                    if (!(derObject instanceof ASN1Sequence)) {
                        throw new CertificateException("input stream does not contain a ASN1 SEQUENCE while reading PkiPath encoded data to load CertPath");
                    }
                    Enumeration e = ((ASN1Sequence)derObject).getObjects();
                    this.certificates = new ArrayList();
                    CertificateFactory certFactory = this.helper.createCertificateFactory("X.509");
                    while (e.hasMoreElements()) {
                        ASN1Encodable element = (ASN1Encodable)e.nextElement();
                        byte[] encoded = element.toASN1Primitive().getEncoded("DER");
                        this.certificates.add(0, certFactory.generateCertificate(new ByteArrayInputStream(encoded)));
                    }
                    break block8;
                }
                if (encoding.equalsIgnoreCase("PKCS7") || encoding.equalsIgnoreCase("PEM")) {
                    Certificate cert;
                    inStream = new BufferedInputStream(inStream);
                    this.certificates = new ArrayList();
                    CertificateFactory certFactory = this.helper.createCertificateFactory("X.509");
                    while ((cert = certFactory.generateCertificate(inStream)) != null) {
                        this.certificates.add(cert);
                    }
                    break block8;
                }
                throw new CertificateException("unsupported encoding: " + encoding);
            }
            catch (IOException ex) {
                throw new CertificateException("IOException throw while decoding CertPath:\n" + ex.toString());
            }
            catch (NoSuchProviderException ex) {
                throw new CertificateException("BouncyCastle provider not found while trying to get a CertificateFactory:\n" + ex.toString());
            }
        }
        this.certificates = this.sortCerts(this.certificates);
    }

    public Iterator getEncodings() {
        return certPathEncodings.iterator();
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        Object enc;
        Iterator iter = this.getEncodings();
        if (iter.hasNext() && (enc = iter.next()) instanceof String) {
            return this.getEncoded((String)enc);
        }
        return null;
    }

    @Override
    public byte[] getEncoded(String encoding) throws CertificateEncodingException {
        if (encoding.equalsIgnoreCase("PkiPath")) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            ListIterator iter = this.certificates.listIterator(this.certificates.size());
            while (iter.hasPrevious()) {
                v.add(this.toASN1Object((X509Certificate)iter.previous()));
            }
            return this.toDEREncoded(new DERSequence(v));
        }
        if (encoding.equalsIgnoreCase("PKCS7")) {
            ContentInfo encInfo = new ContentInfo(PKCSObjectIdentifiers.data, null);
            ASN1EncodableVector v = new ASN1EncodableVector();
            for (int i = 0; i != this.certificates.size(); ++i) {
                v.add(this.toASN1Object((X509Certificate)this.certificates.get(i)));
            }
            SignedData sd = new SignedData(new ASN1Integer(1L), new DERSet(), encInfo, new DERSet(v), null, new DERSet());
            return this.toDEREncoded(new ContentInfo(PKCSObjectIdentifiers.signedData, sd));
        }
        if (encoding.equalsIgnoreCase("PEM")) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut));
            try {
                for (int i = 0; i != this.certificates.size(); ++i) {
                    pWrt.writeObject(new PemObject("CERTIFICATE", ((X509Certificate)this.certificates.get(i)).getEncoded()));
                }
                pWrt.close();
            }
            catch (Exception e) {
                throw new CertificateEncodingException("can't encode certificate for PEM encoded path");
            }
            return bOut.toByteArray();
        }
        throw new CertificateEncodingException("unsupported encoding: " + encoding);
    }

    public List getCertificates() {
        return Collections.unmodifiableList(new ArrayList(this.certificates));
    }

    private ASN1Primitive toASN1Object(X509Certificate cert) throws CertificateEncodingException {
        try {
            return new ASN1InputStream(cert.getEncoded()).readObject();
        }
        catch (Exception e) {
            throw new CertificateEncodingException("Exception while encoding certificate: " + e.toString());
        }
    }

    private byte[] toDEREncoded(ASN1Encodable obj) throws CertificateEncodingException {
        try {
            return obj.toASN1Primitive().getEncoded("DER");
        }
        catch (IOException e) {
            throw new CertificateEncodingException("Exception thrown: " + e);
        }
    }

    static {
        ArrayList<String> encodings = new ArrayList<String>();
        encodings.add("PkiPath");
        encodings.add("PEM");
        encodings.add("PKCS7");
        certPathEncodings = Collections.unmodifiableList(encodings);
    }
}

