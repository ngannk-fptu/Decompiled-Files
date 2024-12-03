/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

final class SQLServerConnectionSecurityManager {
    static final String DLLNAME = SQLServerDriver.AUTH_DLL_NAME + ".dll";
    String serverName;
    int portNumber;

    SQLServerConnectionSecurityManager(String serverName, int portNumber) {
        this.serverName = serverName;
        this.portNumber = portNumber;
    }

    public void checkConnect() {
        SecurityManager security = System.getSecurityManager();
        if (null != security) {
            security.checkConnect(this.serverName, this.portNumber);
        }
    }

    public void checkLink() {
        SecurityManager security = System.getSecurityManager();
        if (null != security) {
            security.checkLink(DLLNAME);
        }
    }
}

