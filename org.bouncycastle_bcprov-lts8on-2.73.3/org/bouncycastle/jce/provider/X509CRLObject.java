/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.jce.provider.ExtCRLException;
import org.bouncycastle.jce.provider.RFC3280CertPathUtilities;
import org.bouncycastle.jce.provider.X509CRLEntryObject;
import org.bouncycastle.jce.provider.X509SignatureUtil;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class X509CRLObject
extends X509CRL {
    private CertificateList c;
    private String sigAlgName;
    private byte[] sigAlgParams;
    private boolean isIndirect;
    private boolean isHashCodeSet = false;
    private int hashCodeValue;

    public static boolean isIndirectCRL(X509CRL crl) throws CRLException {
        try {
            byte[] idp = crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
            return idp != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(idp).getOctets()).isIndirectCRL();
        }
        catch (Exception e) {
            throw new ExtCRLException("Exception reading IssuingDistributionPoint", e);
        }
    }

    public X509CRLObject(CertificateList c) throws CRLException {
        this.c = c;
        try {
            this.sigAlgName = X509SignatureUtil.getSignatureName(c.getSignatureAlgorithm());
            this.sigAlgParams = (byte[])(c.getSignatureAlgorithm().getParameters() != null ? c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER") : null);
            this.isIndirect = X509CRLObject.isIndirectCRL(this);
        }
        catch (Exception e) {
            throw new CRLException("CRL contents invalid: " + e);
        }
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        Set extns = this.getCriticalExtensionOIDs();
        if (extns == null) {
            return false;
        }
        extns.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
        extns.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
        return !extns.isEmpty();
    }

    private Set getExtensionOIDs(boolean critical) {
        Extensions extensions;
        if (this.getVersion() == 2 && (extensions = this.c.getTBSCertList().getExtensions()) != null) {
            HashSet<String> set = new HashSet<String>();
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (critical != ext.isCritical()) continue;
                set.add(oid.getId());
            }
            return set;
        }
        return null;
    }

    public Set getCriticalExtensionOIDs() {
        return this.getExtensionOIDs(true);
    }

    public Set getNonCriticalExtensionOIDs() {
        return this.getExtensionOIDs(false);
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        Extension ext;
        Extensions exts = this.c.getTBSCertList().getExtensions();
        if (exts != null && (ext = exts.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            try {
                return ext.getExtnValue().getEncoded();
            }
            catch (Exception e) {
                throw new IllegalStateException("error parsing " + e.toString());
            }
        }
        return null;
    }

    @Override
    public byte[] getEncoded() throws CRLException {
        try {
            return this.c.getEncoded("DER");
        }
        catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override
    public void verify(PublicKey key) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature sig;
        try {
            sig = Signature.getInstance(this.getSigAlgName(), "BC");
        }
        catch (Exception e) {
            sig = Signature.getInstance(this.getSigAlgName());
        }
        this.doVerify(key, sig);
    }

    @Override
    public void verify(PublicKey key, String sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature sig = sigProvider != null ? Signature.getInstance(this.getSigAlgName(), sigProvider) : Signature.getInstance(this.getSigAlgName());
        this.doVerify(key, sig);
    }

    @Override
    public void verify(PublicKey key, Provider sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = sigProvider != null ? Signature.getInstance(this.getSigAlgName(), sigProvider) : Signature.getInstance(this.getSigAlgName());
        this.doVerify(key, sig);
    }

    private void doVerify(PublicKey key, Signature sig) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        sig.initVerify(key);
        sig.update(this.getTBSCertList());
        if (!sig.verify(this.getSignature())) {
            throw new SignatureException("CRL does not verify with supplied public key.");
        }
    }

    @Override
    public int getVersion() {
        return this.c.getVersionNumber();
    }

    @Override
    public Principal getIssuerDN() {
        return this.getIssuerX500Principal();
    }

    @Override
    public X500Principal getIssuerX500Principal() {
        try {
            return new X500Principal(this.c.getIssuer().getEncoded());
        }
        catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override
    public Date getThisUpdate() {
        return this.c.getThisUpdate().getDate();
    }

    @Override
    public Date getNextUpdate() {
        if (this.c.getNextUpdate() != null) {
            return this.c.getNextUpdate().getDate();
        }
        return null;
    }

    private Set loadCRLEntries() {
        HashSet<X509CRLEntryObject> entrySet = new HashSet<X509CRLEntryObject>();
        Enumeration certs = this.c.getRevokedCertificateEnumeration();
        X500Name previousCertificateIssuer = null;
        while (certs.hasMoreElements()) {
            Extension currentCaName;
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry)certs.nextElement();
            X509CRLEntryObject crlEntry = new X509CRLEntryObject(entry, this.isIndirect, previousCertificateIssuer);
            entrySet.add(crlEntry);
            if (!this.isIndirect || !entry.hasExtensions() || (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) == null) continue;
            previousCertificateIssuer = X500Name.getInstance(GeneralNames.getInstance(currentCaName.getParsedValue()).getNames()[0].getName());
        }
        return entrySet;
    }

    @Override
    public X509CRLEntry getRevokedCertificate(BigInteger serialNumber) {
        Enumeration certs = this.c.getRevokedCertificateEnumeration();
        X500Name previousCertificateIssuer = null;
        while (certs.hasMoreElements()) {
            Extension currentCaName;
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry)certs.nextElement();
            if (entry.getUserCertificate().hasValue(serialNumber)) {
                return new X509CRLEntryObject(entry, this.isIndirect, previousCertificateIssuer);
            }
            if (!this.isIndirect || !entry.hasExtensions() || (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) == null) continue;
            previousCertificateIssuer = X500Name.getInstance(GeneralNames.getInstance(currentCaName.getParsedValue()).getNames()[0].getName());
        }
        return null;
    }

    public Set getRevokedCertificates() {
        Set entrySet = this.loadCRLEntries();
        if (!entrySet.isEmpty()) {
            return Collections.unmodifiableSet(entrySet);
        }
        return null;
    }

    @Override
    public byte[] getTBSCertList() throws CRLException {
        try {
            return this.c.getTBSCertList().getEncoded("DER");
        }
        catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override
    public byte[] getSignature() {
        return this.c.getSignature().getOctets();
    }

    @Override
    public String getSigAlgName() {
        return this.sigAlgName;
    }

    @Override
    public String getSigAlgOID() {
        return this.c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override
    public byte[] getSigAlgParams() {
        if (this.sigAlgParams != null) {
            byte[] tmp = new byte[this.sigAlgParams.length];
            System.arraycopy(this.sigAlgParams, 0, tmp, 0, tmp.length);
            return tmp;
        }
        return null;
    }

    @Override
    public String toString() {
        Set set;
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("              Version: ").append(this.getVersion()).append(nl);
        buf.append("             IssuerDN: ").append(this.getIssuerDN()).append(nl);
        buf.append("          This update: ").append(this.getThisUpdate()).append(nl);
        buf.append("          Next update: ").append(this.getNextUpdate()).append(nl);
        buf.append("  Signature Algorithm: ").append(this.getSigAlgName()).append(nl);
        byte[] sig = this.getSignature();
        buf.append("            Signature: ").append(new String(Hex.encode(sig, 0, 20))).append(nl);
        for (int i = 20; i < sig.length; i += 20) {
            if (i < sig.length - 20) {
                buf.append("                       ").append(new String(Hex.encode(sig, i, 20))).append(nl);
                continue;
            }
            buf.append("                       ").append(new String(Hex.encode(sig, i, sig.length - i))).append(nl);
        }
        Extensions extensions = this.c.getTBSCertList().getExtensions();
        if (extensions != null) {
            Enumeration e = extensions.oids();
            if (e.hasMoreElements()) {
                buf.append("           Extensions: ").append(nl);
            }
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (ext.getExtnValue() != null) {
                    byte[] octs = ext.getExtnValue().getOctets();
                    ASN1InputStream dIn = new ASN1InputStream(octs);
                    buf.append("                       critical(").append(ext.isCritical()).append(") ");
                    try {
                        if (oid.equals(Extension.cRLNumber)) {
                            buf.append(new CRLNumber(ASN1Integer.getInstance(dIn.readObject()).getPositiveValue())).append(nl);
                            continue;
                        }
                        if (oid.equals(Extension.deltaCRLIndicator)) {
                            buf.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(dIn.readObject()).getPositiveValue())).append(nl);
                            continue;
                        }
                        if (oid.equals(Extension.issuingDistributionPoint)) {
                            buf.append(IssuingDistributionPoint.getInstance(dIn.readObject())).append(nl);
                            continue;
                        }
                        if (oid.equals(Extension.cRLDistributionPoints)) {
                            buf.append(CRLDistPoint.getInstance(dIn.readObject())).append(nl);
                            continue;
                        }
                        if (oid.equals(Extension.freshestCRL)) {
                            buf.append(CRLDistPoint.getInstance(dIn.readObject())).append(nl);
                            continue;
                        }
                        buf.append(oid.getId());
                        buf.append(" value = ").append(ASN1Dump.dumpAsString(dIn.readObject())).append(nl);
                    }
                    catch (Exception ex) {
                        buf.append(oid.getId());
                        buf.append(" value = ").append("*****").append(nl);
                    }
                    continue;
                }
                buf.append(nl);
            }
        }
        if ((set = this.getRevokedCertificates()) != null) {
            Iterator it = set.iterator();
            while (it.hasNext()) {
                buf.append(it.next());
                buf.append(nl);
            }
        }
        return buf.toString();
    }

    @Override
    public boolean isRevoked(java.security.cert.Certificate cert) {
        if (!cert.getType().equals("X.509")) {
            throw new RuntimeException("X.509 CRL used with non X.509 Cert");
        }
        Enumeration certs = this.c.getRevokedCertificateEnumeration();
        X500Name caName = this.c.getIssuer();
        if (certs != null) {
            BigInteger serial = ((X509Certificate)cert).getSerialNumber();
            while (certs.hasMoreElements()) {
                X500Name issuer;
                Extension currentCaName;
                TBSCertList.CRLEntry entry = TBSCertList.CRLEntry.getInstance(certs.nextElement());
                if (this.isIndirect && entry.hasExtensions() && (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                    caName = X500Name.getInstance(GeneralNames.getInstance(currentCaName.getParsedValue()).getNames()[0].getName());
                }
                if (!entry.getUserCertificate().hasValue(serial)) continue;
                if (cert instanceof X509Certificate) {
                    issuer = X500Name.getInstance(((X509Certificate)cert).getIssuerX500Principal().getEncoded());
                } else {
                    try {
                        issuer = Certificate.getInstance(cert.getEncoded()).getIssuer();
                    }
                    catch (CertificateEncodingException e) {
                        throw new RuntimeException("Cannot process certificate");
                    }
                }
                return caName.equals(issuer);
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof X509CRL)) {
            return false;
        }
        if (other instanceof X509CRLObject) {
            boolean otherIsHashCodeSet;
            X509CRLObject crlObject = (X509CRLObject)other;
            if (this.isHashCodeSet && (otherIsHashCodeSet = crlObject.isHashCodeSet) && crlObject.hashCodeValue != this.hashCodeValue) {
                return false;
            }
            return this.c.equals(crlObject.c);
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        if (!this.isHashCodeSet) {
            this.isHashCodeSet = true;
            this.hashCodeValue = super.hashCode();
        }
        return this.hashCodeValue;
    }
}

