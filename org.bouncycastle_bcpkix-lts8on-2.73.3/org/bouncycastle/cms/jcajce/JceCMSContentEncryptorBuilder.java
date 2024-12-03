/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.cms.GCMParameters
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CryptoServicesRegistrar
 *  org.bouncycastle.jcajce.io.CipherOutputStream
 */
package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.JceAADStream;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCaptureStream;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCMSContentEncryptorBuilder {
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    private SecureRandom random;
    private AlgorithmIdentifier algorithmIdentifier;
    private AlgorithmParameters algorithmParameters;

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID) {
        this(encryptionOID, KEY_SIZE_PROVIDER.getKeySize(encryptionOID));
    }

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID, int keySize) {
        this.encryptionOID = encryptionOID;
        int fixedSize = KEY_SIZE_PROVIDER.getKeySize(encryptionOID);
        if (encryptionOID.equals((ASN1Primitive)PKCSObjectIdentifiers.des_EDE3_CBC)) {
            if (keySize != 168 && keySize != fixedSize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 168;
        } else if (encryptionOID.equals((ASN1Primitive)OIWObjectIdentifiers.desCBC)) {
            if (keySize != 56 && keySize != fixedSize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 56;
        } else {
            if (fixedSize > 0 && fixedSize != keySize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = keySize;
        }
    }

    public JceCMSContentEncryptorBuilder(AlgorithmIdentifier encryptionAlgId) {
        this(encryptionAlgId.getAlgorithm(), KEY_SIZE_PROVIDER.getKeySize(encryptionAlgId.getAlgorithm()));
        this.algorithmIdentifier = encryptionAlgId;
    }

    public JceCMSContentEncryptorBuilder setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceCMSContentEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceCMSContentEncryptorBuilder setAlgorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    public OutputEncryptor build() throws CMSException {
        ASN1Encodable params;
        if (this.algorithmParameters != null) {
            if (this.helper.isAuthEnveloped(this.encryptionOID)) {
                return new CMSAuthOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
            }
            return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
        }
        if (this.algorithmIdentifier != null && (params = this.algorithmIdentifier.getParameters()) != null && !params.equals(DERNull.INSTANCE)) {
            try {
                this.algorithmParameters = this.helper.createAlgorithmParameters(this.algorithmIdentifier.getAlgorithm());
                this.algorithmParameters.init(params.toASN1Primitive().getEncoded());
            }
            catch (Exception e) {
                throw new CMSException("unable to process provided algorithmIdentifier: " + e.toString(), e);
            }
        }
        if (this.helper.isAuthEnveloped(this.encryptionOID)) {
            return new CMSAuthOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
        }
        return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
    }

    private static boolean checkForAEAD() {
        return (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    return Cipher.class.getMethod("updateAAD", byte[].class) != null;
                }
                catch (Exception ignore) {
                    return Boolean.FALSE;
                }
            }
        });
    }

    private class CMSAuthOutputEncryptor
    implements OutputAEADEncryptor {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Cipher cipher;
        private MacCaptureStream macOut;

        CMSAuthOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, AlgorithmParameters params, SecureRandom random) throws CMSException {
            KeyGenerator keyGen = JceCMSContentEncryptorBuilder.this.helper.createKeyGenerator(encryptionOID);
            random = CryptoServicesRegistrar.getSecureRandom((SecureRandom)random);
            if (keySize < 0) {
                keyGen.init(random);
            } else {
                keyGen.init(keySize, random);
            }
            this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(encryptionOID);
            this.encKey = keyGen.generateKey();
            if (params == null) {
                params = JceCMSContentEncryptorBuilder.this.helper.generateParameters(encryptionOID, this.encKey, random);
            }
            try {
                this.cipher.init(1, (Key)this.encKey, params, random);
            }
            catch (GeneralSecurityException e) {
                throw new CMSException("unable to initialize cipher: " + e.getMessage(), e);
            }
            if (params == null) {
                params = this.cipher.getParameters();
            }
            this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(encryptionOID, params);
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override
        public OutputStream getOutputStream(OutputStream dOut) {
            GCMParameters p = GCMParameters.getInstance((Object)this.algorithmIdentifier.getParameters());
            this.macOut = new MacCaptureStream(dOut, p.getIcvLen());
            return new CipherOutputStream((OutputStream)this.macOut, this.cipher);
        }

        @Override
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }

        @Override
        public OutputStream getAADStream() {
            if (JceCMSContentEncryptorBuilder.checkForAEAD()) {
                return new JceAADStream(this.cipher);
            }
            return null;
        }

        @Override
        public byte[] getMAC() {
            return this.macOut.getMac();
        }
    }

    private class CMSOutputEncryptor
    implements OutputEncryptor {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Cipher cipher;

        CMSOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, AlgorithmParameters params, SecureRandom random) throws CMSException {
            KeyGenerator keyGen = JceCMSContentEncryptorBuilder.this.helper.createKeyGenerator(encryptionOID);
            random = CryptoServicesRegistrar.getSecureRandom((SecureRandom)random);
            if (keySize < 0) {
                keyGen.init(random);
            } else {
                keyGen.init(keySize, random);
            }
            this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(encryptionOID);
            this.encKey = keyGen.generateKey();
            if (params == null) {
                params = JceCMSContentEncryptorBuilder.this.helper.generateParameters(encryptionOID, this.encKey, random);
            }
            try {
                this.cipher.init(1, (Key)this.encKey, params, random);
            }
            catch (GeneralSecurityException e) {
                throw new CMSException("unable to initialize cipher: " + e.getMessage(), e);
            }
            if (params == null) {
                params = this.cipher.getParameters();
            }
            this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(encryptionOID, params);
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

