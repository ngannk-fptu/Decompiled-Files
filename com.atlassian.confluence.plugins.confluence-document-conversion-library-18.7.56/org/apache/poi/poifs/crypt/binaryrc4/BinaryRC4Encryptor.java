/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.binaryrc4;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4Decryptor;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionHeader;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionVerifier;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.RandomSingleton;

public class BinaryRC4Encryptor
extends Encryptor {
    private int chunkSize = 512;

    protected BinaryRC4Encryptor() {
    }

    protected BinaryRC4Encryptor(BinaryRC4Encryptor other) {
        super(other);
        this.chunkSize = other.chunkSize;
    }

    @Override
    public void confirmPassword(String password) {
        SecureRandom r = RandomSingleton.getInstance();
        byte[] salt = new byte[16];
        byte[] verifier = new byte[16];
        r.nextBytes(salt);
        r.nextBytes(verifier);
        this.confirmPassword(password, null, null, verifier, salt, null);
    }

    @Override
    public void confirmPassword(String password, byte[] keySpec, byte[] keySalt, byte[] verifier, byte[] verifierSalt, byte[] integritySalt) {
        BinaryRC4EncryptionVerifier ver = (BinaryRC4EncryptionVerifier)this.getEncryptionInfo().getVerifier();
        ver.setSalt(verifierSalt);
        SecretKey skey = BinaryRC4Decryptor.generateSecretKey(password, ver);
        this.setSecretKey(skey);
        try {
            Cipher cipher = BinaryRC4Decryptor.initCipherForBlock(null, 0, this.getEncryptionInfo(), skey, 1);
            byte[] encryptedVerifier = new byte[16];
            cipher.update(verifier, 0, 16, encryptedVerifier);
            ver.setEncryptedVerifier(encryptedVerifier);
            HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            byte[] calcVerifierHash = hashAlg.digest(verifier);
            byte[] encryptedVerifierHash = cipher.doFinal(calcVerifierHash);
            ver.setEncryptedVerifierHash(encryptedVerifierHash);
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException("Password confirmation failed", e);
        }
    }

    @Override
    public OutputStream getDataStream(DirectoryNode dir) throws IOException, GeneralSecurityException {
        return new BinaryRC4CipherOutputStream(dir);
    }

    @Override
    public BinaryRC4CipherOutputStream getDataStream(OutputStream stream, int initialOffset) throws IOException, GeneralSecurityException {
        return new BinaryRC4CipherOutputStream(stream);
    }

    protected int getKeySizeInBytes() {
        return this.getEncryptionInfo().getHeader().getKeySize() / 8;
    }

    protected void createEncryptionInfoEntry(DirectoryNode dir) throws IOException {
        DataSpaceMapUtils.addDefaultDataSpace(dir);
        EncryptionInfo info = this.getEncryptionInfo();
        BinaryRC4EncryptionHeader header = (BinaryRC4EncryptionHeader)info.getHeader();
        BinaryRC4EncryptionVerifier verifier = (BinaryRC4EncryptionVerifier)info.getVerifier();
        EncryptionRecord er = bos -> {
            bos.writeShort(info.getVersionMajor());
            bos.writeShort(info.getVersionMinor());
            header.write(bos);
            verifier.write(bos);
        };
        DataSpaceMapUtils.createEncryptionEntry(dir, "EncryptionInfo", er);
    }

    @Override
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public BinaryRC4Encryptor copy() {
        return new BinaryRC4Encryptor(this);
    }

    protected class BinaryRC4CipherOutputStream
    extends ChunkedCipherOutputStream {
        public BinaryRC4CipherOutputStream(OutputStream stream) throws IOException, GeneralSecurityException {
            super(stream, BinaryRC4Encryptor.this.chunkSize);
        }

        public BinaryRC4CipherOutputStream(DirectoryNode dir) throws IOException, GeneralSecurityException {
            super(dir, BinaryRC4Encryptor.this.chunkSize);
        }

        @Override
        protected Cipher initCipherForBlock(Cipher cipher, int block, boolean lastChunk) throws GeneralSecurityException {
            return BinaryRC4Decryptor.initCipherForBlock(cipher, block, BinaryRC4Encryptor.this.getEncryptionInfo(), BinaryRC4Encryptor.this.getSecretKey(), 1);
        }

        @Override
        protected void calculateChecksum(File file, int i) {
        }

        @Override
        protected void createEncryptionInfoEntry(DirectoryNode dir, File tmpFile) throws IOException, GeneralSecurityException {
            BinaryRC4Encryptor.this.createEncryptionInfoEntry(dir);
        }

        @Override
        public void flush() throws IOException {
            this.writeChunk(false);
            super.flush();
        }
    }
}

