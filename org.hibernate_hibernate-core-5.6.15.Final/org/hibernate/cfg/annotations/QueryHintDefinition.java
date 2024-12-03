/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 *  javax.persistence.NamedQuery
 *  javax.persistence.QueryHint
 */
package org.hibernate.cfg.annotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import org.hibernate.AnnotationException;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.internal.util.LockModeConverter;

public class QueryHintDefinition {
    private final Map<String, Object> hintsMap;

    public QueryHintDefinition(QueryHint[] hints) {
        if (hints == null || hints.length == 0) {
            this.hintsMap = Collections.emptyMap();
        } else {
            HashMap<String, Object> hintsMap = new HashMap<String, Object>();
            for (QueryHint hint : hints) {
                hintsMap.put(hint.name(), hint.value());
            }
            this.hintsMap = hintsMap;
        }
    }

    public CacheMode getCacheMode(String query) {
        String hitName = "org.hibernate.cacheMode";
        String value = (String)this.hintsMap.get(hitName);
        if (value == null) {
            return null;
        }
        try {
            return CacheMode.interpretExternalSetting(value);
        }
        catch (MappingException e) {
            throw new AnnotationException("Unknown CacheMode in hint: " + query + ":" + hitName, (Throwable)((Object)e));
        }
    }

    public FlushMode getFlushMode(String query) {
        String hitName = "org.hibernate.flushMode";
        String value = (String)this.hintsMap.get(hitName);
        if (value == null) {
            return null;
        }
        try {
            return FlushMode.interpretExternalSetting(value);
        }
        catch (MappingException e) {
            throw new AnnotationException("Unknown FlushMode in hint: " + query + ":" + hitName, (Throwable)((Object)e));
        }
    }

    public LockMode getLockMode(String query) {
        String hitName = "org.hibernate.lockMode";
        String value = (String)this.hintsMap.get(hitName);
        if (value == null) {
            return null;
        }
        try {
            return LockMode.fromExternalForm(value);
        }
        catch (MappingException e) {
            throw new AnnotationException("Unknown LockMode in hint: " + query + ":" + hitName, (Throwable)((Object)e));
        }
    }

    public Boolean getPassDistinctThrough(String query) {
        return this.doGetBoolean(query, "hibernate.query.passDistinctThrough").orElse(null);
    }

    public boolean getBoolean(String query, String hintName) {
        return this.doGetBoolean(query, hintName).orElse(false);
    }

    private Optional<Boolean> doGetBoolean(String query, String hintName) {
        String value = (String)this.hintsMap.get(hintName);
        if (value == null) {
            return Optional.empty();
        }
        if (value.equalsIgnoreCase("true")) {
            return Optional.of(true);
        }
        if (value.equalsIgnoreCase("false")) {
            return Optional.of(false);
        }
        throw new AnnotationException("Not a boolean in hint: " + query + ":" + hintName);
    }

    public String getString(String query, String hintName) {
        return (String)this.hintsMap.get(hintName);
    }

    public Integer getInteger(String query, String hintName) {
        String value = (String)this.hintsMap.get(hintName);
        if (value == null) {
            return null;
        }
        try {
            return Integer.decode(value);
        }
        catch (NumberFormatException nfe) {
            throw new AnnotationException("Not an integer in hint: " + query + ":" + hintName, nfe);
        }
    }

    public Integer getTimeout(String queryName) {
        Integer timeout = this.getInteger(queryName, "javax.persistence.query.timeout");
        if (timeout == null) {
            timeout = this.getInteger(queryName, "jakarta.persistence.query.timeout");
        }
        timeout = timeout != null ? Integer.valueOf((int)Math.round(timeout.doubleValue() / 1000.0)) : this.getInteger(queryName, "org.hibernate.timeout");
        return timeout;
    }

    public LockOptions determineLockOptions(NamedQuery namedQueryAnnotation) {
        LockModeType lockModeType = namedQueryAnnotation.lockMode();
        Integer lockTimeoutHint = this.getInteger(namedQueryAnnotation.name(), "javax.persistence.lock.timeout");
        if (lockTimeoutHint == null) {
            lockTimeoutHint = this.getInteger(namedQueryAnnotation.name(), "jakarta.persistence.lock.timeout");
        }
        Boolean followOnLocking = this.getBoolean(namedQueryAnnotation.name(), "hibernate.query.followOnLocking");
        return this.determineLockOptions(lockModeType, lockTimeoutHint, followOnLocking);
    }

    private LockOptions determineLockOptions(LockModeType lockModeType, Integer lockTimeoutHint, Boolean followOnLocking) {
        LockOptions lockOptions = new LockOptions(LockModeConverter.convertToLockMode(lockModeType)).setFollowOnLocking(followOnLocking);
        if (lockTimeoutHint != null) {
            lockOptions.setTimeOut(lockTimeoutHint);
        }
        return lockOptions;
    }

    public Map<String, Object> getHintsMap() {
        return this.hintsMap;
    }
}

