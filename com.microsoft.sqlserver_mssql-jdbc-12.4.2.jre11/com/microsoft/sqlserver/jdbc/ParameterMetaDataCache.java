/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CryptoCache;
import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mssql.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

class ParameterMetaDataCache {
    static final int CACHE_SIZE = 2000;
    static final int MAX_WEIGHTED_CAPACITY = 2300;
    static CryptoCache cache = new CryptoCache();
    private static Logger metadataCacheLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.ParameterMetaDataCache");

    private ParameterMetaDataCache() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static boolean getQueryMetadata(Parameter[] params, ArrayList<String> parameterNames, SQLServerConnection connection, SQLServerStatement stmt, String userSql) throws SQLServerException {
        int i;
        AbstractMap.SimpleEntry<String, String> encryptionValues = ParameterMetaDataCache.getCacheLookupKeys(connection, userSql);
        ConcurrentLinkedHashMap<String, CryptoMetadata> metadataMap = cache.getCacheEntry(encryptionValues.getKey());
        if (metadataMap == null) {
            if (metadataCacheLogger.isLoggable(Level.FINEST)) {
                metadataCacheLogger.finest("Cache Miss. Unable to retrieve cache entry from cache.");
            }
            return false;
        }
        for (i = 0; i < params.length; ++i) {
            CryptoMetadata foundData = metadataMap.get(parameterNames.get(i));
            if (!metadataMap.containsKey(parameterNames.get(i)) && metadataCacheLogger.isLoggable(Level.FINEST)) {
                metadataCacheLogger.finest("Parameter uses Plaintext (type 0) encryption.");
            }
            if (foundData != null && foundData.isAlgorithmInitialized()) {
                for (Parameter param : params) {
                    param.cryptoMeta = null;
                }
                if (metadataCacheLogger.isLoggable(Level.FINEST)) {
                    metadataCacheLogger.finest("Cache Miss. Cache entry either has missing parameter or initialized algorithm.");
                }
                return false;
            }
            params[i].cryptoMeta = foundData;
        }
        for (i = 0; i < params.length; ++i) {
            try {
                CryptoMetadata cryptoCopy = null;
                CryptoMetadata metaData = params[i].getCryptoMetadata();
                if (metaData != null) {
                    cryptoCopy = new CryptoMetadata(metaData.getCekTableEntry(), metaData.getOrdinal(), metaData.getEncryptionAlgorithmId(), metaData.getEncryptionAlgorithmName(), metaData.getEncryptionType().getValue(), metaData.getNormalizationRuleVersion());
                }
                params[i].cryptoMeta = cryptoCopy;
                if (cryptoCopy == null) continue;
                try {
                    SQLServerSecurityUtility.decryptSymmetricKey(cryptoCopy, connection, stmt);
                    continue;
                }
                catch (SQLServerException e) {
                    ParameterMetaDataCache.removeCacheEntry(connection, userSql);
                    for (Parameter paramToCleanup : params) {
                        paramToCleanup.cryptoMeta = null;
                    }
                    if (metadataCacheLogger.isLoggable(Level.FINEST)) {
                        metadataCacheLogger.finest("Cache Miss. Unable to decrypt CEK.");
                    }
                    return false;
                }
            }
            catch (SQLServerException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CryptoCacheInaccessible"));
                Object[] msgArgs = new Object[]{"R_unknownColumnEncryptionType", e.getMessage()};
                throw new SQLServerException(form.format(msgArgs), null);
            }
        }
        if (metadataCacheLogger.isLoggable(Level.FINEST)) {
            metadataCacheLogger.finest("Cache Hit. Successfully retrieved metadata from cache.");
        }
        return true;
    }

    static boolean addQueryMetadata(Parameter[] params, ArrayList<String> parameterNames, SQLServerConnection connection, SQLServerStatement stmt, String userSql) throws SQLServerException {
        AbstractMap.SimpleEntry<String, String> encryptionValues = ParameterMetaDataCache.getCacheLookupKeys(connection, userSql);
        if (encryptionValues.getKey() == null) {
            return false;
        }
        ConcurrentLinkedHashMap<String, CryptoMetadata> metadataMap = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(params.length).build();
        for (int i = 0; i < params.length; ++i) {
            try {
                CryptoMetadata cryptoCopy = null;
                CryptoMetadata metaData = params[i].getCryptoMetadata();
                if (metaData == null) continue;
                cryptoCopy = new CryptoMetadata(metaData.getCekTableEntry(), metaData.getOrdinal(), metaData.getEncryptionAlgorithmId(), metaData.getEncryptionAlgorithmName(), metaData.getEncryptionType().getValue(), metaData.getNormalizationRuleVersion());
                if (cryptoCopy.isAlgorithmInitialized()) {
                    return false;
                }
                String paramName = parameterNames.get(i);
                metadataMap.put(paramName, cryptoCopy);
                continue;
            }
            catch (SQLServerException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CryptoCacheInaccessible"));
                Object[] msgArgs = new Object[]{"R_unknownColumnEncryptionType", e.getMessage()};
                throw new SQLServerException(form.format(msgArgs), null);
            }
        }
        int cacheSizeCurrent = cache.getParamMap().size();
        if (cacheSizeCurrent > 2300) {
            int entriesToRemove = cacheSizeCurrent - 2000;
            ConcurrentLinkedHashMap<String, ConcurrentLinkedHashMap<String, CryptoMetadata>> map = cache.getParamMap();
            int count = 0;
            for (Map.Entry<String, ConcurrentLinkedHashMap<String, CryptoMetadata>> entry : map.entrySet()) {
                if (count >= entriesToRemove) break;
                map.remove(entry.getKey(), entry.getValue());
                ++count;
            }
            if (metadataCacheLogger.isLoggable(Level.FINEST)) {
                metadataCacheLogger.finest("Cache successfully trimmed.");
            }
        }
        cache.addParamEntry(encryptionValues.getKey(), metadataMap);
        return true;
    }

    static void removeCacheEntry(SQLServerConnection connection, String userSql) {
        AbstractMap.SimpleEntry<String, String> encryptionValues = ParameterMetaDataCache.getCacheLookupKeys(connection, userSql);
        if (encryptionValues.getKey() == null) {
            return;
        }
        cache.removeParamEntry(encryptionValues.getKey());
    }

    private static AbstractMap.SimpleEntry<String, String> getCacheLookupKeys(SQLServerConnection connection, String userSql) {
        StringBuilder cacheLookupKeyBuilder = new StringBuilder();
        cacheLookupKeyBuilder.append(":::");
        String databaseName = connection.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.DATABASE_NAME.toString());
        cacheLookupKeyBuilder.append(databaseName);
        cacheLookupKeyBuilder.append(":::");
        cacheLookupKeyBuilder.append(userSql);
        String cacheLookupKey = cacheLookupKeyBuilder.toString();
        String enclaveLookupKey = cacheLookupKeyBuilder.append(":::enclaveKeys").toString();
        return new AbstractMap.SimpleEntry<String, String>(cacheLookupKey, enclaveLookupKey);
    }
}

