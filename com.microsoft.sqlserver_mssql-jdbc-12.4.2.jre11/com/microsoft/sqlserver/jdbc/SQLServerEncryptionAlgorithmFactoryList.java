/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerAeadAes256CbcHmac256Factory;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithm;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithmFactory;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKey;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

final class SQLServerEncryptionAlgorithmFactoryList {
    private ConcurrentHashMap<String, SQLServerEncryptionAlgorithmFactory> encryptionAlgoFactoryMap = new ConcurrentHashMap();
    private static final SQLServerEncryptionAlgorithmFactoryList instance = new SQLServerEncryptionAlgorithmFactoryList();

    private SQLServerEncryptionAlgorithmFactoryList() {
        this.encryptionAlgoFactoryMap.putIfAbsent("AEAD_AES_256_CBC_HMAC_SHA256", new SQLServerAeadAes256CbcHmac256Factory());
    }

    static SQLServerEncryptionAlgorithmFactoryList getInstance() {
        return instance;
    }

    String getRegisteredCipherAlgorithmNames() {
        StringBuffer stringBuff = new StringBuffer();
        boolean first = true;
        for (String key : this.encryptionAlgoFactoryMap.keySet()) {
            if (first) {
                stringBuff.append("'");
                first = false;
            } else {
                stringBuff.append(", '");
            }
            stringBuff.append(key);
            stringBuff.append("'");
        }
        return stringBuff.toString();
    }

    SQLServerEncryptionAlgorithm getAlgorithm(SQLServerSymmetricKey key, SQLServerEncryptionType encryptionType, String algorithmName) throws SQLServerException {
        SQLServerEncryptionAlgorithm encryptionAlgorithm = null;
        SQLServerEncryptionAlgorithmFactory factory = null;
        if (!this.encryptionAlgoFactoryMap.containsKey(algorithmName)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnknownColumnEncryptionAlgorithm"));
            Object[] msgArgs = new Object[]{algorithmName, SQLServerEncryptionAlgorithmFactoryList.getInstance().getRegisteredCipherAlgorithmNames()};
            throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
        }
        factory = this.encryptionAlgoFactoryMap.get(algorithmName);
        assert (null != factory) : "Null Algorithm Factory class detected";
        encryptionAlgorithm = factory.create(key, encryptionType, algorithmName);
        return encryptionAlgorithm;
    }
}

