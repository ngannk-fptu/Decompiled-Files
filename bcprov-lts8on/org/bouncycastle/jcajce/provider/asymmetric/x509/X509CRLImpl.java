/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLEntryObject;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509SignatureUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

abstract class X509CRLImpl
extends X509CRL {
    protected JcaJceHelper bcHelper;
    protected CertificateList c;
    protected String sigAlgName;
    protected byte[] sigAlgParams;
    protected boolean isIndirect;

    X509CRLImpl(JcaJceHelper bcHelper, CertificateList c, String sigAlgName, byte[] sigAlgParams, boolean isIndirect) {
        this.bcHelper = bcHelper;
        this.c = c;
        this.sigAlgName = sigAlgName;
        this.sigAlgParams = sigAlgParams;
        this.isIndirect = isIndirect;
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        Set extns = this.getCriticalExtensionOIDs();
        if (extns == null) {
            return false;
        }
        extns.remove(Extension.issuingDistributionPoint.getId());
        extns.remove(Extension.deltaCRLIndicator.getId());
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
        ASN1OctetString extValue = X509CRLImpl.getExtensionValue(this.c, oid);
        if (null != extValue) {
            try {
                return extValue.getEncoded();
            }
            catch (Exception e) {
                throw new IllegalStateException("error parsing " + e.toString());
            }
        }
        return null;
    }

    @Override
    public void verify(PublicKey key) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.doVerify(key, new SignatureCreator(){

            @Override
            public Signature createSignature(String sigName) throws NoSuchAlgorithmException, NoSuchProviderException {
                try {
                    return X509CRLImpl.this.bcHelper.createSignature(sigName);
                }
                catch (Exception e) {
                    return Signature.getInstance(sigName);
                }
            }
        });
    }

    @Override
    public void verify(PublicKey key, final String sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.doVerify(key, new SignatureCreator(){

            @Override
            public Signature createSignature(String sigName) throws NoSuchAlgorithmException, NoSuchProviderException {
                if (sigProvider != null) {
                    return Signature.getInstance(sigName, sigProvider);
                }
                return Signature.getInstance(sigName);
            }
        });
    }

    @Override
    public void verify(PublicKey key, final Provider sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            this.doVerify(key, new SignatureCreator(){

                @Override
                public Signature createSignature(String sigName) throws NoSuchAlgorithmException, NoSuchProviderException {
                    if (sigProvider != null) {
                        return Signature.getInstance(X509CRLImpl.this.getSigAlgName(), sigProvider);
                    }
                    return Signature.getInstance(X509CRLImpl.this.getSigAlgName());
                }
            });
        }
        catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("provider issue: " + e.getMessage());
        }
    }

    private void doVerify(PublicKey key, SignatureCreator sigCreator) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        if (key instanceof CompositePublicKey && X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            List<PublicKey> pubKeys = ((CompositePublicKey)key).getPublicKeys();
            ASN1Sequence keySeq = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence sigSeq = ASN1Sequence.getInstance(ASN1BitString.getInstance(this.c.getSignature()).getBytes());
            boolean success = false;
            for (int i = 0; i != pubKeys.size(); ++i) {
                if (pubKeys.get(i) == null) continue;
                AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance(keySeq.getObjectAt(i));
                String sigName = X509SignatureUtil.getSignatureName(sigAlg);
                Signature signature = sigCreator.createSignature(sigName);
                SignatureException sigExc = null;
                try {
                    this.checkSignature(pubKeys.get(i), signature, sigAlg.getParameters(), ASN1BitString.getInstance(sigSeq.getObjectAt(i)).getBytes());
                    success = true;
                }
                catch (SignatureException e) {
                    sigExc = e;
                }
                if (sigExc == null) continue;
                throw sigExc;
            }
            if (!success) {
                throw new InvalidKeyException("no matching key found");
            }
        } else if (X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            ASN1Sequence keySeq = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence sigSeq = ASN1Sequence.getInstance(ASN1BitString.getInstance(this.c.getSignature()).getBytes());
            boolean success = false;
            for (int i = 0; i != sigSeq.size(); ++i) {
                AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance(keySeq.getObjectAt(i));
                String sigName = X509SignatureUtil.getSignatureName(sigAlg);
                SignatureException sigExc = null;
                try {
                    Signature signature = sigCreator.createSignature(sigName);
                    this.checkSignature(key, signature, sigAlg.getParameters(), ASN1BitString.getInstance(sigSeq.getObjectAt(i)).getBytes());
                    success = true;
                }
                catch (InvalidKeyException signature) {
                }
                catch (NoSuchAlgorithmException signature) {
                }
                catch (SignatureException e) {
                    sigExc = e;
                }
                if (sigExc == null) continue;
                throw sigExc;
            }
            if (!success) {
                throw new InvalidKeyException("no matching key found");
            }
        } else {
            Signature sig = sigCreator.createSignature(this.getSigAlgName());
            if (this.sigAlgParams == null) {
                this.checkSignature(key, sig, null, this.getSignature());
            } else {
                try {
                    this.checkSignature(key, sig, ASN1Primitive.fromByteArray(this.sigAlgParams), this.getSignature());
                }
                catch (IOException e) {
                    throw new SignatureException("cannot decode signature parameters: " + e.getMessage());
                }
            }
        }
    }

    private void checkSignature(PublicKey key, Signature sig, ASN1Encodable sigAlgParams, byte[] encSig) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CRLException {
        if (sigAlgParams != null) {
            X509SignatureUtil.setSignatureParameters(sig, sigAlgParams);
        }
        sig.initVerify(key);
        try {
            BufferedOutputStream sigOut = new BufferedOutputStream(OutputStreamFactory.createStream(sig), 512);
            this.c.getTBSCertList().encodeTo(sigOut, "DER");
            ((OutputStream)sigOut).close();
        }
        catch (IOException e) {
            throw new CRLException(e.toString());
        }
        if (!sig.verify(encSig)) {
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
        Time nextUpdate = this.c.getNextUpdate();
        return null == nextUpdate ? null : nextUpdate.getDate();
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
        return Arrays.clone(this.sigAlgParams);
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
        X509SignatureUtil.prettyPrintSignature(this.getSignature(), buf, nl);
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
            throw new IllegalArgumentException("X.509 CRL used with non X.509 Cert");
        }
        Enumeration certs = this.c.getRevokedCertificateEnumeration();
        X500Name caName = this.c.getIssuer();
        if (certs.hasMoreElements()) {
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
                        throw new IllegalArgumentException("Cannot process certificate: " + e.getMessage());
                    }
                }
                return caName.equals(issuer);
            }
        }
        return false;
    }

    protected static byte[] getExtensionOctets(CertificateList c, String oid) {
        ASN1OctetString extValue = X509CRLImpl.getExtensionValue(c, oid);
        if (null != extValue) {
            return extValue.getOctets();
        }
        return null;
    }

    protected static ASN1OctetString getExtensionValue(CertificateList c, String oid) {
        Extension ext;
        Extensions exts = c.getTBSCertList().getExtensions();
        if (null != exts && null != (ext = exts.getExtension(new ASN1ObjectIdentifier(oid)))) {
            return ext.getExtnValue();
        }
        return null;
    }
}

