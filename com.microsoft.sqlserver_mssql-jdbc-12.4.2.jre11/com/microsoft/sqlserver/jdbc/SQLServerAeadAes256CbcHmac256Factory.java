/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256Algorithm;
import com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256EncryptionKey;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithm;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithmFactory;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKey;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

class SQLServerAeadAes256CbcHmac256Factory
extends SQLServerEncryptionAlgorithmFactory {
    private byte algorithmVersion = 1;
    private ConcurrentHashMap<String, SQLServerAeadAes256CbcHmac256Algorithm> encryptionAlgorithms = new ConcurrentHashMap();

    SQLServerAeadAes256CbcHmac256Factory() {
    }

    @Override
    SQLServerEncryptionAlgorithm create(SQLServerSymmetricKey columnEncryptionKey, SQLServerEncryptionType encryptionType, String encryptionAlgorithm) throws SQLServerException {
        assert (columnEncryptionKey != null);
        if (encryptionType != SQLServerEncryptionType.DETERMINISTIC && encryptionType != SQLServerEncryptionType.RANDOMIZED) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEncryptionType"));
            Object[] msgArgs = new Object[]{encryptionType, encryptionAlgorithm, "'" + SQLServerEncryptionType.DETERMINISTIC + "," + SQLServerEncryptionType.RANDOMIZED + "'"};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        StringBuilder factoryKeyBuilder = new StringBuilder();
        factoryKeyBuilder.append(Base64.getEncoder().encodeToString(new String(columnEncryptionKey.getRootKey(), StandardCharsets.UTF_8).getBytes()));
        factoryKeyBuilder.append(":");
        factoryKeyBuilder.append((Object)encryptionType);
        factoryKeyBuilder.append(":");
        factoryKeyBuilder.append(this.algorithmVersion);
        String factoryKey = factoryKeyBuilder.toString();
        if (!this.encryptionAlgorithms.containsKey(factoryKey)) {
            SQLServerAeadAes256CbcHmac256EncryptionKey encryptedKey = new SQLServerAeadAes256CbcHmac256EncryptionKey(columnEncryptionKey.getRootKey(), "AEAD_AES_256_CBC_HMAC_SHA256");
            SQLServerAeadAes256CbcHmac256Algorithm aesAlgorithm = new SQLServerAeadAes256CbcHmac256Algorithm(encryptedKey, encryptionType, this.algorithmVersion);
            this.encryptionAlgorithms.putIfAbsent(factoryKey, aesAlgorithm);
        }
        return this.encryptionAlgorithms.get(factoryKey);
    }
}

