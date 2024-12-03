/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.MessageDigests;
import org.apache.pdfbox.pdmodel.encryption.PDCryptFilterDictionary;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.encryption.SaslPrep;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.util.Charsets;

public final class StandardSecurityHandler
extends SecurityHandler {
    private static final Log LOG = LogFactory.getLog(StandardSecurityHandler.class);
    public static final String FILTER = "Standard";
    public static final Class<?> PROTECTION_POLICY_CLASS = StandardProtectionPolicy.class;
    private static final byte[] ENCRYPT_PADDING = new byte[]{40, -65, 78, 94, 78, 117, -118, 65, 100, 0, 78, 86, -1, -6, 1, 8, 46, 46, 0, -74, -48, 104, 62, -128, 47, 12, -87, -2, 100, 83, 105, 122};
    private static final String[] HASHES_2B = new String[]{"SHA-256", "SHA-384", "SHA-512"};

    public StandardSecurityHandler() {
    }

    public StandardSecurityHandler(StandardProtectionPolicy standardProtectionPolicy) {
        this.setProtectionPolicy(standardProtectionPolicy);
        this.setKeyLength(standardProtectionPolicy.getEncryptionKeyLength());
    }

    private int computeRevisionNumber(int version) {
        StandardProtectionPolicy protectionPolicy = (StandardProtectionPolicy)this.getProtectionPolicy();
        AccessPermission permissions = protectionPolicy.getPermissions();
        if (version < 2 && !permissions.hasAnyRevision3PermissionSet()) {
            return 2;
        }
        if (version == 5) {
            return 6;
        }
        if (version == 4) {
            return 4;
        }
        if (version == 2 || version == 3 || permissions.hasAnyRevision3PermissionSet()) {
            return 3;
        }
        return 4;
    }

    @Override
    public void prepareForDecryption(PDEncryption encryption, COSArray documentIDArray, DecryptionMaterial decryptionMaterial) throws IOException {
        PDCryptFilterDictionary stdCryptFilterDictionary;
        int dicLength;
        if (!(decryptionMaterial instanceof StandardDecryptionMaterial)) {
            throw new IOException("Decryption material is not compatible with the document");
        }
        if (encryption.getVersion() >= 4) {
            this.setStreamFilterName(encryption.getStreamFilterName());
            this.setStringFilterName(encryption.getStringFilterName());
        }
        this.setDecryptMetadata(encryption.isEncryptMetaData());
        StandardDecryptionMaterial material = (StandardDecryptionMaterial)decryptionMaterial;
        String password = material.getPassword();
        if (password == null) {
            password = "";
        }
        int dicPermissions = encryption.getPermissions();
        int dicRevision = encryption.getRevision();
        int n = dicLength = encryption.getVersion() == 1 ? 5 : encryption.getLength() / 8;
        if ((encryption.getVersion() == 4 || encryption.getVersion() == 5) && (stdCryptFilterDictionary = encryption.getStdCryptFilterDictionary()) != null) {
            int newLength;
            COSName cryptFilterMethod = stdCryptFilterDictionary.getCryptFilterMethod();
            if (COSName.AESV2.equals(cryptFilterMethod)) {
                dicLength = 16;
                this.setAES(true);
                if (encryption.getCOSObject().containsKey(COSName.LENGTH) && (newLength = encryption.getLength() / 8) < dicLength) {
                    LOG.warn((Object)("Using " + newLength + " bytes key length instead of " + dicLength + " in AESV2 encryption?!"));
                    dicLength = newLength;
                }
            }
            if (COSName.AESV3.equals(cryptFilterMethod)) {
                dicLength = 32;
                this.setAES(true);
                if (encryption.getCOSObject().containsKey(COSName.LENGTH) && (newLength = encryption.getLength() / 8) < dicLength) {
                    LOG.warn((Object)("Using " + newLength + " bytes key length instead of " + dicLength + " in AESV3 encryption?!"));
                    dicLength = newLength;
                }
            }
        }
        byte[] documentIDBytes = this.getDocumentIDBytes(documentIDArray);
        boolean encryptMetadata = encryption.isEncryptMetaData();
        byte[] userKey = encryption.getUserKey();
        byte[] ownerKey = encryption.getOwnerKey();
        byte[] ue = null;
        byte[] oe = null;
        Charset passwordCharset = Charsets.ISO_8859_1;
        if (dicRevision == 6 || dicRevision == 5) {
            passwordCharset = Charsets.UTF_8;
            ue = encryption.getUserEncryptionKey();
            oe = encryption.getOwnerEncryptionKey();
        }
        if (dicRevision == 6) {
            password = SaslPrep.saslPrepQuery(password);
        }
        if (this.isOwnerPassword(password.getBytes(passwordCharset), userKey, ownerKey, dicPermissions, documentIDBytes, dicRevision, dicLength, encryptMetadata)) {
            AccessPermission currentAccessPermission = AccessPermission.getOwnerAccessPermission();
            this.setCurrentAccessPermission(currentAccessPermission);
            byte[] computedPassword = dicRevision == 6 || dicRevision == 5 ? password.getBytes(passwordCharset) : this.getUserPassword(password.getBytes(passwordCharset), ownerKey, dicRevision, dicLength);
            this.setEncryptionKey(this.computeEncryptedKey(computedPassword, ownerKey, userKey, oe, ue, dicPermissions, documentIDBytes, dicRevision, dicLength, encryptMetadata, true));
        } else if (this.isUserPassword(password.getBytes(passwordCharset), userKey, ownerKey, dicPermissions, documentIDBytes, dicRevision, dicLength, encryptMetadata)) {
            AccessPermission currentAccessPermission = new AccessPermission(dicPermissions);
            currentAccessPermission.setReadOnly();
            this.setCurrentAccessPermission(currentAccessPermission);
            this.setEncryptionKey(this.computeEncryptedKey(password.getBytes(passwordCharset), ownerKey, userKey, oe, ue, dicPermissions, documentIDBytes, dicRevision, dicLength, encryptMetadata, false));
        } else {
            throw new InvalidPasswordException("Cannot decrypt PDF, the password is incorrect");
        }
        if (dicRevision == 6 || dicRevision == 5) {
            this.validatePerms(encryption, dicPermissions, encryptMetadata);
        }
    }

    private byte[] getDocumentIDBytes(COSArray documentIDArray) {
        byte[] documentIDBytes;
        if (documentIDArray != null && documentIDArray.size() >= 1) {
            COSString id = (COSString)documentIDArray.getObject(0);
            documentIDBytes = id.getBytes();
        } else {
            documentIDBytes = new byte[]{};
        }
        return documentIDBytes;
    }

    private void validatePerms(PDEncryption encryption, int dicPermissions, boolean encryptMetadata) throws IOException {
        try {
            int permsP;
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(2, new SecretKeySpec(this.getEncryptionKey(), "AES"));
            byte[] perms = cipher.doFinal(encryption.getPerms());
            if (perms[9] != 97 || perms[10] != 100 || perms[11] != 98) {
                LOG.warn((Object)"Verification of permissions failed (constant)");
            }
            if ((permsP = perms[0] & 0xFF | (perms[1] & 0xFF) << 8 | (perms[2] & 0xFF) << 16 | (perms[3] & 0xFF) << 24) != dicPermissions) {
                LOG.warn((Object)("Verification of permissions failed (" + String.format("%08X", permsP) + " != " + String.format("%08X", dicPermissions) + ")"));
            }
            if (encryptMetadata && perms[8] != 84 || !encryptMetadata && perms[8] != 70) {
                LOG.warn((Object)"Verification of permissions failed (EncryptMetadata)");
            }
        }
        catch (GeneralSecurityException e) {
            StandardSecurityHandler.logIfStrongEncryptionMissing();
            throw new IOException(e);
        }
    }

    @Override
    public void prepareDocumentForEncryption(PDDocument document) throws IOException {
        PDEncryption encryptionDictionary = document.getEncryption();
        if (encryptionDictionary == null) {
            encryptionDictionary = new PDEncryption();
        }
        int version = this.computeVersionNumber();
        int revision = this.computeRevisionNumber(version);
        encryptionDictionary.setFilter(FILTER);
        encryptionDictionary.setVersion(version);
        if (version != 4 && version != 5) {
            encryptionDictionary.removeV45filters();
        }
        encryptionDictionary.setRevision(revision);
        encryptionDictionary.setLength(this.getKeyLength());
        StandardProtectionPolicy protectionPolicy = (StandardProtectionPolicy)this.getProtectionPolicy();
        String ownerPassword = protectionPolicy.getOwnerPassword();
        String userPassword = protectionPolicy.getUserPassword();
        if (ownerPassword == null) {
            ownerPassword = "";
        }
        if (userPassword == null) {
            userPassword = "";
        }
        if (ownerPassword.isEmpty()) {
            ownerPassword = userPassword;
        }
        int permissionInt = protectionPolicy.getPermissions().getPermissionBytes();
        encryptionDictionary.setPermissions(permissionInt);
        int length = this.getKeyLength() / 8;
        if (revision == 6) {
            ownerPassword = SaslPrep.saslPrepStored(ownerPassword);
            userPassword = SaslPrep.saslPrepStored(userPassword);
            this.prepareEncryptionDictRev6(ownerPassword, userPassword, encryptionDictionary, permissionInt);
        } else {
            this.prepareEncryptionDictRev2345(ownerPassword, userPassword, encryptionDictionary, permissionInt, document, revision, length);
        }
        document.setEncryptionDictionary(encryptionDictionary);
        document.getDocument().setEncryptionDictionary(encryptionDictionary.getCOSObject());
    }

    private void prepareEncryptionDictRev6(String ownerPassword, String userPassword, PDEncryption encryptionDictionary, int permissionInt) throws IOException {
        try {
            SecureRandom rnd = new SecureRandom();
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            this.setEncryptionKey(new byte[32]);
            rnd.nextBytes(this.getEncryptionKey());
            byte[] userPasswordBytes = StandardSecurityHandler.truncate127(userPassword.getBytes(Charsets.UTF_8));
            byte[] userValidationSalt = new byte[8];
            byte[] userKeySalt = new byte[8];
            rnd.nextBytes(userValidationSalt);
            rnd.nextBytes(userKeySalt);
            byte[] hashU = StandardSecurityHandler.computeHash2B(StandardSecurityHandler.concat(userPasswordBytes, userValidationSalt), userPasswordBytes, null);
            byte[] u = StandardSecurityHandler.concat(hashU, userValidationSalt, userKeySalt);
            byte[] hashUE = StandardSecurityHandler.computeHash2B(StandardSecurityHandler.concat(userPasswordBytes, userKeySalt), userPasswordBytes, null);
            cipher.init(1, (Key)new SecretKeySpec(hashUE, "AES"), new IvParameterSpec(new byte[16]));
            byte[] ue = cipher.doFinal(this.getEncryptionKey());
            byte[] ownerPasswordBytes = StandardSecurityHandler.truncate127(ownerPassword.getBytes(Charsets.UTF_8));
            byte[] ownerValidationSalt = new byte[8];
            byte[] ownerKeySalt = new byte[8];
            rnd.nextBytes(ownerValidationSalt);
            rnd.nextBytes(ownerKeySalt);
            byte[] hashO = StandardSecurityHandler.computeHash2B(StandardSecurityHandler.concat(ownerPasswordBytes, ownerValidationSalt, u), ownerPasswordBytes, u);
            byte[] o = StandardSecurityHandler.concat(hashO, ownerValidationSalt, ownerKeySalt);
            byte[] hashOE = StandardSecurityHandler.computeHash2B(StandardSecurityHandler.concat(ownerPasswordBytes, ownerKeySalt, u), ownerPasswordBytes, u);
            cipher.init(1, (Key)new SecretKeySpec(hashOE, "AES"), new IvParameterSpec(new byte[16]));
            byte[] oe = cipher.doFinal(this.getEncryptionKey());
            encryptionDictionary.setUserKey(u);
            encryptionDictionary.setUserEncryptionKey(ue);
            encryptionDictionary.setOwnerKey(o);
            encryptionDictionary.setOwnerEncryptionKey(oe);
            this.prepareEncryptionDictAES(encryptionDictionary, COSName.AESV3);
            byte[] perms = new byte[16];
            perms[0] = (byte)permissionInt;
            perms[1] = (byte)(permissionInt >>> 8);
            perms[2] = (byte)(permissionInt >>> 16);
            perms[3] = (byte)(permissionInt >>> 24);
            perms[4] = -1;
            perms[5] = -1;
            perms[6] = -1;
            perms[7] = -1;
            perms[8] = 84;
            perms[9] = 97;
            perms[10] = 100;
            perms[11] = 98;
            for (int i = 12; i <= 15; ++i) {
                perms[i] = (byte)rnd.nextInt();
            }
            cipher.init(1, (Key)new SecretKeySpec(this.getEncryptionKey(), "AES"), new IvParameterSpec(new byte[16]));
            byte[] permsEnc = cipher.doFinal(perms);
            encryptionDictionary.setPerms(permsEnc);
        }
        catch (GeneralSecurityException e) {
            StandardSecurityHandler.logIfStrongEncryptionMissing();
            throw new IOException(e);
        }
    }

    private void prepareEncryptionDictRev2345(String ownerPassword, String userPassword, PDEncryption encryptionDictionary, int permissionInt, PDDocument document, int revision, int length) throws IOException {
        COSArray idArray = document.getDocument().getDocumentID();
        if (idArray == null || idArray.size() < 2) {
            MessageDigest md = MessageDigests.getMD5();
            BigInteger time = BigInteger.valueOf(System.currentTimeMillis());
            md.update(time.toByteArray());
            md.update(ownerPassword.getBytes(Charsets.ISO_8859_1));
            md.update(userPassword.getBytes(Charsets.ISO_8859_1));
            md.update(document.getDocument().toString().getBytes(Charsets.ISO_8859_1));
            byte[] id = md.digest(this.toString().getBytes(Charsets.ISO_8859_1));
            COSString idString = new COSString(id);
            idArray = new COSArray();
            idArray.add(idString);
            idArray.add(idString);
            document.getDocument().setDocumentID(idArray);
        }
        COSString id = (COSString)idArray.getObject(0);
        byte[] ownerBytes = this.computeOwnerPassword(ownerPassword.getBytes(Charsets.ISO_8859_1), userPassword.getBytes(Charsets.ISO_8859_1), revision, length);
        byte[] userBytes = this.computeUserPassword(userPassword.getBytes(Charsets.ISO_8859_1), ownerBytes, permissionInt, id.getBytes(), revision, length, true);
        this.setEncryptionKey(this.computeEncryptedKey(userPassword.getBytes(Charsets.ISO_8859_1), ownerBytes, null, null, null, permissionInt, id.getBytes(), revision, length, true, false));
        encryptionDictionary.setOwnerKey(ownerBytes);
        encryptionDictionary.setUserKey(userBytes);
        if (revision == 4) {
            this.prepareEncryptionDictAES(encryptionDictionary, COSName.AESV2);
        }
    }

    private void prepareEncryptionDictAES(PDEncryption encryptionDictionary, COSName aesVName) {
        PDCryptFilterDictionary cryptFilterDictionary = new PDCryptFilterDictionary();
        cryptFilterDictionary.setCryptFilterMethod(aesVName);
        cryptFilterDictionary.setLength(this.getKeyLength());
        encryptionDictionary.setStdCryptFilterDictionary(cryptFilterDictionary);
        encryptionDictionary.setStreamFilterName(COSName.STD_CF);
        encryptionDictionary.setStringFilterName(COSName.STD_CF);
        this.setAES(true);
    }

    public boolean isOwnerPassword(byte[] ownerPassword, byte[] user, byte[] owner, int permissions, byte[] id, int encRevision, int keyLengthInBytes, boolean encryptMetadata) throws IOException {
        if (encRevision == 6 || encRevision == 5) {
            byte[] truncatedOwnerPassword = StandardSecurityHandler.truncate127(ownerPassword);
            byte[] oHash = new byte[32];
            byte[] oValidationSalt = new byte[8];
            if (owner.length < 40) {
                throw new IOException("Owner password is too short");
            }
            System.arraycopy(owner, 0, oHash, 0, 32);
            System.arraycopy(owner, 32, oValidationSalt, 0, 8);
            byte[] hash = encRevision == 5 ? StandardSecurityHandler.computeSHA256(truncatedOwnerPassword, oValidationSalt, user) : this.computeHash2A(truncatedOwnerPassword, oValidationSalt, user);
            return Arrays.equals(hash, oHash);
        }
        byte[] userPassword = this.getUserPassword(ownerPassword, owner, encRevision, keyLengthInBytes);
        return this.isUserPassword(userPassword, user, owner, permissions, id, encRevision, keyLengthInBytes, encryptMetadata);
    }

    public byte[] getUserPassword(byte[] ownerPassword, byte[] owner, int encRevision, int length) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] rc4Key = this.computeRC4key(ownerPassword, encRevision, length);
        if (encRevision == 2) {
            this.encryptDataRC4(rc4Key, owner, (OutputStream)result);
        } else if (encRevision == 3 || encRevision == 4) {
            byte[] iterationKey = new byte[rc4Key.length];
            byte[] otemp = new byte[owner.length];
            System.arraycopy(owner, 0, otemp, 0, owner.length);
            for (int i = 19; i >= 0; --i) {
                System.arraycopy(rc4Key, 0, iterationKey, 0, rc4Key.length);
                for (int j = 0; j < iterationKey.length; ++j) {
                    iterationKey[j] = (byte)(iterationKey[j] ^ (byte)i);
                }
                result.reset();
                this.encryptDataRC4(iterationKey, otemp, (OutputStream)result);
                otemp = result.toByteArray();
            }
        }
        return result.toByteArray();
    }

    public byte[] computeEncryptedKey(byte[] password, byte[] o, byte[] u, byte[] oe, byte[] ue, int permissions, byte[] id, int encRevision, int keyLengthInBytes, boolean encryptMetadata, boolean isOwnerPassword) throws IOException {
        if (encRevision == 6 || encRevision == 5) {
            return this.computeEncryptedKeyRev56(password, isOwnerPassword, o, u, oe, ue, encRevision);
        }
        return this.computeEncryptedKeyRev234(password, o, permissions, id, encryptMetadata, keyLengthInBytes, encRevision);
    }

    private byte[] computeEncryptedKeyRev234(byte[] password, byte[] o, int permissions, byte[] id, boolean encryptMetadata, int length, int encRevision) {
        byte[] padded = this.truncateOrPad(password);
        MessageDigest md = MessageDigests.getMD5();
        md.update(padded);
        md.update(o);
        md.update((byte)permissions);
        md.update((byte)(permissions >>> 8));
        md.update((byte)(permissions >>> 16));
        md.update((byte)(permissions >>> 24));
        md.update(id);
        if (encRevision == 4 && !encryptMetadata) {
            md.update(new byte[]{-1, -1, -1, -1});
        }
        byte[] digest = md.digest();
        if (encRevision == 3 || encRevision == 4) {
            for (int i = 0; i < 50; ++i) {
                md.update(digest, 0, length);
                digest = md.digest();
            }
        }
        byte[] result = new byte[length];
        System.arraycopy(digest, 0, result, 0, length);
        return result;
    }

    private byte[] computeEncryptedKeyRev56(byte[] password, boolean isOwnerPassword, byte[] o, byte[] u, byte[] oe, byte[] ue, int encRevision) throws IOException {
        byte[] fileKeyEnc;
        byte[] hash;
        if (isOwnerPassword) {
            if (oe == null) {
                throw new IOException("/Encrypt/OE entry is missing");
            }
            byte[] oKeySalt = new byte[8];
            System.arraycopy(o, 40, oKeySalt, 0, 8);
            hash = encRevision == 5 ? StandardSecurityHandler.computeSHA256(password, oKeySalt, u) : this.computeHash2A(password, oKeySalt, u);
            fileKeyEnc = oe;
        } else {
            if (ue == null) {
                throw new IOException("/Encrypt/UE entry is missing");
            }
            byte[] uKeySalt = new byte[8];
            System.arraycopy(u, 40, uKeySalt, 0, 8);
            hash = encRevision == 5 ? StandardSecurityHandler.computeSHA256(password, uKeySalt, null) : this.computeHash2A(password, uKeySalt, null);
            fileKeyEnc = ue;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(2, (Key)new SecretKeySpec(hash, "AES"), new IvParameterSpec(new byte[16]));
            return cipher.doFinal(fileKeyEnc);
        }
        catch (GeneralSecurityException e) {
            StandardSecurityHandler.logIfStrongEncryptionMissing();
            throw new IOException(e);
        }
    }

    public byte[] computeUserPassword(byte[] password, byte[] owner, int permissions, byte[] id, int encRevision, int keyLengthInBytes, boolean encryptMetadata) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] encKey = this.computeEncryptedKey(password, owner, null, null, null, permissions, id, encRevision, keyLengthInBytes, encryptMetadata, true);
        if (encRevision == 2) {
            this.encryptDataRC4(encKey, ENCRYPT_PADDING, (OutputStream)result);
        } else if (encRevision == 3 || encRevision == 4) {
            MessageDigest md = MessageDigests.getMD5();
            md.update(ENCRYPT_PADDING);
            md.update(id);
            result.write(md.digest());
            byte[] iterationKey = new byte[encKey.length];
            for (int i = 0; i < 20; ++i) {
                System.arraycopy(encKey, 0, iterationKey, 0, iterationKey.length);
                for (int j = 0; j < iterationKey.length; ++j) {
                    iterationKey[j] = (byte)(iterationKey[j] ^ i);
                }
                ByteArrayInputStream input = new ByteArrayInputStream(result.toByteArray());
                result.reset();
                this.encryptDataRC4(iterationKey, input, (OutputStream)result);
            }
            byte[] finalResult = new byte[32];
            System.arraycopy(result.toByteArray(), 0, finalResult, 0, 16);
            System.arraycopy(ENCRYPT_PADDING, 0, finalResult, 16, 16);
            result.reset();
            result.write(finalResult);
        }
        return result.toByteArray();
    }

    public byte[] computeOwnerPassword(byte[] ownerPassword, byte[] userPassword, int encRevision, int length) throws IOException {
        if (encRevision == 2 && length != 5) {
            throw new IOException("Expected length=5 actual=" + length);
        }
        byte[] rc4Key = this.computeRC4key(ownerPassword, encRevision, length);
        byte[] paddedUser = this.truncateOrPad(userPassword);
        ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
        this.encryptDataRC4(rc4Key, new ByteArrayInputStream(paddedUser), (OutputStream)encrypted);
        if (encRevision == 3 || encRevision == 4) {
            byte[] iterationKey = new byte[rc4Key.length];
            for (int i = 1; i < 20; ++i) {
                System.arraycopy(rc4Key, 0, iterationKey, 0, rc4Key.length);
                for (int j = 0; j < iterationKey.length; ++j) {
                    iterationKey[j] = (byte)(iterationKey[j] ^ (byte)i);
                }
                ByteArrayInputStream input = new ByteArrayInputStream(encrypted.toByteArray());
                encrypted.reset();
                this.encryptDataRC4(iterationKey, input, (OutputStream)encrypted);
            }
        }
        return encrypted.toByteArray();
    }

    private byte[] computeRC4key(byte[] ownerPassword, int encRevision, int length) {
        MessageDigest md = MessageDigests.getMD5();
        byte[] digest = md.digest(this.truncateOrPad(ownerPassword));
        if (encRevision == 3 || encRevision == 4) {
            for (int i = 0; i < 50; ++i) {
                md.update(digest, 0, length);
                digest = md.digest();
            }
        }
        byte[] rc4Key = new byte[length];
        System.arraycopy(digest, 0, rc4Key, 0, length);
        return rc4Key;
    }

    private byte[] truncateOrPad(byte[] password) {
        byte[] padded = new byte[ENCRYPT_PADDING.length];
        int bytesBeforePad = Math.min(password.length, padded.length);
        System.arraycopy(password, 0, padded, 0, bytesBeforePad);
        System.arraycopy(ENCRYPT_PADDING, 0, padded, bytesBeforePad, ENCRYPT_PADDING.length - bytesBeforePad);
        return padded;
    }

    public boolean isUserPassword(byte[] password, byte[] user, byte[] owner, int permissions, byte[] id, int encRevision, int keyLengthInBytes, boolean encryptMetadata) throws IOException {
        switch (encRevision) {
            case 2: 
            case 3: 
            case 4: {
                return this.isUserPassword234(password, user, owner, permissions, id, encRevision, keyLengthInBytes, encryptMetadata);
            }
            case 5: 
            case 6: {
                return this.isUserPassword56(password, user, encRevision);
            }
        }
        throw new IOException("Unknown Encryption Revision " + encRevision);
    }

    private boolean isUserPassword234(byte[] password, byte[] user, byte[] owner, int permissions, byte[] id, int encRevision, int length, boolean encryptMetadata) throws IOException {
        byte[] passwordBytes = this.computeUserPassword(password, owner, permissions, id, encRevision, length, encryptMetadata);
        if (encRevision == 2) {
            return Arrays.equals(user, passwordBytes);
        }
        return Arrays.equals(Arrays.copyOf(user, 16), Arrays.copyOf(passwordBytes, 16));
    }

    private boolean isUserPassword56(byte[] password, byte[] user, int encRevision) throws IOException {
        byte[] truncatedPassword = StandardSecurityHandler.truncate127(password);
        byte[] uHash = new byte[32];
        byte[] uValidationSalt = new byte[8];
        System.arraycopy(user, 0, uHash, 0, 32);
        System.arraycopy(user, 32, uValidationSalt, 0, 8);
        byte[] hash = encRevision == 5 ? StandardSecurityHandler.computeSHA256(truncatedPassword, uValidationSalt, null) : this.computeHash2A(truncatedPassword, uValidationSalt, null);
        return Arrays.equals(hash, uHash);
    }

    public boolean isUserPassword(String password, byte[] user, byte[] owner, int permissions, byte[] id, int encRevision, int keyLengthInBytes, boolean encryptMetadata) throws IOException {
        if (encRevision == 6 || encRevision == 5) {
            return this.isUserPassword(password.getBytes(Charsets.UTF_8), user, owner, permissions, id, encRevision, keyLengthInBytes, encryptMetadata);
        }
        return this.isUserPassword(password.getBytes(Charsets.ISO_8859_1), user, owner, permissions, id, encRevision, keyLengthInBytes, encryptMetadata);
    }

    public boolean isOwnerPassword(String password, byte[] user, byte[] owner, int permissions, byte[] id, int encRevision, int keyLengthInBytes, boolean encryptMetadata) throws IOException {
        return this.isOwnerPassword(password.getBytes(Charsets.ISO_8859_1), user, owner, permissions, id, encRevision, keyLengthInBytes, encryptMetadata);
    }

    private byte[] computeHash2A(byte[] password, byte[] salt, byte[] u) throws IOException {
        byte[] userKey = StandardSecurityHandler.adjustUserKey(u);
        byte[] truncatedPassword = StandardSecurityHandler.truncate127(password);
        byte[] input = StandardSecurityHandler.concat(truncatedPassword, salt, userKey);
        return StandardSecurityHandler.computeHash2B(input, truncatedPassword, userKey);
    }

    private static byte[] computeHash2B(byte[] input, byte[] password, byte[] userKey) throws IOException {
        try {
            MessageDigest md = MessageDigests.getSHA256();
            byte[] k = md.digest(input);
            byte[] e = null;
            for (int round = 0; round < 64 || (e[e.length - 1] & 0xFF) > round - 32; ++round) {
                byte[] k1 = userKey != null && userKey.length >= 48 ? new byte[64 * (password.length + k.length + 48)] : new byte[64 * (password.length + k.length)];
                int pos = 0;
                for (int i = 0; i < 64; ++i) {
                    System.arraycopy(password, 0, k1, pos, password.length);
                    System.arraycopy(k, 0, k1, pos += password.length, k.length);
                    pos += k.length;
                    if (userKey == null || userKey.length < 48) continue;
                    System.arraycopy(userKey, 0, k1, pos, 48);
                    pos += 48;
                }
                byte[] kFirst = new byte[16];
                byte[] kSecond = new byte[16];
                System.arraycopy(k, 0, kFirst, 0, 16);
                System.arraycopy(k, 16, kSecond, 0, 16);
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                SecretKeySpec keySpec = new SecretKeySpec(kFirst, "AES");
                IvParameterSpec ivSpec = new IvParameterSpec(kSecond);
                cipher.init(1, (Key)keySpec, ivSpec);
                e = cipher.doFinal(k1);
                byte[] eFirst = new byte[16];
                System.arraycopy(e, 0, eFirst, 0, 16);
                BigInteger bi = new BigInteger(1, eFirst);
                BigInteger remainder = bi.mod(new BigInteger("3"));
                String nextHash = HASHES_2B[remainder.intValue()];
                md = MessageDigest.getInstance(nextHash);
                k = md.digest(e);
            }
            if (k.length > 32) {
                byte[] kTrunc = new byte[32];
                System.arraycopy(k, 0, kTrunc, 0, 32);
                return kTrunc;
            }
            return k;
        }
        catch (GeneralSecurityException e) {
            StandardSecurityHandler.logIfStrongEncryptionMissing();
            throw new IOException(e);
        }
    }

    private static byte[] computeSHA256(byte[] input, byte[] password, byte[] userKey) throws IOException {
        MessageDigest md = MessageDigests.getSHA256();
        md.update(input);
        md.update(password);
        return md.digest(StandardSecurityHandler.adjustUserKey(userKey));
    }

    private static byte[] adjustUserKey(byte[] u) throws IOException {
        if (u == null) {
            return new byte[0];
        }
        if (u.length < 48) {
            throw new IOException("Bad U length");
        }
        if (u.length > 48) {
            byte[] userKey = new byte[48];
            System.arraycopy(u, 0, userKey, 0, 48);
            return userKey;
        }
        return u;
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] o = new byte[a.length + b.length];
        System.arraycopy(a, 0, o, 0, a.length);
        System.arraycopy(b, 0, o, a.length, b.length);
        return o;
    }

    private static byte[] concat(byte[] a, byte[] b, byte[] c) {
        byte[] o = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, o, 0, a.length);
        System.arraycopy(b, 0, o, a.length, b.length);
        System.arraycopy(c, 0, o, a.length + b.length, c.length);
        return o;
    }

    private static byte[] truncate127(byte[] in) {
        if (in.length <= 127) {
            return in;
        }
        byte[] trunc = new byte[127];
        System.arraycopy(in, 0, trunc, 0, 127);
        return trunc;
    }

    private static void logIfStrongEncryptionMissing() {
        try {
            if (Cipher.getMaxAllowedKeyLength("AES") != Integer.MAX_VALUE) {
                LOG.warn((Object)"JCE unlimited strength jurisdiction policy files are not installed");
            }
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            // empty catch block
        }
    }
}

