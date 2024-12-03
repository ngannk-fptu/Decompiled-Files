/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKey;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

class SQLServerAeadAes256CbcHmac256EncryptionKey
extends SQLServerSymmetricKey {
    static final int KEYSIZE = 256;
    static final int KEYSIZE_IN_BYTES = 32;
    private final String algorithmName;
    private String encryptionKeySaltFormat;
    private String macKeySaltFormat;
    private String ivKeySaltFormat;
    private SQLServerSymmetricKey encryptionKey;
    private SQLServerSymmetricKey macKey;
    private SQLServerSymmetricKey ivKey;

    SQLServerAeadAes256CbcHmac256EncryptionKey(byte[] rootKey, String algorithmName) throws SQLServerException {
        super(rootKey);
        this.algorithmName = algorithmName;
        this.encryptionKeySaltFormat = "Microsoft SQL Server cell encryption key with encryption algorithm:" + this.algorithmName + " and key length:256";
        this.macKeySaltFormat = "Microsoft SQL Server cell MAC key with encryption algorithm:" + this.algorithmName + " and key length:256";
        this.ivKeySaltFormat = "Microsoft SQL Server cell IV key with encryption algorithm:" + this.algorithmName + " and key length:256";
        if (rootKey.length != 32) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidKeySize"));
            Object[] msgArgs = new Object[]{rootKey.length, 32, this.algorithmName};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        byte[] encKeyBuff = new byte[32];
        try {
            encKeyBuff = SQLServerSecurityUtility.getHMACWithSHA256(this.encryptionKeySaltFormat.getBytes(StandardCharsets.UTF_16LE), rootKey, encKeyBuff.length);
            this.encryptionKey = new SQLServerSymmetricKey(encKeyBuff);
            byte[] macKeyBuff = new byte[32];
            macKeyBuff = SQLServerSecurityUtility.getHMACWithSHA256(this.macKeySaltFormat.getBytes(StandardCharsets.UTF_16LE), rootKey, macKeyBuff.length);
            this.macKey = new SQLServerSymmetricKey(macKeyBuff);
            byte[] ivKeyBuff = new byte[32];
            ivKeyBuff = SQLServerSecurityUtility.getHMACWithSHA256(this.ivKeySaltFormat.getBytes(StandardCharsets.UTF_16LE), rootKey, ivKeyBuff.length);
            this.ivKey = new SQLServerSymmetricKey(ivKeyBuff);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_KeyExtractionFailed"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
    }

    byte[] getEncryptionKey() {
        return this.encryptionKey.getRootKey();
    }

    byte[] getMacKey() {
        return this.macKey.getRootKey();
    }

    byte[] getIVKey() {
        return this.ivKey.getRootKey();
    }
}

