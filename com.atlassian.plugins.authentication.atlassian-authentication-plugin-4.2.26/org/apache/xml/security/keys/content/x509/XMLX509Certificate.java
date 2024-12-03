/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.content.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509DataContent;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509Certificate
extends SignatureElementProxy
implements XMLX509DataContent {
    public static final String JCA_CERT_ID = "X.509";

    public XMLX509Certificate(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public XMLX509Certificate(Document doc, byte[] certificateBytes) {
        super(doc);
        this.addBase64Text(certificateBytes);
    }

    public XMLX509Certificate(Document doc, X509Certificate x509certificate) throws XMLSecurityException {
        super(doc);
        try {
            this.addBase64Text(x509certificate.getEncoded());
        }
        catch (CertificateEncodingException ex) {
            throw new XMLSecurityException(ex);
        }
    }

    public byte[] getCertificateBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public X509Certificate getX509Certificate() throws XMLSecurityException {
        byte[] certbytes = this.getCertificateBytes();
        try (ByteArrayInputStream is = new ByteArrayInputStream(certbytes);){
            CertificateFactory certFact = CertificateFactory.getInstance(JCA_CERT_ID);
            X509Certificate x509Certificate = (X509Certificate)certFact.generateCertificate(is);
            return x509Certificate;
        }
        catch (IOException | CertificateException ex) {
            throw new XMLSecurityException(ex);
        }
    }

    public PublicKey getPublicKey() throws XMLSecurityException, IOException {
        X509Certificate cert = this.getX509Certificate();
        if (cert != null) {
            return cert.getPublicKey();
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLX509Certificate)) {
            return false;
        }
        XMLX509Certificate other = (XMLX509Certificate)obj;
        try {
            return Arrays.equals(other.getCertificateBytes(), this.getCertificateBytes());
        }
        catch (XMLSecurityException ex) {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        try {
            byte[] bytes = this.getCertificateBytes();
            for (int i = 0; i < bytes.length; ++i) {
                result = 31 * result + bytes[i];
            }
        }
        catch (XMLSecurityException e) {
            LOG.debug(e.getMessage(), (Throwable)e);
        }
        return result;
    }

    @Override
    public String getBaseLocalName() {
        return "X509Certificate";
    }
}

