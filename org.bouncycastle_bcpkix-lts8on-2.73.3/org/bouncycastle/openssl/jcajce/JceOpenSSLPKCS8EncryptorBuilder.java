/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.EncryptionScheme
 *  org.bouncycastle.asn1.pkcs.KeyDerivationFunc
 *  org.bouncycastle.asn1.pkcs.PBES2Parameters
 *  org.bouncycastle.asn1.pkcs.PBKDF2Params
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.PKCS12KeyWithParameters
 *  org.bouncycastle.jcajce.io.CipherOutputStream
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.jcajce.PEMUtilities;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceOpenSSLPKCS8EncryptorBuilder {
    public static final String AES_128_CBC = NISTObjectIdentifiers.id_aes128_CBC.getId();
    public static final String AES_192_CBC = NISTObjectIdentifiers.id_aes192_CBC.getId();
    public static final String AES_256_CBC = NISTObjectIdentifiers.id_aes256_CBC.getId();
    public static final String DES3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC.getId();
    public static final String PBE_SHA1_RC4_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId();
    public static final String PBE_SHA1_RC4_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4.getId();
    public static final String PBE_SHA1_3DES = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC.getId();
    public static final String PBE_SHA1_2DES = PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC.getId();
    public static final String PBE_SHA1_RC2_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC.getId();
    public static final String PBE_SHA1_RC2_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC.getId();
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private AlgorithmParameters params;
    private ASN1ObjectIdentifier algOID;
    byte[] salt;
    int iterationCount;
    private Cipher cipher;
    private SecureRandom random;
    private AlgorithmParameterGenerator paramGen;
    private char[] password;
    private SecretKey key;
    private AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE);

    public JceOpenSSLPKCS8EncryptorBuilder(ASN1ObjectIdentifier algorithm) {
        this.algOID = algorithm;
        this.iterationCount = 2048;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setPassword(char[] password) {
        this.password = password;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setPRF(AlgorithmIdentifier prf) {
        this.prf = prf;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JceOpenSSLPKCS8EncryptorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public OutputEncryptor build() throws OperatorCreationException {
        AlgorithmIdentifier algID;
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            this.cipher = this.helper.createCipher(PEMUtilities.getCipherName(this.algOID));
            if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
                this.paramGen = this.helper.createAlgorithmParameterGenerator(this.algOID.getId());
            }
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException(this.algOID + " not available: " + e.getMessage(), e);
        }
        if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
            this.salt = new byte[PEMUtilities.getSaltSize(this.prf.getAlgorithm())];
            this.random.nextBytes(this.salt);
            this.params = this.paramGen.generateParameters();
            try {
                EncryptionScheme scheme = new EncryptionScheme(this.algOID, (ASN1Encodable)ASN1Primitive.fromByteArray((byte[])this.params.getEncoded()));
                KeyDerivationFunc func = new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(this.salt, this.iterationCount, this.prf));
                ASN1EncodableVector v = new ASN1EncodableVector();
                v.add((ASN1Encodable)func);
                v.add((ASN1Encodable)scheme);
                algID = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, (ASN1Encodable)PBES2Parameters.getInstance((Object)new DERSequence(v)));
            }
            catch (IOException e) {
                throw new OperatorCreationException(e.getMessage(), e);
            }
            try {
                this.key = PEMUtilities.isHmacSHA1(this.prf) ? PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount) : PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount, this.prf);
                this.cipher.init(1, (Key)this.key, this.params);
            }
            catch (GeneralSecurityException e) {
                throw new OperatorCreationException(e.getMessage(), e);
            }
        }
        if (PEMUtilities.isPKCS12(this.algOID)) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            this.salt = new byte[20];
            this.random.nextBytes(this.salt);
            v.add((ASN1Encodable)new DEROctetString(this.salt));
            v.add((ASN1Encodable)new ASN1Integer((long)this.iterationCount));
            algID = new AlgorithmIdentifier(this.algOID, (ASN1Encodable)PKCS12PBEParams.getInstance((Object)new DERSequence(v)));
            try {
                this.cipher.init(1, (Key)new PKCS12KeyWithParameters(this.password, this.salt, this.iterationCount));
            }
            catch (GeneralSecurityException e) {
                throw new OperatorCreationException(e.getMessage(), e);
            }
        } else {
            throw new OperatorCreationException("unknown algorithm: " + this.algOID, null);
        }
        return new OutputEncryptor(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algID;
            }

            @Override
            public OutputStream getOutputStream(OutputStream encOut) {
                return new CipherOutputStream(encOut, JceOpenSSLPKCS8EncryptorBuilder.this.cipher);
            }

            @Override
            public GenericKey getKey() {
                return new JceGenericKey(algID, JceOpenSSLPKCS8EncryptorBuilder.this.key);
            }
        };
    }
}

