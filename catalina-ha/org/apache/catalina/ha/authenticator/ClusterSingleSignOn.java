/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Container
 *  org.apache.catalina.Host
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.Session
 *  org.apache.catalina.SessionListener
 *  org.apache.catalina.authenticator.SingleSignOn
 *  org.apache.catalina.tribes.tipis.AbstractReplicatedMap$MapOwner
 *  org.apache.catalina.tribes.tipis.ReplicatedMap
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.authenticator;

import java.security.Principal;
import org.apache.catalina.Container;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.SessionListener;
import org.apache.catalina.authenticator.SingleSignOn;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.ha.authenticator.ClusterSingleSignOnListener;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.tribes.tipis.ReplicatedMap;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class ClusterSingleSignOn
extends SingleSignOn
implements ClusterValve,
AbstractReplicatedMap.MapOwner {
    private static final StringManager sm = StringManager.getManager(ClusterSingleSignOn.class);
    private CatalinaCluster cluster = null;
    private long rpcTimeout = 15000L;
    private int mapSendOptions = 6;
    private boolean terminateOnStartFailure = false;
    private long accessTimeout = 5000L;

    @Override
    public CatalinaCluster getCluster() {
        return this.cluster;
    }

    @Override
    public void setCluster(CatalinaCluster cluster) {
        this.cluster = cluster;
    }

    public long getRpcTimeout() {
        return this.rpcTimeout;
    }

    public void setRpcTimeout(long rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }

    public int getMapSendOptions() {
        return this.mapSendOptions;
    }

    public void setMapSendOptions(int mapSendOptions) {
        this.mapSendOptions = mapSendOptions;
    }

    public boolean getTerminateOnStartFailure() {
        return this.terminateOnStartFailure;
    }

    public void setTerminateOnStartFailure(boolean terminateOnStartFailure) {
        this.terminateOnStartFailure = terminateOnStartFailure;
    }

    public long getAccessTimeout() {
        return this.accessTimeout;
    }

    public void setAccessTimeout(long accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

    protected boolean associate(String ssoId, Session session) {
        boolean result = super.associate(ssoId, session);
        if (result) {
            ((ReplicatedMap)this.cache).replicate((Object)ssoId, true);
        }
        return result;
    }

    protected boolean update(String ssoId, Principal principal, String authType, String username, String password) {
        boolean result = super.update(ssoId, principal, authType, username, password);
        if (result) {
            ((ReplicatedMap)this.cache).replicate((Object)ssoId, true);
        }
        return result;
    }

    protected SessionListener getSessionListener(String ssoId) {
        return new ClusterSingleSignOnListener(ssoId);
    }

    public void objectMadePrimary(Object key, Object value) {
    }

    protected synchronized void startInternal() throws LifecycleException {
        try {
            Container host;
            if (this.cluster == null && (host = this.getContainer()) instanceof Host && host.getCluster() instanceof CatalinaCluster) {
                this.setCluster((CatalinaCluster)host.getCluster());
            }
            if (this.cluster == null) {
                throw new LifecycleException(sm.getString("clusterSingleSignOn.nocluster"));
            }
            ClassLoader[] cls = new ClassLoader[]{this.getClass().getClassLoader()};
            ReplicatedMap cache = new ReplicatedMap((AbstractReplicatedMap.MapOwner)this, this.cluster.getChannel(), this.rpcTimeout, this.cluster.getClusterName() + "-SSO-cache", cls, this.terminateOnStartFailure);
            cache.setChannelSendOptions(this.mapSendOptions);
            cache.setAccessTimeout(this.accessTimeout);
            this.cache = cache;
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            throw new LifecycleException(sm.getString("clusterSingleSignOn.clusterLoad.fail"), t);
        }
        super.startInternal();
    }

    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (this.getCluster() != null) {
            ((ReplicatedMap)this.cache).breakdown();
        }
    }
}

