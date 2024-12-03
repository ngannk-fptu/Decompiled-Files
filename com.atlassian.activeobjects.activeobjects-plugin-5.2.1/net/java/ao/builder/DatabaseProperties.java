/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import net.java.ao.builder.ConnectionPool;

public interface DatabaseProperties {
    public String getUrl();

    public String getUsername();

    public String getPassword();

    public String getSchema();

    public ConnectionPool getConnectionPool();
}

