/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.EncryptionKeyInfo;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKey;
import com.microsoft.sqlserver.jdbc.SimpleTtlCache;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SQLServerSymmetricKeyCache {
    static final Lock lock = new ReentrantLock();
    private final SimpleTtlCache<String, SQLServerSymmetricKey> cache = new SimpleTtlCache();
    private static final SQLServerSymmetricKeyCache instance = new SQLServerSymmetricKeyCache();
    private static final Logger aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerSymmetricKeyCache");

    private SQLServerSymmetricKeyCache() {
    }

    static SQLServerSymmetricKeyCache getInstance() {
        return instance;
    }

    SimpleTtlCache<String, SQLServerSymmetricKey> getCache() {
        return this.cache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    SQLServerSymmetricKey getKey(EncryptionKeyInfo keyInfo, SQLServerConnection connection) throws SQLServerException {
        SQLServerSymmetricKey encryptionKey = null;
        lock.lock();
        try {
            String serverName = connection.getTrustedServerNameAE();
            assert (null != serverName) : "serverName should not be null in getKey.";
            StringBuilder keyLookupValuebuffer = new StringBuilder(serverName);
            keyLookupValuebuffer.append(":");
            keyLookupValuebuffer.append(Base64.getEncoder().encodeToString(new String(keyInfo.encryptedKey, StandardCharsets.UTF_8).getBytes()));
            keyLookupValuebuffer.append(":");
            keyLookupValuebuffer.append(keyInfo.keyStoreName);
            String keyLookupValue = keyLookupValuebuffer.toString();
            keyLookupValuebuffer.setLength(0);
            if (aeLogger.isLoggable(Level.FINE)) {
                aeLogger.fine("Checking trusted master key path...");
            }
            Boolean[] hasEntry = new Boolean[1];
            List<String> trustedKeyPaths = SQLServerConnection.getColumnEncryptionTrustedMasterKeyPaths(serverName, hasEntry);
            if (hasEntry[0].booleanValue() && (null == trustedKeyPaths || trustedKeyPaths.isEmpty() || !trustedKeyPaths.contains(keyInfo.keyPath))) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UntrustedKeyPath"));
                Object[] msgArgs = new Object[]{keyInfo.keyPath, serverName};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
            if (aeLogger.isLoggable(Level.FINE)) {
                aeLogger.fine("Checking Symmetric key cache...");
            }
            if (!this.cache.contains(keyLookupValue)) {
                SQLServerColumnEncryptionKeyStoreProvider provider = connection.getSystemOrGlobalColumnEncryptionKeyStoreProvider(keyInfo.keyStoreName);
                assert (null != provider) : "Provider should not be null.";
                provider.setColumnEncryptionCacheTtl(Duration.ZERO);
                byte[] plaintextKey = provider.decryptColumnEncryptionKey(keyInfo.keyPath, keyInfo.algorithmName, keyInfo.encryptedKey);
                encryptionKey = new SQLServerSymmetricKey(plaintextKey);
                long columnEncryptionKeyCacheTtl = SQLServerConnection.getColumnEncryptionKeyCacheTtl();
                if (0L != columnEncryptionKeyCacheTtl) {
                    this.cache.setCacheTtl(columnEncryptionKeyCacheTtl);
                    this.cache.put(keyLookupValue, encryptionKey);
                }
            } else {
                encryptionKey = this.cache.get(keyLookupValue);
            }
            SQLServerSymmetricKey sQLServerSymmetricKey = encryptionKey;
            return sQLServerSymmetricKey;
        }
        finally {
            lock.unlock();
        }
    }
}

