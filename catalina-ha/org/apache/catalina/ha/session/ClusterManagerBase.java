/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Cluster
 *  org.apache.catalina.Context
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.Loader
 *  org.apache.catalina.SessionIdGenerator
 *  org.apache.catalina.Valve
 *  org.apache.catalina.session.ManagerBase
 *  org.apache.catalina.tribes.io.ReplicationStream
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.collections.SynchronizedStack
 */
package org.apache.catalina.ha.session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.Cluster;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.Valve;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.session.DeltaRequest;
import org.apache.catalina.ha.session.DeltaSession;
import org.apache.catalina.ha.tcp.ReplicationValve;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.tribes.io.ReplicationStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.collections.SynchronizedStack;

public abstract class ClusterManagerBase
extends ManagerBase
implements ClusterManager {
    private final Log log = LogFactory.getLog(ClusterManagerBase.class);
    protected CatalinaCluster cluster = null;
    private boolean notifyListenersOnReplication = true;
    private volatile ReplicationValve replicationValve = null;
    private boolean recordAllActions = false;
    private SynchronizedStack<DeltaRequest> deltaRequestPool = new SynchronizedStack();

    protected SynchronizedStack<DeltaRequest> getDeltaRequestPool() {
        return this.deltaRequestPool;
    }

    @Override
    public CatalinaCluster getCluster() {
        return this.cluster;
    }

    @Override
    public void setCluster(CatalinaCluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public boolean isNotifyListenersOnReplication() {
        return this.notifyListenersOnReplication;
    }

    public void setNotifyListenersOnReplication(boolean notifyListenersOnReplication) {
        this.notifyListenersOnReplication = notifyListenersOnReplication;
    }

    public boolean isRecordAllActions() {
        return this.recordAllActions;
    }

    public void setRecordAllActions(boolean recordAllActions) {
        this.recordAllActions = recordAllActions;
    }

    public static ClassLoader[] getClassLoaders(Context context) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Loader loader = context.getLoader();
        ClassLoader classLoader = null;
        if (loader != null) {
            classLoader = loader.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = tccl;
        }
        if (classLoader == tccl) {
            return new ClassLoader[]{classLoader};
        }
        return new ClassLoader[]{classLoader, tccl};
    }

    public ClassLoader[] getClassLoaders() {
        return ClusterManagerBase.getClassLoaders(this.getContext());
    }

    @Override
    public ReplicationStream getReplicationStream(byte[] data) throws IOException {
        return this.getReplicationStream(data, 0, data.length);
    }

    @Override
    public ReplicationStream getReplicationStream(byte[] data, int offset, int length) throws IOException {
        ByteArrayInputStream fis = new ByteArrayInputStream(data, offset, length);
        return new ReplicationStream((InputStream)fis, this.getClassLoaders());
    }

    public void load() {
    }

    public void unload() {
    }

    protected void clone(ClusterManagerBase copy) {
        copy.setName("Clone-from-" + this.getName());
        copy.setMaxActiveSessions(this.getMaxActiveSessions());
        copy.setProcessExpiresFrequency(this.getProcessExpiresFrequency());
        copy.setNotifyListenersOnReplication(this.isNotifyListenersOnReplication());
        copy.setSessionAttributeNameFilter(this.getSessionAttributeNameFilter());
        copy.setSessionAttributeValueClassNameFilter(this.getSessionAttributeValueClassNameFilter());
        copy.setWarnOnSessionAttributeFilterFailure(this.getWarnOnSessionAttributeFilterFailure());
        copy.setSecureRandomClass(this.getSecureRandomClass());
        copy.setSecureRandomProvider(this.getSecureRandomProvider());
        copy.setSecureRandomAlgorithm(this.getSecureRandomAlgorithm());
        if (this.getSessionIdGenerator() != null) {
            try {
                SessionIdGenerator copyIdGenerator = (SessionIdGenerator)this.sessionIdGeneratorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                copyIdGenerator.setSessionIdLength(this.getSessionIdGenerator().getSessionIdLength());
                copyIdGenerator.setJvmRoute(this.getSessionIdGenerator().getJvmRoute());
                copy.setSessionIdGenerator(copyIdGenerator);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        }
        copy.setRecordAllActions(this.isRecordAllActions());
    }

    protected void registerSessionAtReplicationValve(DeltaSession session) {
        Valve[] valves;
        CatalinaCluster cluster;
        if (this.replicationValve == null && (cluster = this.getCluster()) != null && (valves = cluster.getValves()) != null && valves.length > 0) {
            for (int i = 0; this.replicationValve == null && i < valves.length; ++i) {
                if (!(valves[i] instanceof ReplicationValve)) continue;
                this.replicationValve = (ReplicationValve)valves[i];
            }
            if (this.replicationValve == null && this.log.isDebugEnabled()) {
                this.log.debug((Object)"no ReplicationValve found for CrossContext Support");
            }
        }
        if (this.replicationValve != null) {
            this.replicationValve.registerReplicationSession(session);
        }
    }

    protected void startInternal() throws LifecycleException {
        Cluster cluster;
        super.startInternal();
        if (this.getCluster() == null && (cluster = this.getContext().getCluster()) instanceof CatalinaCluster) {
            this.setCluster((CatalinaCluster)cluster);
        }
        if (this.cluster != null) {
            this.cluster.registerManager(this);
        }
    }

    protected void stopInternal() throws LifecycleException {
        if (this.cluster != null) {
            this.cluster.removeManager(this);
        }
        this.replicationValve = null;
        super.stopInternal();
    }
}

