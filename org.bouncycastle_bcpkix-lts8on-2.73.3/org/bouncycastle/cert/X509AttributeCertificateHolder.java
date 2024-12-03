/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AttCertValidityPeriod
 *  org.bouncycastle.asn1.x509.Attribute
 *  org.bouncycastle.asn1.x509.AttributeCertificate
 *  org.bouncycastle.asn1.x509.AttributeCertificateInfo
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttCertValidityPeriod;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509AttributeCertificateHolder
implements Encodable,
Serializable {
    private static final long serialVersionUID = 20170722001L;
    private static Attribute[] EMPTY_ARRAY = new Attribute[0];
    private transient AttributeCertificate attrCert;
    private transient Extensions extensions;

    private static AttributeCertificate parseBytes(byte[] certEncoding) throws IOException {
        try {
            return AttributeCertificate.getInstance((Object)CertUtils.parseNonEmptyASN1(certEncoding));
        }
        catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public X509AttributeCertificateHolder(byte[] certEncoding) throws IOException {
        this(X509AttributeCertificateHolder.parseBytes(certEncoding));
    }

    public X509AttributeCertificateHolder(AttributeCertificate attrCert) {
        this.init(attrCert);
    }

    private void init(AttributeCertificate attrCert) {
        this.attrCert = attrCert;
        this.extensions = attrCert.getAcinfo().getExtensions();
    }

    public byte[] getEncoded() throws IOException {
        return this.attrCert.getEncoded();
    }

    public int getVersion() {
        return this.attrCert.getAcinfo().getVersion().intValueExact() + 1;
    }

    public BigInteger getSerialNumber() {
        return this.attrCert.getAcinfo().getSerialNumber().getValue();
    }

    public AttributeCertificateHolder getHolder() {
        return new AttributeCertificateHolder((ASN1Sequence)this.attrCert.getAcinfo().getHolder().toASN1Primitive());
    }

    public AttributeCertificateIssuer getIssuer() {
        return new AttributeCertificateIssuer(this.attrCert.getAcinfo().getIssuer());
    }

    public Date getNotBefore() {
        return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime());
    }

    public Date getNotAfter() {
        return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime());
    }

    public Attribute[] getAttributes() {
        ASN1Sequence seq = this.attrCert.getAcinfo().getAttributes();
        Attribute[] attrs = new Attribute[seq.size()];
        for (int i = 0; i != seq.size(); ++i) {
            attrs[i] = Attribute.getInstance((Object)seq.getObjectAt(i));
        }
        return attrs;
    }

    public Attribute[] getAttributes(ASN1ObjectIdentifier type) {
        ASN1Sequence seq = this.attrCert.getAcinfo().getAttributes();
        ArrayList<Attribute> list = new ArrayList<Attribute>();
        for (int i = 0; i != seq.size(); ++i) {
            Attribute attr = Attribute.getInstance((Object)seq.getObjectAt(i));
            if (!attr.getAttrType().equals((ASN1Primitive)type)) continue;
            list.add(attr);
        }
        if (list.size() == 0) {
            return EMPTY_ARRAY;
        }
        return list.toArray(new Attribute[list.size()]);
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

    public boolean[] getIssuerUniqueID() {
        return CertUtils.bitStringToBoolean(this.attrCert.getAcinfo().getIssuerUniqueID());
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.attrCert.getSignatureAlgorithm();
    }

    public byte[] getSignature() {
        return this.attrCert.getSignatureValue().getOctets();
    }

    public AttributeCertificate toASN1Structure() {
        return this.attrCert;
    }

    public boolean isValidOn(Date date) {
        AttCertValidityPeriod certValidityPeriod = this.attrCert.getAcinfo().getAttrCertValidityPeriod();
        return !date.before(CertUtils.recoverDate(certValidityPeriod.getNotBeforeTime())) && !date.after(CertUtils.recoverDate(certValidityPeriod.getNotAfterTime()));
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws CertException {
        ContentVerifier verifier;
        AttributeCertificateInfo acinfo = this.attrCert.getAcinfo();
        if (!CertUtils.isAlgIdEqual(acinfo.getSignature(), this.attrCert.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        try {
            verifier = verifierProvider.get(acinfo.getSignature());
            OutputStream sOut = verifier.getOutputStream();
            acinfo.encodeTo(sOut, "DER");
            sOut.close();
        }
        catch (Exception e) {
            throw new CertException("unable to process signature: " + e.getMessage(), e);
        }
        return verifier.verify(this.getSignature());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof X509AttributeCertificateHolder)) {
            return false;
        }
        X509AttributeCertificateHolder other = (X509AttributeCertificateHolder)o;
        return this.attrCert.equals((Object)other.attrCert);
    }

    public int hashCode() {
        return this.attrCert.hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init(AttributeCertificate.getInstance((Object)in.readObject()));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

