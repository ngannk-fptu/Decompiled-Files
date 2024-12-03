/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.OutputStreamEncryption;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPublicKeyRecipient;
import com.lowagie.text.pdf.PdfPublicKeySecurityHandler;
import com.lowagie.text.pdf.StandardDecryption;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class PdfEncryption {
    public static final int STANDARD_ENCRYPTION_40 = 2;
    public static final int STANDARD_ENCRYPTION_128 = 3;
    public static final int AES_128 = 4;
    private static final byte[] pad = new byte[]{40, -65, 78, 94, 78, 117, -118, 65, 100, 0, 78, 86, -1, -6, 1, 8, 46, 46, 0, -74, -48, 104, 62, -128, 47, 12, -87, -2, 100, 83, 105, 122};
    private static final byte[] salt = new byte[]{115, 65, 108, 84};
    private static final byte[] metadataPad = new byte[]{-1, -1, -1, -1};
    byte[] key;
    int keySize;
    byte[] mkey;
    byte[] extra = new byte[5];
    MessageDigest md5;
    byte[] ownerKey = new byte[32];
    byte[] userKey = new byte[32];
    protected PdfPublicKeySecurityHandler publicKeyHandler = null;
    int permissions;
    byte[] documentID;
    static long seq = System.currentTimeMillis();
    private int revision;
    private ARCFOUREncryption arcfour = new ARCFOUREncryption();
    private int keyLength;
    private boolean encryptMetadata;
    private boolean embeddedFilesOnly;
    private int cryptoMode;

    public PdfEncryption() {
        try {
            this.md5 = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        this.publicKeyHandler = new PdfPublicKeySecurityHandler();
    }

    public PdfEncryption(PdfEncryption enc) {
        this();
        this.mkey = (byte[])enc.mkey.clone();
        this.ownerKey = (byte[])enc.ownerKey.clone();
        this.userKey = (byte[])enc.userKey.clone();
        this.permissions = enc.permissions;
        if (enc.documentID != null) {
            this.documentID = (byte[])enc.documentID.clone();
        }
        this.revision = enc.revision;
        this.keyLength = enc.keyLength;
        this.encryptMetadata = enc.encryptMetadata;
        this.embeddedFilesOnly = enc.embeddedFilesOnly;
        this.publicKeyHandler = enc.publicKeyHandler;
    }

    public void setCryptoMode(int mode, int kl) {
        this.cryptoMode = mode;
        this.encryptMetadata = (mode & 8) == 0;
        this.embeddedFilesOnly = (mode & 0x18) != 0;
        switch (mode &= 7) {
            case 0: {
                this.encryptMetadata = true;
                this.embeddedFilesOnly = false;
                this.keyLength = 40;
                this.revision = 2;
                break;
            }
            case 1: {
                this.embeddedFilesOnly = false;
                this.keyLength = kl > 0 ? kl : 128;
                this.revision = 3;
                break;
            }
            case 2: {
                this.keyLength = 128;
                this.revision = 4;
                break;
            }
            default: {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("no.valid.encryption.mode"));
            }
        }
    }

    public int getCryptoMode() {
        return this.cryptoMode;
    }

    public boolean isMetadataEncrypted() {
        return this.encryptMetadata;
    }

    public boolean isEmbeddedFilesOnly() {
        return this.embeddedFilesOnly;
    }

    private byte[] padPassword(byte[] userPassword) {
        byte[] userPad = new byte[32];
        if (userPassword == null) {
            System.arraycopy(pad, 0, userPad, 0, 32);
        } else {
            System.arraycopy(userPassword, 0, userPad, 0, Math.min(userPassword.length, 32));
            if (userPassword.length < 32) {
                System.arraycopy(pad, 0, userPad, userPassword.length, 32 - userPassword.length);
            }
        }
        return userPad;
    }

    private byte[] computeOwnerKey(byte[] userPad, byte[] ownerPad) {
        byte[] ownerKey = new byte[32];
        byte[] digest = this.md5.digest(ownerPad);
        if (this.revision == 3 || this.revision == 4) {
            byte[] mkey = new byte[this.keyLength / 8];
            for (int k = 0; k < 50; ++k) {
                System.arraycopy(this.md5.digest(digest), 0, digest, 0, mkey.length);
            }
            System.arraycopy(userPad, 0, ownerKey, 0, 32);
            for (int i = 0; i < 20; ++i) {
                for (int j = 0; j < mkey.length; ++j) {
                    mkey[j] = (byte)(digest[j] ^ i);
                }
                this.arcfour.prepareARCFOURKey(mkey);
                this.arcfour.encryptARCFOUR(ownerKey);
            }
        } else {
            this.arcfour.prepareARCFOURKey(digest, 0, 5);
            this.arcfour.encryptARCFOUR(userPad, ownerKey);
        }
        return ownerKey;
    }

    private void setupGlobalEncryptionKey(byte[] documentID, byte[] userPad, byte[] ownerKey, int permissions) {
        this.documentID = documentID;
        this.ownerKey = ownerKey;
        this.permissions = permissions;
        this.mkey = new byte[this.keyLength / 8];
        this.md5.reset();
        this.md5.update(userPad);
        this.md5.update(ownerKey);
        byte[] ext = new byte[]{(byte)permissions, (byte)(permissions >> 8), (byte)(permissions >> 16), (byte)(permissions >> 24)};
        this.md5.update(ext, 0, 4);
        if (documentID != null) {
            this.md5.update(documentID);
        }
        if (!this.encryptMetadata) {
            this.md5.update(metadataPad);
        }
        byte[] digest = new byte[this.mkey.length];
        System.arraycopy(this.md5.digest(), 0, digest, 0, this.mkey.length);
        if (this.revision == 3 || this.revision == 4) {
            for (int k = 0; k < 50; ++k) {
                System.arraycopy(this.md5.digest(digest), 0, digest, 0, this.mkey.length);
            }
        }
        System.arraycopy(digest, 0, this.mkey, 0, this.mkey.length);
    }

    private void setupUserKey() {
        if (this.revision == 3 || this.revision == 4) {
            this.md5.update(pad);
            byte[] digest = this.md5.digest(this.documentID);
            System.arraycopy(digest, 0, this.userKey, 0, 16);
            for (int k = 16; k < 32; ++k) {
                this.userKey[k] = 0;
            }
            for (int i = 0; i < 20; ++i) {
                for (int j = 0; j < this.mkey.length; ++j) {
                    digest[j] = (byte)(this.mkey[j] ^ i);
                }
                this.arcfour.prepareARCFOURKey(digest, 0, this.mkey.length);
                this.arcfour.encryptARCFOUR(this.userKey, 0, 16);
            }
        } else {
            this.arcfour.prepareARCFOURKey(this.mkey);
            this.arcfour.encryptARCFOUR(pad, this.userKey);
        }
    }

    public void setupAllKeys(byte[] userPassword, byte[] ownerPassword, int permissions) {
        if (ownerPassword == null || ownerPassword.length == 0) {
            ownerPassword = this.md5.digest(PdfEncryption.createDocumentId());
        }
        permissions |= this.revision == 3 || this.revision == 4 ? -3904 : -64;
        byte[] userPad = this.padPassword(userPassword);
        byte[] ownerPad = this.padPassword(ownerPassword);
        this.ownerKey = this.computeOwnerKey(userPad, ownerPad);
        this.documentID = PdfEncryption.createDocumentId();
        this.setupByUserPad(this.documentID, userPad, this.ownerKey, permissions &= 0xFFFFFFFC);
    }

    public static byte[] createDocumentId() {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        long time = System.currentTimeMillis();
        long mem = Runtime.getRuntime().freeMemory();
        String s = time + "+" + mem + "+" + seq++;
        return md5.digest(s.getBytes());
    }

    public void setupByUserPassword(byte[] documentID, byte[] userPassword, byte[] ownerKey, int permissions) {
        this.setupByUserPad(documentID, this.padPassword(userPassword), ownerKey, permissions);
    }

    private void setupByUserPad(byte[] documentID, byte[] userPad, byte[] ownerKey, int permissions) {
        this.setupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions);
        this.setupUserKey();
    }

    public void setupByOwnerPassword(byte[] documentID, byte[] ownerPassword, byte[] userKey, byte[] ownerKey, int permissions) {
        this.setupByOwnerPad(documentID, this.padPassword(ownerPassword), userKey, ownerKey, permissions);
    }

    private void setupByOwnerPad(byte[] documentID, byte[] ownerPad, byte[] userKey, byte[] ownerKey, int permissions) {
        byte[] userPad = this.computeOwnerKey(ownerKey, ownerPad);
        this.setupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions);
        this.setupUserKey();
    }

    public void setupByEncryptionKey(byte[] key, int keylength) {
        this.mkey = new byte[keylength / 8];
        System.arraycopy(key, 0, this.mkey, 0, this.mkey.length);
    }

    public void setHashKey(int number, int generation) {
        this.md5.reset();
        this.extra[0] = (byte)number;
        this.extra[1] = (byte)(number >> 8);
        this.extra[2] = (byte)(number >> 16);
        this.extra[3] = (byte)generation;
        this.extra[4] = (byte)(generation >> 8);
        this.md5.update(this.mkey);
        this.md5.update(this.extra);
        if (this.revision == 4) {
            this.md5.update(salt);
        }
        this.key = this.md5.digest();
        this.keySize = this.mkey.length + 5;
        if (this.keySize > 16) {
            this.keySize = 16;
        }
    }

    public static PdfObject createInfoId(byte[] id) {
        int k;
        ByteBuffer buf = new ByteBuffer(90);
        buf.append('[').append('<');
        for (k = 0; k < 16; ++k) {
            buf.appendHex(id[k]);
        }
        buf.append('>').append('<');
        id = PdfEncryption.createDocumentId();
        for (k = 0; k < 16; ++k) {
            buf.appendHex(id[k]);
        }
        buf.append('>').append(']');
        return new PdfLiteral(buf.toByteArray());
    }

    public PdfDictionary getEncryptionDictionary() {
        PdfDictionary dic = new PdfDictionary();
        if (this.publicKeyHandler.getRecipientsSize() > 0) {
            PdfArray recipients = null;
            dic.put(PdfName.FILTER, PdfName.PUBSEC);
            dic.put(PdfName.R, new PdfNumber(this.revision));
            try {
                recipients = this.publicKeyHandler.getEncodedRecipients();
            }
            catch (Exception f) {
                throw new ExceptionConverter(f);
            }
            if (this.revision == 2) {
                dic.put(PdfName.V, new PdfNumber(1));
                dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                dic.put(PdfName.RECIPIENTS, recipients);
            } else if (this.revision == 3 && this.encryptMetadata) {
                dic.put(PdfName.V, new PdfNumber(2));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
                dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                dic.put(PdfName.RECIPIENTS, recipients);
            } else {
                dic.put(PdfName.R, new PdfNumber(4));
                dic.put(PdfName.V, new PdfNumber(4));
                dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S5);
                PdfDictionary stdcf = new PdfDictionary();
                stdcf.put(PdfName.RECIPIENTS, recipients);
                if (!this.encryptMetadata) {
                    stdcf.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                }
                if (this.revision == 4) {
                    stdcf.put(PdfName.CFM, PdfName.AESV2);
                } else {
                    stdcf.put(PdfName.CFM, PdfName.V2);
                }
                PdfDictionary cf = new PdfDictionary();
                cf.put(PdfName.DEFAULTCRYPTFILTER, stdcf);
                dic.put(PdfName.CF, cf);
                if (this.embeddedFilesOnly) {
                    dic.put(PdfName.EFF, PdfName.DEFAULTCRYPTFILTER);
                    dic.put(PdfName.STRF, PdfName.IDENTITY);
                    dic.put(PdfName.STMF, PdfName.IDENTITY);
                } else {
                    dic.put(PdfName.STRF, PdfName.DEFAULTCRYPTFILTER);
                    dic.put(PdfName.STMF, PdfName.DEFAULTCRYPTFILTER);
                }
            }
            MessageDigest md = null;
            byte[] encodedRecipient = null;
            try {
                md = MessageDigest.getInstance("SHA-1");
                md.update(this.publicKeyHandler.getSeed());
                for (int i = 0; i < this.publicKeyHandler.getRecipientsSize(); ++i) {
                    encodedRecipient = this.publicKeyHandler.getEncodedRecipient(i);
                    md.update(encodedRecipient);
                }
                if (!this.encryptMetadata) {
                    md.update(new byte[]{-1, -1, -1, -1});
                }
            }
            catch (Exception f) {
                throw new ExceptionConverter(f);
            }
            byte[] mdResult = md.digest();
            this.setupByEncryptionKey(mdResult, this.keyLength);
        } else {
            dic.put(PdfName.FILTER, PdfName.STANDARD);
            dic.put(PdfName.O, new PdfLiteral(PdfContentByte.escapeString(this.ownerKey)));
            dic.put(PdfName.U, new PdfLiteral(PdfContentByte.escapeString(this.userKey)));
            dic.put(PdfName.P, new PdfNumber(this.permissions));
            dic.put(PdfName.R, new PdfNumber(this.revision));
            if (this.revision == 2) {
                dic.put(PdfName.V, new PdfNumber(1));
            } else if (this.revision == 3 && this.encryptMetadata) {
                dic.put(PdfName.V, new PdfNumber(2));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
            } else {
                if (!this.encryptMetadata) {
                    dic.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                }
                dic.put(PdfName.R, new PdfNumber(4));
                dic.put(PdfName.V, new PdfNumber(4));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
                PdfDictionary stdcf = new PdfDictionary();
                stdcf.put(PdfName.LENGTH, new PdfNumber(16));
                if (this.embeddedFilesOnly) {
                    stdcf.put(PdfName.AUTHEVENT, PdfName.EFOPEN);
                    dic.put(PdfName.EFF, PdfName.STDCF);
                    dic.put(PdfName.STRF, PdfName.IDENTITY);
                    dic.put(PdfName.STMF, PdfName.IDENTITY);
                } else {
                    stdcf.put(PdfName.AUTHEVENT, PdfName.DOCOPEN);
                    dic.put(PdfName.STRF, PdfName.STDCF);
                    dic.put(PdfName.STMF, PdfName.STDCF);
                }
                if (this.revision == 4) {
                    stdcf.put(PdfName.CFM, PdfName.AESV2);
                } else {
                    stdcf.put(PdfName.CFM, PdfName.V2);
                }
                PdfDictionary cf = new PdfDictionary();
                cf.put(PdfName.STDCF, stdcf);
                dic.put(PdfName.CF, cf);
            }
        }
        return dic;
    }

    public PdfObject getFileID() {
        return PdfEncryption.createInfoId(this.documentID);
    }

    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return new OutputStreamEncryption(os, this.key, 0, this.keySize, this.revision);
    }

    public int calculateStreamSize(int n) {
        if (this.revision == 4) {
            return (n & 0x7FFFFFF0) + 32;
        }
        return n;
    }

    public byte[] encryptByteArray(byte[] b) {
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            OutputStreamEncryption os2 = this.getEncryptionStream(ba);
            os2.write(b);
            os2.finish();
            return ba.toByteArray();
        }
        catch (IOException ex) {
            throw new ExceptionConverter(ex);
        }
    }

    public StandardDecryption getDecryptor() {
        return new StandardDecryption(this.key, 0, this.keySize, this.revision);
    }

    public byte[] decryptByteArray(byte[] b) {
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            StandardDecryption dec = this.getDecryptor();
            byte[] b2 = dec.update(b, 0, b.length);
            if (b2 != null) {
                ba.write(b2);
            }
            if ((b2 = dec.finish()) != null) {
                ba.write(b2);
            }
            return ba.toByteArray();
        }
        catch (IOException ex) {
            throw new ExceptionConverter(ex);
        }
    }

    public void addRecipient(Certificate cert, int permission) {
        this.documentID = PdfEncryption.createDocumentId();
        this.publicKeyHandler.addRecipient(new PdfPublicKeyRecipient(cert, permission));
    }

    public byte[] computeUserPassword(byte[] ownerPassword) {
        byte[] userPad = this.computeOwnerKey(this.ownerKey, this.padPassword(ownerPassword));
        for (int i = 0; i < userPad.length; ++i) {
            boolean match = true;
            for (int j = 0; j < userPad.length - i; ++j) {
                if (userPad[i + j] == pad[j]) continue;
                match = false;
                break;
            }
            if (!match) continue;
            byte[] userPassword = new byte[i];
            System.arraycopy(userPad, 0, userPassword, 0, i);
            return userPassword;
        }
        return userPad;
    }
}

