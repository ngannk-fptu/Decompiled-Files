/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.pocketknife.api.querydsl.schema;

import com.atlassian.annotations.PublicApi;
import com.atlassian.pocketknife.api.querydsl.schema.SchemaState;
import com.querydsl.sql.RelationalPath;
import java.sql.Connection;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@PublicApi
public interface SchemaStateProvider {
    public SchemaState getSchemaState(Connection var1, RelationalPath<?> var2);
}

