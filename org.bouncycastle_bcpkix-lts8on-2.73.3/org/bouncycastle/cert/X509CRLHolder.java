/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AltSignatureAlgorithm
 *  org.bouncycastle.asn1.x509.AltSignatureValue
 *  org.bouncycastle.asn1.x509.CertificateList
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.IssuingDistributionPoint
 *  org.bouncycastle.asn1.x509.TBSCertList
 *  org.bouncycastle.asn1.x509.TBSCertList$CRLEntry
 *  org.bouncycastle.asn1.x509.Time
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AltSignatureAlgorithm;
import org.bouncycastle.asn1.x509.AltSignatureValue;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.cert.X509CRLEntryHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509CRLHolder
implements Encodable,
Serializable {
    private static final long serialVersionUID = 20170722001L;
    private transient CertificateList x509CRL;
    private transient boolean isIndirect;
    private transient Extensions extensions;
    private transient GeneralNames issuerName;

    private static CertificateList parseStream(InputStream stream) throws IOException {
        try {
            ASN1Primitive obj = new ASN1InputStream(stream, true).readObject();
            if (obj == null) {
                throw new IOException("no content found");
            }
            return CertificateList.getInstance((Object)obj);
        }
        catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
    }

    private static boolean isIndirectCRL(Extensions extensions) {
        if (extensions == null) {
            return false;
        }
        Extension ext = extensions.getExtension(Extension.issuingDistributionPoint);
        return ext != null && IssuingDistributionPoint.getInstance((Object)ext.getParsedValue()).isIndirectCRL();
    }

    public X509CRLHolder(byte[] crlEncoding) throws IOException {
        this(X509CRLHolder.parseStream(new ByteArrayInputStream(crlEncoding)));
    }

    public X509CRLHolder(InputStream crlStream) throws IOException {
        this(X509CRLHolder.parseStream(crlStream));
    }

    public X509CRLHolder(CertificateList x509CRL) {
        this.init(x509CRL);
    }

    private void init(CertificateList x509CRL) {
        this.x509CRL = x509CRL;
        this.extensions = x509CRL.getTBSCertList().getExtensions();
        this.isIndirect = X509CRLHolder.isIndirectCRL(this.extensions);
        this.issuerName = new GeneralNames(new GeneralName(x509CRL.getIssuer()));
    }

    public byte[] getEncoded() throws IOException {
        return this.x509CRL.getEncoded();
    }

    public X500Name getIssuer() {
        return X500Name.getInstance((Object)this.x509CRL.getIssuer());
    }

    public Date getThisUpdate() {
        return this.x509CRL.getThisUpdate().getDate();
    }

    public Date getNextUpdate() {
        Time update = this.x509CRL.getNextUpdate();
        if (update != null) {
            return update.getDate();
        }
        return null;
    }

    public X509CRLEntryHolder getRevokedCertificate(BigInteger serialNumber) {
        GeneralNames currentCA = this.issuerName;
        Enumeration en = this.x509CRL.getRevokedCertificateEnumeration();
        while (en.hasMoreElements()) {
            Extension currentCaName;
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry)en.nextElement();
            if (entry.getUserCertificate().hasValue(serialNumber)) {
                return new X509CRLEntryHolder(entry, this.isIndirect, currentCA);
            }
            if (!this.isIndirect || !entry.hasExtensions() || (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) == null) continue;
            currentCA = GeneralNames.getInstance((Object)currentCaName.getParsedValue());
        }
        return null;
    }

    public Collection getRevokedCertificates() {
        TBSCertList.CRLEntry[] entries = this.x509CRL.getRevokedCertificates();
        ArrayList<X509CRLEntryHolder> l = new ArrayList<X509CRLEntryHolder>(entries.length);
        GeneralNames currentCA = this.issuerName;
        Enumeration en = this.x509CRL.getRevokedCertificateEnumeration();
        while (en.hasMoreElements()) {
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry)en.nextElement();
            X509CRLEntryHolder crlEntry = new X509CRLEntryHolder(entry, this.isIndirect, currentCA);
            l.add(crlEntry);
            currentCA = crlEntry.getCertificateIssuer();
        }
        return l;
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

    public CertificateList toASN1Structure() {
        return this.x509CRL;
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws CertException {
        ContentVerifier verifier;
        TBSCertList tbsCRL = this.x509CRL.getTBSCertList();
        if (!CertUtils.isAlgIdEqual(tbsCRL.getSignature(), this.x509CRL.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        try {
            verifier = verifierProvider.get(tbsCRL.getSignature());
            OutputStream sOut = verifier.getOutputStream();
            tbsCRL.encodeTo(sOut, "DER");
            sOut.close();
        }
        catch (Exception e) {
            throw new CertException("unable to process signature: " + e.getMessage(), e);
        }
        return verifier.verify(this.x509CRL.getSignature().getOctets());
    }

    public boolean isAlternativeSignatureValid(ContentVerifierProvider verifierProvider) throws CertException {
        ContentVerifier verifier;
        TBSCertList tbsCrList = this.x509CRL.getTBSCertList();
        AltSignatureAlgorithm altSigAlg = AltSignatureAlgorithm.fromExtensions((Extensions)tbsCrList.getExtensions());
        AltSignatureValue altSigValue = AltSignatureValue.fromExtensions((Extensions)tbsCrList.getExtensions());
        try {
            verifier = verifierProvider.get(AlgorithmIdentifier.getInstance((Object)altSigAlg.toASN1Primitive()));
            OutputStream sOut = verifier.getOutputStream();
            ASN1Sequence tbsSeq = ASN1Sequence.getInstance((Object)tbsCrList.toASN1Primitive());
            ASN1EncodableVector v = new ASN1EncodableVector();
            int start = 1;
            if (tbsSeq.getObjectAt(0) instanceof ASN1Integer) {
                v.add(tbsSeq.getObjectAt(0));
                ++start;
            }
            for (int i = start; i != tbsSeq.size() - 1; ++i) {
                v.add(tbsSeq.getObjectAt(i));
            }
            v.add((ASN1Encodable)CertUtils.trimExtensions(0, tbsCrList.getExtensions()));
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
        if (!(o instanceof X509CRLHolder)) {
            return false;
        }
        X509CRLHolder other = (X509CRLHolder)o;
        return this.x509CRL.equals((Object)other.x509CRL);
    }

    public int hashCode() {
        return this.x509CRL.hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init(CertificateList.getInstance((Object)in.readObject()));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

