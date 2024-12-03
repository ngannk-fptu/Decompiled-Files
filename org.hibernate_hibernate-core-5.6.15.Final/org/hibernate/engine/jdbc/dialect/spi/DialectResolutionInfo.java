/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.spi;

public interface DialectResolutionInfo {
    public static final int NO_VERSION = -9999;

    public String getDatabaseName();

    public int getDatabaseMajorVersion();

    public int getDatabaseMinorVersion();

    public String getDriverName();

    public int getDriverMajorVersion();

    public int getDriverMinorVersion();
}

