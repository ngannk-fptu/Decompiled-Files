/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import java.sql.Connection;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SchemaProvider {
    public Optional<String> getProductSchema();

    public Optional<String> getTableName(Connection var1, String var2);

    public Optional<String> getColumnName(Connection var1, String var2, String var3);
}

