/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.realm;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.CombinedRealm;
import org.apache.catalina.realm.RealmBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

public class LockOutRealm
extends CombinedRealm {
    private static final Log log = LogFactory.getLog(LockOutRealm.class);
    protected int failureCount = 5;
    protected int lockOutTime = 300;
    protected int cacheSize = 1000;
    protected int cacheRemovalWarningTime = 3600;
    protected Map<String, LockRecord> failedUsers = null;

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        this.failedUsers = new LinkedHashMap<String, LockRecord>(this.cacheSize, 0.75f, true){
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, LockRecord> eldest) {
                if (this.size() > LockOutRealm.this.cacheSize) {
                    long timeInCache = (System.currentTimeMillis() - eldest.getValue().getLastFailureTime()) / 1000L;
                    if (timeInCache < (long)LockOutRealm.this.cacheRemovalWarningTime) {
                        log.warn((Object)RealmBase.sm.getString("lockOutRealm.removeWarning", new Object[]{eldest.getKey(), timeInCache}));
                    }
                    return true;
                }
                return false;
            }
        };
        super.startInternal();
    }

    @Override
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realmName, String digestA2, String algorithm) {
        Principal authenticatedUser = super.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realmName, digestA2, algorithm);
        return this.filterLockedAccounts(username, authenticatedUser);
    }

    @Override
    public Principal authenticate(String username, String credentials) {
        Principal authenticatedUser = super.authenticate(username, credentials);
        return this.filterLockedAccounts(username, authenticatedUser);
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) {
        String username = null;
        if (certs != null && certs.length > 0) {
            username = certs[0].getSubjectX500Principal().toString();
        }
        Principal authenticatedUser = super.authenticate(certs);
        return this.filterLockedAccounts(username, authenticatedUser);
    }

    @Override
    public Principal authenticate(GSSContext gssContext, boolean storeCreds) {
        if (gssContext.isEstablished()) {
            String username = null;
            GSSName name = null;
            try {
                name = gssContext.getSrcName();
            }
            catch (GSSException e) {
                log.warn((Object)sm.getString("realmBase.gssNameFail"), (Throwable)e);
                return null;
            }
            username = name.toString();
            Principal authenticatedUser = super.authenticate(gssContext, storeCreds);
            return this.filterLockedAccounts(username, authenticatedUser);
        }
        return null;
    }

    @Override
    public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        String username = gssName.toString();
        Principal authenticatedUser = super.authenticate(gssName, gssCredential);
        return this.filterLockedAccounts(username, authenticatedUser);
    }

    private Principal filterLockedAccounts(String username, Principal authenticatedUser) {
        if (authenticatedUser == null && this.isAvailable()) {
            this.registerAuthFailure(username);
        }
        if (this.isLocked(username)) {
            log.warn((Object)sm.getString("lockOutRealm.authLockedUser", new Object[]{username}));
            return null;
        }
        if (authenticatedUser != null) {
            this.registerAuthSuccess(username);
        }
        return authenticatedUser;
    }

    public void unlock(String username) {
        this.registerAuthSuccess(username);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isLocked(String username) {
        LockRecord lockRecord = null;
        LockOutRealm lockOutRealm = this;
        synchronized (lockOutRealm) {
            lockRecord = this.failedUsers.get(username);
        }
        if (lockRecord == null) {
            return false;
        }
        return lockRecord.getFailures() >= this.failureCount && (System.currentTimeMillis() - lockRecord.getLastFailureTime()) / 1000L < (long)this.lockOutTime;
    }

    private synchronized void registerAuthSuccess(String username) {
        this.failedUsers.remove(username);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void registerAuthFailure(String username) {
        LockRecord lockRecord = null;
        LockOutRealm lockOutRealm = this;
        synchronized (lockOutRealm) {
            if (!this.failedUsers.containsKey(username)) {
                lockRecord = new LockRecord();
                this.failedUsers.put(username, lockRecord);
            } else {
                lockRecord = this.failedUsers.get(username);
                if (lockRecord.getFailures() >= this.failureCount && (System.currentTimeMillis() - lockRecord.getLastFailureTime()) / 1000L > (long)this.lockOutTime) {
                    lockRecord.setFailures(0);
                }
            }
        }
        lockRecord.registerFailure();
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getLockOutTime() {
        return this.lockOutTime;
    }

    public void setLockOutTime(int lockOutTime) {
        this.lockOutTime = lockOutTime;
    }

    public int getCacheSize() {
        return this.cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getCacheRemovalWarningTime() {
        return this.cacheRemovalWarningTime;
    }

    public void setCacheRemovalWarningTime(int cacheRemovalWarningTime) {
        this.cacheRemovalWarningTime = cacheRemovalWarningTime;
    }

    protected static class LockRecord {
        private final AtomicInteger failures = new AtomicInteger(0);
        private long lastFailureTime = 0L;

        protected LockRecord() {
        }

        public int getFailures() {
            return this.failures.get();
        }

        public void setFailures(int theFailures) {
            this.failures.set(theFailures);
        }

        public long getLastFailureTime() {
            return this.lastFailureTime;
        }

        public void registerFailure() {
            this.failures.incrementAndGet();
            this.lastFailureTime = System.currentTimeMillis();
        }
    }
}

