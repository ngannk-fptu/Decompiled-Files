/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.PDFStringUtil;
import com.sun.pdfview.decrypt.EncryptionUnsupportedByPlatformException;
import com.sun.pdfview.decrypt.EncryptionUnsupportedByProductException;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.decrypt.PDFDecrypter;
import com.sun.pdfview.decrypt.PDFPassword;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StandardDecrypter
implements PDFDecrypter {
    private static final byte[] AESV2_SALT = new byte[]{115, 65, 108, 84};
    private static final byte[] PW_PADDING = new byte[]{40, -65, 78, 94, 78, 117, -118, 65, 100, 0, 78, 86, -1, -6, 1, 8, 46, 46, 0, -74, -48, 104, 62, -128, 47, 12, -87, -2, 100, 83, 105, 122};
    private static final String CIPHER_RC4 = "RC4";
    private static final String KEY_RC4 = "RC4";
    private static final String CIPHER_AES = "AES/CBC/PKCS5Padding";
    private static final String KEY_AES = "AES";
    private boolean ownerAuthorised = false;
    private byte[] generalKeyBytes;
    private EncryptionAlgorithm encryptionAlgorithm;

    public StandardDecrypter(EncryptionAlgorithm encryptionAlgorithm, PDFObject documentId, int keyBitLength, int revision, byte[] oValue, byte[] uValue, int pValue, boolean encryptMetadata, PDFPassword password) throws IOException, EncryptionUnsupportedByProductException, EncryptionUnsupportedByPlatformException {
        this.encryptionAlgorithm = encryptionAlgorithm;
        byte[] firstDocIdValue = documentId == null ? null : documentId.getAt(0).getStream();
        this.testJceAvailability(keyBitLength);
        try {
            List<byte[]> passwordBytePossibilities = password.getPasswordBytes(false);
            for (int i = 0; this.generalKeyBytes == null && i < passwordBytePossibilities.size(); ++i) {
                byte[] passwordBytes = passwordBytePossibilities.get(i);
                this.generalKeyBytes = this.checkOwnerPassword(passwordBytes, firstDocIdValue, keyBitLength, revision, oValue, uValue, pValue, encryptMetadata);
                if (this.generalKeyBytes != null) {
                    this.ownerAuthorised = true;
                    continue;
                }
                this.generalKeyBytes = this.checkUserPassword(passwordBytes, firstDocIdValue, keyBitLength, revision, oValue, uValue, pValue, encryptMetadata);
            }
        }
        catch (GeneralSecurityException e) {
            throw new PDFParseException("Unable to check passwords: " + e.getMessage(), e);
        }
        if (this.generalKeyBytes == null) {
            throw new PDFAuthenticationFailureException("Password failed authentication for both owner and user password");
        }
    }

    @Override
    public ByteBuffer decryptBuffer(String cryptFilterName, PDFObject streamObj, ByteBuffer streamBuf) throws PDFParseException {
        if (cryptFilterName != null) {
            throw new PDFParseException("This encryption version does not support Crypt filters");
        }
        if (streamObj != null) {
            this.checkNums(streamObj.getObjNum(), streamObj.getObjGen());
        }
        byte[] decryptionKeyBytes = streamObj == null ? this.getUnsaltedDecryptionKey() : this.getObjectSaltedDecryptionKey(streamObj.getObjNum(), streamObj.getObjGen());
        return this.decryptBuffer(streamBuf, decryptionKeyBytes);
    }

    @Override
    public String decryptString(int objNum, int objGen, String inputBasicString) throws PDFParseException {
        byte[] crypted = PDFStringUtil.asBytes(inputBasicString);
        byte[] decryptionKey = this.getObjectSaltedDecryptionKey(objNum, objGen);
        ByteBuffer decrypted = this.decryptBuffer(ByteBuffer.wrap(crypted), decryptionKey);
        return PDFStringUtil.asBasicString(decrypted.array(), decrypted.arrayOffset(), decrypted.limit());
    }

    @Override
    public boolean isOwnerAuthorised() {
        return this.ownerAuthorised;
    }

    @Override
    public boolean isEncryptionPresent() {
        return true;
    }

    private void testJceAvailability(int keyBitLength) throws EncryptionUnsupportedByPlatformException, PDFParseException {
        byte[] junkBuffer = new byte[16];
        Arrays.fill(junkBuffer, (byte)-82);
        byte[] junkKey = new byte[this.getSaltedContentKeyByteLength(keyBitLength / 8)];
        Arrays.fill(junkKey, (byte)-82);
        try {
            this.createAndInitialiseContentCipher(ByteBuffer.wrap(junkBuffer), junkKey);
        }
        catch (PDFParseException e) {
            throw new PDFParseException("Internal error; failed to produce test cipher: " + e.getMessage());
        }
        catch (NoSuchAlgorithmException e) {
            throw new EncryptionUnsupportedByPlatformException("JCE does not offer required cipher", e);
        }
        catch (NoSuchPaddingException e) {
            throw new EncryptionUnsupportedByPlatformException("JCE does not offer required padding", e);
        }
        catch (InvalidKeyException e) {
            throw new EncryptionUnsupportedByPlatformException("JCE does accept key size of " + this.getSaltedContentKeyByteLength() * 8 + " bits- could it be a policy restriction?", e);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionUnsupportedByPlatformException("JCE did not accept cipher parameter", e);
        }
        try {
            this.createMD5Digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new EncryptionUnsupportedByPlatformException("No MD5 digest available from JCE", e);
        }
        if (this.encryptionAlgorithm != EncryptionAlgorithm.RC4) {
            Cipher rc4;
            try {
                rc4 = this.createRC4Cipher();
            }
            catch (GeneralSecurityException e) {
                throw new EncryptionUnsupportedByPlatformException("JCE did not offer RC4 cipher", e);
            }
            byte[] rc4JunkKey = new byte[5];
            Arrays.fill(junkKey, (byte)-82);
            try {
                this.initDecryption(rc4, this.createRC4Key(rc4JunkKey));
            }
            catch (InvalidKeyException ex) {
                throw new EncryptionUnsupportedByPlatformException("JCE did not accept 40-bit RC4 key; policy problem?", ex);
            }
        }
    }

    private ByteBuffer decryptBuffer(ByteBuffer encrypted, byte[] decryptionKeyBytes) throws PDFParseException {
        Cipher cipher;
        try {
            cipher = this.createAndInitialiseContentCipher(encrypted, decryptionKeyBytes);
        }
        catch (GeneralSecurityException e) {
            throw new PDFParseException("Unable to create cipher due to platform limitation: " + e.getMessage(), e);
        }
        try {
            ByteBuffer decryptedBuf = ByteBuffer.allocate(encrypted.remaining());
            cipher.doFinal(encrypted, decryptedBuf);
            decryptedBuf.flip();
            return decryptedBuf;
        }
        catch (GeneralSecurityException e) {
            throw new PDFParseException("Could not decrypt: " + e.getMessage(), e);
        }
    }

    private Cipher createAndInitialiseContentCipher(ByteBuffer encrypted, byte[] decryptionKeyBytes) throws PDFParseException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher;
        if (this.encryptionAlgorithm.isRC4()) {
            cipher = Cipher.getInstance("RC4");
            cipher.init(2, this.createRC4Key(decryptionKeyBytes));
        } else if (this.encryptionAlgorithm.isAES()) {
            cipher = this.createAESCipher();
            byte[] initialisationVector = new byte[16];
            if (encrypted.remaining() < initialisationVector.length) {
                throw new PDFParseException("AES encrypted stream too short - no room for initialisation vector");
            }
            encrypted.get(initialisationVector);
            SecretKeySpec aesKey = new SecretKeySpec(decryptionKeyBytes, KEY_AES);
            IvParameterSpec aesIv = new IvParameterSpec(initialisationVector);
            cipher.init(2, (Key)aesKey, aesIv);
        } else {
            throw new PDFParseException("Internal error - unhandled cipher type: " + (Object)((Object)this.encryptionAlgorithm));
        }
        return cipher;
    }

    private byte[] getUnsaltedDecryptionKey() {
        return this.generalKeyBytes;
    }

    private byte[] getObjectSaltedDecryptionKey(int objNum, int objGen) throws PDFParseException {
        MessageDigest md5;
        try {
            md5 = this.createMD5Digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new PDFParseException("Unable to get MD5 digester", e);
        }
        md5.update(this.generalKeyBytes);
        md5.update((byte)objNum);
        md5.update((byte)(objNum >> 8));
        md5.update((byte)(objNum >> 16));
        md5.update((byte)objGen);
        md5.update((byte)(objGen >> 8));
        if (this.encryptionAlgorithm == EncryptionAlgorithm.AESV2) {
            md5.update(AESV2_SALT);
        }
        byte[] hash = md5.digest();
        int keyLen = this.getSaltedContentKeyByteLength();
        byte[] decryptionKeyBytes = new byte[keyLen];
        System.arraycopy(hash, 0, decryptionKeyBytes, 0, keyLen);
        return decryptionKeyBytes;
    }

    private int getSaltedContentKeyByteLength() {
        return this.getSaltedContentKeyByteLength(this.generalKeyBytes.length);
    }

    private int getSaltedContentKeyByteLength(int generalKeyByteLength) {
        return Math.min(generalKeyByteLength + 5, 16);
    }

    private void checkNums(int objNum, int objGen) throws PDFParseException {
        if (objNum < 0) {
            throw new PDFParseException("Internal error: Object has bogus object number");
        }
        if (objGen < 0) {
            throw new PDFParseException("Internal error: Object has bogus generation number");
        }
    }

    private byte[] calculateUValue(byte[] generalKey, byte[] firstDocIdValue, int revision) throws GeneralSecurityException, EncryptionUnsupportedByProductException {
        if (revision == 2) {
            Cipher rc4 = this.createRC4Cipher();
            SecretKeySpec key = this.createRC4Key(generalKey);
            this.initEncryption(rc4, key);
            return this.crypt(rc4, PW_PADDING);
        }
        if (revision >= 3) {
            MessageDigest md5 = this.createMD5Digest();
            md5.update(PW_PADDING);
            if (firstDocIdValue != null) {
                md5.update(firstDocIdValue);
            }
            byte[] hash = md5.digest();
            Cipher rc4 = this.createRC4Cipher();
            SecretKeySpec key = this.createRC4Key(generalKey);
            this.initEncryption(rc4, key);
            byte[] v = this.crypt(rc4, hash);
            this.rc4shuffle(v, generalKey, rc4);
            assert (v.length == 16);
            byte[] entryValue = new byte[32];
            System.arraycopy(v, 0, entryValue, 0, v.length);
            System.arraycopy(v, 0, entryValue, 16, v.length);
            return entryValue;
        }
        throw new EncryptionUnsupportedByProductException("Unsupported standard security handler revision " + revision);
    }

    private byte[] calculuateOValue(byte[] ownerPassword, byte[] userPassword, int keyBitLength, int revision) throws GeneralSecurityException {
        byte[] rc4KeyBytes = this.getInitialOwnerPasswordKeyBytes(ownerPassword, keyBitLength, revision);
        Cipher rc4 = this.createRC4Cipher();
        this.initEncryption(rc4, this.createRC4Key(rc4KeyBytes));
        byte[] pwvalue = this.crypt(rc4, this.padPassword(userPassword));
        if (revision >= 3) {
            this.rc4shuffle(pwvalue, rc4KeyBytes, rc4);
        }
        assert (pwvalue.length == 32);
        return pwvalue;
    }

    private byte[] checkOwnerPassword(byte[] ownerPassword, byte[] firstDocIdValue, int keyBitLength, int revision, byte[] oValue, byte[] uValue, int pValue, boolean encryptMetadata) throws GeneralSecurityException, EncryptionUnsupportedByProductException, PDFParseException {
        byte[] possibleUserPassword;
        byte[] rc4KeyBytes = this.getInitialOwnerPasswordKeyBytes(ownerPassword, keyBitLength, revision);
        Cipher rc4 = this.createRC4Cipher();
        this.initDecryption(rc4, this.createRC4Key(rc4KeyBytes));
        if (revision == 2) {
            possibleUserPassword = this.crypt(rc4, oValue);
        } else if (revision >= 3) {
            possibleUserPassword = new byte[32];
            System.arraycopy(oValue, 0, possibleUserPassword, 0, possibleUserPassword.length);
            this.rc4unshuffle(rc4, possibleUserPassword, rc4KeyBytes);
        } else {
            throw new EncryptionUnsupportedByProductException("Unsupported revision: " + revision);
        }
        return this.checkUserPassword(possibleUserPassword, firstDocIdValue, keyBitLength, revision, oValue, uValue, pValue, encryptMetadata);
    }

    private byte[] getInitialOwnerPasswordKeyBytes(byte[] ownerPassword, int keyBitLength, int revision) throws GeneralSecurityException {
        MessageDigest md5 = this.createMD5Digest();
        md5.update(this.padPassword(ownerPassword));
        byte[] hash = md5.digest();
        if (revision >= 3) {
            for (int i = 0; i < 50; ++i) {
                md5.update(hash);
                this.digestTo(md5, hash);
            }
        }
        byte[] rc4KeyBytes = new byte[keyBitLength / 8];
        System.arraycopy(hash, 0, rc4KeyBytes, 0, rc4KeyBytes.length);
        return rc4KeyBytes;
    }

    private byte[] checkUserPassword(byte[] userPassword, byte[] firstDocIdValue, int keyBitLength, int revision, byte[] oValue, byte[] uValue, int pValue, boolean encryptMetadata) throws GeneralSecurityException, EncryptionUnsupportedByProductException, PDFParseException {
        byte[] generalKey = this.calculateGeneralEncryptionKey(userPassword, firstDocIdValue, keyBitLength, revision, oValue, pValue, encryptMetadata);
        byte[] calculatedUValue = this.calculateUValue(generalKey, firstDocIdValue, revision);
        assert (calculatedUValue.length == 32);
        if (uValue.length != calculatedUValue.length) {
            throw new PDFParseException("Improper U entry length; expected 32, is " + uValue.length);
        }
        int numSignificantBytes = revision == 2 ? 32 : 16;
        for (int i = 0; i < numSignificantBytes; ++i) {
            if (uValue[i] == calculatedUValue[i]) continue;
            return null;
        }
        return generalKey;
    }

    private byte[] calculateGeneralEncryptionKey(byte[] userPassword, byte[] firstDocIdValue, int keyBitLength, int revision, byte[] oValue, int pValue, boolean encryptMetadata) throws GeneralSecurityException {
        byte[] paddedPassword = this.padPassword(userPassword);
        MessageDigest md5 = this.createMD5Digest();
        md5.reset();
        md5.update(paddedPassword);
        md5.update(oValue);
        md5.update((byte)(pValue & 0xFF));
        md5.update((byte)(pValue >> 8 & 0xFF));
        md5.update((byte)(pValue >> 16 & 0xFF));
        md5.update((byte)(pValue >> 24));
        if (firstDocIdValue != null) {
            md5.update(firstDocIdValue);
        }
        if (revision >= 4 && !encryptMetadata) {
            for (int i = 0; i < 4; ++i) {
                md5.update((byte)-1);
            }
        }
        byte[] hash = md5.digest();
        int keyLen = revision == 2 ? 5 : keyBitLength / 8;
        byte[] key = new byte[keyLen];
        if (revision >= 3) {
            for (int i = 0; i < 50; ++i) {
                md5.update(hash, 0, key.length);
                this.digestTo(md5, hash);
            }
        }
        System.arraycopy(hash, 0, key, 0, key.length);
        return key;
    }

    private byte[] padPassword(byte[] password) {
        byte[] padded;
        if (password == null) {
            password = new byte[]{};
        }
        int numContributingPasswordBytes = password.length > (padded = new byte[32]).length ? padded.length : password.length;
        System.arraycopy(password, 0, padded, 0, numContributingPasswordBytes);
        if (password.length < padded.length) {
            System.arraycopy(PW_PADDING, 0, padded, password.length, padded.length - password.length);
        }
        return padded;
    }

    private byte[] crypt(Cipher cipher, byte[] input) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(input);
    }

    private void initEncryption(Cipher cipher, SecretKey key) throws InvalidKeyException {
        cipher.init(1, key);
    }

    private void rc4shuffle(byte[] shuffle, byte[] key, Cipher rc4) throws GeneralSecurityException {
        byte[] shuffleKey = new byte[key.length];
        for (int i = 1; i <= 19; ++i) {
            for (int j = 0; j < shuffleKey.length; ++j) {
                shuffleKey[j] = (byte)(key[j] ^ i);
            }
            this.initEncryption(rc4, this.createRC4Key(shuffleKey));
            this.cryptInPlace(rc4, shuffle);
        }
    }

    private void rc4unshuffle(Cipher rc4, byte[] shuffle, byte[] key) throws GeneralSecurityException {
        byte[] shuffleKeyBytes = new byte[key.length];
        for (int i = 19; i >= 0; --i) {
            for (int j = 0; j < shuffleKeyBytes.length; ++j) {
                shuffleKeyBytes[j] = (byte)(key[j] ^ i);
            }
            this.initDecryption(rc4, this.createRC4Key(shuffleKeyBytes));
            this.cryptInPlace(rc4, shuffle);
        }
    }

    private void cryptInPlace(Cipher rc4, byte[] buffer) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        rc4.doFinal(buffer, 0, buffer.length, buffer);
    }

    private void initDecryption(Cipher cipher, Key aKey) throws InvalidKeyException {
        cipher.init(2, aKey);
    }

    private Cipher createRC4Cipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance("RC4");
    }

    private Cipher createAESCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance(CIPHER_AES);
    }

    private MessageDigest createMD5Digest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5");
    }

    private SecretKeySpec createRC4Key(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "RC4");
    }

    private void digestTo(MessageDigest md5, byte[] hash) throws GeneralSecurityException {
        md5.digest(hash, 0, hash.length);
    }

    public static enum EncryptionAlgorithm {
        RC4,
        AESV2;


        boolean isRC4() {
            return this == RC4;
        }

        boolean isAES() {
            return this == AESV2;
        }
    }
}

