/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.DistributedManager
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.LifecycleState
 *  org.apache.catalina.Session
 *  org.apache.catalina.tribes.Channel
 *  org.apache.catalina.tribes.tipis.AbstractReplicatedMap$MapOwner
 *  org.apache.catalina.tribes.tipis.LazyReplicatedMap
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.session;

import java.util.HashSet;
import java.util.Set;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.session.ClusterManagerBase;
import org.apache.catalina.ha.session.DeltaSession;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.tribes.tipis.LazyReplicatedMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class BackupManager
extends ClusterManagerBase
implements AbstractReplicatedMap.MapOwner,
DistributedManager {
    private final Log log = LogFactory.getLog(BackupManager.class);
    protected static final StringManager sm = StringManager.getManager(BackupManager.class);
    protected static final long DEFAULT_REPL_TIMEOUT = 15000L;
    protected String name;
    private int mapSendOptions = 6;
    private long rpcTimeout = 15000L;
    private boolean terminateOnStartFailure = false;
    private long accessTimeout = 5000L;

    @Override
    public void messageDataReceived(ClusterMessage msg) {
    }

    @Override
    public ClusterMessage requestCompleted(String sessionId) {
        if (!this.getState().isAvailable()) {
            return null;
        }
        LazyReplicatedMap map = (LazyReplicatedMap)this.sessions;
        map.replicate((Object)sessionId, false);
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void objectMadePrimary(Object key, Object value) {
        if (value instanceof DeltaSession) {
            DeltaSession session;
            DeltaSession deltaSession = session = (DeltaSession)value;
            synchronized (deltaSession) {
                session.access();
                session.setPrimarySession(true);
                session.endAccess();
            }
        }
    }

    public Session createEmptySession() {
        return new DeltaSession(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            if (this.cluster == null) {
                throw new LifecycleException(sm.getString("backupManager.noCluster", new Object[]{this.getName()}));
            }
            LazyReplicatedMap map = new LazyReplicatedMap((AbstractReplicatedMap.MapOwner)this, this.cluster.getChannel(), this.rpcTimeout, this.getMapName(), this.getClassLoaders(), this.terminateOnStartFailure);
            map.setChannelSendOptions(this.mapSendOptions);
            map.setAccessTimeout(this.accessTimeout);
            this.sessions = map;
        }
        catch (Exception x) {
            this.log.error((Object)sm.getString("backupManager.startUnable", new Object[]{this.getName()}), (Throwable)x);
            throw new LifecycleException(sm.getString("backupManager.startFailed", new Object[]{this.getName()}), (Throwable)x);
        }
        this.setState(LifecycleState.STARTING);
    }

    public String getMapName() {
        String name = this.cluster.getManagerName(this.getName(), this) + "-map";
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Backup manager, Setting map name to:" + name));
        }
        return name;
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("backupManager.stopped", new Object[]{this.getName()}));
        }
        this.setState(LifecycleState.STOPPING);
        if (this.sessions instanceof LazyReplicatedMap) {
            LazyReplicatedMap map = (LazyReplicatedMap)this.sessions;
            map.breakdown();
        }
        super.stopInternal();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setMapSendOptions(int mapSendOptions) {
        this.mapSendOptions = mapSendOptions;
    }

    public void setMapSendOptions(String mapSendOptions) {
        int value = Channel.parseSendOptions((String)mapSendOptions);
        if (value > 0) {
            this.setMapSendOptions(value);
        }
    }

    public int getMapSendOptions() {
        return this.mapSendOptions;
    }

    public String getMapSendOptionsName() {
        return Channel.getSendOptionsAsString((int)this.mapSendOptions);
    }

    public void setRpcTimeout(long rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }

    public long getRpcTimeout() {
        return this.rpcTimeout;
    }

    public void setTerminateOnStartFailure(boolean terminateOnStartFailure) {
        this.terminateOnStartFailure = terminateOnStartFailure;
    }

    public boolean isTerminateOnStartFailure() {
        return this.terminateOnStartFailure;
    }

    public long getAccessTimeout() {
        return this.accessTimeout;
    }

    public void setAccessTimeout(long accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

    @Override
    public String[] getInvalidatedSessions() {
        return new String[0];
    }

    @Override
    public ClusterManager cloneFromTemplate() {
        BackupManager result = new BackupManager();
        this.clone(result);
        result.mapSendOptions = this.mapSendOptions;
        result.rpcTimeout = this.rpcTimeout;
        result.terminateOnStartFailure = this.terminateOnStartFailure;
        result.accessTimeout = this.accessTimeout;
        return result;
    }

    public int getActiveSessionsFull() {
        LazyReplicatedMap map = (LazyReplicatedMap)this.sessions;
        return map.sizeFull();
    }

    public Set<String> getSessionIdsFull() {
        LazyReplicatedMap map = (LazyReplicatedMap)this.sessions;
        HashSet<String> sessionIds = new HashSet<String>(map.keySetFull());
        return sessionIds;
    }
}

