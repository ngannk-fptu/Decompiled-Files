/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.Connection;
import org.hibernate.engine.jdbc.spi.ConnectionObserver;

public class ConnectionObserverAdapter
implements ConnectionObserver {
    @Override
    public void physicalConnectionObtained(Connection connection) {
    }

    @Override
    public void physicalConnectionReleased() {
    }

    @Override
    public void logicalConnectionClosed() {
    }

    @Override
    public void statementPrepared() {
    }
}

