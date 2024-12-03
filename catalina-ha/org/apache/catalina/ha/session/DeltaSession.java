/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Manager
 *  org.apache.catalina.SessionListener
 *  org.apache.catalina.session.ManagerBase
 *  org.apache.catalina.session.StandardSession
 *  org.apache.catalina.tribes.io.ReplicationStream
 *  org.apache.catalina.tribes.tipis.ReplicatedMapEntry
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.session;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.WriteAbortedException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionListener;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.ClusterSession;
import org.apache.catalina.ha.session.ClusterManagerBase;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.session.DeltaRequest;
import org.apache.catalina.ha.session.ReplicatedSessionListener;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.tribes.io.ReplicationStream;
import org.apache.catalina.tribes.tipis.ReplicatedMapEntry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.res.StringManager;

public class DeltaSession
extends StandardSession
implements Externalizable,
ClusterSession,
ReplicatedMapEntry {
    public static final Log log = LogFactory.getLog(DeltaSession.class);
    protected static final StringManager sm = StringManager.getManager(DeltaSession.class);
    private transient boolean isPrimarySession = true;
    private transient DeltaRequest deltaRequest = null;
    private transient long lastTimeReplicated = System.currentTimeMillis();
    protected final Lock diffLock = new ReentrantReadWriteLock().writeLock();
    private long version;

    public DeltaSession() {
        this(null);
    }

    public DeltaSession(Manager manager) {
        super(manager);
        boolean recordAllActions = manager instanceof ClusterManagerBase && ((ClusterManagerBase)manager).isRecordAllActions();
        this.deltaRequest = this.createRequest(this.getIdInternal(), recordAllActions);
    }

    private DeltaRequest createRequest() {
        return this.createRequest(null, false);
    }

    protected DeltaRequest createRequest(String sessionId, boolean recordAllActions) {
        return new DeltaRequest(sessionId, recordAllActions);
    }

    public boolean isDirty() {
        return this.deltaRequest.getSize() > 0;
    }

    public boolean isDiffable() {
        return true;
    }

    public byte[] getDiff() throws IOException {
        SynchronizedStack<DeltaRequest> deltaRequestPool = null;
        DeltaRequest newDeltaRequest = null;
        if (this.manager instanceof ClusterManagerBase) {
            deltaRequestPool = ((ClusterManagerBase)this.manager).getDeltaRequestPool();
            newDeltaRequest = (DeltaRequest)deltaRequestPool.pop();
            if (newDeltaRequest == null) {
                newDeltaRequest = this.createRequest(null, ((ClusterManagerBase)this.manager).isRecordAllActions());
            }
        } else {
            newDeltaRequest = this.createRequest();
        }
        DeltaRequest oldDeltaRequest = this.replaceDeltaRequest(newDeltaRequest);
        byte[] result = oldDeltaRequest.serialize();
        if (deltaRequestPool != null) {
            oldDeltaRequest.reset();
            deltaRequestPool.push((Object)oldDeltaRequest);
        }
        return result;
    }

    public ClassLoader[] getClassLoaders() {
        if (this.manager instanceof ClusterManagerBase) {
            return ((ClusterManagerBase)this.manager).getClassLoaders();
        }
        if (this.manager instanceof ManagerBase) {
            ManagerBase mb = (ManagerBase)this.manager;
            return ClusterManagerBase.getClassLoaders(mb.getContext());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyDiff(byte[] diff, int offset, int length) throws IOException, ClassNotFoundException {
        Thread currentThread = Thread.currentThread();
        ClassLoader contextLoader = currentThread.getContextClassLoader();
        this.lockInternal();
        try (ReplicationStream stream = ((ClusterManager)this.getManager()).getReplicationStream(diff, offset, length);){
            ClassLoader[] loaders = this.getClassLoaders();
            if (loaders != null && loaders.length > 0) {
                currentThread.setContextClassLoader(loaders[0]);
            }
            this.deltaRequest.readExternal((ObjectInput)stream);
            this.deltaRequest.execute(this, ((ClusterManager)this.getManager()).isNotifyListenersOnReplication());
        }
        finally {
            this.unlockInternal();
            currentThread.setContextClassLoader(contextLoader);
        }
    }

    public void resetDiff() {
        this.resetDeltaRequest();
    }

    public void lock() {
    }

    public void unlock() {
    }

    private void lockInternal() {
        this.diffLock.lock();
    }

    private void unlockInternal() {
        this.diffLock.unlock();
    }

    public void setOwner(Object owner) {
        if (owner instanceof ClusterManager && this.getManager() == null) {
            ClusterManager cm = (ClusterManager)owner;
            this.setManager(cm);
            this.setValid(true);
            this.setPrimarySession(false);
            this.access();
            this.resetDeltaRequest();
            this.endAccess();
        }
    }

    public boolean isAccessReplicate() {
        long replDelta = System.currentTimeMillis() - this.getLastTimeReplicated();
        return this.maxInactiveInterval >= 0 && replDelta > (long)this.maxInactiveInterval * 1000L;
    }

    public void accessEntry() {
        this.access();
        this.setPrimarySession(false);
        this.endAccess();
    }

    @Override
    public boolean isPrimarySession() {
        return this.isPrimarySession;
    }

    @Override
    public void setPrimarySession(boolean primarySession) {
        this.isPrimarySession = primarySession;
    }

    public void setId(String id, boolean notify) {
        super.setId(id, notify);
        this.lockInternal();
        try {
            this.deltaRequest.setSessionId(this.getIdInternal());
        }
        finally {
            this.unlockInternal();
        }
    }

    public void setId(String id) {
        this.setId(id, true);
    }

    public void setMaxInactiveInterval(int interval) {
        this.setMaxInactiveInterval(interval, true);
    }

    public void setMaxInactiveInterval(int interval, boolean addDeltaRequest) {
        this.maxInactiveInterval = interval;
        if (addDeltaRequest) {
            this.lockInternal();
            try {
                this.deltaRequest.setMaxInactiveInterval(interval);
            }
            finally {
                this.unlockInternal();
            }
        }
    }

    public void setNew(boolean isNew) {
        this.setNew(isNew, true);
    }

    public void setNew(boolean isNew, boolean addDeltaRequest) {
        super.setNew(isNew);
        if (addDeltaRequest) {
            this.lockInternal();
            try {
                this.deltaRequest.setNew(isNew);
            }
            finally {
                this.unlockInternal();
            }
        }
    }

    public void setPrincipal(Principal principal) {
        this.setPrincipal(principal, true);
    }

    public void setPrincipal(Principal principal, boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.setPrincipal(principal);
            if (addDeltaRequest) {
                this.deltaRequest.setPrincipal(principal);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    public void setAuthType(String authType) {
        this.setAuthType(authType, true);
    }

    public void setAuthType(String authType, boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.setAuthType(authType);
            if (addDeltaRequest) {
                this.deltaRequest.setAuthType(authType);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    public boolean isValid() {
        if (!this.isValid) {
            return false;
        }
        if (this.expiring) {
            return true;
        }
        if (ACTIVITY_CHECK && this.accessCount.get() > 0) {
            return true;
        }
        if (this.maxInactiveInterval > 0) {
            int timeIdle = (int)(this.getIdleTimeInternal() / 1000L);
            if (this.isPrimarySession()) {
                if (timeIdle >= this.maxInactiveInterval) {
                    this.expire(true);
                }
            } else if (timeIdle >= 2 * this.maxInactiveInterval) {
                this.expire(true, false);
            }
        }
        return this.isValid;
    }

    public void endAccess() {
        super.endAccess();
        if (this.manager instanceof ClusterManagerBase) {
            ((ClusterManagerBase)this.manager).registerSessionAtReplicationValve(this);
        }
    }

    public void expire(boolean notify) {
        this.expire(notify, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void expire(boolean notify, boolean notifyCluster) {
        if (!this.isValid) {
            return;
        }
        DeltaSession deltaSession = this;
        synchronized (deltaSession) {
            if (!this.isValid) {
                return;
            }
            if (this.manager == null) {
                return;
            }
            String expiredId = this.getIdInternal();
            if (notifyCluster && expiredId != null && this.manager instanceof DeltaManager) {
                DeltaManager dmanager = (DeltaManager)this.manager;
                CatalinaCluster cluster = dmanager.getCluster();
                ClusterMessage msg = dmanager.requestCompleted(expiredId, true);
                if (msg != null) {
                    cluster.send(msg);
                }
            }
            super.expire(notify);
            if (notifyCluster) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("deltaSession.notifying", new Object[]{((ClusterManager)this.manager).getName(), this.isPrimarySession(), expiredId}));
                }
                if (this.manager instanceof DeltaManager) {
                    ((DeltaManager)this.manager).sessionExpired(expiredId);
                }
            }
        }
    }

    public void recycle() {
        this.lockInternal();
        try {
            super.recycle();
            this.deltaRequest.clear();
        }
        finally {
            this.unlockInternal();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeltaSession[");
        sb.append(this.id);
        sb.append(']');
        return sb.toString();
    }

    public void addSessionListener(SessionListener listener) {
        this.addSessionListener(listener, true);
    }

    public void addSessionListener(SessionListener listener, boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.addSessionListener(listener);
            if (addDeltaRequest && listener instanceof ReplicatedSessionListener) {
                this.deltaRequest.addSessionListener(listener);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    public void removeSessionListener(SessionListener listener) {
        this.removeSessionListener(listener, true);
    }

    public void removeSessionListener(SessionListener listener, boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.removeSessionListener(listener);
            if (addDeltaRequest && listener instanceof ReplicatedSessionListener) {
                this.deltaRequest.removeSessionListener(listener);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.lockInternal();
        try {
            this.readObjectData(in);
        }
        finally {
            this.unlockInternal();
        }
    }

    public void readObjectData(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        this.doReadObject((ObjectInput)stream);
    }

    public void readObjectData(ObjectInput stream) throws ClassNotFoundException, IOException {
        this.doReadObject(stream);
    }

    public void writeObjectData(ObjectOutputStream stream) throws IOException {
        this.writeObjectData((ObjectOutput)stream);
    }

    public void writeObjectData(ObjectOutput stream) throws IOException {
        this.doWriteObject(stream);
    }

    public void resetDeltaRequest() {
        this.lockInternal();
        try {
            this.deltaRequest.reset();
            this.deltaRequest.setSessionId(this.getIdInternal());
        }
        finally {
            this.unlockInternal();
        }
    }

    @Deprecated
    public DeltaRequest getDeltaRequest() {
        return this.deltaRequest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    DeltaRequest replaceDeltaRequest(DeltaRequest deltaRequest) {
        this.lockInternal();
        try {
            DeltaRequest oldDeltaRequest = this.deltaRequest;
            this.deltaRequest = deltaRequest;
            this.deltaRequest.setSessionId(this.getIdInternal());
            DeltaRequest deltaRequest2 = oldDeltaRequest;
            return deltaRequest2;
        }
        finally {
            this.unlockInternal();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deserializeAndExecuteDeltaRequest(byte[] delta) throws IOException, ClassNotFoundException {
        if (this.manager instanceof ClusterManagerBase) {
            SynchronizedStack<DeltaRequest> deltaRequestPool = ((ClusterManagerBase)this.manager).getDeltaRequestPool();
            DeltaRequest newDeltaRequest = (DeltaRequest)deltaRequestPool.pop();
            if (newDeltaRequest == null) {
                newDeltaRequest = this.createRequest(null, ((ClusterManagerBase)this.manager).isRecordAllActions());
            }
            ReplicationStream ois = ((ClusterManagerBase)this.manager).getReplicationStream(delta);
            newDeltaRequest.readExternal((ObjectInput)ois);
            ois.close();
            DeltaRequest oldDeltaRequest = null;
            this.lockInternal();
            try {
                oldDeltaRequest = this.replaceDeltaRequest(newDeltaRequest);
                newDeltaRequest.execute(this, ((ClusterManagerBase)this.manager).isNotifyListenersOnReplication());
                this.setPrimarySession(false);
            }
            finally {
                this.unlockInternal();
                if (oldDeltaRequest != null) {
                    oldDeltaRequest.reset();
                    deltaRequestPool.push((Object)oldDeltaRequest);
                }
            }
        }
    }

    public void removeAttribute(String name, boolean notify) {
        this.removeAttribute(name, notify, true);
    }

    public void removeAttribute(String name, boolean notify, boolean addDeltaRequest) {
        if (!this.isValid()) {
            throw new IllegalStateException(sm.getString("standardSession.removeAttribute.ise"));
        }
        this.removeAttributeInternal(name, notify, addDeltaRequest);
    }

    public void setAttribute(String name, Object value) {
        this.setAttribute(name, value, true, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAttribute(String name, Object value, boolean notify, boolean addDeltaRequest) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("standardSession.setAttribute.namenull"));
        }
        if (value == null) {
            this.removeAttribute(name);
            return;
        }
        this.lockInternal();
        try {
            super.setAttribute(name, value, notify);
            if (addDeltaRequest && !this.exclude(name, value)) {
                this.deltaRequest.setAttribute(name, value);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    public void removeNote(String name) {
        this.removeNote(name, true);
    }

    public void removeNote(String name, boolean addDeltaRequest) {
        this.lockInternal();
        try {
            super.removeNote(name);
            if (addDeltaRequest && this.manager instanceof ManagerBase && ((ManagerBase)this.manager).getPersistAuthenticationNotes()) {
                this.deltaRequest.removeNote(name);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    public void setNote(String name, Object value) {
        this.setNote(name, value, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setNote(String name, Object value, boolean addDeltaRequest) {
        if (value == null) {
            this.removeNote(name, addDeltaRequest);
            return;
        }
        this.lockInternal();
        try {
            super.setNote(name, value);
            if (addDeltaRequest && this.manager instanceof ManagerBase && ((ManagerBase)this.manager).getPersistAuthenticationNotes()) {
                this.deltaRequest.setNote(name, value);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    protected void doReadObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        this.doReadObject((ObjectInput)stream);
    }

    private void doReadObject(ObjectInput stream) throws ClassNotFoundException, IOException {
        int i;
        Object nextObject;
        this.authType = null;
        this.creationTime = (Long)stream.readObject();
        this.lastAccessedTime = (Long)stream.readObject();
        this.maxInactiveInterval = (Integer)stream.readObject();
        this.isNew = (Boolean)stream.readObject();
        this.isValid = (Boolean)stream.readObject();
        this.thisAccessedTime = (Long)stream.readObject();
        this.version = (Long)stream.readObject();
        boolean hasPrincipal = stream.readBoolean();
        this.principal = null;
        if (hasPrincipal) {
            this.principal = (Principal)stream.readObject();
        }
        this.id = (String)stream.readObject();
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("deltaSession.readSession", new Object[]{this.id}));
        }
        if (!((nextObject = stream.readObject()) instanceof Integer)) {
            if (nextObject != null) {
                this.notes.put("org.apache.catalina.authenticator.SESSION_ID", nextObject);
            }
            if ((nextObject = stream.readObject()) != null) {
                this.notes.put("org.apache.catalina.authenticator.REQUEST", nextObject);
            }
            nextObject = stream.readObject();
        }
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap();
        }
        int n = (Integer)nextObject;
        boolean isValidSave = this.isValid;
        this.isValid = true;
        for (i = 0; i < n; ++i) {
            Object value;
            String name = (String)stream.readObject();
            try {
                value = stream.readObject();
            }
            catch (WriteAbortedException wae) {
                if (wae.getCause() instanceof NotSerializableException) continue;
                throw wae;
            }
            if (this.exclude(name, value) || null == value) continue;
            this.attributes.put(name, value);
        }
        this.isValid = isValidSave;
        n = (Integer)stream.readObject();
        if (this.listeners == null || n > 0) {
            this.listeners = new ArrayList();
        }
        for (i = 0; i < n; ++i) {
            SessionListener listener = (SessionListener)stream.readObject();
            this.listeners.add(listener);
        }
        if (this.notes == null) {
            this.notes = new ConcurrentHashMap();
        }
        this.activate();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        this.lockInternal();
        try {
            this.doWriteObject(out);
        }
        finally {
            this.unlockInternal();
        }
    }

    protected void doWriteObject(ObjectOutputStream stream) throws IOException {
        this.doWriteObject((ObjectOutput)stream);
    }

    private void doWriteObject(ObjectOutput stream) throws IOException {
        stream.writeObject(this.creationTime);
        stream.writeObject(this.lastAccessedTime);
        stream.writeObject(this.maxInactiveInterval);
        stream.writeObject(this.isNew);
        stream.writeObject(this.isValid);
        stream.writeObject(this.thisAccessedTime);
        stream.writeObject(this.version);
        stream.writeBoolean(this.getPrincipal() instanceof Serializable);
        if (this.getPrincipal() instanceof Serializable) {
            stream.writeObject(this.getPrincipal());
        }
        stream.writeObject(this.id);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("deltaSession.writeSession", new Object[]{this.id}));
        }
        if (this.manager instanceof ManagerBase && ((ManagerBase)this.manager).getPersistAuthenticationNotes()) {
            stream.writeObject(this.notes.get("org.apache.catalina.authenticator.SESSION_ID"));
            stream.writeObject(this.notes.get("org.apache.catalina.authenticator.REQUEST"));
        }
        String[] keys = this.keys();
        ArrayList<String> saveNames = new ArrayList<String>();
        ArrayList saveValues = new ArrayList();
        for (String key : keys) {
            Object value = null;
            value = this.attributes.get(key);
            if (value == null || this.exclude(key, value) || !this.isAttributeDistributable(key, value)) continue;
            saveNames.add(key);
            saveValues.add(value);
        }
        int n = saveNames.size();
        stream.writeObject(n);
        for (int i = 0; i < n; ++i) {
            stream.writeObject(saveNames.get(i));
            try {
                stream.writeObject(saveValues.get(i));
                continue;
            }
            catch (NotSerializableException e) {
                log.error((Object)sm.getString("standardSession.notSerializable", new Object[]{saveNames.get(i), this.id}), (Throwable)e);
            }
        }
        ArrayList<SessionListener> saveListeners = new ArrayList<SessionListener>();
        for (SessionListener listener : this.listeners) {
            if (!(listener instanceof ReplicatedSessionListener)) continue;
            saveListeners.add(listener);
        }
        stream.writeObject(saveListeners.size());
        for (SessionListener listener : saveListeners) {
            stream.writeObject(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeAttributeInternal(String name, boolean notify, boolean addDeltaRequest) {
        this.lockInternal();
        try {
            Object value = this.attributes.get(name);
            if (value == null) {
                return;
            }
            super.removeAttributeInternal(name, notify);
            if (addDeltaRequest && !this.exclude(name, null)) {
                this.deltaRequest.removeAttribute(name);
            }
        }
        finally {
            this.unlockInternal();
        }
    }

    public long getLastTimeReplicated() {
        return this.lastTimeReplicated;
    }

    public long getVersion() {
        return this.version;
    }

    public void setLastTimeReplicated(long lastTimeReplicated) {
        this.lastTimeReplicated = lastTimeReplicated;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    protected void setAccessCount(int count) {
        if (this.accessCount == null && ACTIVITY_CHECK) {
            this.accessCount = new AtomicInteger();
        }
        if (this.accessCount != null) {
            this.accessCount.set(count);
        }
    }
}

