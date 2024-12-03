/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.sql.Connection;
import java.util.function.Function;
import javax.annotation.Nonnull;

public interface DatabaseService {
    public <R> R runInConnection(@Nonnull Function<Connection, R> var1);

    public String getDialect();
}

