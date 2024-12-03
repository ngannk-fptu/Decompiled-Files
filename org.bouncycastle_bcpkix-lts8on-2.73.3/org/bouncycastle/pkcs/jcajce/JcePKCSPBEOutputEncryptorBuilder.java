/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.bc.BCObjectIdentifiers
 *  org.bouncycastle.asn1.misc.MiscObjectIdentifiers
 *  org.bouncycastle.asn1.misc.ScryptParams
 *  org.bouncycastle.asn1.pkcs.EncryptionScheme
 *  org.bouncycastle.asn1.pkcs.KeyDerivationFunc
 *  org.bouncycastle.asn1.pkcs.PBES2Parameters
 *  org.bouncycastle.asn1.pkcs.PBKDF2Params
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.util.PBKDF2Config
 *  org.bouncycastle.crypto.util.PBKDF2Config$Builder
 *  org.bouncycastle.crypto.util.PBKDFConfig
 *  org.bouncycastle.crypto.util.ScryptConfig
 *  org.bouncycastle.jcajce.PKCS12KeyWithParameters
 *  org.bouncycastle.jcajce.io.CipherOutputStream
 *  org.bouncycastle.jcajce.spec.ScryptKeySpec
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AlgorithmNameFinder;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.pkcs.jcajce.JceUtils;

public class JcePKCSPBEOutputEncryptorBuilder {
    private final PBKDFConfig pbkdf;
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private ASN1ObjectIdentifier algorithm;
    private ASN1ObjectIdentifier keyEncAlgorithm;
    private SecureRandom random;
    private SecretKeySizeProvider keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
    private AlgorithmNameFinder algorithmNameFinder = new DefaultAlgorithmNameFinder();
    private int iterationCount = 1024;
    private PBKDF2Config.Builder pbkdfBuilder = new PBKDF2Config.Builder();

    public JcePKCSPBEOutputEncryptorBuilder(ASN1ObjectIdentifier keyEncryptionAlg) {
        this.pbkdf = null;
        if (this.isPKCS12(keyEncryptionAlg)) {
            this.algorithm = keyEncryptionAlg;
            this.keyEncAlgorithm = keyEncryptionAlg;
        } else {
            this.algorithm = PKCSObjectIdentifiers.id_PBES2;
            this.keyEncAlgorithm = keyEncryptionAlg;
        }
    }

    public JcePKCSPBEOutputEncryptorBuilder(PBKDFConfig pbkdfAlgorithm, ASN1ObjectIdentifier keyEncryptionAlg) {
        this.algorithm = PKCSObjectIdentifiers.id_PBES2;
        this.pbkdf = pbkdfAlgorithm;
        this.keyEncAlgorithm = keyEncryptionAlg;
    }

    public JcePKCSPBEOutputEncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setKeySizeProvider(SecretKeySizeProvider keySizeProvider) {
        this.keySizeProvider = keySizeProvider;
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setPRF(AlgorithmIdentifier prf) {
        if (this.pbkdf != null) {
            throw new IllegalStateException("set PRF count using PBKDFDef");
        }
        this.pbkdfBuilder.withPRF(prf);
        return this;
    }

    public JcePKCSPBEOutputEncryptorBuilder setIterationCount(int iterationCount) {
        if (this.pbkdf != null) {
            throw new IllegalStateException("set iteration count using PBKDFDef");
        }
        this.iterationCount = iterationCount;
        this.pbkdfBuilder.withIterationCount(iterationCount);
        return this;
    }

    public OutputEncryptor build(final char[] password) throws OperatorCreationException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            AlgorithmIdentifier encryptionAlg;
            Cipher cipher;
            if (this.isPKCS12(this.algorithm)) {
                byte[] salt = new byte[20];
                this.random.nextBytes(salt);
                cipher = this.helper.createCipher(this.algorithm.getId());
                cipher.init(1, (Key)new PKCS12KeyWithParameters(password, salt, this.iterationCount));
                encryptionAlg = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)new PKCS12PBEParams(salt, this.iterationCount));
            } else if (this.algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.id_PBES2)) {
                PBKDFConfig pbkDef;
                Object object = pbkDef = this.pbkdf == null ? this.pbkdfBuilder.build() : this.pbkdf;
                if (MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive)pbkDef.getAlgorithm())) {
                    ScryptConfig skdf = (ScryptConfig)pbkDef;
                    byte[] salt = new byte[skdf.getSaltLength()];
                    this.random.nextBytes(salt);
                    ScryptParams params = new ScryptParams(salt, skdf.getCostParameter(), skdf.getBlockSize(), skdf.getParallelizationParameter());
                    SecretKeyFactory keyFact = this.helper.createSecretKeyFactory("SCRYPT");
                    SecretKey key = keyFact.generateSecret((KeySpec)new ScryptKeySpec(password, salt, skdf.getCostParameter(), skdf.getBlockSize(), skdf.getParallelizationParameter(), this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
                    cipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
                    cipher.init(1, (Key)this.simplifyPbeKey(key), this.random);
                    AlgorithmParameters algP = cipher.getParameters();
                    PBES2Parameters algParams = algP != null ? new PBES2Parameters(new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, (ASN1Encodable)params), new EncryptionScheme(this.keyEncAlgorithm, (ASN1Encodable)ASN1Primitive.fromByteArray((byte[])cipher.getParameters().getEncoded()))) : new PBES2Parameters(new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, (ASN1Encodable)params), new EncryptionScheme(this.keyEncAlgorithm));
                    encryptionAlg = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)algParams);
                } else {
                    PBKDF2Config pkdf = (PBKDF2Config)pbkDef;
                    byte[] salt = new byte[pkdf.getSaltLength()];
                    this.random.nextBytes(salt);
                    SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(JceUtils.getAlgorithm(pkdf.getPRF().getAlgorithm()));
                    SecretKey key = keyFact.generateSecret(new PBEKeySpec(password, salt, pkdf.getIterationCount(), this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
                    cipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
                    cipher.init(1, (Key)this.simplifyPbeKey(key), this.random);
                    AlgorithmParameters algP = cipher.getParameters();
                    PBES2Parameters algParams = algP != null ? new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(salt, pkdf.getIterationCount(), pkdf.getPRF())), new EncryptionScheme(this.keyEncAlgorithm, (ASN1Encodable)ASN1Primitive.fromByteArray((byte[])cipher.getParameters().getEncoded()))) : new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(salt, pkdf.getIterationCount(), pkdf.getPRF())), new EncryptionScheme(this.keyEncAlgorithm));
                    encryptionAlg = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)algParams);
                }
            } else {
                throw new OperatorCreationException("unrecognised algorithm");
            }
            return new OutputEncryptor(){

                @Override
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return encryptionAlg;
                }

                @Override
                public OutputStream getOutputStream(OutputStream out) {
                    return new CipherOutputStream(out, cipher);
                }

                @Override
                public GenericKey getKey() {
                    if (JcePKCSPBEOutputEncryptorBuilder.this.isPKCS12(encryptionAlg.getAlgorithm())) {
                        return new GenericKey(encryptionAlg, JcePKCSPBEOutputEncryptorBuilder.PKCS12PasswordToBytes(password));
                    }
                    return new GenericKey(encryptionAlg, JcePKCSPBEOutputEncryptorBuilder.PKCS5PasswordToBytes(password));
                }
            };
        }
        catch (Exception e) {
            throw new OperatorCreationException("unable to create OutputEncryptor: " + e.getMessage(), e);
        }
    }

    private SecretKey simplifyPbeKey(SecretKey key) {
        String algName;
        if (this.algorithmNameFinder.hasAlgorithmName(this.keyEncAlgorithm) && (algName = this.algorithmNameFinder.getAlgorithmName(this.keyEncAlgorithm)).indexOf("AES") >= 0) {
            key = new SecretKeySpec(key.getEncoded(), "AES");
        }
        return key;
    }

    private boolean isPKCS12(ASN1ObjectIdentifier algorithm) {
        return algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds) || algorithm.on(BCObjectIdentifiers.bc_pbe_sha1_pkcs12) || algorithm.on(BCObjectIdentifiers.bc_pbe_sha256_pkcs12);
    }

    private static byte[] PKCS5PasswordToBytes(char[] password) {
        if (password != null) {
            byte[] bytes = new byte[password.length];
            for (int i = 0; i != bytes.length; ++i) {
                bytes[i] = (byte)password[i];
            }
            return bytes;
        }
        return new byte[0];
    }

    private static byte[] PKCS12PasswordToBytes(char[] password) {
        if (password != null && password.length > 0) {
            byte[] bytes = new byte[(password.length + 1) * 2];
            for (int i = 0; i != password.length; ++i) {
                bytes[i * 2] = (byte)(password[i] >>> 8);
                bytes[i * 2 + 1] = (byte)password[i];
            }
            return bytes;
        }
        return new byte[0];
    }
}

