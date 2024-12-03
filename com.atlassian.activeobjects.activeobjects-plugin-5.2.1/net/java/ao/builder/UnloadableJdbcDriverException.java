/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.util.Objects;
import net.java.ao.ActiveObjectsException;

public class UnloadableJdbcDriverException
extends ActiveObjectsException {
    private final String driverClassName;

    public UnloadableJdbcDriverException(String driverClassName) {
        this(driverClassName, null);
    }

    public UnloadableJdbcDriverException(String driverClassName, Throwable t) {
        super(t);
        this.driverClassName = Objects.requireNonNull(driverClassName, "driverClassName can't be null");
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    @Override
    public String getMessage() {
        return "Could not load JDBC driver <" + this.driverClassName + ">";
    }
}

