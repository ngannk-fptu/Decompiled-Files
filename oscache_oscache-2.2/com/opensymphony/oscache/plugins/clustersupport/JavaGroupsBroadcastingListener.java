/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.jgroups.Address
 *  org.jgroups.blocks.NotificationBus
 *  org.jgroups.blocks.NotificationBus$Consumer
 */
package com.opensymphony.oscache.plugins.clustersupport;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.FinalizationException;
import com.opensymphony.oscache.base.InitializationException;
import com.opensymphony.oscache.plugins.clustersupport.AbstractBroadcastingListener;
import com.opensymphony.oscache.plugins.clustersupport.ClusterNotification;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.Address;
import org.jgroups.blocks.NotificationBus;

public class JavaGroupsBroadcastingListener
extends AbstractBroadcastingListener
implements NotificationBus.Consumer {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$plugins$clustersupport$JavaGroupsBroadcastingListener == null ? (class$com$opensymphony$oscache$plugins$clustersupport$JavaGroupsBroadcastingListener = JavaGroupsBroadcastingListener.class$("com.opensymphony.oscache.plugins.clustersupport.JavaGroupsBroadcastingListener")) : class$com$opensymphony$oscache$plugins$clustersupport$JavaGroupsBroadcastingListener));
    private static final String BUS_NAME = "OSCacheBus";
    private static final String CHANNEL_PROPERTIES = "cache.cluster.properties";
    private static final String MULTICAST_IP_PROPERTY = "cache.cluster.multicast.ip";
    private static final String DEFAULT_CHANNEL_PROPERTIES_PRE = "UDP(mcast_addr=";
    private static final String DEFAULT_CHANNEL_PROPERTIES_POST = ";mcast_port=45566;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800;max_xmit_size=8192):UNICAST(timeout=300,600,1200,2400):pbcast.STABLE(desired_avg_gossip=20000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)";
    private static final String DEFAULT_MULTICAST_IP = "231.12.21.132";
    private NotificationBus bus;
    static /* synthetic */ Class class$com$opensymphony$oscache$plugins$clustersupport$JavaGroupsBroadcastingListener;

    public synchronized void initialize(Cache cache, Config config) throws InitializationException {
        super.initialize(cache, config);
        String properties = config.getProperty(CHANNEL_PROPERTIES);
        String multicastIP = config.getProperty(MULTICAST_IP_PROPERTY);
        if (properties == null && multicastIP == null) {
            multicastIP = DEFAULT_MULTICAST_IP;
        }
        properties = properties == null ? DEFAULT_CHANNEL_PROPERTIES_PRE + multicastIP.trim() + DEFAULT_CHANNEL_PROPERTIES_POST : properties.trim();
        if (log.isInfoEnabled()) {
            log.info((Object)("Starting a new JavaGroups broadcasting listener with properties=" + properties));
        }
        try {
            this.bus = new NotificationBus(BUS_NAME, properties);
            this.bus.start();
            this.bus.getChannel().setOpt(3, (Object)new Boolean(false));
            this.bus.setConsumer((NotificationBus.Consumer)this);
            log.info((Object)"JavaGroups clustering support started successfully");
        }
        catch (Exception e) {
            throw new InitializationException("Initialization failed: " + e);
        }
    }

    public synchronized void finialize() throws FinalizationException {
        if (log.isInfoEnabled()) {
            log.info((Object)"JavaGroups shutting down...");
        }
        if (this.bus != null) {
            this.bus.stop();
            this.bus = null;
        } else {
            log.warn((Object)"Notification bus wasn't initialized or finialize was invoked before!");
        }
        if (log.isInfoEnabled()) {
            log.info((Object)"JavaGroups shutdown complete.");
        }
    }

    protected void sendNotification(ClusterNotification message) {
        this.bus.sendNotification((Serializable)message);
    }

    public void handleNotification(Serializable serializable) {
        if (!(serializable instanceof ClusterNotification)) {
            log.error((Object)("An unknown cluster notification message received (class=" + serializable.getClass().getName() + "). Notification ignored."));
            return;
        }
        this.handleClusterNotification((ClusterNotification)serializable);
    }

    public Serializable getCache() {
        return "JavaGroupsBroadcastingListener: " + this.bus.getLocalAddress();
    }

    public void memberJoined(Address address) {
        if (log.isInfoEnabled()) {
            log.info((Object)("A new member at address '" + address + "' has joined the cluster"));
        }
    }

    public void memberLeft(Address address) {
        if (log.isInfoEnabled()) {
            log.info((Object)("Member at address '" + address + "' left the cluster"));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

