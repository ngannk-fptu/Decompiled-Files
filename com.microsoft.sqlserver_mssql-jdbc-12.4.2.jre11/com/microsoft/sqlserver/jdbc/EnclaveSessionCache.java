/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseAttestationRequest;
import com.microsoft.sqlserver.jdbc.EnclaveCacheEntry;
import com.microsoft.sqlserver.jdbc.EnclaveSession;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class EnclaveSessionCache {
    private ConcurrentHashMap<String, EnclaveCacheEntry> sessionCache = new ConcurrentHashMap(0);

    EnclaveSessionCache() {
    }

    void addEntry(String servername, String catalog, String attestationUrl, BaseAttestationRequest b, EnclaveSession e) {
        StringBuilder sb = new StringBuilder(servername).append(catalog).append(attestationUrl);
        this.sessionCache.put(sb.toString(), new EnclaveCacheEntry(b, e));
    }

    void removeEntry(EnclaveSession e) {
        for (Map.Entry<String, EnclaveCacheEntry> entry : this.sessionCache.entrySet()) {
            EnclaveCacheEntry ece = entry.getValue();
            if (!Arrays.equals(ece.getEnclaveSession().getSessionID(), e.getSessionID())) continue;
            this.sessionCache.remove(entry.getKey());
        }
    }

    EnclaveCacheEntry getSession(String key) {
        EnclaveCacheEntry e = this.sessionCache.get(key);
        if (null != e && e.expired()) {
            this.sessionCache.remove(key);
            return null;
        }
        return e;
    }
}

