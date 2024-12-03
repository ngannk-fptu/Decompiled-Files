/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Engine
 *  org.apache.catalina.Host
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.LifecycleState
 *  org.apache.catalina.Session
 *  org.apache.catalina.tribes.Member
 *  org.apache.catalina.tribes.io.ReplicationStream
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.session;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.session.ClusterManagerBase;
import org.apache.catalina.ha.session.DeltaRequest;
import org.apache.catalina.ha.session.DeltaSession;
import org.apache.catalina.ha.session.SessionMessage;
import org.apache.catalina.ha.session.SessionMessageImpl;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.io.ReplicationStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class DeltaManager
extends ClusterManagerBase {
    public final Log log = LogFactory.getLog(DeltaManager.class);
    protected static final StringManager sm = StringManager.getManager(DeltaManager.class);
    protected String name = null;
    private boolean expireSessionsOnShutdown = false;
    private boolean notifySessionListenersOnReplication = true;
    private boolean notifyContainerListenersOnReplication = true;
    private volatile boolean stateTransferred = false;
    private volatile boolean noContextManagerReceived = false;
    private int stateTransferTimeout = 60;
    private boolean sendAllSessions = true;
    private int sendAllSessionsSize = 1000;
    private int sendAllSessionsWaitTime = 2000;
    private final ArrayList<SessionMessage> receivedMessageQueue = new ArrayList();
    private boolean receiverQueue = false;
    private boolean stateTimestampDrop = true;
    private volatile long stateTransferCreateSendTime;
    private volatile long sessionReplaceCounter = 0L;
    private volatile long counterReceive_EVT_GET_ALL_SESSIONS = 0L;
    private volatile long counterReceive_EVT_ALL_SESSION_DATA = 0L;
    private volatile long counterReceive_EVT_SESSION_CREATED = 0L;
    private volatile long counterReceive_EVT_SESSION_EXPIRED = 0L;
    private volatile long counterReceive_EVT_SESSION_ACCESSED = 0L;
    private volatile long counterReceive_EVT_SESSION_DELTA = 0L;
    private volatile int counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
    private volatile long counterReceive_EVT_CHANGE_SESSION_ID = 0L;
    private volatile long counterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER = 0L;
    private volatile long counterSend_EVT_GET_ALL_SESSIONS = 0L;
    private volatile long counterSend_EVT_ALL_SESSION_DATA = 0L;
    private volatile long counterSend_EVT_SESSION_CREATED = 0L;
    private volatile long counterSend_EVT_SESSION_DELTA = 0L;
    private volatile long counterSend_EVT_SESSION_ACCESSED = 0L;
    private volatile long counterSend_EVT_SESSION_EXPIRED = 0L;
    private volatile int counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
    private volatile long counterSend_EVT_CHANGE_SESSION_ID = 0L;
    private volatile int counterNoStateTransferred = 0;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public long getCounterSend_EVT_GET_ALL_SESSIONS() {
        return this.counterSend_EVT_GET_ALL_SESSIONS;
    }

    public long getCounterSend_EVT_SESSION_ACCESSED() {
        return this.counterSend_EVT_SESSION_ACCESSED;
    }

    public long getCounterSend_EVT_SESSION_CREATED() {
        return this.counterSend_EVT_SESSION_CREATED;
    }

    public long getCounterSend_EVT_SESSION_DELTA() {
        return this.counterSend_EVT_SESSION_DELTA;
    }

    public long getCounterSend_EVT_SESSION_EXPIRED() {
        return this.counterSend_EVT_SESSION_EXPIRED;
    }

    public long getCounterSend_EVT_ALL_SESSION_DATA() {
        return this.counterSend_EVT_ALL_SESSION_DATA;
    }

    public int getCounterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE() {
        return this.counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE;
    }

    public long getCounterSend_EVT_CHANGE_SESSION_ID() {
        return this.counterSend_EVT_CHANGE_SESSION_ID;
    }

    public long getCounterReceive_EVT_ALL_SESSION_DATA() {
        return this.counterReceive_EVT_ALL_SESSION_DATA;
    }

    public long getCounterReceive_EVT_GET_ALL_SESSIONS() {
        return this.counterReceive_EVT_GET_ALL_SESSIONS;
    }

    public long getCounterReceive_EVT_SESSION_ACCESSED() {
        return this.counterReceive_EVT_SESSION_ACCESSED;
    }

    public long getCounterReceive_EVT_SESSION_CREATED() {
        return this.counterReceive_EVT_SESSION_CREATED;
    }

    public long getCounterReceive_EVT_SESSION_DELTA() {
        return this.counterReceive_EVT_SESSION_DELTA;
    }

    public long getCounterReceive_EVT_SESSION_EXPIRED() {
        return this.counterReceive_EVT_SESSION_EXPIRED;
    }

    public int getCounterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE() {
        return this.counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE;
    }

    public long getCounterReceive_EVT_CHANGE_SESSION_ID() {
        return this.counterReceive_EVT_CHANGE_SESSION_ID;
    }

    public long getCounterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER() {
        return this.counterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER;
    }

    public long getSessionReplaceCounter() {
        return this.sessionReplaceCounter;
    }

    @Deprecated
    public int getCounterNoStateTransfered() {
        return this.getCounterNoStateTransferred();
    }

    public int getCounterNoStateTransferred() {
        return this.counterNoStateTransferred;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getReceivedQueueSize() {
        ArrayList<SessionMessage> arrayList = this.receivedMessageQueue;
        synchronized (arrayList) {
            return this.receivedMessageQueue.size();
        }
    }

    public int getStateTransferTimeout() {
        return this.stateTransferTimeout;
    }

    public void setStateTransferTimeout(int timeoutAllSession) {
        this.stateTransferTimeout = timeoutAllSession;
    }

    @Deprecated
    public boolean getStateTransfered() {
        return this.getStateTransferred();
    }

    @Deprecated
    public void setStateTransfered(boolean stateTransferred) {
        this.setStateTransferred(stateTransferred);
    }

    public boolean getStateTransferred() {
        return this.stateTransferred;
    }

    public void setStateTransferred(boolean stateTransferred) {
        this.stateTransferred = stateTransferred;
    }

    public boolean isNoContextManagerReceived() {
        return this.noContextManagerReceived;
    }

    public void setNoContextManagerReceived(boolean noContextManagerReceived) {
        this.noContextManagerReceived = noContextManagerReceived;
    }

    public int getSendAllSessionsWaitTime() {
        return this.sendAllSessionsWaitTime;
    }

    public void setSendAllSessionsWaitTime(int sendAllSessionsWaitTime) {
        this.sendAllSessionsWaitTime = sendAllSessionsWaitTime;
    }

    public boolean isStateTimestampDrop() {
        return this.stateTimestampDrop;
    }

    public void setStateTimestampDrop(boolean isTimestampDrop) {
        this.stateTimestampDrop = isTimestampDrop;
    }

    public boolean isSendAllSessions() {
        return this.sendAllSessions;
    }

    public void setSendAllSessions(boolean sendAllSessions) {
        this.sendAllSessions = sendAllSessions;
    }

    public int getSendAllSessionsSize() {
        return this.sendAllSessionsSize;
    }

    public void setSendAllSessionsSize(int sendAllSessionsSize) {
        this.sendAllSessionsSize = sendAllSessionsSize;
    }

    public boolean isNotifySessionListenersOnReplication() {
        return this.notifySessionListenersOnReplication;
    }

    public void setNotifySessionListenersOnReplication(boolean notifyListenersCreateSessionOnReplication) {
        this.notifySessionListenersOnReplication = notifyListenersCreateSessionOnReplication;
    }

    public boolean isExpireSessionsOnShutdown() {
        return this.expireSessionsOnShutdown;
    }

    public void setExpireSessionsOnShutdown(boolean expireSessionsOnShutdown) {
        this.expireSessionsOnShutdown = expireSessionsOnShutdown;
    }

    public boolean isNotifyContainerListenersOnReplication() {
        return this.notifyContainerListenersOnReplication;
    }

    public void setNotifyContainerListenersOnReplication(boolean notifyContainerListenersOnReplication) {
        this.notifyContainerListenersOnReplication = notifyContainerListenersOnReplication;
    }

    public Session createSession(String sessionId) {
        return this.createSession(sessionId, true);
    }

    public Session createSession(String sessionId, boolean distribute) {
        DeltaSession session = (DeltaSession)super.createSession(sessionId);
        if (distribute) {
            this.sendCreateSession(session.getId(), session);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.createSession.newSession", new Object[]{session.getId(), this.sessions.size()}));
        }
        return session;
    }

    protected void sendCreateSession(String sessionId, DeltaSession session) {
        if (this.cluster.getMembers().length > 0) {
            SessionMessageImpl msg = new SessionMessageImpl(this.getName(), 1, null, sessionId, sessionId + "-" + System.currentTimeMillis());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("deltaManager.sendMessage.newSession", new Object[]{this.name, sessionId}));
            }
            msg.setTimestamp(session.getCreationTime());
            ++this.counterSend_EVT_SESSION_CREATED;
            this.send(msg);
        }
    }

    protected void send(SessionMessage msg) {
        if (this.cluster != null) {
            this.cluster.send(msg);
        }
    }

    public Session createEmptySession() {
        return new DeltaSession(this);
    }

    @Deprecated
    protected DeltaSession getNewDeltaSession() {
        return new DeltaSession(this);
    }

    public void changeSessionId(Session session) {
        this.rotateSessionId(session);
    }

    public String rotateSessionId(Session session) {
        return this.rotateSessionId(session, true);
    }

    public void changeSessionId(Session session, String newId) {
        this.changeSessionId(session, newId, true);
    }

    @Deprecated
    protected void changeSessionId(Session session, boolean notify) {
        String orgSessionID = session.getId();
        super.changeSessionId(session);
        if (notify) {
            this.sendChangeSessionId(session.getId(), orgSessionID);
        }
    }

    protected String rotateSessionId(Session session, boolean notify) {
        String orgSessionID = session.getId();
        String newId = super.rotateSessionId(session);
        if (notify) {
            this.sendChangeSessionId(session.getId(), orgSessionID);
        }
        return newId;
    }

    protected void changeSessionId(Session session, String newId, boolean notify) {
        String orgSessionID = session.getId();
        super.changeSessionId(session, newId);
        if (notify) {
            this.sendChangeSessionId(session.getId(), orgSessionID);
        }
    }

    protected void sendChangeSessionId(String newSessionID, String orgSessionID) {
        if (this.cluster.getMembers().length > 0) {
            try {
                byte[] data = this.serializeSessionId(newSessionID);
                SessionMessageImpl msg = new SessionMessageImpl(this.getName(), 15, data, orgSessionID, orgSessionID + "-" + System.currentTimeMillis());
                msg.setTimestamp(System.currentTimeMillis());
                ++this.counterSend_EVT_CHANGE_SESSION_ID;
                this.send(msg);
            }
            catch (IOException e) {
                this.log.error((Object)sm.getString("deltaManager.unableSerializeSessionID", new Object[]{newSessionID}), (Throwable)e);
            }
        }
    }

    protected byte[] serializeSessionId(String sessionId) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeUTF(sessionId);
        oos.flush();
        oos.close();
        return bos.toByteArray();
    }

    protected String deserializeSessionId(byte[] data) throws IOException {
        ReplicationStream ois = this.getReplicationStream(data);
        String sessionId = ois.readUTF();
        ois.close();
        return sessionId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    protected DeltaRequest deserializeDeltaRequest(DeltaSession session, byte[] data) throws ClassNotFoundException, IOException {
        session.lock();
        try {
            ReplicationStream ois = this.getReplicationStream(data);
            session.getDeltaRequest().readExternal((ObjectInput)ois);
            ois.close();
            DeltaRequest deltaRequest = session.getDeltaRequest();
            return deltaRequest;
        }
        finally {
            session.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    protected byte[] serializeDeltaRequest(DeltaSession session, DeltaRequest deltaRequest) throws IOException {
        session.lock();
        try {
            byte[] byArray = deltaRequest.serialize();
            return byArray;
        }
        finally {
            session.unlock();
        }
    }

    protected void deserializeSessions(byte[] data) throws ClassNotFoundException, IOException {
        try (ReplicationStream ois = this.getReplicationStream(data);){
            Integer count = (Integer)ois.readObject();
            int n = count;
            for (int i = 0; i < n; ++i) {
                DeltaSession session = (DeltaSession)this.createEmptySession();
                session.readObjectData((ObjectInputStream)ois);
                session.setManager(this);
                session.setValid(true);
                session.setPrimarySession(false);
                session.access();
                session.setAccessCount(0);
                session.resetDeltaRequest();
                if (this.findSession(session.getIdInternal()) == null) {
                    ++this.sessionCounter;
                } else {
                    ++this.sessionReplaceCounter;
                    if (this.log.isWarnEnabled()) {
                        this.log.warn((Object)sm.getString("deltaManager.loading.existing.session", new Object[]{session.getIdInternal()}));
                    }
                }
                this.add(session);
                if (!this.notifySessionListenersOnReplication) continue;
                session.tellNew();
            }
        }
        catch (ClassNotFoundException e) {
            this.log.error((Object)sm.getString("deltaManager.loading.cnfe", new Object[]{e}), (Throwable)e);
            throw e;
        }
        catch (IOException e) {
            this.log.error((Object)sm.getString("deltaManager.loading.ioe", new Object[]{e}), (Throwable)e);
            throw e;
        }
    }

    protected byte[] serializeSessions(Session[] currentSessions) throws IOException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));){
            oos.writeObject(currentSessions.length);
            for (Session currentSession : currentSessions) {
                ((DeltaSession)currentSession).writeObjectData(oos);
            }
            oos.flush();
        }
        catch (IOException e) {
            this.log.error((Object)sm.getString("deltaManager.unloading.ioe", new Object[]{e}), (Throwable)e);
            throw e;
        }
        return fos.toByteArray();
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            if (this.cluster == null) {
                this.log.error((Object)sm.getString("deltaManager.noCluster", new Object[]{this.getName()}));
                return;
            }
            if (this.log.isInfoEnabled()) {
                String type = "unknown";
                if (this.cluster.getContainer() instanceof Host) {
                    type = "Host";
                } else if (this.cluster.getContainer() instanceof Engine) {
                    type = "Engine";
                }
                this.log.info((Object)sm.getString("deltaManager.registerCluster", new Object[]{this.getName(), type, this.cluster.getClusterName()}));
            }
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("deltaManager.startClustering", new Object[]{this.getName()}));
            }
            this.getAllClusterSessions();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log.error((Object)sm.getString("deltaManager.managerLoad"), t);
        }
        this.setState(LifecycleState.STARTING);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void getAllClusterSessions() {
        if (this.cluster != null && this.cluster.getMembers().length > 0) {
            ArrayList<SessionMessage> arrayList;
            long beforeSendTime = System.currentTimeMillis();
            Member mbr = this.findSessionMasterMember();
            if (mbr == null) {
                return;
            }
            SessionMessageImpl msg = new SessionMessageImpl(this.getName(), 4, null, "GET-ALL", "GET-ALL-" + this.getName());
            msg.setTimestamp(beforeSendTime);
            this.stateTransferCreateSendTime = beforeSendTime;
            ++this.counterSend_EVT_GET_ALL_SESSIONS;
            this.stateTransferred = false;
            try {
                arrayList = this.receivedMessageQueue;
                synchronized (arrayList) {
                    this.receiverQueue = true;
                }
                this.cluster.send(msg, mbr, 8);
                if (this.log.isInfoEnabled()) {
                    this.log.info((Object)sm.getString("deltaManager.waitForSessionState", new Object[]{this.getName(), mbr, this.getStateTransferTimeout()}));
                }
                this.waitForSendAllSessions(beforeSendTime);
                arrayList = this.receivedMessageQueue;
            }
            catch (Throwable throwable) {
                ArrayList<SessionMessage> arrayList2 = this.receivedMessageQueue;
                synchronized (arrayList2) {
                    for (SessionMessage smsg : this.receivedMessageQueue) {
                        if (!this.stateTimestampDrop) {
                            this.messageReceived(smsg, smsg.getAddress());
                            continue;
                        }
                        if (smsg.getEventType() != 4 && smsg.getTimestamp() >= this.stateTransferCreateSendTime) {
                            this.messageReceived(smsg, smsg.getAddress());
                            continue;
                        }
                        if (!this.log.isWarnEnabled()) continue;
                        this.log.warn((Object)sm.getString("deltaManager.dropMessage", new Object[]{this.getName(), smsg.getEventTypeString(), new Date(this.stateTransferCreateSendTime), new Date(smsg.getTimestamp())}));
                    }
                    this.receivedMessageQueue.clear();
                    this.receiverQueue = false;
                }
                throw throwable;
            }
            synchronized (arrayList) {
                for (SessionMessage smsg : this.receivedMessageQueue) {
                    if (!this.stateTimestampDrop) {
                        this.messageReceived(smsg, smsg.getAddress());
                        continue;
                    }
                    if (smsg.getEventType() != 4 && smsg.getTimestamp() >= this.stateTransferCreateSendTime) {
                        this.messageReceived(smsg, smsg.getAddress());
                        continue;
                    }
                    if (!this.log.isWarnEnabled()) continue;
                    this.log.warn((Object)sm.getString("deltaManager.dropMessage", new Object[]{this.getName(), smsg.getEventTypeString(), new Date(this.stateTransferCreateSendTime), new Date(smsg.getTimestamp())}));
                }
                this.receivedMessageQueue.clear();
                this.receiverQueue = false;
            }
        }
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)sm.getString("deltaManager.noMembers", new Object[]{this.getName()}));
        }
    }

    protected Member findSessionMasterMember() {
        Member mbr = null;
        Member[] mbrs = this.cluster.getMembers();
        if (mbrs.length != 0) {
            mbr = mbrs[0];
        }
        if (mbr == null && this.log.isWarnEnabled()) {
            this.log.warn((Object)sm.getString("deltaManager.noMasterMember", new Object[]{this.getName(), ""}));
        }
        if (mbr != null && this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.foundMasterMember", new Object[]{this.getName(), mbr}));
        }
        return mbr;
    }

    protected void waitForSendAllSessions(long beforeSendTime) {
        long reqStart;
        long reqNow = reqStart = System.currentTimeMillis();
        boolean isTimeout = false;
        if (this.getStateTransferTimeout() > 0) {
            do {
                try {
                    Thread.sleep(100L);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                reqNow = System.currentTimeMillis();
                boolean bl = isTimeout = reqNow - reqStart > 1000L * (long)this.getStateTransferTimeout();
            } while (!this.getStateTransferred() && !isTimeout && !this.isNoContextManagerReceived());
        } else if (this.getStateTransferTimeout() == -1) {
            do {
                try {
                    Thread.sleep(100L);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            } while (!this.getStateTransferred() && !this.isNoContextManagerReceived());
            reqNow = System.currentTimeMillis();
        }
        if (isTimeout) {
            ++this.counterNoStateTransferred;
            this.log.error((Object)sm.getString("deltaManager.noSessionState", new Object[]{this.getName(), new Date(beforeSendTime), reqNow - beforeSendTime}));
        } else if (this.isNoContextManagerReceived()) {
            if (this.log.isWarnEnabled()) {
                this.log.warn((Object)sm.getString("deltaManager.noContextManager", new Object[]{this.getName(), new Date(beforeSendTime), reqNow - beforeSendTime}));
            }
        } else if (this.log.isInfoEnabled()) {
            this.log.info((Object)sm.getString("deltaManager.sessionReceived", new Object[]{this.getName(), new Date(beforeSendTime), reqNow - beforeSendTime}));
        }
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        Session[] sessions;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.stopped", new Object[]{this.getName()}));
        }
        this.setState(LifecycleState.STOPPING);
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)sm.getString("deltaManager.expireSessions", new Object[]{this.getName()}));
        }
        for (Session value : sessions = this.findSessions()) {
            DeltaSession session = (DeltaSession)value;
            if (!session.isValid()) continue;
            try {
                session.expire(true, this.isExpireSessionsOnShutdown());
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
            }
        }
        super.stopInternal();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void messageDataReceived(ClusterMessage cmsg) {
        if (cmsg instanceof SessionMessage) {
            SessionMessage msg = (SessionMessage)cmsg;
            switch (msg.getEventType()) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 13: 
                case 15: {
                    ArrayList<SessionMessage> arrayList = this.receivedMessageQueue;
                    synchronized (arrayList) {
                        if (this.receiverQueue) {
                            this.receivedMessageQueue.add(msg);
                            return;
                        }
                        break;
                    }
                }
            }
            this.messageReceived(msg, msg.getAddress());
        }
    }

    @Override
    public ClusterMessage requestCompleted(String sessionId) {
        return this.requestCompleted(sessionId, false);
    }

    public ClusterMessage requestCompleted(String sessionId, boolean expires) {
        DeltaSession session = null;
        ClusterMessage msg = null;
        try {
            session = (DeltaSession)this.findSession(sessionId);
            if (session == null) {
                return null;
            }
            if (session.isDirty()) {
                ++this.counterSend_EVT_SESSION_DELTA;
                msg = new SessionMessageImpl(this.getName(), 13, session.getDiff(), sessionId, sessionId + "-" + System.currentTimeMillis());
            }
        }
        catch (IOException x) {
            this.log.error((Object)sm.getString("deltaManager.createMessage.unableCreateDeltaRequest", new Object[]{sessionId}), (Throwable)x);
            return null;
        }
        if (msg == null) {
            if (!expires && !session.isPrimarySession()) {
                ++this.counterSend_EVT_SESSION_ACCESSED;
                msg = new SessionMessageImpl(this.getName(), 3, null, sessionId, sessionId + "-" + System.currentTimeMillis());
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("deltaManager.createMessage.accessChangePrimary", new Object[]{this.getName(), sessionId}));
                }
            }
        } else if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.createMessage.delta", new Object[]{this.getName(), sessionId}));
        }
        if (!expires) {
            session.setPrimarySession(true);
        }
        if (!expires && msg == null) {
            long replDelta = System.currentTimeMillis() - session.getLastTimeReplicated();
            if (session.getMaxInactiveInterval() >= 0 && replDelta > (long)session.getMaxInactiveInterval() * 1000L) {
                ++this.counterSend_EVT_SESSION_ACCESSED;
                msg = new SessionMessageImpl(this.getName(), 3, null, sessionId, sessionId + "-" + System.currentTimeMillis());
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("deltaManager.createMessage.access", new Object[]{this.getName(), sessionId}));
                }
            }
        }
        if (msg != null) {
            session.setLastTimeReplicated(System.currentTimeMillis());
            msg.setTimestamp(session.getLastTimeReplicated());
        }
        return msg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void resetStatistics() {
        this.processingTime = 0L;
        this.expiredSessions.set(0L);
        Deque deque = this.sessionCreationTiming;
        synchronized (deque) {
            this.sessionCreationTiming.clear();
            while (this.sessionCreationTiming.size() < 100) {
                this.sessionCreationTiming.add(null);
            }
        }
        deque = this.sessionExpirationTiming;
        synchronized (deque) {
            this.sessionExpirationTiming.clear();
            while (this.sessionExpirationTiming.size() < 100) {
                this.sessionExpirationTiming.add(null);
            }
        }
        this.rejectedSessions = 0;
        this.sessionReplaceCounter = 0L;
        this.counterNoStateTransferred = 0;
        this.setMaxActive(this.getActiveSessions());
        this.sessionCounter = this.getActiveSessions();
        this.counterReceive_EVT_ALL_SESSION_DATA = 0L;
        this.counterReceive_EVT_GET_ALL_SESSIONS = 0L;
        this.counterReceive_EVT_SESSION_ACCESSED = 0L;
        this.counterReceive_EVT_SESSION_CREATED = 0L;
        this.counterReceive_EVT_SESSION_DELTA = 0L;
        this.counterReceive_EVT_SESSION_EXPIRED = 0L;
        this.counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
        this.counterReceive_EVT_CHANGE_SESSION_ID = 0L;
        this.counterSend_EVT_ALL_SESSION_DATA = 0L;
        this.counterSend_EVT_GET_ALL_SESSIONS = 0L;
        this.counterSend_EVT_SESSION_ACCESSED = 0L;
        this.counterSend_EVT_SESSION_CREATED = 0L;
        this.counterSend_EVT_SESSION_DELTA = 0L;
        this.counterSend_EVT_SESSION_EXPIRED = 0L;
        this.counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE = 0;
        this.counterSend_EVT_CHANGE_SESSION_ID = 0L;
    }

    protected void sessionExpired(String id) {
        if (this.cluster.getMembers().length > 0) {
            ++this.counterSend_EVT_SESSION_EXPIRED;
            SessionMessageImpl msg = new SessionMessageImpl(this.getName(), 2, null, id, id + "-EXPIRED-MSG");
            msg.setTimestamp(System.currentTimeMillis());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("deltaManager.createMessage.expire", new Object[]{this.getName(), id}));
            }
            this.send(msg);
        }
    }

    public void expireAllLocalSessions() {
        long timeNow = System.currentTimeMillis();
        Session[] sessions = this.findSessions();
        int expireDirect = 0;
        int expireIndirect = 0;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Start expire all sessions " + this.getName() + " at " + timeNow + " sessioncount " + sessions.length));
        }
        for (Session value : sessions) {
            DeltaSession session;
            if (!(value instanceof DeltaSession) || !(session = (DeltaSession)value).isPrimarySession()) continue;
            if (session.isValid()) {
                session.expire();
                ++expireDirect;
                continue;
            }
            ++expireIndirect;
        }
        long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("End expire sessions " + this.getName() + " expire processingTime " + (timeEnd - timeNow) + " expired direct sessions: " + expireDirect + " expired direct sessions: " + expireIndirect));
        }
    }

    @Override
    public String[] getInvalidatedSessions() {
        return new String[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void messageReceived(SessionMessage msg, Member sender) {
        Thread currentThread = Thread.currentThread();
        ClassLoader contextLoader = currentThread.getContextClassLoader();
        try {
            ClassLoader[] loaders = this.getClassLoaders();
            currentThread.setContextClassLoader(loaders[0]);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("deltaManager.receiveMessage.eventType", new Object[]{this.getName(), msg.getEventTypeString(), sender}));
            }
            switch (msg.getEventType()) {
                case 4: {
                    this.handleGET_ALL_SESSIONS(msg, sender);
                    return;
                }
                case 12: {
                    this.handleALL_SESSION_DATA(msg, sender);
                    return;
                }
                case 14: {
                    this.handleALL_SESSION_TRANSFERCOMPLETE(msg, sender);
                    return;
                }
                case 1: {
                    this.handleSESSION_CREATED(msg, sender);
                    return;
                }
                case 2: {
                    this.handleSESSION_EXPIRED(msg, sender);
                    return;
                }
                case 3: {
                    this.handleSESSION_ACCESSED(msg, sender);
                    return;
                }
                case 13: {
                    this.handleSESSION_DELTA(msg, sender);
                    return;
                }
                case 15: {
                    this.handleCHANGE_SESSION_ID(msg, sender);
                    return;
                }
                case 16: {
                    this.handleALL_SESSION_NOCONTEXTMANAGER(msg, sender);
                    return;
                }
            }
            return;
        }
        catch (Exception x) {
            this.log.error((Object)sm.getString("deltaManager.receiveMessage.error", new Object[]{this.getName()}), (Throwable)x);
            return;
        }
        finally {
            currentThread.setContextClassLoader(contextLoader);
        }
    }

    protected void handleALL_SESSION_TRANSFERCOMPLETE(SessionMessage msg, Member sender) {
        ++this.counterReceive_EVT_ALL_SESSION_TRANSFERCOMPLETE;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.receiveMessage.transfercomplete", new Object[]{this.getName(), sender.getHost(), sender.getPort()}));
        }
        this.stateTransferCreateSendTime = msg.getTimestamp();
        this.stateTransferred = true;
    }

    protected void handleSESSION_DELTA(SessionMessage msg, Member sender) throws IOException, ClassNotFoundException {
        ++this.counterReceive_EVT_SESSION_DELTA;
        byte[] delta = msg.getSession();
        DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("deltaManager.receiveMessage.delta.unknown", new Object[]{this.getName(), msg.getSessionID()}));
            }
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("deltaManager.receiveMessage.delta", new Object[]{this.getName(), msg.getSessionID()}));
            }
            session.deserializeAndExecuteDeltaRequest(delta);
        }
    }

    protected void handleSESSION_ACCESSED(SessionMessage msg, Member sender) throws IOException {
        ++this.counterReceive_EVT_SESSION_ACCESSED;
        DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("deltaManager.receiveMessage.accessed", new Object[]{this.getName(), msg.getSessionID()}));
            }
            session.access();
            session.setPrimarySession(false);
            session.endAccess();
        }
    }

    protected void handleSESSION_EXPIRED(SessionMessage msg, Member sender) throws IOException {
        ++this.counterReceive_EVT_SESSION_EXPIRED;
        DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("deltaManager.receiveMessage.expired", new Object[]{this.getName(), msg.getSessionID()}));
            }
            session.expire(this.notifySessionListenersOnReplication, false);
        }
    }

    protected void handleSESSION_CREATED(SessionMessage msg, Member sender) {
        ++this.counterReceive_EVT_SESSION_CREATED;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.receiveMessage.createNewSession", new Object[]{this.getName(), msg.getSessionID()}));
        }
        DeltaSession session = (DeltaSession)this.createEmptySession();
        session.setValid(true);
        session.setPrimarySession(false);
        session.setCreationTime(msg.getTimestamp());
        session.setMaxInactiveInterval(this.getContext().getSessionTimeout() * 60, false);
        session.access();
        session.setId(msg.getSessionID(), this.notifySessionListenersOnReplication);
        session.endAccess();
    }

    protected void handleALL_SESSION_DATA(SessionMessage msg, Member sender) throws ClassNotFoundException, IOException {
        ++this.counterReceive_EVT_ALL_SESSION_DATA;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.receiveMessage.allSessionDataBegin", new Object[]{this.getName()}));
        }
        byte[] data = msg.getSession();
        this.deserializeSessions(data);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.receiveMessage.allSessionDataAfter", new Object[]{this.getName()}));
        }
    }

    protected void handleGET_ALL_SESSIONS(SessionMessage msg, Member sender) throws IOException {
        ++this.counterReceive_EVT_GET_ALL_SESSIONS;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.receiveMessage.unloadingBegin", new Object[]{this.getName()}));
        }
        Session[] currentSessions = this.findSessions();
        long findSessionTimestamp = System.currentTimeMillis();
        if (this.isSendAllSessions()) {
            this.sendSessions(sender, currentSessions, findSessionTimestamp);
        } else {
            int remain = currentSessions.length;
            for (int i = 0; i < currentSessions.length; i += this.getSendAllSessionsSize()) {
                int len = i + this.getSendAllSessionsSize() > currentSessions.length ? currentSessions.length - i : this.getSendAllSessionsSize();
                Session[] sendSessions = new Session[len];
                System.arraycopy(currentSessions, i, sendSessions, 0, len);
                this.sendSessions(sender, sendSessions, findSessionTimestamp);
                if (this.getSendAllSessionsWaitTime() <= 0 || (remain -= len) <= 0) continue;
                try {
                    Thread.sleep(this.getSendAllSessionsWaitTime());
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        SessionMessageImpl newmsg = new SessionMessageImpl(this.name, 14, null, "SESSION-STATE-TRANSFERRED", "SESSION-STATE-TRANSFERRED" + this.getName());
        newmsg.setTimestamp(findSessionTimestamp);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.createMessage.allSessionTransferred", new Object[]{this.getName()}));
        }
        ++this.counterSend_EVT_ALL_SESSION_TRANSFERCOMPLETE;
        this.cluster.send(newmsg, sender);
    }

    protected void handleCHANGE_SESSION_ID(SessionMessage msg, Member sender) throws IOException {
        ++this.counterReceive_EVT_CHANGE_SESSION_ID;
        DeltaSession session = (DeltaSession)this.findSession(msg.getSessionID());
        if (session != null) {
            String newSessionID = this.deserializeSessionId(msg.getSession());
            session.setPrimarySession(false);
            this.changeSessionId(session, newSessionID, this.notifySessionListenersOnReplication, this.notifyContainerListenersOnReplication);
        }
    }

    protected void handleALL_SESSION_NOCONTEXTMANAGER(SessionMessage msg, Member sender) {
        ++this.counterReceive_EVT_ALL_SESSION_NOCONTEXTMANAGER;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.receiveMessage.noContextManager", new Object[]{this.getName(), sender.getHost(), sender.getPort()}));
        }
        this.noContextManagerReceived = true;
    }

    protected void sendSessions(Member sender, Session[] currentSessions, long sendTimestamp) throws IOException {
        byte[] data = this.serializeSessions(currentSessions);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.receiveMessage.unloadingAfter", new Object[]{this.getName()}));
        }
        SessionMessageImpl newmsg = new SessionMessageImpl(this.name, 12, data, "SESSION-STATE", "SESSION-STATE-" + this.getName());
        newmsg.setTimestamp(sendTimestamp);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("deltaManager.createMessage.allSessionData", new Object[]{this.getName()}));
        }
        ++this.counterSend_EVT_ALL_SESSION_DATA;
        int sendOptions = 6;
        this.cluster.send(newmsg, sender, sendOptions);
    }

    @Override
    public ClusterManager cloneFromTemplate() {
        DeltaManager result = new DeltaManager();
        this.clone(result);
        result.expireSessionsOnShutdown = this.expireSessionsOnShutdown;
        result.notifySessionListenersOnReplication = this.notifySessionListenersOnReplication;
        result.notifyContainerListenersOnReplication = this.notifyContainerListenersOnReplication;
        result.stateTransferTimeout = this.stateTransferTimeout;
        result.sendAllSessions = this.sendAllSessions;
        result.sendAllSessionsSize = this.sendAllSessionsSize;
        result.sendAllSessionsWaitTime = this.sendAllSessionsWaitTime;
        result.stateTimestampDrop = this.stateTimestampDrop;
        return result;
    }
}

