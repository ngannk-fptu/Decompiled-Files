/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherKeyGenerator
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.CryptoServicesRegistrar
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.util.CipherFactory
 */
package org.bouncycastle.cert.crmf.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.bc.CRMFHelper;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;

public class BcCRMFEncryptorBuilder {
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private CRMFHelper helper = new CRMFHelper();
    private SecureRandom random;

    public BcCRMFEncryptorBuilder(ASN1ObjectIdentifier encryptionOID) {
        this(encryptionOID, -1);
    }

    public BcCRMFEncryptorBuilder(ASN1ObjectIdentifier encryptionOID, int keySize) {
        this.encryptionOID = encryptionOID;
        this.keySize = keySize;
    }

    public BcCRMFEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public OutputEncryptor build() throws CRMFException {
        return new CRMFOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }

    private class CRMFOutputEncryptor
    implements OutputEncryptor {
        private KeyParameter encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Object cipher;

        CRMFOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, SecureRandom random) throws CRMFException {
            random = CryptoServicesRegistrar.getSecureRandom((SecureRandom)random);
            CipherKeyGenerator keyGen = BcCRMFEncryptorBuilder.this.helper.createKeyGenerator(encryptionOID, random);
            this.encKey = new KeyParameter(keyGen.generateKey());
            this.algorithmIdentifier = BcCRMFEncryptorBuilder.this.helper.generateEncryptionAlgID(encryptionOID, this.encKey, random);
            BcCRMFEncryptorBuilder.this.helper;
            this.cipher = CRMFHelper.createContentCipher(true, (CipherParameters)this.encKey, this.algorithmIdentifier);
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override
        public OutputStream getOutputStream(OutputStream dOut) {
            return CipherFactory.createOutputStream((OutputStream)dOut, (Object)this.cipher);
        }

        @Override
        public GenericKey getKey() {
            return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
        }
    }
}

