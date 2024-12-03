/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherKeyGenerator
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.modes.AEADBlockCipher
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.util.CipherFactory
 */
package org.bouncycastle.cms.bc;

import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.bc.EnvelopedDataHelper;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCaptureStream;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class BcCMSContentEncryptorBuilder {
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper = new EnvelopedDataHelper();
    private SecureRandom random;

    public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID) {
        this(encryptionOID, KEY_SIZE_PROVIDER.getKeySize(encryptionOID));
    }

    public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier encryptionOID, int keySize) {
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

    public BcCMSContentEncryptorBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public OutputEncryptor build() throws CMSException {
        if (this.helper.isAuthEnveloped(this.encryptionOID)) {
            return new CMSAuthOutputEncryptor(this.encryptionOID, this.keySize, this.random);
        }
        return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }

    private static class AADStream
    extends OutputStream {
        private AEADBlockCipher cipher;

        public AADStream(AEADBlockCipher cipher) {
            this.cipher = cipher;
        }

        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            this.cipher.processAADBytes(buf, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            this.cipher.processAADByte((byte)b);
        }
    }

    private class CMSAuthOutputEncryptor
    extends CMSOutputEncryptor
    implements OutputAEADEncryptor {
        private AEADBlockCipher aeadCipher;
        private MacCaptureStream macOut;

        CMSAuthOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, SecureRandom random) throws CMSException {
            super(encryptionOID, keySize, random);
            this.aeadCipher = this.getCipher();
        }

        private AEADBlockCipher getCipher() {
            if (!(this.cipher instanceof AEADBlockCipher)) {
                throw new IllegalArgumentException("Unable to create Authenticated Output Encryptor without Authenticaed Data cipher!");
            }
            return (AEADBlockCipher)this.cipher;
        }

        @Override
        public OutputStream getOutputStream(OutputStream dOut) {
            this.macOut = new MacCaptureStream(dOut, this.aeadCipher.getMac().length);
            return CipherFactory.createOutputStream((OutputStream)this.macOut, (Object)this.cipher);
        }

        @Override
        public OutputStream getAADStream() {
            return new AADStream(this.aeadCipher);
        }

        @Override
        public byte[] getMAC() {
            return this.macOut.getMac();
        }
    }

    private class CMSOutputEncryptor
    implements OutputEncryptor {
        private KeyParameter encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        protected Object cipher;

        CMSOutputEncryptor(ASN1ObjectIdentifier encryptionOID, int keySize, SecureRandom random) throws CMSException {
            if (random == null) {
                random = new SecureRandom();
            }
            CipherKeyGenerator keyGen = BcCMSContentEncryptorBuilder.this.helper.createKeyGenerator(encryptionOID, keySize, random);
            this.encKey = new KeyParameter(keyGen.generateKey());
            this.algorithmIdentifier = BcCMSContentEncryptorBuilder.this.helper.generateEncryptionAlgID(encryptionOID, this.encKey, random);
            this.cipher = EnvelopedDataHelper.createContentCipher(true, (CipherParameters)this.encKey, this.algorithmIdentifier);
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

