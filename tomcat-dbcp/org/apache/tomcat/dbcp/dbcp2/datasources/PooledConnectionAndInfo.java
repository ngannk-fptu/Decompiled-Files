/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import javax.sql.PooledConnection;
import org.apache.tomcat.dbcp.dbcp2.datasources.UserPassKey;

final class PooledConnectionAndInfo {
    private final PooledConnection pooledConnection;
    private final UserPassKey userPassKey;

    PooledConnectionAndInfo(PooledConnection pooledConnection, char[] userName, char[] userPassword) {
        this(pooledConnection, new UserPassKey(userName, userPassword));
    }

    PooledConnectionAndInfo(PooledConnection pooledConnection, UserPassKey userPassKey) {
        this.pooledConnection = pooledConnection;
        this.userPassKey = userPassKey;
    }

    String getPassword() {
        return this.userPassKey.getPassword();
    }

    PooledConnection getPooledConnection() {
        return this.pooledConnection;
    }

    String getUserName() {
        return this.userPassKey.getUserName();
    }

    UserPassKey getUserPassKey() {
        return this.userPassKey;
    }
}

