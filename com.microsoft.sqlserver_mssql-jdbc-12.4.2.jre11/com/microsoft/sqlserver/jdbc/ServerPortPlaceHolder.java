/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionSecurityManager;
import java.io.Serializable;

final class ServerPortPlaceHolder
implements Serializable {
    private static final long serialVersionUID = 7393779415545731523L;
    private final String serverName;
    private final String parsedServerName;
    private final String fullServerName;
    private final int port;
    private final String instanceName;
    private final boolean checkLink;
    private final transient SQLServerConnectionSecurityManager securityManager;

    ServerPortPlaceHolder(String name, int conPort, String instance, boolean fLink) {
        this.serverName = name;
        int px = this.serverName.indexOf(92);
        this.parsedServerName = px >= 0 ? this.serverName.substring(0, px) : this.serverName;
        this.fullServerName = null != instance ? this.serverName + "\\" + instance : this.serverName;
        this.port = conPort;
        this.instanceName = instance;
        this.checkLink = fLink;
        this.securityManager = new SQLServerConnectionSecurityManager(this.serverName, this.port);
        this.doSecurityCheck();
    }

    int getPortNumber() {
        return this.port;
    }

    String getServerName() {
        return this.serverName;
    }

    String getInstanceName() {
        return this.instanceName;
    }

    String getParsedServerName() {
        return this.parsedServerName;
    }

    String getFullServerName() {
        return this.fullServerName;
    }

    void doSecurityCheck() {
        this.securityManager.checkConnect();
        if (this.checkLink) {
            this.securityManager.checkLink();
        }
    }
}

