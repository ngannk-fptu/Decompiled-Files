/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;

interface ConnectionCreator {
    public String getUrl();

    public Connection createConnection();
}

