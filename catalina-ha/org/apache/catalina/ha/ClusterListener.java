/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.ChannelListener
 *  org.apache.catalina.tribes.Member
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.ha;

import java.io.Serializable;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class ClusterListener
implements ChannelListener {
    private static final Log log = LogFactory.getLog(ClusterListener.class);
    protected CatalinaCluster cluster = null;

    public CatalinaCluster getCluster() {
        return this.cluster;
    }

    public void setCluster(CatalinaCluster cluster) {
        if (log.isDebugEnabled()) {
            if (cluster != null) {
                log.debug((Object)("add ClusterListener " + this.toString() + " to cluster" + cluster));
            } else {
                log.debug((Object)("remove ClusterListener " + this.toString() + " from cluster"));
            }
        }
        this.cluster = cluster;
    }

    public final void messageReceived(Serializable msg, Member member) {
        if (msg instanceof ClusterMessage) {
            this.messageReceived((ClusterMessage)msg);
        }
    }

    public final boolean accept(Serializable msg, Member member) {
        return msg instanceof ClusterMessage;
    }

    public abstract void messageReceived(ClusterMessage var1);

    public abstract boolean accept(ClusterMessage var1);
}

