/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.cci.Connection
 */
package org.springframework.jca.cci.connection;

import javax.resource.cci.Connection;
import org.springframework.transaction.support.ResourceHolderSupport;

@Deprecated
public class ConnectionHolder
extends ResourceHolderSupport {
    private final Connection connection;

    public ConnectionHolder(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }
}

