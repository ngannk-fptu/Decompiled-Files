/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.io.CipherOutputStream
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.cert.crmf.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.jcajce.CRMFHelper;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCRMFEncryptorBuilder {
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private CRMFHelper helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private SecureRandom random;

    public JceCRMFEncryptorBuilder(ASN1ObjectIdentifier encryptionOID) {
        this(encryptionOID, -1);
    }

    public JceCRMFEncryptorBuilder(ASN1ObjectIdentifier encryptionOID, int keySize) {
        this.encryptionOID = encryptionOID;
        this.keySize = keySize;
    }

    public JceCRMFEncryptorBuilder setProvider(Provider provider) {
        this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceCRMFEncryptorBuilder setProvider(String providerName) {
        this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceCRMFEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public OutputEncryptor build() throws CRMFException {
        return new CRMFOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }

    private class CRMFOutputEncryptor
    implements OutputEncryptor {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Cipher cipher;

        CRMFOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, SecureRandom random) throws CRMFException {
            KeyGenerator keyGen = JceCRMFEncryptorBuilder.this.helper.createKeyGenerator(encryptionOID);
            if (random == null) {
                random = new SecureRandom();
            }
            if (keySize < 0) {
                keySize = KEY_SIZE_PROVIDER.getKeySize(encryptionOID);
            }
            if (keySize < 0) {
                keyGen.init(random);
            } else {
                keyGen.init(keySize, random);
            }
            this.cipher = JceCRMFEncryptorBuilder.this.helper.createCipher(encryptionOID);
            this.encKey = keyGen.generateKey();
            AlgorithmParameters params = JceCRMFEncryptorBuilder.this.helper.generateParameters(encryptionOID, this.encKey, random);
            try {
                this.cipher.init(1, (Key)this.encKey, params, random);
            }
            catch (GeneralSecurityException e) {
                throw new CRMFException("unable to initialize cipher: " + e.getMessage(), e);
            }
            if (params == null) {
                params = this.cipher.getParameters();
            }
            this.algorithmIdentifier = JceCRMFEncryptorBuilder.this.helper.getAlgorithmIdentifier(encryptionOID, params);
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override
        public OutputStream getOutputStream(OutputStream dOut) {
            return new CipherOutputStream(dOut, this.cipher);
        }

        @Override
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }
}

