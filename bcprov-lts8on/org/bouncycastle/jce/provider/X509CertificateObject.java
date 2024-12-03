/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.RFC3280CertPathUtilities;
import org.bouncycastle.jce.provider.X509SignatureUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class X509CertificateObject
extends X509Certificate
implements PKCS12BagAttributeCarrier {
    private Certificate c;
    private BasicConstraints basicConstraints;
    private boolean[] keyUsage;
    private boolean hashValueSet;
    private int hashValue;
    private PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

    public X509CertificateObject(Certificate c) throws CertificateParsingException {
        byte[] bytes;
        this.c = c;
        try {
            bytes = this.getExtensionBytes("2.5.29.19");
            if (bytes != null) {
                this.basicConstraints = BasicConstraints.getInstance(ASN1Primitive.fromByteArray(bytes));
            }
        }
        catch (Exception e) {
            throw new CertificateParsingException("cannot construct BasicConstraints: " + e);
        }
        try {
            bytes = this.getExtensionBytes("2.5.29.15");
            if (bytes != null) {
                ASN1BitString bits = ASN1BitString.getInstance(ASN1Primitive.fromByteArray(bytes));
                int length = (bytes = bits.getBytes()).length * 8 - bits.getPadBits();
                this.keyUsage = new boolean[length < 9 ? 9 : length];
                for (int i = 0; i != length; ++i) {
                    this.keyUsage[i] = (bytes[i / 8] & 128 >>> i % 8) != 0;
                }
            } else {
                this.keyUsage = null;
            }
        }
        catch (Exception e) {
            throw new CertificateParsingException("cannot construct KeyUsage: " + e);
        }
    }

    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.checkValidity(new Date());
    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        if (date.getTime() > this.getNotAfter().getTime()) {
            throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime());
        }
        if (date.getTime() < this.getNotBefore().getTime()) {
            throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime());
        }
    }

    @Override
    public int getVersion() {
        return this.c.getVersionNumber();
    }

    @Override
    public BigInteger getSerialNumber() {
        return this.c.getSerialNumber().getValue();
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
    public Principal getSubjectDN() {
        return this.getSubjectX500Principal();
    }

    @Override
    public X500Principal getSubjectX500Principal() {
        try {
            return new X500Principal(this.c.getSubject().getEncoded());
        }
        catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override
    public Date getNotBefore() {
        return this.c.getStartDate().getDate();
    }

    @Override
    public Date getNotAfter() {
        return this.c.getEndDate().getDate();
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        try {
            return this.c.getTBSCertificate().getEncoded("DER");
        }
        catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    @Override
    public byte[] getSignature() {
        return this.c.getSignature().getOctets();
    }

    @Override
    public String getSigAlgName() {
        String algName;
        Provider prov = Security.getProvider("BC");
        if (prov != null && (algName = prov.getProperty("Alg.Alias.Signature." + this.getSigAlgOID())) != null) {
            return algName;
        }
        Provider[] provs = Security.getProviders();
        for (int i = 0; i != provs.length; ++i) {
            String algName2 = provs[i].getProperty("Alg.Alias.Signature." + this.getSigAlgOID());
            if (algName2 == null) continue;
            return algName2;
        }
        return this.getSigAlgOID();
    }

    @Override
    public String getSigAlgOID() {
        return this.c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override
    public byte[] getSigAlgParams() {
        if (this.c.getSignatureAlgorithm().getParameters() != null) {
            try {
                return this.c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER");
            }
            catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        ASN1BitString id = this.c.getTBSCertificate().getIssuerUniqueId();
        if (id != null) {
            byte[] bytes = id.getBytes();
            boolean[] boolId = new boolean[bytes.length * 8 - id.getPadBits()];
            for (int i = 0; i != boolId.length; ++i) {
                boolId[i] = (bytes[i / 8] & 128 >>> i % 8) != 0;
            }
            return boolId;
        }
        return null;
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        ASN1BitString id = this.c.getTBSCertificate().getSubjectUniqueId();
        if (id != null) {
            byte[] bytes = id.getBytes();
            boolean[] boolId = new boolean[bytes.length * 8 - id.getPadBits()];
            for (int i = 0; i != boolId.length; ++i) {
                boolId[i] = (bytes[i / 8] & 128 >>> i % 8) != 0;
            }
            return boolId;
        }
        return null;
    }

    @Override
    public boolean[] getKeyUsage() {
        return this.keyUsage;
    }

    public List getExtendedKeyUsage() throws CertificateParsingException {
        byte[] bytes = this.getExtensionBytes("2.5.29.37");
        if (bytes != null) {
            try {
                ASN1InputStream dIn = new ASN1InputStream(bytes);
                ASN1Sequence seq = (ASN1Sequence)dIn.readObject();
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i != seq.size(); ++i) {
                    list.add(((ASN1ObjectIdentifier)seq.getObjectAt(i)).getId());
                }
                return Collections.unmodifiableList(list);
            }
            catch (Exception e) {
                throw new CertificateParsingException("error processing extended key usage extension");
            }
        }
        return null;
    }

    @Override
    public int getBasicConstraints() {
        if (this.basicConstraints == null || !this.basicConstraints.isCA()) {
            return -1;
        }
        ASN1Integer pathLenConstraint = this.basicConstraints.getPathLenConstraintInteger();
        if (pathLenConstraint == null) {
            return Integer.MAX_VALUE;
        }
        return pathLenConstraint.intPositiveValueExact();
    }

    public Collection getSubjectAlternativeNames() throws CertificateParsingException {
        return X509CertificateObject.getAlternativeNames(this.getExtensionBytes(Extension.subjectAlternativeName.getId()));
    }

    public Collection getIssuerAlternativeNames() throws CertificateParsingException {
        return X509CertificateObject.getAlternativeNames(this.getExtensionBytes(Extension.issuerAlternativeName.getId()));
    }

    public Set getCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            HashSet<String> set = new HashSet<String>();
            Extensions extensions = this.c.getTBSCertificate().getExtensions();
            if (extensions != null) {
                Enumeration e = extensions.oids();
                while (e.hasMoreElements()) {
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                    Extension ext = extensions.getExtension(oid);
                    if (!ext.isCritical()) continue;
                    set.add(oid.getId());
                }
                return set;
            }
        }
        return null;
    }

    private byte[] getExtensionBytes(String oid) {
        Extension ext;
        Extensions exts = this.c.getTBSCertificate().getExtensions();
        if (exts != null && (ext = exts.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            return ext.getExtnValue().getOctets();
        }
        return null;
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        Extension ext;
        Extensions exts = this.c.getTBSCertificate().getExtensions();
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

    public Set getNonCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            HashSet<String> set = new HashSet<String>();
            Extensions extensions = this.c.getTBSCertificate().getExtensions();
            if (extensions != null) {
                Enumeration e = extensions.oids();
                while (e.hasMoreElements()) {
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                    Extension ext = extensions.getExtension(oid);
                    if (ext.isCritical()) continue;
                    set.add(oid.getId());
                }
                return set;
            }
        }
        return null;
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        Extensions extensions;
        if (this.getVersion() == 3 && (extensions = this.c.getTBSCertificate().getExtensions()) != null) {
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                Extension ext;
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                String oidId = oid.getId();
                if (oidId.equals(RFC3280CertPathUtilities.KEY_USAGE) || oidId.equals(RFC3280CertPathUtilities.CERTIFICATE_POLICIES) || oidId.equals(RFC3280CertPathUtilities.POLICY_MAPPINGS) || oidId.equals(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY) || oidId.equals(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS) || oidId.equals(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT) || oidId.equals(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR) || oidId.equals(RFC3280CertPathUtilities.POLICY_CONSTRAINTS) || oidId.equals(RFC3280CertPathUtilities.BASIC_CONSTRAINTS) || oidId.equals(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME) || oidId.equals(RFC3280CertPathUtilities.NAME_CONSTRAINTS) || !(ext = extensions.getExtension(oid)).isCritical()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public PublicKey getPublicKey() {
        try {
            return BouncyCastleProvider.getPublicKey(this.c.getSubjectPublicKeyInfo());
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        try {
            return this.c.getEncoded("DER");
        }
        catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof java.security.cert.Certificate)) {
            return false;
        }
        java.security.cert.Certificate other = (java.security.cert.Certificate)o;
        try {
            byte[] b1 = this.getEncoded();
            byte[] b2 = other.getEncoded();
            return Arrays.areEqual(b1, b2);
        }
        catch (CertificateEncodingException e) {
            return false;
        }
    }

    @Override
    public synchronized int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = this.calculateHashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    private int calculateHashCode() {
        try {
            int hashCode = 0;
            byte[] certData = this.getEncoded();
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

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("  [0]         Version: ").append(this.getVersion()).append(nl);
        buf.append("         SerialNumber: ").append(this.getSerialNumber()).append(nl);
        buf.append("             IssuerDN: ").append(this.getIssuerDN()).append(nl);
        buf.append("           Start Date: ").append(this.getNotBefore()).append(nl);
        buf.append("           Final Date: ").append(this.getNotAfter()).append(nl);
        buf.append("            SubjectDN: ").append(this.getSubjectDN()).append(nl);
        buf.append("           Public Key: ").append(this.getPublicKey()).append(nl);
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
        Extensions extensions = this.c.getTBSCertificate().getExtensions();
        if (extensions != null) {
            Enumeration e = extensions.oids();
            if (e.hasMoreElements()) {
                buf.append("       Extensions: \n");
            }
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (ext.getExtnValue() != null) {
                    byte[] octs = ext.getExtnValue().getOctets();
                    ASN1InputStream dIn = new ASN1InputStream(octs);
                    buf.append("                       critical(").append(ext.isCritical()).append(") ");
                    try {
                        if (oid.equals(Extension.basicConstraints)) {
                            buf.append(BasicConstraints.getInstance(dIn.readObject())).append(nl);
                            continue;
                        }
                        if (oid.equals(Extension.keyUsage)) {
                            buf.append(KeyUsage.getInstance(dIn.readObject())).append(nl);
                            continue;
                        }
                        if (oid.equals(MiscObjectIdentifiers.netscapeCertType)) {
                            buf.append(new NetscapeCertType((ASN1BitString)dIn.readObject())).append(nl);
                            continue;
                        }
                        if (oid.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                            buf.append(new NetscapeRevocationURL((ASN1IA5String)dIn.readObject())).append(nl);
                            continue;
                        }
                        if (oid.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                            buf.append(new VerisignCzagExtension((ASN1IA5String)dIn.readObject())).append(nl);
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
        return buf.toString();
    }

    @Override
    public final void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature;
        String sigName = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
        try {
            signature = Signature.getInstance(sigName, "BC");
        }
        catch (Exception e) {
            signature = Signature.getInstance(sigName);
        }
        this.checkSignature(key, signature);
    }

    @Override
    public final void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        String sigName = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
        Signature signature = sigProvider != null ? Signature.getInstance(sigName, sigProvider) : Signature.getInstance(sigName);
        this.checkSignature(key, signature);
    }

    @Override
    public final void verify(PublicKey key, Provider sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String sigName = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
        Signature signature = sigProvider != null ? Signature.getInstance(sigName, sigProvider) : Signature.getInstance(sigName);
        this.checkSignature(key, signature);
    }

    private void checkSignature(PublicKey key, Signature signature) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (!this.isAlgIdEqual(this.c.getSignatureAlgorithm(), this.c.getTBSCertificate().getSignature())) {
            throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
        }
        ASN1Encodable params = this.c.getSignatureAlgorithm().getParameters();
        X509SignatureUtil.setSignatureParameters(signature, params);
        signature.initVerify(key);
        signature.update(this.getTBSCertificate());
        if (!signature.verify(this.getSignature())) {
            throw new SignatureException("certificate does not verify with supplied key");
        }
    }

    private boolean isAlgIdEqual(AlgorithmIdentifier id1, AlgorithmIdentifier id2) {
        if (!id1.getAlgorithm().equals(id2.getAlgorithm())) {
            return false;
        }
        if (id1.getParameters() == null) {
            return id2.getParameters() == null || id2.getParameters().equals(DERNull.INSTANCE);
        }
        if (id2.getParameters() == null) {
            return id1.getParameters() == null || id1.getParameters().equals(DERNull.INSTANCE);
        }
        return id1.getParameters().equals(id2.getParameters());
    }

    private static Collection getAlternativeNames(byte[] extVal) throws CertificateParsingException {
        if (extVal == null) {
            return null;
        }
        try {
            ArrayList temp = new ArrayList();
            Enumeration it = ASN1Sequence.getInstance(extVal).getObjects();
            block11: while (it.hasMoreElements()) {
                GeneralName genName = GeneralName.getInstance(it.nextElement());
                ArrayList<Object> list = new ArrayList<Object>();
                list.add(Integers.valueOf(genName.getTagNo()));
                switch (genName.getTagNo()) {
                    case 0: 
                    case 3: 
                    case 5: {
                        list.add(genName.getEncoded());
                        break;
                    }
                    case 4: {
                        list.add(X500Name.getInstance(RFC4519Style.INSTANCE, genName.getName()).toString());
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 6: {
                        list.add(((ASN1String)((Object)genName.getName())).getString());
                        break;
                    }
                    case 8: {
                        list.add(ASN1ObjectIdentifier.getInstance(genName.getName()).getId());
                        break;
                    }
                    case 7: {
                        String addr;
                        byte[] addrBytes = DEROctetString.getInstance(genName.getName()).getOctets();
                        try {
                            addr = InetAddress.getByAddress(addrBytes).getHostAddress();
                        }
                        catch (UnknownHostException e) {
                            continue block11;
                        }
                        list.add(addr);
                        break;
                    }
                    default: {
                        throw new IOException("Bad tag number: " + genName.getTagNo());
                    }
                }
                temp.add(Collections.unmodifiableList(list));
            }
            if (temp.size() == 0) {
                return null;
            }
            return Collections.unmodifiableCollection(temp);
        }
        catch (Exception e) {
            throw new CertificateParsingException(e.getMessage());
        }
    }
}

