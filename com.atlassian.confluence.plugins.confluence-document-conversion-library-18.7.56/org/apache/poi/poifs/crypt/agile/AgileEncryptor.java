/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.agile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.agile.AgileDecryptor;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionHeader;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionVerifier;
import org.apache.poi.poifs.crypt.agile.DataIntegrity;
import org.apache.poi.poifs.crypt.agile.EncryptionDocument;
import org.apache.poi.poifs.crypt.agile.KeyData;
import org.apache.poi.poifs.crypt.agile.KeyEncryptor;
import org.apache.poi.poifs.crypt.agile.PasswordKeyEncryptor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.RandomSingleton;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;

public class AgileEncryptor
extends Encryptor {
    private byte[] integritySalt;
    private byte[] pwHash;

    protected AgileEncryptor() {
    }

    protected AgileEncryptor(AgileEncryptor other) {
        super(other);
        this.integritySalt = other.integritySalt == null ? null : (byte[])other.integritySalt.clone();
        this.pwHash = other.pwHash == null ? null : (byte[])other.pwHash.clone();
    }

    @Override
    public void confirmPassword(String password) {
        AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        int blockSize = header.getBlockSize();
        int keySize = header.getKeySize() / 8;
        int hashSize = header.getHashAlgorithm().hashSize;
        int maxLen = CryptoFunctions.getMaxRecordLength();
        byte[] newVerifierSalt = IOUtils.safelyAllocate(blockSize, maxLen);
        byte[] newVerifier = IOUtils.safelyAllocate(blockSize, maxLen);
        byte[] newKeySalt = IOUtils.safelyAllocate(blockSize, maxLen);
        byte[] newKeySpec = IOUtils.safelyAllocate(keySize, maxLen);
        byte[] newIntegritySalt = IOUtils.safelyAllocate(hashSize, maxLen);
        SecureRandom r = RandomSingleton.getInstance();
        r.nextBytes(newVerifierSalt);
        r.nextBytes(newVerifier);
        r.nextBytes(newKeySalt);
        r.nextBytes(newKeySpec);
        r.nextBytes(newIntegritySalt);
        this.confirmPassword(password, newKeySpec, newKeySalt, newVerifierSalt, newVerifier, newIntegritySalt);
    }

    @Override
    public void confirmPassword(String password, byte[] keySpec, byte[] keySalt, byte[] verifier, byte[] verifierSalt, byte[] integritySalt) {
        AgileEncryptionVerifier ver = (AgileEncryptionVerifier)this.getEncryptionInfo().getVerifier();
        AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        ver.setSalt(verifierSalt);
        header.setKeySalt(keySalt);
        int blockSize = header.getBlockSize();
        this.pwHash = CryptoFunctions.hashPassword(password, ver.getHashAlgorithm(), verifierSalt, ver.getSpinCount());
        byte[] encryptedVerifier = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kVerifierInputBlock, verifier, 1);
        ver.setEncryptedVerifier(encryptedVerifier);
        MessageDigest hashMD = CryptoFunctions.getMessageDigest(ver.getHashAlgorithm());
        byte[] hashedVerifier = hashMD.digest(verifier);
        byte[] encryptedVerifierHash = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kHashedVerifierBlock, hashedVerifier, 1);
        ver.setEncryptedVerifierHash(encryptedVerifierHash);
        byte[] encryptedKey = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kCryptoKeyBlock, keySpec, 1);
        ver.setEncryptedKey(encryptedKey);
        SecretKeySpec secretKey = new SecretKeySpec(keySpec, header.getCipherAlgorithm().jceId);
        this.setSecretKey(secretKey);
        this.integritySalt = (byte[])integritySalt.clone();
        try {
            byte[] vec = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityKeyBlock, header.getBlockSize());
            Cipher cipher = CryptoFunctions.getCipher(secretKey, header.getCipherAlgorithm(), header.getChainingMode(), vec, 1);
            byte[] hmacKey = CryptoFunctions.getBlock0(this.integritySalt, AgileDecryptor.getNextBlockSize(this.integritySalt.length, blockSize));
            byte[] encryptedHmacKey = cipher.doFinal(hmacKey);
            header.setEncryptedHmacKey(encryptedHmacKey);
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    @Override
    public OutputStream getDataStream(DirectoryNode dir) throws IOException, GeneralSecurityException {
        return new AgileCipherOutputStream(dir);
    }

    protected void updateIntegrityHMAC(File tmpFile, int oleStreamSize) throws GeneralSecurityException, IOException {
        AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        int blockSize = header.getBlockSize();
        HashAlgorithm hashAlgo = header.getHashAlgorithm();
        Mac integrityMD = CryptoFunctions.getMac(hashAlgo);
        byte[] hmacKey = CryptoFunctions.getBlock0(this.integritySalt, AgileDecryptor.getNextBlockSize(this.integritySalt.length, blockSize));
        integrityMD.init(new SecretKeySpec(hmacKey, hashAlgo.jceHmacId));
        byte[] buf = new byte[1024];
        LittleEndian.putLong(buf, 0, oleStreamSize);
        integrityMD.update(buf, 0, 8);
        try (FileInputStream fis = new FileInputStream(tmpFile);){
            int readBytes;
            while ((readBytes = ((InputStream)fis).read(buf)) != -1) {
                integrityMD.update(buf, 0, readBytes);
            }
        }
        byte[] hmacValue = integrityMD.doFinal();
        byte[] hmacValueFilled = CryptoFunctions.getBlock0(hmacValue, AgileDecryptor.getNextBlockSize(hmacValue.length, blockSize));
        byte[] iv = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityValueBlock, blockSize);
        Cipher cipher = CryptoFunctions.getCipher(this.getSecretKey(), header.getCipherAlgorithm(), header.getChainingMode(), iv, 1);
        byte[] encryptedHmacValue = cipher.doFinal(hmacValueFilled);
        header.setEncryptedHmacValue(encryptedHmacValue);
    }

    protected EncryptionDocument createEncryptionDocument() {
        AgileEncryptionVerifier ver = (AgileEncryptionVerifier)this.getEncryptionInfo().getVerifier();
        AgileEncryptionHeader header = (AgileEncryptionHeader)this.getEncryptionInfo().getHeader();
        EncryptionDocument ed = new EncryptionDocument();
        KeyData keyData = new KeyData();
        ed.setKeyData(keyData);
        KeyEncryptor keyEnc = new KeyEncryptor();
        ed.getKeyEncryptors().add(keyEnc);
        PasswordKeyEncryptor keyPass = new PasswordKeyEncryptor();
        keyEnc.setPasswordKeyEncryptor(keyPass);
        keyPass.setSpinCount(ver.getSpinCount());
        keyData.setSaltSize(header.getBlockSize());
        keyPass.setSaltSize(ver.getBlockSize());
        keyData.setBlockSize(header.getBlockSize());
        keyPass.setBlockSize(ver.getBlockSize());
        keyData.setKeyBits(header.getKeySize());
        keyPass.setKeyBits(ver.getKeySize());
        keyData.setHashSize(header.getHashAlgorithm().hashSize);
        keyPass.setHashSize(ver.getHashAlgorithm().hashSize);
        if (!header.getCipherAlgorithm().xmlId.equals(ver.getCipherAlgorithm().xmlId)) {
            throw new EncryptedDocumentException("Cipher algorithm of header and verifier have to match");
        }
        keyData.setCipherAlgorithm(header.getCipherAlgorithm());
        keyPass.setCipherAlgorithm(header.getCipherAlgorithm());
        keyData.setCipherChaining(header.getChainingMode());
        keyPass.setCipherChaining(header.getChainingMode());
        keyData.setHashAlgorithm(header.getHashAlgorithm());
        keyPass.setHashAlgorithm(ver.getHashAlgorithm());
        keyData.setSaltValue(header.getKeySalt());
        keyPass.setSaltValue(ver.getSalt());
        keyPass.setEncryptedVerifierHashInput(ver.getEncryptedVerifier());
        keyPass.setEncryptedVerifierHashValue(ver.getEncryptedVerifierHash());
        keyPass.setEncryptedKeyValue(ver.getEncryptedKey());
        DataIntegrity hmacData = new DataIntegrity();
        ed.setDataIntegrity(hmacData);
        hmacData.setEncryptedHmacKey(header.getEncryptedHmacKey());
        hmacData.setEncryptedHmacValue(header.getEncryptedHmacValue());
        return ed;
    }

    protected void marshallEncryptionDocument(EncryptionDocument ed, LittleEndianByteArrayOutputStream os) {
        Document doc = XMLHelper.newDocumentBuilder().newDocument();
        ed.write(doc);
        try {
            Transformer trans = XMLHelper.newTransformer();
            trans.setOutputProperty("method", "xml");
            trans.setOutputProperty("encoding", "UTF-8");
            trans.setOutputProperty("indent", "no");
            trans.setOutputProperty("standalone", "yes");
            trans.transform(new DOMSource(doc), new StreamResult(os));
        }
        catch (TransformerException e) {
            throw new EncryptedDocumentException("error marshalling encryption info document", e);
        }
    }

    @Override
    public AgileEncryptor copy() {
        return new AgileEncryptor(this);
    }

    private class AgileCipherOutputStream
    extends ChunkedCipherOutputStream {
        public AgileCipherOutputStream(DirectoryNode dir) throws IOException, GeneralSecurityException {
            super(dir, 4096);
        }

        @Override
        protected Cipher initCipherForBlock(Cipher existing, int block, boolean lastChunk) throws GeneralSecurityException {
            return AgileDecryptor.initCipherForBlock(existing, block, lastChunk, AgileEncryptor.this.getEncryptionInfo(), AgileEncryptor.this.getSecretKey(), 1);
        }

        @Override
        protected void calculateChecksum(File fileOut, int oleStreamSize) throws GeneralSecurityException, IOException {
            AgileEncryptor.this.updateIntegrityHMAC(fileOut, oleStreamSize);
        }

        @Override
        protected void createEncryptionInfoEntry(DirectoryNode dir, File tmpFile) throws IOException {
            DataSpaceMapUtils.addDefaultDataSpace(dir);
            DataSpaceMapUtils.createEncryptionEntry(dir, "EncryptionInfo", this::marshallEncryptionRecord);
        }

        private void marshallEncryptionRecord(LittleEndianByteArrayOutputStream bos) {
            EncryptionInfo info = AgileEncryptor.this.getEncryptionInfo();
            bos.writeShort(info.getVersionMajor());
            bos.writeShort(info.getVersionMinor());
            bos.writeInt(info.getEncryptionFlags());
            EncryptionDocument ed = AgileEncryptor.this.createEncryptionDocument();
            AgileEncryptor.this.marshallEncryptionDocument(ed, bos);
        }
    }
}

