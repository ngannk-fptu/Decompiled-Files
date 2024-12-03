/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.LifecycleEvent
 *  org.apache.catalina.LifecycleListener
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.backend;

import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.ha.backend.CollectedInfo;
import org.apache.catalina.ha.backend.MultiCastSender;
import org.apache.catalina.ha.backend.Sender;
import org.apache.catalina.ha.backend.TcpSender;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class HeartbeatListener
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(HeartbeatListener.class);
    private static final StringManager sm = StringManager.getManager(HeartbeatListener.class);
    protected int port = 8009;
    protected String host = null;
    protected String ip = "224.0.1.105";
    protected int multiport = 23364;
    protected int ttl = 16;
    protected String proxyList = null;
    protected String proxyURL = "/HeartbeatListener";
    private CollectedInfo coll = null;
    private Sender sender = null;

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGroup() {
        return this.ip;
    }

    public void setGroup(String group) {
        this.ip = group;
    }

    public int getMultiport() {
        return this.multiport;
    }

    public void setMultiport(int port) {
        this.multiport = port;
    }

    public int getTtl() {
        return this.ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getProxyList() {
        return this.proxyList;
    }

    public void setProxyList(String proxyList) {
        this.proxyList = proxyList;
    }

    public String getProxyURL() {
        return this.proxyURL;
    }

    public void setProxyURLString(String proxyURL) {
        this.proxyURL = proxyURL;
    }

    public void lifecycleEvent(LifecycleEvent event) {
        if ("periodic".equals(event.getType())) {
            if (this.sender == null) {
                this.sender = this.proxyList == null ? new MultiCastSender() : new TcpSender();
            }
            if (this.coll == null) {
                try {
                    this.coll = new CollectedInfo(this.host, this.port);
                    this.port = this.coll.port;
                    this.host = this.coll.host;
                }
                catch (Exception ex) {
                    log.error((Object)sm.getString("heartbeatListener.errorCollectingInfo"), (Throwable)ex);
                    this.coll = null;
                    return;
                }
            }
            try {
                this.sender.init(this);
            }
            catch (Exception ex) {
                log.error((Object)sm.getString("heartbeatListener.senderInitError"), (Throwable)ex);
                this.sender = null;
                return;
            }
            try {
                this.coll.refresh();
            }
            catch (Exception ex) {
                log.error((Object)sm.getString("heartbeatListener.refreshError"), (Throwable)ex);
                this.coll = null;
                return;
            }
            String output = "v=1&ready=" + this.coll.ready + "&busy=" + this.coll.busy + "&port=" + this.port;
            try {
                this.sender.send(output);
            }
            catch (Exception ex) {
                log.error((Object)sm.getString("heartbeatListener.sendError"), (Throwable)ex);
            }
        }
    }
}

