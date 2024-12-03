/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc.spi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import org.hibernate.resource.jdbc.LogicalConnection;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.PhysicalJdbcTransaction;

public interface LogicalConnectionImplementor
extends LogicalConnection {
    public Connection getPhysicalConnection();

    public PhysicalConnectionHandlingMode getConnectionHandlingMode();

    public void afterStatement();

    public void beforeTransactionCompletion();

    public void afterTransaction();

    public Connection manualDisconnect();

    public void manualReconnect(Connection var1);

    @Deprecated
    public LogicalConnectionImplementor makeShareableCopy();

    public PhysicalJdbcTransaction getPhysicalJdbcTransaction();

    public void serialize(ObjectOutputStream var1) throws IOException;
}

