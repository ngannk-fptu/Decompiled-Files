/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.keystore.pkcs12;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedData;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12StoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.AdaptingKeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class PKCS12KeyStoreSpi
extends KeyStoreSpi
implements PKCSObjectIdentifiers,
X509ObjectIdentifiers,
BCKeyStore {
    static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.bouncycastle.pkcs12.max_it_count";
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private static final int SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 51200;
    private static final DefaultSecretKeyProvider keySizeProvider = new DefaultSecretKeyProvider();
    private IgnoresCaseHashtable keys = new IgnoresCaseHashtable();
    private IgnoresCaseHashtable localIds = new IgnoresCaseHashtable();
    private IgnoresCaseHashtable certs = new IgnoresCaseHashtable();
    private Hashtable chainCerts = new Hashtable();
    private Hashtable keyCerts = new Hashtable();
    static final int NULL = 0;
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int SECRET = 3;
    static final int SEALED = 4;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    private CertificateFactory certFact;
    private ASN1ObjectIdentifier keyAlgorithm;
    private ASN1ObjectIdentifier certAlgorithm;
    private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
    private int itCount = 102400;
    private int saltLength = 20;

    public PKCS12KeyStoreSpi(JcaJceHelper helper, ASN1ObjectIdentifier keyAlgorithm, ASN1ObjectIdentifier certAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
        this.certAlgorithm = certAlgorithm;
        try {
            this.certFact = helper.createCertificateFactory("X.509");
        }
        catch (Exception e) {
            throw new IllegalArgumentException("can't create cert factory - " + e.toString());
        }
    }

    private SubjectKeyIdentifier createSubjectKeyId(PublicKey pubKey) {
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(pubKey.getEncoded());
            return new SubjectKeyIdentifier(PKCS12KeyStoreSpi.getDigest(info));
        }
        catch (Exception e) {
            throw new RuntimeException("error creating key");
        }
    }

    private static byte[] getDigest(SubjectPublicKeyInfo spki) {
        Digest digest = DigestFactory.createSHA1();
        byte[] resBuf = new byte[digest.getDigestSize()];
        byte[] bytes = spki.getPublicKeyData().getBytes();
        digest.update(bytes, 0, bytes.length);
        digest.doFinal(resBuf, 0);
        return resBuf;
    }

    @Override
    public void setRandom(SecureRandom rand) {
        this.random = rand;
    }

    @Override
    public boolean engineProbe(InputStream stream) throws IOException {
        return false;
    }

    public Enumeration engineAliases() {
        Hashtable<Object, String> tab = new Hashtable<Object, String>();
        Enumeration e = this.certs.keys();
        while (e.hasMoreElements()) {
            tab.put(e.nextElement(), "cert");
        }
        e = this.keys.keys();
        while (e.hasMoreElements()) {
            String a = (String)e.nextElement();
            if (tab.get(a) != null) continue;
            tab.put(a, "key");
        }
        return tab.keys();
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        return this.certs.get(alias) != null || this.keys.get(alias) != null;
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        Certificate keyCert;
        String id;
        Key key;
        Certificate cert = (Certificate)this.certs.remove(alias);
        if (cert != null) {
            this.chainCerts.remove(new CertId(cert.getPublicKey()));
        }
        if ((key = (Key)this.keys.remove(alias)) != null && (id = (String)this.localIds.remove(alias)) != null && (keyCert = (Certificate)this.keyCerts.remove(id)) != null) {
            this.chainCerts.remove(new CertId(keyCert.getPublicKey()));
        }
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getCertificate.");
        }
        Certificate c = (Certificate)this.certs.get(alias);
        if (c == null) {
            String id = (String)this.localIds.get(alias);
            c = id != null ? (Certificate)this.keyCerts.get(id) : (Certificate)this.keyCerts.get(alias);
        }
        return c;
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        String ta;
        Certificate tc;
        Enumeration c = this.certs.elements();
        Enumeration k = this.certs.keys();
        while (c.hasMoreElements()) {
            tc = (Certificate)c.nextElement();
            ta = (String)k.nextElement();
            if (!tc.equals(cert)) continue;
            return ta;
        }
        c = this.keyCerts.elements();
        k = this.keyCerts.keys();
        while (c.hasMoreElements()) {
            tc = (Certificate)c.nextElement();
            ta = (String)k.nextElement();
            if (!tc.equals(cert)) continue;
            return ta;
        }
        return null;
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getCertificateChain.");
        }
        if (!this.engineIsKeyEntry(alias)) {
            return null;
        }
        Certificate c = this.engineGetCertificate(alias);
        if (c != null) {
            Vector<Certificate> cs = new Vector<Certificate>();
            while (c != null) {
                Principal s;
                Principal i;
                ASN1OctetString akiValue;
                AuthorityKeyIdentifier aki;
                byte[] keyID;
                X509Certificate x509c = (X509Certificate)c;
                Certificate nextC = null;
                byte[] akiBytes = x509c.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (akiBytes != null && null != (keyID = (aki = AuthorityKeyIdentifier.getInstance((akiValue = ASN1OctetString.getInstance(akiBytes)).getOctets())).getKeyIdentifier())) {
                    nextC = (Certificate)this.chainCerts.get(new CertId(keyID));
                }
                if (nextC == null && !(i = x509c.getIssuerDN()).equals(s = x509c.getSubjectDN())) {
                    Enumeration e = this.chainCerts.keys();
                    while (e.hasMoreElements()) {
                        X509Certificate crt = (X509Certificate)this.chainCerts.get(e.nextElement());
                        Principal sub = crt.getSubjectDN();
                        if (!sub.equals(i)) continue;
                        try {
                            x509c.verify(crt.getPublicKey());
                            nextC = crt;
                            break;
                        }
                        catch (Exception exception) {
                        }
                    }
                }
                if (cs.contains(c)) {
                    c = null;
                    continue;
                }
                cs.addElement(c);
                if (nextC != c) {
                    c = nextC;
                    continue;
                }
                c = null;
            }
            Certificate[] certChain = new Certificate[cs.size()];
            for (int i = 0; i != certChain.length; ++i) {
                certChain[i] = (Certificate)cs.elementAt(i);
            }
            return certChain;
        }
        return null;
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        if (this.keys.get(alias) == null && this.certs.get(alias) == null) {
            return null;
        }
        return new Date();
    }

    @Override
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getKey.");
        }
        return (Key)this.keys.get(alias);
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        return this.certs.get(alias) != null && this.keys.get(alias) == null;
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        return this.keys.get(alias) != null;
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        if (this.keys.get(alias) != null) {
            throw new KeyStoreException("There is a key entry with the name " + alias + ".");
        }
        this.certs.put(alias, cert);
        this.chainCerts.put(new CertId(cert.getPublicKey()), cert);
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        throw new RuntimeException("operation not supported");
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
        }
        if (key instanceof PrivateKey && chain == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        if (this.keys.get(alias) != null) {
            this.engineDeleteEntry(alias);
        }
        this.keys.put(alias, key);
        if (chain != null) {
            this.certs.put(alias, chain[0]);
            for (int i = 0; i != chain.length; ++i) {
                this.chainCerts.put(new CertId(chain[i].getPublicKey()), chain[i]);
            }
        }
    }

    @Override
    public int engineSize() {
        Hashtable<Object, String> tab = new Hashtable<Object, String>();
        Enumeration e = this.certs.keys();
        while (e.hasMoreElements()) {
            tab.put(e.nextElement(), "cert");
        }
        e = this.keys.keys();
        while (e.hasMoreElements()) {
            String a = (String)e.nextElement();
            if (tab.get(a) != null) continue;
            tab.put(a, "key");
        }
        return tab.size();
    }

    protected PrivateKey unwrapKey(AlgorithmIdentifier algId, byte[] data, char[] password, boolean wrongPKCS12Zero) throws IOException {
        ASN1ObjectIdentifier algorithm = algId.getAlgorithm();
        try {
            if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
                PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), this.validateIterationCount(pbeParams.getIterations()));
                Cipher cipher = this.helper.createCipher(algorithm.getId());
                PKCS12Key key = new PKCS12Key(password, wrongPKCS12Zero);
                cipher.init(4, (Key)key, defParams);
                return (PrivateKey)cipher.unwrap(data, "", 2);
            }
            if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
                Cipher cipher = this.createCipher(4, password, algId);
                return (PrivateKey)cipher.unwrap(data, "", 2);
            }
        }
        catch (Exception e) {
            throw new IOException("exception unwrapping private key - " + e.toString());
        }
        throw new IOException("exception unwrapping private key - cannot recognise: " + algorithm);
    }

    protected byte[] wrapKey(String algorithm, Key key, PKCS12PBEParams pbeParams, char[] password) throws IOException {
        byte[] out;
        PBEKeySpec pbeSpec = new PBEKeySpec(password);
        try {
            SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(algorithm);
            PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), pbeParams.getIterations().intValue());
            Cipher cipher = this.helper.createCipher(algorithm);
            cipher.init(3, (Key)keyFact.generateSecret(pbeSpec), defParams);
            out = cipher.wrap(key);
        }
        catch (Exception e) {
            throw new IOException("exception encrypting data - " + e.toString());
        }
        return out;
    }

    protected byte[] cryptData(boolean forEncryption, AlgorithmIdentifier algId, char[] password, boolean wrongPKCS12Zero, byte[] data) throws IOException {
        int mode;
        ASN1ObjectIdentifier algorithm = algId.getAlgorithm();
        int n = mode = forEncryption ? 1 : 2;
        if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
            try {
                PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), pbeParams.getIterations().intValue());
                PKCS12Key key = new PKCS12Key(password, wrongPKCS12Zero);
                Cipher cipher = this.helper.createCipher(algorithm.getId());
                cipher.init(mode, (Key)key, defParams);
                return cipher.doFinal(data);
            }
            catch (Exception e) {
                throw new IOException("exception decrypting data - " + e.toString());
            }
        }
        if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
            try {
                Cipher cipher = this.createCipher(mode, password, algId);
                return cipher.doFinal(data);
            }
            catch (Exception e) {
                throw new IOException("exception decrypting data - " + e.toString());
            }
        }
        throw new IOException("unknown PBE algorithm: " + algorithm);
    }

    private Cipher createCipher(int mode, char[] password, AlgorithmIdentifier algId) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        PBES2Parameters alg = PBES2Parameters.getInstance(algId.getParameters());
        PBKDF2Params func = PBKDF2Params.getInstance(alg.getKeyDerivationFunc().getParameters());
        AlgorithmIdentifier encScheme = AlgorithmIdentifier.getInstance(alg.getEncryptionScheme());
        SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(alg.getKeyDerivationFunc().getAlgorithm().getId());
        SecretKey key = func.isDefaultPrf() ? keyFact.generateSecret(new PBEKeySpec(password, func.getSalt(), this.validateIterationCount(func.getIterationCount()), keySizeProvider.getKeySize(encScheme))) : keyFact.generateSecret(new PBKDF2KeySpec(password, func.getSalt(), this.validateIterationCount(func.getIterationCount()), keySizeProvider.getKeySize(encScheme), func.getPrf()));
        Cipher cipher = this.helper.createCipher(alg.getEncryptionScheme().getAlgorithm().getId());
        ASN1Encodable encParams = alg.getEncryptionScheme().getParameters();
        if (encParams instanceof ASN1OctetString) {
            cipher.init(mode, (Key)key, new IvParameterSpec(ASN1OctetString.getInstance(encParams).getOctets()));
        } else {
            GOST28147Parameters gParams = GOST28147Parameters.getInstance(encParams);
            cipher.init(mode, (Key)key, new GOST28147ParameterSpec(gParams.getEncryptionParamSet(), gParams.getIV()));
        }
        return cipher;
    }

    @Override
    public void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (loadStoreParameter == null) {
            this.engineLoad(null, null);
        } else if (loadStoreParameter instanceof BCLoadStoreParameter) {
            BCLoadStoreParameter bcParam = (BCLoadStoreParameter)loadStoreParameter;
            this.engineLoad(bcParam.getInputStream(), ParameterUtil.extractPassword(loadStoreParameter));
        } else {
            throw new IllegalArgumentException("no support for 'param' of type " + loadStoreParameter.getClass().getName());
        }
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void engineLoad(InputStream stream, char[] password) throws IOException {
        if (stream == null) {
            return;
        }
        bufIn = new BufferedInputStream(stream);
        bufIn.mark(10);
        head = bufIn.read();
        if (head < 0) {
            throw new EOFException("no data in keystore stream");
        }
        if (head != 48) {
            throw new IOException("stream does not represent a PKCS12 key store");
        }
        bufIn.reset();
        bIn = new ASN1InputStream(bufIn);
        try {
            bag = Pfx.getInstance(bIn.readObject());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        info = bag.getAuthSafe();
        chain = new Vector<SafeBag>();
        unmarkedKey = false;
        wrongPKCS12Zero = false;
        if (bag.getMacData() != null) {
            if (password == null) {
                throw new NullPointerException("no password supplied when one expected");
            }
            mData = bag.getMacData();
            dInfo = mData.getMac();
            this.macAlgorithm = dInfo.getAlgorithmId();
            salt = mData.getSalt();
            this.itCount = this.validateIterationCount(mData.getIterationCount());
            this.saltLength = salt.length;
            data = ((ASN1OctetString)info.getContent()).getOctets();
            try {
                res = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, password, false, data);
                dig = dInfo.getDigest();
                if (Arrays.constantTimeAreEqual(res, dig)) ** GOTO lbl48
                if (password.length > 0) {
                    throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                }
                res = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, password, true, data);
                if (!Arrays.constantTimeAreEqual(res, dig)) {
                    throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                }
                wrongPKCS12Zero = true;
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new IOException("error constructing MAC: " + e.toString());
            }
        } else if (password != null && password.length != 0 && !Properties.isOverrideSet("org.bouncycastle.pkcs12.ignore_useless_passwd")) {
            throw new IOException("password supplied for keystore that does not require one");
        }
