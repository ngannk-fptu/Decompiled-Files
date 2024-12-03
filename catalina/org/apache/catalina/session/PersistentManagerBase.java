/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.session;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.StoreManager;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StoreBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class PersistentManagerBase
extends ManagerBase
implements StoreManager {
    private final Log log = LogFactory.getLog(PersistentManagerBase.class);
    private static final String name = "PersistentManagerBase";
    private static final String PERSISTED_LAST_ACCESSED_TIME = "org.apache.catalina.session.PersistentManagerBase.persistedLastAccessedTime";
    protected Store store = null;
    protected boolean saveOnRestart = true;
    protected int maxIdleBackup = -1;
    protected int minIdleSwap = -1;
    protected int maxIdleSwap = -1;
    private final Map<String, Object> sessionSwapInLocks = new HashMap<String, Object>();
    private final ThreadLocal<Session> sessionToSwapIn = new ThreadLocal();

    public int getMaxIdleBackup() {
        return this.maxIdleBackup;
    }

    public void setMaxIdleBackup(int backup) {
        if (backup == this.maxIdleBackup) {
            return;
        }
        int oldBackup = this.maxIdleBackup;
        this.maxIdleBackup = backup;
        this.support.firePropertyChange("maxIdleBackup", (Object)oldBackup, (Object)this.maxIdleBackup);
    }

    public int getMaxIdleSwap() {
        return this.maxIdleSwap;
    }

    public void setMaxIdleSwap(int max) {
        if (max == this.maxIdleSwap) {
            return;
        }
        int oldMaxIdleSwap = this.maxIdleSwap;
        this.maxIdleSwap = max;
        this.support.firePropertyChange("maxIdleSwap", (Object)oldMaxIdleSwap, (Object)this.maxIdleSwap);
    }

    public int getMinIdleSwap() {
        return this.minIdleSwap;
    }

    public void setMinIdleSwap(int min) {
        if (this.minIdleSwap == min) {
            return;
        }
        int oldMinIdleSwap = this.minIdleSwap;
        this.minIdleSwap = min;
        this.support.firePropertyChange("minIdleSwap", (Object)oldMinIdleSwap, (Object)this.minIdleSwap);
    }

    public boolean isLoaded(String id) {
        try {
            if (super.findSession(id) != null) {
                return true;
            }
        }
        catch (IOException e) {
            this.log.error((Object)sm.getString("persistentManager.isLoadedError", new Object[]{id}), (Throwable)e);
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setStore(Store store) {
        this.store = store;
        store.setManager(this);
    }

    @Override
    public Store getStore() {
        return this.store;
    }

    public boolean getSaveOnRestart() {
        return this.saveOnRestart;
    }

    public void setSaveOnRestart(boolean saveOnRestart) {
        if (saveOnRestart == this.saveOnRestart) {
            return;
        }
        boolean oldSaveOnRestart = this.saveOnRestart;
        this.saveOnRestart = saveOnRestart;
        this.support.firePropertyChange("saveOnRestart", (Object)oldSaveOnRestart, (Object)this.saveOnRestart);
    }

    public void clearStore() {
        if (this.store == null) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreClear());
                }
                catch (PrivilegedActionException e) {
                    this.log.error((Object)sm.getString("persistentManager.storeClearError"), (Throwable)e.getException());
                }
            } else {
                this.store.clear();
            }
        }
        catch (IOException e) {
            this.log.error((Object)sm.getString("persistentManager.storeClearError"), (Throwable)e);
        }
    }

    @Override
    public void processExpires() {
        long timeNow = System.currentTimeMillis();
        Session[] sessions = this.findSessions();
        int expireHere = 0;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Start expire sessions " + this.getName() + " at " + timeNow + " sessioncount " + sessions.length));
        }
        for (Session session : sessions) {
            if (session.isValid()) continue;
            this.expiredSessions.incrementAndGet();
            ++expireHere;
        }
        this.processPersistenceChecks();
        if (this.getStore() instanceof StoreBase) {
            ((StoreBase)this.getStore()).processExpires();
        }
        long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("End expire sessions " + this.getName() + " processingTime " + (timeEnd - timeNow) + " expired sessions: " + expireHere));
        }
        this.processingTime += timeEnd - timeNow;
    }

    public void processPersistenceChecks() {
        this.processMaxIdleSwaps();
        this.processMaxActiveSwaps();
        this.processMaxIdleBackups();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Session findSession(String id) throws IOException {
        Session session = super.findSession(id);
        if (session != null) {
            Session session2 = session;
            synchronized (session2) {
                session = super.findSession(session.getIdInternal());
                if (session != null) {
                    session.access();
                    session.endAccess();
                }
            }
        }
        if (session != null) {
            return session;
        }
        session = this.swapIn(id);
        return session;
    }

    @Override
    public void removeSuper(Session session) {
        super.remove(session, false);
    }

    @Override
    public void load() {
        String[] ids;
        block11: {
            this.sessions.clear();
            if (this.store == null) {
                return;
            }
            ids = null;
            try {
                if (SecurityUtil.isPackageProtectionEnabled()) {
                    try {
                        ids = AccessController.doPrivileged(new PrivilegedStoreKeys());
                        break block11;
                    }
                    catch (PrivilegedActionException e) {
                        this.log.error((Object)sm.getString("persistentManager.storeLoadKeysError"), (Throwable)e.getException());
                        return;
                    }
                }
                ids = this.store.keys();
            }
            catch (IOException e) {
                this.log.error((Object)sm.getString("persistentManager.storeLoadKeysError"), (Throwable)e);
                return;
            }
        }
        int n = ids.length;
        if (n == 0) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("persistentManager.loading", new Object[]{String.valueOf(n)}));
        }
        for (String id : ids) {
            try {
                this.swapIn(id);
            }
            catch (IOException e) {
                this.log.error((Object)sm.getString("persistentManager.storeLoadError"), (Throwable)e);
            }
        }
    }

    @Override
    public void remove(Session session, boolean update) {
        super.remove(session, update);
        if (this.store != null) {
            this.removeSession(session.getIdInternal());
        }
    }

    protected void removeSession(String id) {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreRemove(id));
                }
                catch (PrivilegedActionException e) {
                    this.log.error((Object)sm.getString("persistentManager.removeError"), (Throwable)e.getException());
                }
            } else {
                this.store.remove(id);
            }
        }
        catch (IOException e) {
            this.log.error((Object)sm.getString("persistentManager.removeError"), (Throwable)e);
        }
    }

    @Override
    public void unload() {
        if (this.store == null) {
            return;
        }
        Session[] sessions = this.findSessions();
        int n = sessions.length;
        if (n == 0) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("persistentManager.unloading", new Object[]{String.valueOf(n)}));
        }
        for (Session session : sessions) {
            try {
                this.swapOut(session);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    public int getActiveSessionsFull() {
        int result = this.getActiveSessions();
        try {
            result += this.getStore().getSize();
        }
        catch (IOException ioe) {
            this.log.warn((Object)sm.getString("persistentManager.storeSizeException"));
        }
        return result;
    }

    @Override
    public Set<String> getSessionIdsFull() {
        HashSet<String> sessionIds = new HashSet<String>(this.sessions.keySet());
        try {
            sessionIds.addAll(Arrays.asList(this.getStore().keys()));
        }
        catch (IOException e) {
            this.log.warn((Object)sm.getString("persistentManager.storeKeysException"));
        }
        return sessionIds;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Session swapIn(String id) throws IOException {
        if (this.store == null) {
            return null;
        }
        Object swapInLock = null;
        PersistentManagerBase persistentManagerBase = this;
        synchronized (persistentManagerBase) {
            swapInLock = this.sessionSwapInLocks.computeIfAbsent(id, k -> new Object());
        }
        Session session = null;
        Object object = swapInLock;
        synchronized (object) {
            session = (Session)this.sessions.get(id);
            if (session == null) {
                Session currentSwapInSession = this.sessionToSwapIn.get();
                try {
                    if (currentSwapInSession == null || !id.equals(currentSwapInSession.getId())) {
                        session = this.loadSessionFromStore(id);
                        this.sessionToSwapIn.set(session);
                        if (session != null && !session.isValid()) {
                            this.log.error((Object)sm.getString("persistentManager.swapInInvalid", new Object[]{id}));
                            session.expire();
                            this.removeSession(id);
                            session = null;
                        }
                        if (session != null) {
                            this.reactivateLoadedSession(id, session);
                        }
                    }
                }
                finally {
                    this.sessionToSwapIn.remove();
                }
            }
        }
        object = this;
        synchronized (object) {
            this.sessionSwapInLocks.remove(id);
        }
        return session;
    }

    private void reactivateLoadedSession(String id, Session session) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("persistentManager.swapIn", new Object[]{id}));
        }
        session.setManager(this);
        ((StandardSession)session).tellNew();
        this.add(session);
        ((StandardSession)session).activate();
        session.access();
        session.endAccess();
    }

    private Session loadSessionFromStore(String id) throws IOException {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                return this.securedStoreLoad(id);
            }
            return this.store.load(id);
        }
        catch (ClassNotFoundException e) {
            String msg = sm.getString("persistentManager.deserializeError", new Object[]{id});
            this.log.error((Object)msg, (Throwable)e);
            throw new IllegalStateException(msg, e);
        }
    }

    private Session securedStoreLoad(String id) throws IOException, ClassNotFoundException {
        try {
            return AccessController.doPrivileged(new PrivilegedStoreLoad(id));
        }
        catch (PrivilegedActionException ex) {
            Exception e = ex.getException();
            this.log.error((Object)sm.getString("persistentManager.swapInException", new Object[]{id}), (Throwable)e);
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            if (e instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)e;
            }
            return null;
        }
    }

    protected void swapOut(Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        ((StandardSession)session).passivate();
        this.writeSession(session);
        super.remove(session, true);
        session.recycle();
    }

    protected void writeSession(Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreSave(session));
                }
                catch (PrivilegedActionException ex) {
                    Exception exception = ex.getException();
                    if (exception instanceof IOException) {
                        throw (IOException)exception;
                    }
                    this.log.error((Object)sm.getString("persistentManager.serializeError", new Object[]{session.getIdInternal(), exception}));
                }
            } else {
                this.store.save(session);
            }
        }
        catch (IOException e) {
            this.log.error((Object)sm.getString("persistentManager.serializeError", new Object[]{session.getIdInternal(), e}));
            throw e;
        }
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        if (this.store == null) {
            this.log.error((Object)"No Store configured, persistence disabled");
        } else if (this.store instanceof Lifecycle) {
            ((Lifecycle)((Object)this.store)).start();
        }
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Stopping");
        }
        this.setState(LifecycleState.STOPPING);
        if (this.getStore() != null && this.saveOnRestart) {
            this.unload();
        } else {
            Session[] sessions;
            for (Session value : sessions = this.findSessions()) {
                StandardSession session = (StandardSession)value;
                if (!session.isValid()) continue;
                session.expire();
            }
        }
        if (this.getStore() instanceof Lifecycle) {
            ((Lifecycle)((Object)this.getStore())).stop();
        }
        super.stopInternal();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processMaxIdleSwaps() {
        if (!this.getState().isAvailable() || this.maxIdleSwap < 0) {
            return;
        }
        Session[] sessions = this.findSessions();
        if (this.maxIdleSwap >= 0) {
            for (Session value : sessions) {
                StandardSession session;
                StandardSession standardSession = session = (StandardSession)value;
                synchronized (standardSession) {
                    if (!session.isValid()) {
                        continue;
                    }
                    int timeIdle = (int)(session.getIdleTimeInternal() / 1000L);
                    if (timeIdle >= this.maxIdleSwap && timeIdle >= this.minIdleSwap) {
                        if (session.accessCount != null && session.accessCount.get() > 0) {
                            continue;
                        }
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)sm.getString("persistentManager.swapMaxIdle", new Object[]{session.getIdInternal(), timeIdle}));
                        }
                        try {
                            this.swapOut(session);
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processMaxActiveSwaps() {
        if (!this.getState().isAvailable() || this.minIdleSwap < 0 || this.getMaxActiveSessions() < 0) {
            return;
        }
        Session[] sessions = this.findSessions();
        int limit = (int)((double)this.getMaxActiveSessions() * 0.9);
        if (limit >= sessions.length) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("persistentManager.tooManyActive", new Object[]{sessions.length}));
        }
        int toswap = sessions.length - limit;
        for (int i = 0; i < sessions.length && toswap > 0; ++i) {
            StandardSession session;
            StandardSession standardSession = session = (StandardSession)sessions[i];
            synchronized (standardSession) {
                int timeIdle = (int)(session.getIdleTimeInternal() / 1000L);
                if (timeIdle >= this.minIdleSwap) {
                    if (session.accessCount != null && session.accessCount.get() > 0) {
                        continue;
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)sm.getString("persistentManager.swapTooManyActive", new Object[]{session.getIdInternal(), timeIdle}));
                    }
                    try {
                        this.swapOut(session);
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    --toswap;
                }
                continue;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processMaxIdleBackups() {
        if (!this.getState().isAvailable() || this.maxIdleBackup < 0) {
            return;
        }
        Session[] sessions = this.findSessions();
        if (this.maxIdleBackup >= 0) {
            for (Session value : sessions) {
                StandardSession session;
                StandardSession standardSession = session = (StandardSession)value;
                synchronized (standardSession) {
                    if (!session.isValid()) {
                        continue;
                    }
                    long lastAccessedTime = session.getLastAccessedTimeInternal();
                    Long persistedLastAccessedTime = (Long)session.getNote(PERSISTED_LAST_ACCESSED_TIME);
                    if (persistedLastAccessedTime != null && lastAccessedTime == persistedLastAccessedTime) {
                        continue;
                    }
                    int timeIdle = (int)(session.getIdleTimeInternal() / 1000L);
                    if (timeIdle >= this.maxIdleBackup) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)sm.getString("persistentManager.backupMaxIdle", new Object[]{session.getIdInternal(), timeIdle}));
                        }
                        try {
                            this.writeSession(session);
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                        session.setNote(PERSISTED_LAST_ACCESSED_TIME, lastAccessedTime);
                    }
                }
            }
        }
    }

    private class PrivilegedStoreClear
    implements PrivilegedExceptionAction<Void> {
        PrivilegedStoreClear() {
        }

        @Override
        public Void run() throws Exception {
            PersistentManagerBase.this.store.clear();
            return null;
        }
    }

    private class PrivilegedStoreKeys
    implements PrivilegedExceptionAction<String[]> {
        PrivilegedStoreKeys() {
        }

        @Override
        public String[] run() throws Exception {
            return PersistentManagerBase.this.store.keys();
        }
    }

    private class PrivilegedStoreRemove
    implements PrivilegedExceptionAction<Void> {
        private String id;

        PrivilegedStoreRemove(String id) {
            this.id = id;
        }

        @Override
        public Void run() throws Exception {
            PersistentManagerBase.this.store.remove(this.id);
            return null;
        }
    }

    private class PrivilegedStoreLoad
    implements PrivilegedExceptionAction<Session> {
        private String id;

        PrivilegedStoreLoad(String id) {
            this.id = id;
        }

        @Override
        public Session run() throws Exception {
            return PersistentManagerBase.this.store.load(this.id);
        }
    }

    private class PrivilegedStoreSave
    implements PrivilegedExceptionAction<Void> {
        private Session session;

        PrivilegedStoreSave(Session session) {
            this.session = session;
        }

        @Override
        public Void run() throws Exception {
            PersistentManagerBase.this.store.save(this.session);
            return null;
        }
    }
}

