/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.Connection;

public interface ConnectionObserver {
    public void physicalConnectionObtained(Connection var1);

    public void physicalConnectionReleased();

    public void logicalConnectionClosed();

    public void statementPrepared();
}