lbl48:
        // 4 sources

        this.keys = new IgnoresCaseHashtable();
        this.localIds = new IgnoresCaseHashtable();
        if (info.getContentType().equals(PKCS12KeyStoreSpi.data)) {
            content = ASN1OctetString.getInstance(info.getContent());
            authSafe = AuthenticatedSafe.getInstance(content.getOctets());
            c = authSafe.getContentInfo();
            for (i = 0; i != c.length; ++i) {
                if (c[i].getContentType().equals(PKCS12KeyStoreSpi.data)) {
                    authSafeContent = ASN1OctetString.getInstance(c[i].getContent());
                    seq = ASN1Sequence.getInstance(authSafeContent.getOctets());
                    for (j = 0; j != seq.size(); ++j) {
                        b = SafeBag.getInstance(seq.getObjectAt(j));
                        if (b.getBagId().equals(PKCS12KeyStoreSpi.pkcs8ShroudedKeyBag)) {
                            eIn = EncryptedPrivateKeyInfo.getInstance(b.getBagValue());
                            privKey = this.unwrapKey(eIn.getEncryptionAlgorithm(), eIn.getEncryptedData(), password, wrongPKCS12Zero);
                            alias = null;
                            localId = null;
                            if (b.getBagAttributes() != null) {
                                e = b.getBagAttributes().getObjects();
                                while (e.hasMoreElements()) {
                                    sq = (ASN1Sequence)e.nextElement();
                                    aOid = (ASN1ObjectIdentifier)sq.getObjectAt(0);
                                    attrSet = (ASN1Set)sq.getObjectAt(1);
                                    attr = null;
                                    if (attrSet.size() > 0) {
                                        attr = (ASN1Primitive)attrSet.getObjectAt(0);
                                        if (privKey instanceof PKCS12BagAttributeCarrier) {
                                            bagAttr = (PKCS12BagAttributeCarrier)privKey;
                                            existing = bagAttr.getBagAttribute(aOid);
                                            if (existing != null) {
                                                if (!existing.toASN1Primitive().equals(attr)) {
                                                    throw new IOException("attempt to add existing attribute with different value");
                                                }
                                            } else {
                                                bagAttr.setBagAttribute(aOid, attr);
                                            }
                                        }
                                    }
                                    if (aOid.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                                        alias = ((ASN1BMPString)attr).getString();
                                        this.keys.put(alias, privKey);
                                        continue;
                                    }
                                    if (!aOid.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) continue;
                                    localId = (ASN1OctetString)attr;
                                }
                            }
                            if (localId != null) {
                                name = new String(Hex.encode(localId.getOctets()));
                                if (alias == null) {
                                    this.keys.put(name, privKey);
                                    continue;
                                }
                                this.localIds.put(alias, name);
                                continue;
                            }
                            unmarkedKey = true;
                            this.keys.put("unmarked", privKey);
                            continue;
                        }
                        if (b.getBagId().equals(PKCS12KeyStoreSpi.certBag)) {
                            chain.addElement(b);
                            continue;
                        }
                        System.out.println("extra in data " + b.getBagId());
                        System.out.println(ASN1Dump.dumpAsString(b));
                    }
                    continue;
                }
                if (c[i].getContentType().equals(PKCS12KeyStoreSpi.encryptedData)) {
                    d = EncryptedData.getInstance(c[i].getContent());
                    octets = this.cryptData(false, d.getEncryptionAlgorithm(), password, wrongPKCS12Zero, d.getContent().getOctets());
                    seq = ASN1Sequence.getInstance(octets);
                    for (j = 0; j != seq.size(); ++j) {
                        b = SafeBag.getInstance(seq.getObjectAt(j));
                        if (b.getBagId().equals(PKCS12KeyStoreSpi.certBag)) {
                            chain.addElement(b);
                            continue;
                        }
                        if (b.getBagId().equals(PKCS12KeyStoreSpi.pkcs8ShroudedKeyBag)) {
                            eIn = EncryptedPrivateKeyInfo.getInstance(b.getBagValue());
                            privKey = this.unwrapKey(eIn.getEncryptionAlgorithm(), eIn.getEncryptedData(), password, wrongPKCS12Zero);
                            bagAttr = (PKCS12BagAttributeCarrier)privKey;
                            alias = null;
                            localId = null;
                            e = b.getBagAttributes().getObjects();
                            while (e.hasMoreElements()) {
                                sq = (ASN1Sequence)e.nextElement();
                                aOid = (ASN1ObjectIdentifier)sq.getObjectAt(0);
                                attrSet = (ASN1Set)sq.getObjectAt(1);
                                attr = null;
                                if (attrSet.size() > 0) {
                                    attr = (ASN1Primitive)attrSet.getObjectAt(0);
                                    existing = bagAttr.getBagAttribute(aOid);
                                    if (existing != null) {
                                        if (!existing.toASN1Primitive().equals(attr)) {
                                            throw new IOException("attempt to add existing attribute with different value");
                                        }
                                    } else {
                                        bagAttr.setBagAttribute(aOid, attr);
                                    }
                                }
                                if (aOid.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                                    alias = ((ASN1BMPString)attr).getString();
                                    this.keys.put(alias, privKey);
                                    continue;
                                }
                                if (!aOid.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) continue;
                                localId = (ASN1OctetString)attr;
                            }
                            name = new String(Hex.encode(localId.getOctets()));
                            if (alias == null) {
                                this.keys.put(name, privKey);
                                continue;
                            }
                            this.localIds.put(alias, name);
                            continue;
                        }
                        if (b.getBagId().equals(PKCS12KeyStoreSpi.keyBag)) {
                            kInfo = PrivateKeyInfo.getInstance(b.getBagValue());
                            privKey = BouncyCastleProvider.getPrivateKey(kInfo);
                            bagAttr = (PKCS12BagAttributeCarrier)privKey;
                            alias = null;
                            localId = null;
                            e = b.getBagAttributes().getObjects();
                            while (e.hasMoreElements()) {
                                sq = ASN1Sequence.getInstance(e.nextElement());
                                aOid = ASN1ObjectIdentifier.getInstance(sq.getObjectAt(0));
                                attrSet = ASN1Set.getInstance(sq.getObjectAt(1));
                                attr = null;
                                if (attrSet.size() <= 0) continue;
                                attr = (ASN1Primitive)attrSet.getObjectAt(0);
                                existing = bagAttr.getBagAttribute(aOid);
                                if (existing != null) {
                                    if (!existing.toASN1Primitive().equals(attr)) {
                                        throw new IOException("attempt to add existing attribute with different value");
                                    }
                                } else {
                                    bagAttr.setBagAttribute(aOid, attr);
                                }
                                if (aOid.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                                    alias = ((ASN1BMPString)attr).getString();
                                    this.keys.put(alias, privKey);
                                    continue;
                                }
                                if (!aOid.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) continue;
                                localId = (ASN1OctetString)attr;
                            }
                            name = new String(Hex.encode(localId.getOctets()));
                            if (alias == null) {
                                this.keys.put(name, privKey);
                                continue;
                            }
                            this.localIds.put(alias, name);
                            continue;
                        }
                        System.out.println("extra in encryptedData " + b.getBagId());
                        System.out.println(ASN1Dump.dumpAsString(b));
                    }
                    continue;
                }
                System.out.println("extra " + c[i].getContentType().getId());
                System.out.println("extra " + ASN1Dump.dumpAsString(c[i].getContent()));
            }
        }
        this.certs = new IgnoresCaseHashtable();
        this.chainCerts = new Hashtable<K, V>();
        this.keyCerts = new Hashtable<K, V>();
        for (i = 0; i != chain.size(); ++i) {
            b = (SafeBag)chain.elementAt(i);
            cb = CertBag.getInstance(b.getBagValue());
            if (!cb.getCertId().equals(PKCS12KeyStoreSpi.x509Certificate)) {
                throw new RuntimeException("Unsupported certificate type: " + cb.getCertId());
            }
            try {
                cIn = new ByteArrayInputStream(((ASN1OctetString)cb.getCertValue()).getOctets());
                cert = this.certFact.generateCertificate(cIn);
            }
            catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
            localId = null;
            alias = null;
            if (b.getBagAttributes() != null) {
                e = b.getBagAttributes().getObjects();
                while (e.hasMoreElements()) {
                    sq = ASN1Sequence.getInstance(e.nextElement());
                    oid = ASN1ObjectIdentifier.getInstance(sq.getObjectAt(0));
                    attrSet = ASN1Set.getInstance(sq.getObjectAt(1));
                    if (attrSet.size() <= 0) continue;
                    attr = (ASN1Primitive)attrSet.getObjectAt(0);
                    bagAttr = null;
                    if (cert instanceof PKCS12BagAttributeCarrier) {
                        bagAttr = (PKCS12BagAttributeCarrier)cert;
                        existing = bagAttr.getBagAttribute(oid);
                        if (existing != null) {
                            if (oid.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) {
                                id = Hex.toHexString(((ASN1OctetString)attr).getOctets());
                                if (!IgnoresCaseHashtable.access$200(this.keys).containsKey(id) && !IgnoresCaseHashtable.access$200(this.localIds).containsKey(id)) continue;
                            }
                            if (!existing.toASN1Primitive().equals(attr)) {
                                throw new IOException("attempt to add existing attribute with different value");
                            }
                        } else if (attrSet.size() > 1) {
                            bagAttr.setBagAttribute(oid, attrSet);
                        } else {
                            bagAttr.setBagAttribute(oid, attr);
                        }
                    }
                    if (oid.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                        alias = ((ASN1BMPString)attr).getString();
                        continue;
                    }
                    if (!oid.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) continue;
                    localId = (ASN1OctetString)attr;
                }
            }
            this.chainCerts.put(new CertId(cert.getPublicKey()), cert);
            if (unmarkedKey) {
                if (!this.keyCerts.isEmpty()) continue;
                name = new String(Hex.encode(this.createSubjectKeyId(cert.getPublicKey()).getKeyIdentifier()));
                this.keyCerts.put(name, cert);
                this.keys.put(name, this.keys.remove("unmarked"));
                continue;
            }
            if (localId != null) {
                name = new String(Hex.encode(localId.getOctets()));
                this.keyCerts.put(name, cert);
            }
            if (alias == null) continue;
            this.certs.put(alias, cert);
        }
    }

    private int validateIterationCount(BigInteger i) {
        int count = i.intValue();
        if (count < 0) {
            throw new IllegalStateException("negative iteration count found");
        }
        BigInteger maxValue = Properties.asBigInteger(PKCS12_MAX_IT_COUNT_PROPERTY);
        if (maxValue != null && maxValue.intValue() < count) {
            throw new IllegalStateException("iteration count " + count + " greater than " + maxValue.intValue());
        }
        return count;
    }

    @Override
    public void engineStore(KeyStore.LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        char[] password;
        if (param == null) {
            throw new IllegalArgumentException("'param' arg cannot be null");
        }
        if (!(param instanceof PKCS12StoreParameter)) {
            throw new IllegalArgumentException("No support for 'param' of type " + param.getClass().getName());
        }
        PKCS12StoreParameter bcParam = (PKCS12StoreParameter)param;
        KeyStore.ProtectionParameter protParam = param.getProtectionParameter();
        if (protParam == null) {
            password = null;
        } else if (protParam instanceof KeyStore.PasswordProtection) {
            password = ((KeyStore.PasswordProtection)protParam).getPassword();
        } else {
            throw new IllegalArgumentException("No support for protection parameter of type " + protParam.getClass().getName());
        }
        this.doStore(bcParam.getOutputStream(), password, bcParam.isForDEREncoding());
    }

    @Override
    public void engineStore(OutputStream stream, char[] password) throws IOException {
        this.doStore(stream, password, false);
    }

    private void doStore(OutputStream stream, char[] password, boolean useDEREncoding) throws IOException {
        MacData mData;
        SafeBag sBag;
        PKCS12BagAttributeCarrier bagAttrs;
        ASN1EncodableVector fName;
        CertBag cBag;
        Certificate cert;
        if (this.keys.size() == 0) {
            if (password == null) {
                Pfx pfx;
                ContentInfo bagInfo;
                Enumeration cs = this.certs.keys();
                ASN1EncodableVector certSeq = new ASN1EncodableVector();
                while (cs.hasMoreElements()) {
                    try {
                        String certId = (String)cs.nextElement();
                        Certificate cert2 = (Certificate)this.certs.get(certId);
                        SafeBag sBag2 = this.createSafeBag(certId, cert2);
                        certSeq.add(sBag2);
                    }
                    catch (CertificateEncodingException e) {
                        throw new IOException("Error encoding certificate: " + e.toString());
                    }
                }
                if (useDEREncoding) {
                    bagInfo = new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence(certSeq).getEncoded()));
                    pfx = new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence(bagInfo).getEncoded())), null);
                    pfx.encodeTo(stream, "DER");
                } else {
                    bagInfo = new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence(certSeq).getEncoded()));
                    pfx = new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence(bagInfo).getEncoded())), null);
                    pfx.encodeTo(stream, "BER");
                }
                return;
            }
        } else if (password == null) {
            throw new NullPointerException("no password supplied for PKCS#12 KeyStore");
        }
        ASN1EncodableVector keyS = new ASN1EncodableVector();
        Enumeration ks = this.keys.keys();
        while (ks.hasMoreElements()) {
            byte[] kSalt = new byte[20];
            this.random.nextBytes(kSalt);
            String name = (String)ks.nextElement();
            PrivateKey privKey = (PrivateKey)this.keys.get(name);
            PKCS12PBEParams kParams = new PKCS12PBEParams(kSalt, 51200);
            byte[] kBytes = this.wrapKey(this.keyAlgorithm.getId(), privKey, kParams, password);
            AlgorithmIdentifier kAlgId = new AlgorithmIdentifier(this.keyAlgorithm, kParams.toASN1Primitive());
            EncryptedPrivateKeyInfo kInfo = new EncryptedPrivateKeyInfo(kAlgId, kBytes);
            boolean attrSet = false;
            ASN1EncodableVector kName = new ASN1EncodableVector();
            if (privKey instanceof PKCS12BagAttributeCarrier) {
                PKCS12BagAttributeCarrier bagAttrs2 = (PKCS12BagAttributeCarrier)((Object)privKey);
                ASN1BMPString nm = (ASN1BMPString)bagAttrs2.getBagAttribute(pkcs_9_at_friendlyName);
                if (nm == null || !nm.getString().equals(name)) {
                    bagAttrs2.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(name));
                }
                if (bagAttrs2.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                    Certificate ct = this.engineGetCertificate(name);
                    bagAttrs2.setBagAttribute(pkcs_9_at_localKeyId, this.createSubjectKeyId(ct.getPublicKey()));
                }
                Enumeration e = bagAttrs2.getBagAttributeKeys();
                while (e.hasMoreElements()) {
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                    ASN1EncodableVector kSeq = new ASN1EncodableVector();
                    kSeq.add(oid);
                    kSeq.add(new DERSet(bagAttrs2.getBagAttribute(oid)));
                    attrSet = true;
                    kName.add(new DERSequence(kSeq));
                }
            }
            if (!attrSet) {
                ASN1EncodableVector kSeq = new ASN1EncodableVector();
                Certificate ct = this.engineGetCertificate(name);
                kSeq.add(pkcs_9_at_localKeyId);
                kSeq.add(new DERSet(this.createSubjectKeyId(ct.getPublicKey())));
                kName.add(new DERSequence(kSeq));
                kSeq = new ASN1EncodableVector();
                kSeq.add(pkcs_9_at_friendlyName);
                kSeq.add(new DERSet(new DERBMPString(name)));
                kName.add(new DERSequence(kSeq));
            }
            SafeBag kBag = new SafeBag(pkcs8ShroudedKeyBag, kInfo.toASN1Primitive(), new DERSet(kName));
            keyS.add(kBag);
        }
        byte[] keySEncoded = new DERSequence(keyS).getEncoded("DER");
        BEROctetString keyString = new BEROctetString(keySEncoded);
        byte[] cSalt = new byte[20];
        this.random.nextBytes(cSalt);
        ASN1EncodableVector certSeq = new ASN1EncodableVector();
        PKCS12PBEParams cParams = new PKCS12PBEParams(cSalt, 51200);
        AlgorithmIdentifier cAlgId = new AlgorithmIdentifier(this.certAlgorithm, cParams.toASN1Primitive());
        Hashtable<Certificate, Certificate> doneCerts = new Hashtable<Certificate, Certificate>();
        Enumeration cs = this.keys.keys();
        while (cs.hasMoreElements()) {
            try {
                String name = (String)cs.nextElement();
                cert = this.engineGetCertificate(name);
                boolean cAttrSet = false;
                cBag = new CertBag(x509Certificate, new DEROctetString(cert.getEncoded()));
                fName = new ASN1EncodableVector();
                if (cert instanceof PKCS12BagAttributeCarrier) {
                    bagAttrs = (PKCS12BagAttributeCarrier)((Object)cert);
                    ASN1BMPString nm = (ASN1BMPString)bagAttrs.getBagAttribute(pkcs_9_at_friendlyName);
                    if (nm == null || !nm.getString().equals(name)) {
                        bagAttrs.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(name));
                    }
                    if (bagAttrs.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                        bagAttrs.setBagAttribute(pkcs_9_at_localKeyId, this.createSubjectKeyId(cert.getPublicKey()));
                    }
                    Enumeration e = bagAttrs.getBagAttributeKeys();
                    while (e.hasMoreElements()) {
                        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();
                        fSeq.add(oid);
                        fSeq.add(new DERSet(bagAttrs.getBagAttribute(oid)));
                        fName.add(new DERSequence(fSeq));
                        cAttrSet = true;
                    }
                }
                if (!cAttrSet) {
                    ASN1EncodableVector fSeq = new ASN1EncodableVector();
                    fSeq.add(pkcs_9_at_localKeyId);
                    fSeq.add(new DERSet(this.createSubjectKeyId(cert.getPublicKey())));
                    fName.add(new DERSequence(fSeq));
                    fSeq = new ASN1EncodableVector();
                    fSeq.add(pkcs_9_at_friendlyName);
                    fSeq.add(new DERSet(new DERBMPString(name)));
                    fName.add(new DERSequence(fSeq));
                }
                sBag = new SafeBag(certBag, cBag.toASN1Primitive(), new DERSet(fName));
                certSeq.add(sBag);
                doneCerts.put(cert, cert);
            }
            catch (CertificateEncodingException e) {
                throw new IOException("Error encoding certificate: " + e.toString());
            }
        }
        cs = this.certs.keys();
        while (cs.hasMoreElements()) {
            try {
                String certId = (String)cs.nextElement();
                cert = (Certificate)this.certs.get(certId);
                if (this.keys.get(certId) != null) continue;
                SafeBag sBag3 = this.createSafeBag(certId, cert);
                certSeq.add(sBag3);
                doneCerts.put(cert, cert);
            }
            catch (CertificateEncodingException e) {
                throw new IOException("Error encoding certificate: " + e.toString());
            }
        }
        Set usedSet = this.getUsedCertificateSet();
        cs = this.chainCerts.keys();
        while (cs.hasMoreElements()) {
            try {
                CertId certId = (CertId)cs.nextElement();
                Certificate cert3 = (Certificate)this.chainCerts.get(certId);
                if (!usedSet.contains(cert3) || doneCerts.get(cert3) != null) continue;
                cBag = new CertBag(x509Certificate, new DEROctetString(cert3.getEncoded()));
                fName = new ASN1EncodableVector();
                if (cert3 instanceof PKCS12BagAttributeCarrier) {
                    bagAttrs = (PKCS12BagAttributeCarrier)((Object)cert3);
                    Enumeration e = bagAttrs.getBagAttributeKeys();
                    while (e.hasMoreElements()) {
                        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                        if (oid.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) continue;
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();
                        fSeq.add(oid);
                        fSeq.add(new DERSet(bagAttrs.getBagAttribute(oid)));
                        fName.add(new DERSequence(fSeq));
                    }
                }
                sBag = new SafeBag(certBag, cBag.toASN1Primitive(), new DERSet(fName));
                certSeq.add(sBag);
            }
            catch (CertificateEncodingException e) {
                throw new IOException("Error encoding certificate: " + e.toString());
            }
        }
        byte[] certSeqEncoded = new DERSequence(certSeq).getEncoded("DER");
        byte[] certBytes = this.cryptData(true, cAlgId, password, false, certSeqEncoded);
        EncryptedData cInfo = new EncryptedData(data, cAlgId, new BEROctetString(certBytes));
        ContentInfo[] info = new ContentInfo[]{new ContentInfo(data, keyString), new ContentInfo(encryptedData, cInfo.toASN1Primitive())};
        AuthenticatedSafe auth = new AuthenticatedSafe(info);
        byte[] pkg = auth.getEncoded(useDEREncoding ? "DER" : "BER");
        ContentInfo mainInfo = new ContentInfo(data, new BEROctetString(pkg));
        byte[] mSalt = new byte[this.saltLength];
        this.random.nextBytes(mSalt);
        byte[] data = ((ASN1OctetString)mainInfo.getContent()).getOctets();
        try {
            byte[] res = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), mSalt, this.itCount, password, false, data);
            DigestInfo dInfo = new DigestInfo(this.macAlgorithm, res);
            mData = new MacData(dInfo, mSalt, this.itCount);
        }
        catch (Exception e) {
            throw new IOException("error constructing MAC: " + e.toString());
        }
        Pfx pfx = new Pfx(mainInfo, mData);
        pfx.encodeTo(stream, useDEREncoding ? "DER" : "BER");
    }

    private SafeBag createSafeBag(String certId, Certificate cert) throws CertificateEncodingException {
        CertBag cBag = new CertBag(x509Certificate, new DEROctetString(cert.getEncoded()));
        ASN1EncodableVector fName = new ASN1EncodableVector();
        boolean cAttrSet = false;
        if (cert instanceof PKCS12BagAttributeCarrier) {
            PKCS12BagAttributeCarrier bagAttrs = (PKCS12BagAttributeCarrier)((Object)cert);
            ASN1BMPString nm = (ASN1BMPString)bagAttrs.getBagAttribute(pkcs_9_at_friendlyName);
            if (!(nm != null && nm.getString().equals(certId) || certId == null)) {
                bagAttrs.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(certId));
            }
            Enumeration e = bagAttrs.getBagAttributeKeys();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                if (oid.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) continue;
                ASN1EncodableVector fSeq = new ASN1EncodableVector();
                fSeq.add(oid);
                fSeq.add(new DERSet(bagAttrs.getBagAttribute(oid)));
                fName.add(new DERSequence(fSeq));
                cAttrSet = true;
            }
        }
        if (!cAttrSet) {
            ASN1EncodableVector fSeq = new ASN1EncodableVector();
            fSeq.add(pkcs_9_at_friendlyName);
            fSeq.add(new DERSet(new DERBMPString(certId)));
            fName.add(new DERSequence(fSeq));
        }
        if (cert instanceof X509Certificate) {
            TBSCertificate tbsCert = TBSCertificate.getInstance(((X509Certificate)cert).getTBSCertificate());
            Extensions exts = tbsCert.getExtensions();
            if (exts != null) {
                ASN1EncodableVector fSeq;
                Extension extUsage = exts.getExtension(Extension.extendedKeyUsage);
                if (extUsage != null) {
                    fSeq = new ASN1EncodableVector();
                    fSeq.add(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage);
                    fSeq.add(new DERSet(ExtendedKeyUsage.getInstance(extUsage.getParsedValue()).getUsages()));
                    fName.add(new DERSequence(fSeq));
                } else {
                    fSeq = new ASN1EncodableVector();
                    fSeq.add(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage);
                    fSeq.add(new DERSet(KeyPurposeId.anyExtendedKeyUsage));
                    fName.add(new DERSequence(fSeq));
                }
            } else {
                ASN1EncodableVector fSeq = new ASN1EncodableVector();
                fSeq.add(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage);
                fSeq.add(new DERSet(KeyPurposeId.anyExtendedKeyUsage));
                fName.add(new DERSequence(fSeq));
            }
        }
        return new SafeBag(certBag, cBag.toASN1Primitive(), new DERSet(fName));
    }

    private Set getUsedCertificateSet() {
        String alias;
        HashSet<Certificate> usedSet = new HashSet<Certificate>();
        Enumeration en = this.keys.keys();
        while (en.hasMoreElements()) {
            alias = (String)en.nextElement();
            Certificate[] certs = this.engineGetCertificateChain(alias);
            for (int i = 0; i != certs.length; ++i) {
                usedSet.add(certs[i]);
            }
        }
        en = this.certs.keys();
        while (en.hasMoreElements()) {
            alias = (String)en.nextElement();
            Certificate cert = this.engineGetCertificate(alias);
            usedSet.add(cert);
        }
        return usedSet;
    }

    private byte[] calculatePbeMac(ASN1ObjectIdentifier oid, byte[] salt, int itCount, char[] password, boolean wrongPkcs12Zero, byte[] data) throws Exception {
        PBEParameterSpec defParams = new PBEParameterSpec(salt, itCount);
        Mac mac = this.helper.createMac(oid.getId());
        mac.init(new PKCS12Key(password, wrongPkcs12Zero), defParams);
        mac.update(data);
        return mac.doFinal();
    }

    public static class BCPKCS12KeyStore
    extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStore() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC));
        }
    }

    public static class BCPKCS12KeyStore3DES
    extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStore3DES() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC));
        }
    }

    private class CertId {
        byte[] id;

        CertId(PublicKey key) {
            this.id = PKCS12KeyStoreSpi.this.createSubjectKeyId(key).getKeyIdentifier();
        }

        CertId(byte[] id) {
            this.id = id;
        }

        public int hashCode() {
            return Arrays.hashCode(this.id);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CertId)) {
                return false;
            }
            CertId cId = (CertId)o;
            return Arrays.areEqual(this.id, cId.id);
        }
    }

    public static class DefPKCS12KeyStore
    extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStore() {
            super(new DefaultJcaJceHelper(), new PKCS12KeyStoreSpi(new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC));
        }
    }

    public static class DefPKCS12KeyStore3DES
    extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStore3DES() {
            super(new DefaultJcaJceHelper(), new PKCS12KeyStoreSpi(new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC));
        }
    }

    private static class DefaultSecretKeyProvider {
        private final Map KEY_SIZES;

        DefaultSecretKeyProvider() {
            HashMap<ASN1ObjectIdentifier, Integer> keySizes = new HashMap<ASN1ObjectIdentifier, Integer>();
            keySizes.put(new ASN1ObjectIdentifier("1.2.840.113533.7.66.10"), Integers.valueOf(128));
            keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
            keySizes.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
            keySizes.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
            keySizes.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
            keySizes.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
            keySizes.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
            keySizes.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
            keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
            this.KEY_SIZES = Collections.unmodifiableMap(keySizes);
        }

        public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
            Integer keySize = (Integer)this.KEY_SIZES.get(algorithmIdentifier.getAlgorithm());
            if (keySize != null) {
                return keySize;
            }
            return -1;
        }
    }

    private static class IgnoresCaseHashtable {
        private Hashtable orig = new Hashtable();
        private Hashtable keys = new Hashtable();

        private IgnoresCaseHashtable() {
        }

        public void put(String key, Object value) {
            String lower = key == null ? null : Strings.toLowerCase(key);
            String k = (String)this.keys.get(lower);
            if (k != null) {
                this.orig.remove(k);
            }
            this.keys.put(lower, key);
            this.orig.put(key, value);
        }

        public Enumeration keys() {
            return this.orig.keys();
        }

        public Object remove(String alias) {
            String k = (String)this.keys.remove(alias == null ? null : Strings.toLowerCase(alias));
            if (k == null) {
                return null;
            }
            return this.orig.remove(k);
        }

        public Object get(String alias) {
            String k = (String)this.keys.get(alias == null ? null : Strings.toLowerCase(alias));
            if (k == null) {
                return null;
            }
            return this.orig.get(k);
        }

        public Enumeration elements() {
            return this.orig.elements();
        }

        public int size() {
            return this.orig.size();
        }

        static /* synthetic */ Hashtable access$200(IgnoresCaseHashtable x0) {
            return x0.keys;
        }
    }
}

