/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.api.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.querydsl.sql.dml.SQLInsertClause;

public interface DatabaseCompatibilityKit {
    public <T> T executeWithKey(DatabaseConnection var1, SQLInsertClause var2, Class<T> var3);
}

