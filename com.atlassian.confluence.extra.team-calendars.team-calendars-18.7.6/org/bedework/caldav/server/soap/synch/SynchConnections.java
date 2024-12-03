/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.soap.synch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bedework.caldav.server.soap.synch.SynchConnection;
import org.bedework.caldav.server.soap.synch.SynchConnectionsMBean;
import org.bedework.util.jmx.ConfBase;
import org.bedework.util.misc.Util;

public class SynchConnections
extends ConfBase
implements SynchConnectionsMBean {
    public static final String confuriPname = "org.bedework.caldav.confuri";
    static Map<String, SynchConnection> activeConnections = new HashMap<String, SynchConnection>();
    static Map<String, SynchConnection> activeConnectionsById = new HashMap<String, SynchConnection>();

    public SynchConnections() {
        super("org.bedework.caldav:service=SynchConnections");
        this.setConfigName("SynchConnections");
        this.setConfigPname(confuriPname);
    }

    @Override
    public String loadConfig() {
        return "No config to load";
    }

    @Override
    public void setConnection(SynchConnection val) {
        activeConnections.put(val.getSubscribeUrl(), val);
        activeConnectionsById.put(val.getConnectorId(), val);
    }

    @Override
    public SynchConnection getConnection(String callbackUrl) {
        return activeConnections.get(callbackUrl);
    }

    @Override
    public SynchConnection getConnectionById(String id) {
        return activeConnectionsById.get(id);
    }

    @Override
    public String[] activeConnectionInfo() {
        Collection<SynchConnection> conns = activeConnections.values();
        if (Util.isEmpty(conns)) {
            return new String[0];
        }
        String[] res = new String[conns.size()];
        int i = 0;
        for (SynchConnection sc : conns) {
            res[i] = sc.shortToString();
            ++i;
        }
        return res;
    }
}

