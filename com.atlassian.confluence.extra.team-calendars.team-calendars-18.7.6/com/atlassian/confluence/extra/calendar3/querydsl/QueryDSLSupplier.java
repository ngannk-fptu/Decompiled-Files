/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl;

import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import javax.annotation.Nonnull;

public interface QueryDSLSupplier {
    public <A> A executeSQLQuery(@Nonnull QueryCallback<A> var1);

    public <A> A executeDeleteSQLClause(RelationalPath<?> var1, @Nonnull DeleteQueryCallback<A> var2);

    public static interface DeleteQueryCallback<A> {
        public A execute(SQLDeleteClause var1);
    }

    public static interface QueryCallback<A> {
        public A execute(SQLQuery<Void> var1);
    }
}

