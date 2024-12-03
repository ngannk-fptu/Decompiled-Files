/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.keystore.bcfks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.EncryptedObjectStoreData;
import org.bouncycastle.asn1.bc.EncryptedPrivateKeyData;
import org.bouncycastle.asn1.bc.EncryptedSecretKeyData;
import org.bouncycastle.asn1.bc.ObjectData;
import org.bouncycastle.asn1.bc.ObjectDataSequence;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.bouncycastle.asn1.bc.ObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreIntegrityCheck;
import org.bouncycastle.asn1.bc.PbkdMacIntegrityCheck;
import org.bouncycastle.asn1.bc.SecretKeyData;
import org.bouncycastle.asn1.bc.SignatureCheck;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.AdaptingKeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

class BcFKSKeyStoreSpi
extends KeyStoreSpi {
    private static final Map<String, ASN1ObjectIdentifier> oidMap = new HashMap<String, ASN1ObjectIdentifier>();
    private static final Map<ASN1ObjectIdentifier, String> publicAlgMap = new HashMap<ASN1ObjectIdentifier, String>();
    private PublicKey verificationKey;
    private BCFKSLoadStoreParameter.CertChainValidator validator;
    private static final BigInteger CERTIFICATE;
    private static final BigInteger PRIVATE_KEY;
    private static final BigInteger SECRET_KEY;
    private static final BigInteger PROTECTED_PRIVATE_KEY;
    private static final BigInteger PROTECTED_SECRET_KEY;
    private final JcaJceHelper helper;
    private final Map<String, ObjectData> entries = new HashMap<String, ObjectData>();
    private final Map<String, PrivateKey> privateKeyCache = new HashMap<String, PrivateKey>();
    private AlgorithmIdentifier hmacAlgorithm;
    private KeyDerivationFunc hmacPkbdAlgorithm;
    private AlgorithmIdentifier signatureAlgorithm;
    private Date creationDate;
    private Date lastModifiedDate;
    private ASN1ObjectIdentifier storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;

    private static String getPublicKeyAlg(ASN1ObjectIdentifier oid) {
        String algName = publicAlgMap.get(oid);
        if (algName != null) {
            return algName;
        }
        return oid.getId();
    }

    BcFKSKeyStoreSpi(JcaJceHelper helper) {
        this.helper = helper;
    }

    @Override
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectData ent = this.entries.get(alias);
        if (ent != null) {
            if (ent.getType().equals(PRIVATE_KEY) || ent.getType().equals(PROTECTED_PRIVATE_KEY)) {
                PrivateKey cachedKey = this.privateKeyCache.get(alias);
                if (cachedKey != null) {
                    return cachedKey;
                }
                EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
                EncryptedPrivateKeyInfo encInfo = EncryptedPrivateKeyInfo.getInstance(encPrivData.getEncryptedPrivateKeyInfo());
                try {
                    PrivateKeyInfo pInfo = PrivateKeyInfo.getInstance(this.decryptData("PRIVATE_KEY_ENCRYPTION", encInfo.getEncryptionAlgorithm(), password, encInfo.getEncryptedData()));
                    KeyFactory kFact = this.helper.createKeyFactory(BcFKSKeyStoreSpi.getPublicKeyAlg(pInfo.getPrivateKeyAlgorithm().getAlgorithm()));
                    PrivateKey privateKey = kFact.generatePrivate(new PKCS8EncodedKeySpec(pInfo.getEncoded()));
                    this.privateKeyCache.put(alias, privateKey);
                    return privateKey;
                }
                catch (Exception e) {
                    throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover private key (" + alias + "): " + e.getMessage());
                }
            }
            if (ent.getType().equals(SECRET_KEY) || ent.getType().equals(PROTECTED_SECRET_KEY)) {
                EncryptedSecretKeyData encKeyData = EncryptedSecretKeyData.getInstance(ent.getData());
                try {
                    SecretKeyData keyData = SecretKeyData.getInstance(this.decryptData("SECRET_KEY_ENCRYPTION", encKeyData.getKeyEncryptionAlgorithm(), password, encKeyData.getEncryptedKeyData()));
                    SecretKeyFactory kFact = this.helper.createSecretKeyFactory(keyData.getKeyAlgorithm().getId());
                    return kFact.generateSecret(new SecretKeySpec(keyData.getKeyBytes(), keyData.getKeyAlgorithm().getId()));
                }
                catch (Exception e) {
                    throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + alias + "): " + e.getMessage());
                }
            }
            throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + alias + "): type not recognized");
        }
        return null;
    }

    @Override
    public java.security.cert.Certificate[] engineGetCertificateChain(String alias) {
        ObjectData ent = this.entries.get(alias);
        if (ent != null && (ent.getType().equals(PRIVATE_KEY) || ent.getType().equals(PROTECTED_PRIVATE_KEY))) {
            EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
            Certificate[] certificates = encPrivData.getCertificateChain();
            java.security.cert.Certificate[] chain = new X509Certificate[certificates.length];
            for (int i = 0; i != chain.length; ++i) {
                chain[i] = this.decodeCertificate(certificates[i]);
            }
            return chain;
        }
        return null;
    }

    @Override
    public java.security.cert.Certificate engineGetCertificate(String s) {
        ObjectData ent = this.entries.get(s);
        if (ent != null) {
            if (ent.getType().equals(PRIVATE_KEY) || ent.getType().equals(PROTECTED_PRIVATE_KEY)) {
                EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
                Certificate[] certificates = encPrivData.getCertificateChain();
                return this.decodeCertificate(certificates[0]);
            }
            if (ent.getType().equals(CERTIFICATE)) {
                return this.decodeCertificate(ent.getData());
            }
        }
        return null;
    }

    private java.security.cert.Certificate decodeCertificate(Object cert) {
        if (this.helper != null) {
            try {
                CertificateFactory certFact = this.helper.createCertificateFactory("X.509");
                return certFact.generateCertificate(new ByteArrayInputStream(Certificate.getInstance(cert).getEncoded()));
            }
            catch (Exception e) {
                return null;
            }
        }
        try {
            CertificateFactory certFact = CertificateFactory.getInstance("X.509");
            return certFact.generateCertificate(new ByteArrayInputStream(Certificate.getInstance(cert).getEncoded()));
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public Date engineGetCreationDate(String s) {
        ObjectData ent = this.entries.get(s);
        if (ent != null) {
            try {
                return ent.getLastModifiedDate().getDate();
            }
            catch (ParseException e) {
                return new Date();
            }
        }
        return null;
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, java.security.cert.Certificate[] chain) throws KeyStoreException {
        Date creationDate;
        Date lastEditDate = creationDate = new Date();
        ObjectData entry = this.entries.get(alias);
        if (entry != null) {
            creationDate = this.extractCreationDate(entry, creationDate);
        }
        this.privateKeyCache.remove(alias);
        if (key instanceof PrivateKey) {
            if (chain == null) {
                throw new KeyStoreException("BCFKS KeyStore requires a certificate chain for private key storage.");
            }
            try {
                EncryptedPrivateKeyInfo keyInfo;
                Cipher c;
                byte[] encodedKey = key.getEncoded();
                KeyDerivationFunc pbkdAlgId = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                byte[] keyBytes = this.generateKey(pbkdAlgId, "PRIVATE_KEY_ENCRYPTION", password != null ? password : new char[]{}, 32);
                if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                    c = this.createCipher("AES/CCM/NoPadding", keyBytes);
                    byte[] encryptedKey = c.doFinal(encodedKey);
                    AlgorithmParameters algParams = c.getParameters();
                    PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(algParams.getEncoded())));
                    keyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encryptedKey);
                } else {
                    c = this.createCipher("AESKWP", keyBytes);
                    byte[] encryptedKey = c.doFinal(encodedKey);
                    PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad));
                    keyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encryptedKey);
                }
                EncryptedPrivateKeyData keySeq = this.createPrivateKeySequence(keyInfo, chain);
                this.entries.put(alias, new ObjectData(PRIVATE_KEY, alias, creationDate, lastEditDate, keySeq.getEncoded(), null));
            }
            catch (Exception e) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + e.toString(), e);
            }
        } else if (key instanceof SecretKey) {
            if (chain != null) {
                throw new KeyStoreException("BCFKS KeyStore cannot store certificate chain with secret key.");
            }
            try {
                EncryptedSecretKeyData keyData;
                SecretKeyData secKeyData;
                byte[] encodedKey = key.getEncoded();
                KeyDerivationFunc pbkdAlgId = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                byte[] keyBytes = this.generateKey(pbkdAlgId, "SECRET_KEY_ENCRYPTION", password != null ? password : new char[]{}, 32);
                String keyAlg = Strings.toUpperCase(key.getAlgorithm());
                if (keyAlg.indexOf("AES") > -1) {
                    secKeyData = new SecretKeyData(NISTObjectIdentifiers.aes, encodedKey);
                } else {
                    ASN1ObjectIdentifier algOid = oidMap.get(keyAlg);
                    if (algOid != null) {
                        secKeyData = new SecretKeyData(algOid, encodedKey);
                    } else {
                        algOid = oidMap.get(keyAlg + "." + encodedKey.length * 8);
                        if (algOid != null) {
                            secKeyData = new SecretKeyData(algOid, encodedKey);
                        } else {
                            throw new KeyStoreException("BCFKS KeyStore cannot recognize secret key (" + keyAlg + ") for storage.");
                        }
                    }
                }
                if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                    Cipher c = this.createCipher("AES/CCM/NoPadding", keyBytes);
                    byte[] encryptedKey = c.doFinal(secKeyData.getEncoded());
                    AlgorithmParameters algParams = c.getParameters();
                    PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(algParams.getEncoded())));
                    keyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encryptedKey);
                } else {
                    Cipher c = this.createCipher("AESKWP", keyBytes);
                    byte[] encryptedKey = c.doFinal(secKeyData.getEncoded());
                    PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad));
                    keyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encryptedKey);
                }
                this.entries.put(alias, new ObjectData(SECRET_KEY, alias, creationDate, lastEditDate, keyData.getEncoded(), null));
            }
            catch (Exception e) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + e.toString(), e);
            }
        } else {
            throw new KeyStoreException("BCFKS KeyStore unable to recognize key.");
        }
        this.lastModifiedDate = lastEditDate;
    }

    private Cipher createCipher(String algorithm, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
        Cipher c = this.helper.createCipher(algorithm);
        c.init(1, new SecretKeySpec(keyBytes, "AES"));
        return c;
    }

    private SecureRandom getDefaultSecureRandom() {
        return CryptoServicesRegistrar.getSecureRandom();
    }

    private EncryptedPrivateKeyData createPrivateKeySequence(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, java.security.cert.Certificate[] chain) throws CertificateEncodingException {
        Certificate[] certChain = new Certificate[chain.length];
        for (int i = 0; i != chain.length; ++i) {
            certChain[i] = Certificate.getInstance(chain[i].getEncoded());
        }
        return new EncryptedPrivateKeyData(encryptedPrivateKeyInfo, certChain);
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] keyBytes, java.security.cert.Certificate[] chain) throws KeyStoreException {
        Date creationDate;
        Date lastEditDate = creationDate = new Date();
        ObjectData entry = this.entries.get(alias);
        if (entry != null) {
            creationDate = this.extractCreationDate(entry, creationDate);
        }
        if (chain != null) {
            EncryptedPrivateKeyInfo encInfo;
            try {
                encInfo = EncryptedPrivateKeyInfo.getInstance(keyBytes);
            }
            catch (Exception e) {
                throw new ExtKeyStoreException("BCFKS KeyStore private key encoding must be an EncryptedPrivateKeyInfo.", e);
            }
            try {
                this.privateKeyCache.remove(alias);
                this.entries.put(alias, new ObjectData(PROTECTED_PRIVATE_KEY, alias, creationDate, lastEditDate, this.createPrivateKeySequence(encInfo, chain).getEncoded(), null));
            }
            catch (Exception e) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + e.toString(), e);
            }
        }
        try {
            this.entries.put(alias, new ObjectData(PROTECTED_SECRET_KEY, alias, creationDate, lastEditDate, keyBytes, null));
        }
        catch (Exception e) {
            throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + e.toString(), e);
        }
        this.lastModifiedDate = lastEditDate;
    }

    @Override
    public void engineSetCertificateEntry(String alias, java.security.cert.Certificate certificate) throws KeyStoreException {
        Date creationDate;
        ObjectData entry = this.entries.get(alias);
        Date lastEditDate = creationDate = new Date();
        if (entry != null) {
            if (!entry.getType().equals(CERTIFICATE)) {
                throw new KeyStoreException("BCFKS KeyStore already has a key entry with alias " + alias);
            }
            creationDate = this.extractCreationDate(entry, creationDate);
        }
        try {
            this.entries.put(alias, new ObjectData(CERTIFICATE, alias, creationDate, lastEditDate, certificate.getEncoded(), null));
        }
        catch (CertificateEncodingException e) {
            throw new ExtKeyStoreException("BCFKS KeyStore unable to handle certificate: " + e.getMessage(), e);
        }
        this.lastModifiedDate = lastEditDate;
    }

    private Date extractCreationDate(ObjectData entry, Date creationDate) {
        try {
            creationDate = entry.getCreationDate().getDate();
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return creationDate;
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        ObjectData entry = this.entries.get(alias);
        if (entry == null) {
            return;
        }
        this.privateKeyCache.remove(alias);
        this.entries.remove(alias);
        this.lastModifiedDate = new Date();
    }

    @Override
    public Enumeration<String> engineAliases() {
        final Iterator<String> it = new HashSet<String>(this.entries.keySet()).iterator();
        return new Enumeration(){

            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }
        };
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias value is null");
        }
        return this.entries.containsKey(alias);
    }

    @Override
    public int engineSize() {
        return this.entries.size();
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        ObjectData ent = this.entries.get(alias);
        if (ent != null) {
            BigInteger entryType = ent.getType();
            return entryType.equals(PRIVATE_KEY) || entryType.equals(SECRET_KEY) || entryType.equals(PROTECTED_PRIVATE_KEY) || entryType.equals(PROTECTED_SECRET_KEY);
        }
        return false;
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        ObjectData ent = this.entries.get(alias);
        if (ent != null) {
            return ent.getType().equals(CERTIFICATE);
        }
        return false;
    }

    @Override
    public String engineGetCertificateAlias(java.security.cert.Certificate certificate) {
        byte[] encodedCert;
        if (certificate == null) {
            return null;
        }
        try {
            encodedCert = certificate.getEncoded();
        }
        catch (CertificateEncodingException e) {
            return null;
        }
        for (String alias : this.entries.keySet()) {
            ObjectData ent = this.entries.get(alias);
            if (ent.getType().equals(CERTIFICATE)) {
                if (!Arrays.areEqual(ent.getData(), encodedCert)) continue;
                return alias;
            }
            if (!ent.getType().equals(PRIVATE_KEY) && !ent.getType().equals(PROTECTED_PRIVATE_KEY)) continue;
            try {
                EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
                if (!Arrays.areEqual(encPrivData.getCertificateChain()[0].toASN1Primitive().getEncoded(), encodedCert)) continue;
                return alias;
            }
            catch (IOException iOException) {
            }
        }
        return null;
    }

    private byte[] generateKey(KeyDerivationFunc pbkdAlgorithm, String purpose, char[] password, int defKeySize) throws IOException {
        byte[] encPassword = PBEParametersGenerator.PKCS12PasswordToBytes(password);
        byte[] differentiator = PBEParametersGenerator.PKCS12PasswordToBytes(purpose.toCharArray());
        int keySizeInBytes = defKeySize;
        if (MiscObjectIdentifiers.id_scrypt.equals(pbkdAlgorithm.getAlgorithm())) {
            ScryptParams params = ScryptParams.getInstance(pbkdAlgorithm.getParameters());
            if (params.getKeyLength() != null) {
                keySizeInBytes = params.getKeyLength().intValue();
            } else if (keySizeInBytes == -1) {
                throw new IOException("no keyLength found in ScryptParams");
            }
            return SCrypt.generate(Arrays.concatenate(encPassword, differentiator), params.getSalt(), params.getCostParameter().intValue(), params.getBlockSize().intValue(), params.getBlockSize().intValue(), keySizeInBytes);
        }
        if (pbkdAlgorithm.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBKDF2)) {
            PBKDF2Params pbkdf2Params = PBKDF2Params.getInstance(pbkdAlgorithm.getParameters());
            if (pbkdf2Params.getKeyLength() != null) {
                keySizeInBytes = pbkdf2Params.getKeyLength().intValue();
            } else if (keySizeInBytes == -1) {
                throw new IOException("no keyLength found in PBKDF2Params");
            }
            if (pbkdf2Params.getPrf().getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA512)) {
                PKCS5S2ParametersGenerator pGen = new PKCS5S2ParametersGenerator(new SHA512Digest());
                pGen.init(Arrays.concatenate(encPassword, differentiator), pbkdf2Params.getSalt(), pbkdf2Params.getIterationCount().intValue());
                return ((KeyParameter)pGen.generateDerivedParameters(keySizeInBytes * 8)).getKey();
            }
            if (pbkdf2Params.getPrf().getAlgorithm().equals(NISTObjectIdentifiers.id_hmacWithSHA3_512)) {
                PKCS5S2ParametersGenerator pGen = new PKCS5S2ParametersGenerator(new SHA3Digest(512));
                pGen.init(Arrays.concatenate(encPassword, differentiator), pbkdf2Params.getSalt(), pbkdf2Params.getIterationCount().intValue());
                return ((KeyParameter)pGen.generateDerivedParameters(keySizeInBytes * 8)).getKey();
            }
            throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD PRF: " + pbkdf2Params.getPrf().getAlgorithm());
        }
        throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD.");
    }

    private void verifySig(ASN1Encodable store, SignatureCheck integrityCheck, PublicKey key) throws GeneralSecurityException, IOException {
        Signature sig = this.helper.createSignature(integrityCheck.getSignatureAlgorithm().getAlgorithm().getId());
        sig.initVerify(key);
        sig.update(store.toASN1Primitive().getEncoded("DER"));
        if (!sig.verify(integrityCheck.getSignature().getOctets())) {
            throw new IOException("BCFKS KeyStore corrupted: signature calculation failed");
        }
    }

    private void verifyMac(byte[] content, PbkdMacIntegrityCheck integrityCheck, char[] password) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        byte[] check = this.calculateMac(content, integrityCheck.getMacAlgorithm(), integrityCheck.getPbkdAlgorithm(), password);
        if (!Arrays.constantTimeAreEqual(check, integrityCheck.getMac())) {
            throw new IOException("BCFKS KeyStore corrupted: MAC calculation failed");
        }
    }

    private byte[] calculateMac(byte[] content, AlgorithmIdentifier algorithm, KeyDerivationFunc pbkdAlgorithm, char[] password) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        String algorithmId = algorithm.getAlgorithm().getId();
        Mac mac = this.helper.createMac(algorithmId);
        try {
            mac.init(new SecretKeySpec(this.generateKey(pbkdAlgorithm, "INTEGRITY_CHECK", password != null ? password : new char[]{}, -1), algorithmId));
        }
        catch (InvalidKeyException e) {
            throw new IOException("Cannot set up MAC calculation: " + e.getMessage());
        }
        return mac.doFinal(content);
    }

    @Override
    public void engineStore(KeyStore.LoadStoreParameter parameter) throws CertificateException, NoSuchAlgorithmException, IOException {
        if (parameter == null) {
            throw new IllegalArgumentException("'parameter' arg cannot be null");
        }
        if (parameter instanceof BCFKSLoadStoreParameter) {
            BCFKSLoadStoreParameter bcParam = (BCFKSLoadStoreParameter)parameter;
            if (bcParam.getStoreSignatureKey() != null) {
                this.signatureAlgorithm = this.generateSignatureAlgId(bcParam.getStoreSignatureKey(), bcParam.getStoreSignatureAlgorithm());
                this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(bcParam.getStorePBKDFConfig(), 64);
                this.storeEncryptionAlgorithm = bcParam.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM ? NISTObjectIdentifiers.id_aes256_CCM : NISTObjectIdentifiers.id_aes256_wrap_pad;
                this.hmacAlgorithm = bcParam.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512 ? new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE) : new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
                char[] password = ParameterUtil.extractPassword(bcParam);
                EncryptedObjectStoreData encStoreData = this.getEncryptedObjectStoreData(this.signatureAlgorithm, password);
                try {
                    SignatureCheck signatureCheck;
                    Signature sig = this.helper.createSignature(this.signatureAlgorithm.getAlgorithm().getId());
                    sig.initSign((PrivateKey)bcParam.getStoreSignatureKey());
                    sig.update(encStoreData.getEncoded());
                    X509Certificate[] certs = bcParam.getStoreCertificates();
                    if (certs != null) {
                        Certificate[] certificates = new Certificate[certs.length];
                        for (int i = 0; i != certificates.length; ++i) {
                            certificates[i] = Certificate.getInstance(certs[i].getEncoded());
                        }
                        signatureCheck = new SignatureCheck(this.signatureAlgorithm, certificates, sig.sign());
                    } else {
                        signatureCheck = new SignatureCheck(this.signatureAlgorithm, sig.sign());
                    }
                    ObjectStore store = new ObjectStore(encStoreData, new ObjectStoreIntegrityCheck(signatureCheck));
                    bcParam.getOutputStream().write(store.getEncoded());
                    bcParam.getOutputStream().flush();
                }
                catch (GeneralSecurityException e) {
                    throw new IOException("error creating signature: " + e.getMessage(), e);
                }
            } else {
                char[] password = ParameterUtil.extractPassword(bcParam);
                this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(bcParam.getStorePBKDFConfig(), 64);
                this.storeEncryptionAlgorithm = bcParam.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM ? NISTObjectIdentifiers.id_aes256_CCM : NISTObjectIdentifiers.id_aes256_wrap_pad;
                this.hmacAlgorithm = bcParam.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512 ? new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE) : new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
                this.engineStore(bcParam.getOutputStream(), password);
            }
        } else if (parameter instanceof BCLoadStoreParameter) {
            BCLoadStoreParameter bcParam = (BCLoadStoreParameter)parameter;
            this.engineStore(bcParam.getOutputStream(), ParameterUtil.extractPassword(parameter));
        } else {
            throw new IllegalArgumentException("no support for 'parameter' of type " + parameter.getClass().getName());
        }
    }

    @Override
    public void engineStore(OutputStream outputStream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        byte[] mac;
        if (this.creationDate == null) {
            throw new IOException("KeyStore not initialized");
        }
        EncryptedObjectStoreData encStoreData = this.getEncryptedObjectStoreData(this.hmacAlgorithm, password);
        if (MiscObjectIdentifiers.id_scrypt.equals(this.hmacPkbdAlgorithm.getAlgorithm())) {
            ScryptParams sParams = ScryptParams.getInstance(this.hmacPkbdAlgorithm.getParameters());
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, sParams.getKeyLength().intValue());
        } else {
            PBKDF2Params pbkdf2Params = PBKDF2Params.getInstance(this.hmacPkbdAlgorithm.getParameters());
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, pbkdf2Params.getKeyLength().intValue());
        }
        try {
            mac = this.calculateMac(encStoreData.getEncoded(), this.hmacAlgorithm, this.hmacPkbdAlgorithm, password);
        }
        catch (NoSuchProviderException e) {
            throw new IOException("cannot calculate mac: " + e.getMessage());
        }
        ObjectStore store = new ObjectStore(encStoreData, new ObjectStoreIntegrityCheck(new PbkdMacIntegrityCheck(this.hmacAlgorithm, this.hmacPkbdAlgorithm, mac)));
        outputStream.write(store.getEncoded());
        outputStream.flush();
    }

    private EncryptedObjectStoreData getEncryptedObjectStoreData(AlgorithmIdentifier integrityAlgorithm, char[] password) throws IOException, NoSuchAlgorithmException {
        EncryptedObjectStoreData encStoreData;
        ObjectData[] dataArray = this.entries.values().toArray(new ObjectData[this.entries.size()]);
        KeyDerivationFunc pbkdAlgId = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, 32);
        byte[] keyBytes = this.generateKey(pbkdAlgId, "STORE_ENCRYPTION", password != null ? password : new char[]{}, 32);
        ObjectStoreData storeData = new ObjectStoreData(integrityAlgorithm, this.creationDate, this.lastModifiedDate, new ObjectDataSequence(dataArray), null);
        try {
            if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                Cipher c = this.createCipher("AES/CCM/NoPadding", keyBytes);
                byte[] encOut = c.doFinal(storeData.getEncoded());
                AlgorithmParameters algorithmParameters = c.getParameters();
                PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(algorithmParameters.getEncoded())));
                encStoreData = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encOut);
            } else {
                Cipher c = this.createCipher("AESKWP", keyBytes);
                byte[] encOut = c.doFinal(storeData.getEncoded());
                PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad));
                encStoreData = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encOut);
            }
        }
        catch (NoSuchPaddingException e) {
            throw new NoSuchAlgorithmException(e.toString());
        }
        catch (BadPaddingException e) {
            throw new IOException(e.toString());
        }
        catch (IllegalBlockSizeException e) {
            throw new IOException(e.toString());
        }
        catch (InvalidKeyException e) {
            throw new IOException(e.toString());
        }
        catch (NoSuchProviderException e) {
            throw new IOException(e.toString());
        }
        return encStoreData;
    }

    @Override
    public void engineLoad(KeyStore.LoadStoreParameter parameter) throws CertificateException, NoSuchAlgorithmException, IOException {
        if (parameter == null) {
            this.engineLoad(null, null);
        } else if (parameter instanceof BCFKSLoadStoreParameter) {
            BCFKSLoadStoreParameter bcParam = (BCFKSLoadStoreParameter)parameter;
            char[] password = ParameterUtil.extractPassword(bcParam);
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(bcParam.getStorePBKDFConfig(), 64);
            this.storeEncryptionAlgorithm = bcParam.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM ? NISTObjectIdentifiers.id_aes256_CCM : NISTObjectIdentifiers.id_aes256_wrap_pad;
            this.hmacAlgorithm = bcParam.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512 ? new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE) : new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
            this.verificationKey = (PublicKey)bcParam.getStoreSignatureKey();
            this.validator = bcParam.getCertChainValidator();
            this.signatureAlgorithm = this.generateSignatureAlgId(this.verificationKey, bcParam.getStoreSignatureAlgorithm());
            AlgorithmIdentifier presetHmacAlgorithm = this.hmacAlgorithm;
            ASN1ObjectIdentifier presetStoreEncryptionAlgorithm = this.storeEncryptionAlgorithm;
            InputStream inputStream = bcParam.getInputStream();
            this.engineLoad(inputStream, password);
            if (!(inputStream == null || this.isSimilarHmacPbkd(bcParam.getStorePBKDFConfig(), this.hmacPkbdAlgorithm) && presetStoreEncryptionAlgorithm.equals(this.storeEncryptionAlgorithm))) {
                throw new IOException("configuration parameters do not match existing store");
            }
        } else if (parameter instanceof BCLoadStoreParameter) {
            BCLoadStoreParameter bcParam = (BCLoadStoreParameter)parameter;
            this.engineLoad(bcParam.getInputStream(), ParameterUtil.extractPassword(parameter));
        } else {
            throw new IllegalArgumentException("no support for 'parameter' of type " + parameter.getClass().getName());
        }
    }

    private boolean isSimilarHmacPbkd(PBKDFConfig storePBKDFConfig, KeyDerivationFunc hmacPkbdAlgorithm) {
        if (!storePBKDFConfig.getAlgorithm().equals(hmacPkbdAlgorithm.getAlgorithm())) {
            return false;
        }
        if (MiscObjectIdentifiers.id_scrypt.equals(hmacPkbdAlgorithm.getAlgorithm())) {
            if (!(storePBKDFConfig instanceof ScryptConfig)) {
                return false;
            }
            ScryptConfig scryptConfig = (ScryptConfig)storePBKDFConfig;
            ScryptParams sParams = ScryptParams.getInstance(hmacPkbdAlgorithm.getParameters());
            if (scryptConfig.getSaltLength() != sParams.getSalt().length || scryptConfig.getBlockSize() != sParams.getBlockSize().intValue() || scryptConfig.getCostParameter() != sParams.getCostParameter().intValue() || scryptConfig.getParallelizationParameter() != sParams.getParallelizationParameter().intValue()) {
                return false;
            }
        } else {
            if (!(storePBKDFConfig instanceof PBKDF2Config)) {
                return false;
            }
            PBKDF2Config pbkdf2Config = (PBKDF2Config)storePBKDFConfig;
            PBKDF2Params pbkdf2Params = PBKDF2Params.getInstance(hmacPkbdAlgorithm.getParameters());
            if (pbkdf2Config.getSaltLength() != pbkdf2Params.getSalt().length || pbkdf2Config.getIterationCount() != pbkdf2Params.getIterationCount().intValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void engineLoad(InputStream inputStream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        ObjectStoreData storeData;
        AlgorithmIdentifier integrityAlg;
        ObjectStore store;
        this.entries.clear();
        this.privateKeyCache.clear();
        this.creationDate = null;
        this.lastModifiedDate = null;
        this.hmacAlgorithm = null;
        if (inputStream == null) {
            this.lastModifiedDate = this.creationDate = new Date();
            this.verificationKey = null;
            this.validator = null;
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 64);
            return;
        }
        ASN1InputStream aIn = new ASN1InputStream(inputStream);
        try {
            store = ObjectStore.getInstance(aIn.readObject());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        ObjectStoreIntegrityCheck integrityCheck = store.getIntegrityCheck();
        if (integrityCheck.getType() == 0) {
            PbkdMacIntegrityCheck pbkdMacIntegrityCheck = PbkdMacIntegrityCheck.getInstance(integrityCheck.getIntegrityCheck());
            this.hmacAlgorithm = pbkdMacIntegrityCheck.getMacAlgorithm();
            this.hmacPkbdAlgorithm = pbkdMacIntegrityCheck.getPbkdAlgorithm();
            integrityAlg = this.hmacAlgorithm;
            try {
                this.verifyMac(store.getStoreData().toASN1Primitive().getEncoded(), pbkdMacIntegrityCheck, password);
            }
            catch (NoSuchProviderException e) {
                throw new IOException(e.getMessage());
            }
        } else if (integrityCheck.getType() == 1) {
            SignatureCheck sigCheck = SignatureCheck.getInstance(integrityCheck.getIntegrityCheck());
            integrityAlg = sigCheck.getSignatureAlgorithm();
            try {
                Certificate[] certificates = sigCheck.getCertificates();
                if (this.validator != null) {
                    if (certificates == null) {
                        throw new IOException("validator specified but no certifcates in store");
                    }
                    CertificateFactory certFact = this.helper.createCertificateFactory("X.509");
                    X509Certificate[] certs = new X509Certificate[certificates.length];
                    for (int i = 0; i != certs.length; ++i) {
                        certs[i] = (X509Certificate)certFact.generateCertificate(new ByteArrayInputStream(certificates[i].getEncoded()));
                    }
                    if (this.validator.isValid(certs)) {
                        this.verifySig(store.getStoreData(), sigCheck, certs[0].getPublicKey());
                    }
                    throw new IOException("certificate chain in key store signature not valid");
                }
                this.verifySig(store.getStoreData(), sigCheck, this.verificationKey);
            }
            catch (GeneralSecurityException e) {
                throw new IOException("error verifying signature: " + e.getMessage(), e);
            }
        } else {
            throw new IOException("BCFKS KeyStore unable to recognize integrity check.");
        }
        ASN1Encodable sData = store.getStoreData();
        if (sData instanceof EncryptedObjectStoreData) {
            EncryptedObjectStoreData encryptedStoreData = (EncryptedObjectStoreData)sData;
            AlgorithmIdentifier protectAlgId = encryptedStoreData.getEncryptionAlgorithm();
            storeData = ObjectStoreData.getInstance(this.decryptData("STORE_ENCRYPTION", protectAlgId, password, encryptedStoreData.getEncryptedContent().getOctets()));
        } else {
            storeData = ObjectStoreData.getInstance(sData);
        }
        try {
            this.creationDate = storeData.getCreationDate().getDate();
            this.lastModifiedDate = storeData.getLastModifiedDate().getDate();
        }
        catch (ParseException e) {
            throw new IOException("BCFKS KeyStore unable to parse store data information.");
        }
        if (!storeData.getIntegrityAlgorithm().equals(integrityAlg)) {
            throw new IOException("BCFKS KeyStore storeData integrity algorithm does not match store integrity algorithm.");
        }
        Iterator<ASN1Encodable> it = storeData.getObjectDataSequence().iterator();
        while (it.hasNext()) {
            ObjectData objData = ObjectData.getInstance(it.next());
            this.entries.put(objData.getIdentifier(), objData);
        }
    }

    private byte[] decryptData(String purpose, AlgorithmIdentifier protectAlgId, char[] password, byte[] encryptedData) throws IOException {
        if (!protectAlgId.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBES2)) {
            throw new IOException("BCFKS KeyStore cannot recognize protection algorithm.");
        }
        PBES2Parameters pbes2Parameters = PBES2Parameters.getInstance(protectAlgId.getParameters());
        EncryptionScheme algId = pbes2Parameters.getEncryptionScheme();
        try {
            AlgorithmParameters algParams;
            Cipher c;
            if (algId.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_CCM)) {
                c = this.helper.createCipher("AES/CCM/NoPadding");
                algParams = this.helper.createAlgorithmParameters("CCM");
                CCMParameters ccmParameters = CCMParameters.getInstance(algId.getParameters());
                algParams.init(ccmParameters.getEncoded());
            } else if (algId.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_wrap_pad)) {
                c = this.helper.createCipher("AESKWP");
                algParams = null;
            } else {
                throw new IOException("BCFKS KeyStore cannot recognize protection encryption algorithm.");
            }
            byte[] keyBytes = this.generateKey(pbes2Parameters.getKeyDerivationFunc(), purpose, password != null ? password : new char[]{}, 32);
            c.init(2, (Key)new SecretKeySpec(keyBytes, "AES"), algParams);
            byte[] rv = c.doFinal(encryptedData);
            return rv;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    private AlgorithmIdentifier generateSignatureAlgId(Key key, BCFKSLoadStoreParameter.SignatureAlgorithm sigAlg) throws IOException {
        if (key == null) {
            return null;
        }
        if (key instanceof ECKey) {
            if (sigAlg == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withECDSA) {
                return new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512);
            }
            if (sigAlg == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withECDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_512);
            }
        }
        if (key instanceof DSAKey) {
            if (sigAlg == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha512);
            }
            if (sigAlg == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_512);
            }
        }
        if (key instanceof RSAKey) {
            if (sigAlg == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withRSA) {
                return new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE);
            }
            if (sigAlg == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withRSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, DERNull.INSTANCE);
            }
        }
        throw new IOException("unknown signature algorithm");
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(PBKDFConfig pbkdfConfig, int keySizeInBytes) {
        if (MiscObjectIdentifiers.id_scrypt.equals(pbkdfConfig.getAlgorithm())) {
            ScryptConfig scryptConfig = (ScryptConfig)pbkdfConfig;
            byte[] pbkdSalt = new byte[scryptConfig.getSaltLength()];
            this.getDefaultSecureRandom().nextBytes(pbkdSalt);
            ScryptParams params = new ScryptParams(pbkdSalt, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter(), keySizeInBytes);
            return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, params);
        }
        PBKDF2Config pbkdf2Config = (PBKDF2Config)pbkdfConfig;
        byte[] pbkdSalt = new byte[pbkdf2Config.getSaltLength()];
        this.getDefaultSecureRandom().nextBytes(pbkdSalt);
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(pbkdSalt, pbkdf2Config.getIterationCount(), keySizeInBytes, pbkdf2Config.getPRF()));
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(KeyDerivationFunc baseAlg, int keySizeInBytes) {
        if (MiscObjectIdentifiers.id_scrypt.equals(baseAlg.getAlgorithm())) {
            ScryptParams oldParams = ScryptParams.getInstance(baseAlg.getParameters());
            byte[] pbkdSalt = new byte[oldParams.getSalt().length];
            this.getDefaultSecureRandom().nextBytes(pbkdSalt);
            ScryptParams params = new ScryptParams(pbkdSalt, oldParams.getCostParameter(), oldParams.getBlockSize(), oldParams.getParallelizationParameter(), BigInteger.valueOf(keySizeInBytes));
            return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, params);
        }
        PBKDF2Params oldParams = PBKDF2Params.getInstance(baseAlg.getParameters());
        byte[] pbkdSalt = new byte[oldParams.getSalt().length];
        this.getDefaultSecureRandom().nextBytes(pbkdSalt);
        PBKDF2Params params = new PBKDF2Params(pbkdSalt, oldParams.getIterationCount().intValue(), keySizeInBytes, oldParams.getPrf());
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, params);
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(ASN1ObjectIdentifier derivationAlgorithm, int keySizeInBytes) {
        byte[] pbkdSalt = new byte[64];
        this.getDefaultSecureRandom().nextBytes(pbkdSalt);
        if (PKCSObjectIdentifiers.id_PBKDF2.equals(derivationAlgorithm)) {
            return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(pbkdSalt, 51200, keySizeInBytes, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE)));
        }
        throw new IllegalStateException("unknown derivation algorithm: " + derivationAlgorithm);
    }

    static {
        oidMap.put("DESEDE", OIWObjectIdentifiers.desEDE);
        oidMap.put("TRIPLEDES", OIWObjectIdentifiers.desEDE);
        oidMap.put("TDEA", OIWObjectIdentifiers.desEDE);
        oidMap.put("HMACSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
        oidMap.put("HMACSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
        oidMap.put("HMACSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
        oidMap.put("HMACSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
        oidMap.put("HMACSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
        oidMap.put("SEED", KISAObjectIdentifiers.id_seedCBC);
        oidMap.put("CAMELLIA.128", NTTObjectIdentifiers.id_camellia128_cbc);
        oidMap.put("CAMELLIA.192", NTTObjectIdentifiers.id_camellia192_cbc);
        oidMap.put("CAMELLIA.256", NTTObjectIdentifiers.id_camellia256_cbc);
        oidMap.put("ARIA.128", NSRIObjectIdentifiers.id_aria128_cbc);
        oidMap.put("ARIA.192", NSRIObjectIdentifiers.id_aria192_cbc);
        oidMap.put("ARIA.256", NSRIObjectIdentifiers.id_aria256_cbc);
        publicAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        publicAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
        publicAlgMap.put(OIWObjectIdentifiers.elGamalAlgorithm, "DH");
        publicAlgMap.put(PKCSObjectIdentifiers.dhKeyAgreement, "DH");
        publicAlgMap.put(X9ObjectIdentifiers.id_dsa, "DSA");
        CERTIFICATE = BigInteger.valueOf(0L);
        PRIVATE_KEY = BigInteger.valueOf(1L);
        SECRET_KEY = BigInteger.valueOf(2L);
        PROTECTED_PRIVATE_KEY = BigInteger.valueOf(3L);
        PROTECTED_SECRET_KEY = BigInteger.valueOf(4L);
    }

    public static class Def
    extends BcFKSKeyStoreSpi {
        public Def() {
            super(new DefaultJcaJceHelper());
        }
    }

    public static class DefCompat
    extends AdaptingKeyStoreSpi {
        public DefCompat() {
            super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new DefaultJcaJceHelper()));
        }
    }

    public static class DefShared
    extends SharedKeyStoreSpi {
        public DefShared() {
            super(new DefaultJcaJceHelper());
        }
    }

    public static class DefSharedCompat
    extends AdaptingKeyStoreSpi {
        public DefSharedCompat() {
            super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new DefaultJcaJceHelper()));
        }
    }

    private static class ExtKeyStoreException
    extends KeyStoreException {
        private final Throwable cause;

        ExtKeyStoreException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }

    private static class SharedKeyStoreSpi
    extends BcFKSKeyStoreSpi
    implements PKCSObjectIdentifiers,
    X509ObjectIdentifiers {
        private final Map<String, byte[]> cache;
        private final byte[] seedKey;

        public SharedKeyStoreSpi(JcaJceHelper provider) {
            super(provider);
            try {
                this.seedKey = new byte[32];
                provider.createSecureRandom("DEFAULT").nextBytes(this.seedKey);
            }
            catch (GeneralSecurityException e) {
                throw new IllegalArgumentException("can't create random - " + e.toString());
            }
            this.cache = new HashMap<String, byte[]>();
        }

        @Override
        public void engineDeleteEntry(String alias) throws KeyStoreException {
            throw new KeyStoreException("delete operation not supported in shared mode");
        }

        @Override
        public void engineSetKeyEntry(String alias, Key key, char[] password, java.security.cert.Certificate[] chain) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        @Override
        public void engineSetKeyEntry(String alias, byte[] keyEncoding, java.security.cert.Certificate[] chain) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        @Override
        public void engineSetCertificateEntry(String alias, java.security.cert.Certificate cert) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        @Override
        public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            byte[] hash;
            byte[] mac;
            try {
                mac = this.calculateMac(alias, password);
            }
            catch (InvalidKeyException e) {
                throw new UnrecoverableKeyException("unable to recover key (" + alias + "): " + e.getMessage());
            }
            if (this.cache.containsKey(alias) && !Arrays.constantTimeAreEqual(hash = this.cache.get(alias), mac)) {
                throw new UnrecoverableKeyException("unable to recover key (" + alias + ")");
            }
            Key key = super.engineGetKey(alias, password);
            if (key != null && !this.cache.containsKey(alias)) {
                this.cache.put(alias, mac);
            }
            return key;
        }

        private byte[] calculateMac(String alias, char[] password) throws NoSuchAlgorithmException, InvalidKeyException {
            byte[] encoding = password != null ? Arrays.concatenate(Strings.toUTF8ByteArray(password), Strings.toUTF8ByteArray(alias)) : Arrays.concatenate(this.seedKey, Strings.toUTF8ByteArray(alias));
            return SCrypt.generate(encoding, this.seedKey, 16384, 8, 1, 32);
        }
    }

    public static class Std
    extends BcFKSKeyStoreSpi {
        public Std() {
            super(new BCJcaJceHelper());
        }
    }

    public static class StdCompat
    extends AdaptingKeyStoreSpi {
        public StdCompat() {
            super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new BCJcaJceHelper()));
        }
    }

    public static class StdShared
    extends SharedKeyStoreSpi {
        public StdShared() {
            super(new BCJcaJceHelper());
        }
    }

    public static class StdSharedCompat
    extends AdaptingKeyStoreSpi {
        public StdSharedCompat() {
            super(new BCJcaJceHelper(), new BcFKSKeyStoreSpi(new BCJcaJceHelper()));
        }
    }
}

