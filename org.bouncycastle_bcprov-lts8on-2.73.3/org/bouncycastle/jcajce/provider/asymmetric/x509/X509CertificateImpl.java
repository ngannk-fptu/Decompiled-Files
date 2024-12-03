/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
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
import org.bouncycastle.asn1.ASN1OctetString;
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
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509SignatureUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

abstract class X509CertificateImpl
extends X509Certificate
implements BCX509Certificate {
    protected JcaJceHelper bcHelper;
    protected Certificate c;
    protected BasicConstraints basicConstraints;
    protected boolean[] keyUsage;
    protected String sigAlgName;
    protected byte[] sigAlgParams;

    X509CertificateImpl(JcaJceHelper bcHelper, Certificate c, BasicConstraints basicConstraints, boolean[] keyUsage, String sigAlgName, byte[] sigAlgParams) {
        this.bcHelper = bcHelper;
        this.c = c;
        this.basicConstraints = basicConstraints;
        this.keyUsage = keyUsage;
        this.sigAlgName = sigAlgName;
        this.sigAlgParams = sigAlgParams;
    }

    @Override
    public X500Name getIssuerX500Name() {
        return this.c.getIssuer();
    }

    @Override
    public TBSCertificate getTBSCertificateNative() {
        return this.c.getTBSCertificate();
    }

    @Override
    public X500Name getSubjectX500Name() {
        return this.c.getSubject();
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
            byte[] encoding = this.c.getIssuer().getEncoded("DER");
            return new X500Principal(encoding);
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
            byte[] encoding = this.c.getSubject().getEncoded("DER");
            return new X500Principal(encoding);
        }
        catch (IOException e) {
            throw new IllegalStateException("can't encode subject DN");
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
        return Arrays.clone(this.keyUsage);
    }

    public List getExtendedKeyUsage() throws CertificateParsingException {
        byte[] extOctets = X509CertificateImpl.getExtensionOctets(this.c, "2.5.29.37");
        if (null == extOctets) {
            return null;
        }
        try {
            ASN1Sequence seq = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(extOctets));
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
        return X509CertificateImpl.getAlternativeNames(this.c, Extension.subjectAlternativeName.getId());
    }

    public Collection getIssuerAlternativeNames() throws CertificateParsingException {
        return X509CertificateImpl.getAlternativeNames(this.c, Extension.issuerAlternativeName.getId());
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

    @Override
    public byte[] getExtensionValue(String oid) {
        ASN1OctetString extValue = X509CertificateImpl.getExtensionValue(this.c, oid);
        if (null != extValue) {
            try {
                return extValue.getEncoded();
            }
            catch (Exception e) {
                throw Exceptions.illegalStateException("error parsing " + e.getMessage(), e);
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
                if (oid.equals(Extension.keyUsage) || oid.equals(Extension.certificatePolicies) || oid.equals(Extension.policyMappings) || oid.equals(Extension.inhibitAnyPolicy) || oid.equals(Extension.cRLDistributionPoints) || oid.equals(Extension.issuingDistributionPoint) || oid.equals(Extension.deltaCRLIndicator) || oid.equals(Extension.policyConstraints) || oid.equals(Extension.basicConstraints) || oid.equals(Extension.subjectAlternativeName) || oid.equals(Extension.nameConstraints) || !(ext = extensions.getExtension(oid)).isCritical()) continue;
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
            throw Exceptions.illegalStateException("failed to recover public key: " + e.getMessage(), e);
        }
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
        X509SignatureUtil.prettyPrintSignature(this.getSignature(), buf, nl);
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
                            buf.append(new NetscapeCertType(ASN1BitString.getInstance(dIn.readObject()))).append(nl);
                            continue;
                        }
                        if (oid.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                            buf.append(new NetscapeRevocationURL(ASN1IA5String.getInstance(dIn.readObject()))).append(nl);
                            continue;
                        }
                        if (oid.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                            buf.append(new VerisignCzagExtension(ASN1IA5String.getInstance(dIn.readObject()))).append(nl);
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
        this.doVerify(key, new SignatureCreator(){

            @Override
            public Signature createSignature(String sigName) throws NoSuchAlgorithmException {
                try {
                    return X509CertificateImpl.this.bcHelper.createSignature(sigName);
                }
                catch (Exception e) {
                    return Signature.getInstance(sigName);
                }
            }
        });
    }

    @Override
    public final void verify(PublicKey key, final String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
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
    public final void verify(PublicKey key, final Provider sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            this.doVerify(key, new SignatureCreator(){

                @Override
                public Signature createSignature(String sigName) throws NoSuchAlgorithmException {
                    if (sigProvider != null) {
                        return Signature.getInstance(sigName, sigProvider);
                    }
                    return Signature.getInstance(sigName);
                }
            });
        }
        catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("provider issue: " + e.getMessage());
        }
    }

    private void doVerify(PublicKey key, SignatureCreator signatureCreator) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        if (key instanceof CompositePublicKey && X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            List<PublicKey> pubKeys = ((CompositePublicKey)key).getPublicKeys();
            ASN1Sequence keySeq = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence sigSeq = ASN1Sequence.getInstance(ASN1BitString.getInstance(this.c.getSignature()).getBytes());
            boolean success = false;
            for (int i = 0; i != pubKeys.size(); ++i) {
                if (pubKeys.get(i) == null) continue;
                AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance(keySeq.getObjectAt(i));
                String sigName = X509SignatureUtil.getSignatureName(sigAlg);
                Signature signature = signatureCreator.createSignature(sigName);
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
                    Signature signature = signatureCreator.createSignature(sigName);
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
            String sigName = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
            Signature signature = signatureCreator.createSignature(sigName);
            if (key instanceof CompositePublicKey) {
                List<PublicKey> keys = ((CompositePublicKey)key).getPublicKeys();
                for (int i = 0; i != keys.size(); ++i) {
                    try {
                        this.checkSignature(keys.get(i), signature, this.c.getSignatureAlgorithm().getParameters(), this.getSignature());
                        return;
                    }
                    catch (InvalidKeyException invalidKeyException) {
                        continue;
                    }
                }
                throw new InvalidKeyException("no matching signature found");
            }
            this.checkSignature(key, signature, this.c.getSignatureAlgorithm().getParameters(), this.getSignature());
        }
    }

    private void checkSignature(PublicKey key, Signature signature, ASN1Encodable params, byte[] sigBytes) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (!this.isAlgIdEqual(this.c.getSignatureAlgorithm(), this.c.getTBSCertificate().getSignature())) {
            throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
        }
        X509SignatureUtil.setSignatureParameters(signature, params);
        signature.initVerify(key);
        try {
            BufferedOutputStream sigOut = new BufferedOutputStream(OutputStreamFactory.createStream(signature), 512);
            this.c.getTBSCertificate().encodeTo(sigOut, "DER");
            ((OutputStream)sigOut).close();
        }
        catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
        if (!signature.verify(sigBytes)) {
            throw new SignatureException("certificate does not verify with supplied key");
        }
    }

    private boolean isAlgIdEqual(AlgorithmIdentifier id1, AlgorithmIdentifier id2) {
        if (!id1.getAlgorithm().equals(id2.getAlgorithm())) {
            return false;
        }
        if (Properties.isOverrideSet("org.bouncycastle.x509.allow_absent_equiv_NULL")) {
            if (id1.getParameters() == null) {
                return id2.getParameters() == null || id2.getParameters().equals(DERNull.INSTANCE);
            }
            if (id2.getParameters() == null) {
                return id1.getParameters() == null || id1.getParameters().equals(DERNull.INSTANCE);
            }
        }
        if (id1.getParameters() != null) {
            return id1.getParameters().equals(id2.getParameters());
        }
        if (id2.getParameters() != null) {
            return id2.getParameters().equals(id1.getParameters());
        }
        return true;
    }

    private static Collection getAlternativeNames(Certificate c, String oid) throws CertificateParsingException {
        byte[] extOctets = X509CertificateImpl.getExtensionOctets(c, oid);
        if (extOctets == null) {
            return null;
        }
        try {
            ArrayList temp = new ArrayList();
            Enumeration it = ASN1Sequence.getInstance(extOctets).getObjects();
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

    protected static byte[] getExtensionOctets(Certificate c, String oid) {
        ASN1OctetString extValue = X509CertificateImpl.getExtensionValue(c, oid);
        if (null != extValue) {
            return extValue.getOctets();
        }
        return null;
    }

    protected static ASN1OctetString getExtensionValue(Certificate c, String oid) {
        Extension ext;
        Extensions exts = c.getTBSCertificate().getExtensions();
        if (null != exts && null != (ext = exts.getExtension(new ASN1ObjectIdentifier(oid)))) {
            return ext.getExtnValue();
        }
        return null;
    }
}

