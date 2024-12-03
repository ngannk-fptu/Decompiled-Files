/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TokenMap
extends HashMap {
    private long tokenTimeout;
    Map tokenTimeouts;

    public TokenMap(long tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
        this.tokenTimeouts = new HashMap();
    }

    @Override
    public Object put(Object key, Object value) {
        this.tokenTimeouts.put(key, this.nextExpiryTime());
        return super.put(key, value);
    }

    @Override
    public Object get(Object key) {
        if (!super.containsKey(key)) {
            return null;
        }
        Long expiryTime = (Long)this.tokenTimeouts.get(key);
        if (expiryTime == null) {
            this.tokenTimeouts.remove(key);
            super.remove(key);
            return null;
        }
        if (expiryTime < System.currentTimeMillis()) {
            this.tokenTimeouts.remove(key);
            super.remove(key);
            return null;
        }
        this.tokenTimeouts.put(key, this.nextExpiryTime());
        return super.get(key);
    }

    private Long nextExpiryTime() {
        return System.currentTimeMillis() + this.tokenTimeout;
    }

    @Override
    public Object remove(Object key) {
        this.tokenTimeouts.remove(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        this.tokenTimeouts.clear();
        super.clear();
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException("Not written yet.");
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException("Not written yet.");
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException("Not written yet.");
    }

    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("Not written yet.");
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException("Not written yet.");
    }
}

