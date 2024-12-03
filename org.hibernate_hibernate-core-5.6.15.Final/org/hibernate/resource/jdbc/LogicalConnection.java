/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc;

import java.sql.Connection;
import org.hibernate.resource.jdbc.ResourceRegistry;

public interface LogicalConnection {
    public boolean isOpen();

    public Connection close();

    public boolean isPhysicallyConnected();

    public ResourceRegistry getResourceRegistry();
}

