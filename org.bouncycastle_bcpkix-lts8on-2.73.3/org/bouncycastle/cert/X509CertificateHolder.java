/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AltSignatureAlgorithm
 *  org.bouncycastle.asn1.x509.AltSignatureValue
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x509.TBSCertificate
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AltSignatureAlgorithm;
import org.bouncycastle.asn1.x509.AltSignatureValue;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509CertificateHolder
implements Encodable,
Serializable {
    private static final long serialVersionUID = 20170722001L;
    private transient Certificate x509Certificate;
    private transient Extensions extensions;

    private static Certificate parseBytes(byte[] certEncoding) throws IOException {
        try {
            return Certificate.getInstance((Object)CertUtils.parseNonEmptyASN1(certEncoding));
        }
        catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public X509CertificateHolder(byte[] certEncoding) throws IOException {
        this(X509CertificateHolder.parseBytes(certEncoding));
    }

    public X509CertificateHolder(Certificate x509Certificate) {
        this.init(x509Certificate);
    }

    private void init(Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
        this.extensions = x509Certificate.getTBSCertificate().getExtensions();
    }

    public int getVersionNumber() {
        return this.x509Certificate.getVersionNumber();
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        if (this.extensions != null) {
            return this.extensions.getExtension(oid);
        }
        return null;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public List getExtensionOIDs() {
        return CertUtils.getExtensionOIDs(this.extensions);
    }

    public Set getCriticalExtensionOIDs() {
        return CertUtils.getCriticalExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        return CertUtils.getNonCriticalExtensionOIDs(this.extensions);
    }

    public BigInteger getSerialNumber() {
        return this.x509Certificate.getSerialNumber().getValue();
    }

    public X500Name getIssuer() {
        return X500Name.getInstance((Object)this.x509Certificate.getIssuer());
    }

    public X500Name getSubject() {
        return X500Name.getInstance((Object)this.x509Certificate.getSubject());
    }

    public Date getNotBefore() {
        return this.x509Certificate.getStartDate().getDate();
    }

    public Date getNotAfter() {
        return this.x509Certificate.getEndDate().getDate();
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.x509Certificate.getSubjectPublicKeyInfo();
    }

    public Certificate toASN1Structure() {
        return this.x509Certificate;
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.x509Certificate.getSignatureAlgorithm();
    }

    public byte[] getSignature() {
        return this.x509Certificate.getSignature().getOctets();
    }

    public boolean isValidOn(Date date) {
        return !date.before(this.x509Certificate.getStartDate().getDate()) && !date.after(this.x509Certificate.getEndDate().getDate());
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws CertException {
        ContentVerifier verifier;
        TBSCertificate tbsCert = this.x509Certificate.getTBSCertificate();
        if (!CertUtils.isAlgIdEqual(tbsCert.getSignature(), this.x509Certificate.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        try {
            verifier = verifierProvider.get(tbsCert.getSignature());
            OutputStream sOut = verifier.getOutputStream();
            tbsCert.encodeTo(sOut, "DER");
            sOut.close();
        }
        catch (Exception e) {
            throw new CertException("unable to process signature: " + e.getMessage(), e);
        }
        return verifier.verify(this.getSignature());
    }

    public boolean isAlternativeSignatureValid(ContentVerifierProvider verifierProvider) throws CertException {
        ContentVerifier verifier;
        TBSCertificate tbsCert = this.x509Certificate.getTBSCertificate();
        AltSignatureAlgorithm altSigAlg = AltSignatureAlgorithm.fromExtensions((Extensions)tbsCert.getExtensions());
        AltSignatureValue altSigValue = AltSignatureValue.fromExtensions((Extensions)tbsCert.getExtensions());
        try {
            verifier = verifierProvider.get(AlgorithmIdentifier.getInstance((Object)altSigAlg.toASN1Primitive()));
            OutputStream sOut = verifier.getOutputStream();
            ASN1Sequence tbsSeq = ASN1Sequence.getInstance((Object)tbsCert.toASN1Primitive());
            ASN1EncodableVector v = new ASN1EncodableVector();
            for (int i = 0; i != tbsSeq.size() - 1; ++i) {
                if (i == 2) continue;
                v.add(tbsSeq.getObjectAt(i));
            }
            v.add((ASN1Encodable)CertUtils.trimExtensions(3, tbsCert.getExtensions()));
            new DERSequence(v).encodeTo(sOut, "DER");
            sOut.close();
        }
        catch (Exception e) {
            throw new CertException("unable to process signature: " + e.getMessage(), e);
        }
        return verifier.verify(altSigValue.getSignature().getOctets());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof X509CertificateHolder)) {
            return false;
        }
        X509CertificateHolder other = (X509CertificateHolder)o;
        return this.x509Certificate.equals((Object)other.x509Certificate);
    }

    public int hashCode() {
        return this.x509Certificate.hashCode();
    }

    public byte[] getEncoded() throws IOException {
        return this.x509Certificate.getEncoded();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init(Certificate.getInstance((Object)in.readObject()));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

