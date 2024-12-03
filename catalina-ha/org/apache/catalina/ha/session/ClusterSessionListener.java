/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.session;

import java.util.Map;
import org.apache.catalina.ha.ClusterListener;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.session.SessionMessage;
import org.apache.catalina.ha.session.SessionMessageImpl;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class ClusterSessionListener
extends ClusterListener {
    private static final Log log = LogFactory.getLog(ClusterSessionListener.class);
    private static final StringManager sm = StringManager.getManager(ClusterSessionListener.class);

    @Override
    public void messageReceived(ClusterMessage myobj) {
        if (myobj instanceof SessionMessage) {
            SessionMessage msg = (SessionMessage)myobj;
            String ctxname = msg.getContextName();
            Map<String, ClusterManager> managers = this.cluster.getManagers();
            if (ctxname == null) {
                for (Map.Entry<String, ClusterManager> entry : managers.entrySet()) {
                    if (entry.getValue() != null) {
                        entry.getValue().messageDataReceived(msg);
                        continue;
                    }
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)sm.getString("clusterSessionListener.noManager", new Object[]{entry.getKey()}));
                }
            } else {
                ClusterManager mgr = managers.get(ctxname);
                if (mgr != null) {
                    mgr.messageDataReceived(msg);
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn((Object)sm.getString("clusterSessionListener.noManager", new Object[]{ctxname}));
                    }
                    if (msg.getEventType() == 4) {
                        SessionMessageImpl replymsg = new SessionMessageImpl(ctxname, 16, null, "NO-CONTEXT-MANAGER", "NO-CONTEXT-MANAGER-" + ctxname);
                        this.cluster.send(replymsg, msg.getAddress());
                    }
                }
            }
        }
    }

    @Override
    public boolean accept(ClusterMessage msg) {
        return msg instanceof SessionMessage;
    }
}

