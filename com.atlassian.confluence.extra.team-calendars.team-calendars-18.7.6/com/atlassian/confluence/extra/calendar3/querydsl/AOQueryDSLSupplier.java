/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.DatabaseProvider
 *  net.java.ao.EntityManager
 *  net.java.ao.Query
 */
package com.atlassian.confluence.extra.calendar3.querydsl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDslHelper;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import java.sql.SQLException;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.java.ao.DatabaseProvider;
import net.java.ao.EntityManager;
import net.java.ao.Query;

public class AOQueryDSLSupplier
implements QueryDSLSupplier {
    private EntityManager entityManager;
    private DatabaseProvider databaseProvider;
    private Configuration configuration;

    public void init(ActiveObjects activeObjects) {
        Objects.nonNull(activeObjects);
        EventEntity[] firstMatches = (EventEntity[])activeObjects.find(EventEntity.class, Query.select().limit(1));
        EventEntity firstMatch = firstMatches[0];
        this.entityManager = firstMatch.getEntityManager();
        this.databaseProvider = this.entityManager.getProvider();
        this.configuration = QueryDslHelper.getConfiguration(this.databaseProvider);
    }

    @Override
    public <A> A executeSQLQuery(@Nonnull QueryDSLSupplier.QueryCallback<A> queryCallback) {
        return queryCallback.execute(this.getSQLQuery());
    }

    @Override
    public <A> A executeDeleteSQLClause(RelationalPath<?> relationalPath, @Nonnull QueryDSLSupplier.DeleteQueryCallback<A> queryCallback) {
        return queryCallback.execute(this.getSQLDeleteClause(relationalPath));
    }

    private SQLQuery<Void> getSQLQuery() {
        try {
            SQLQuery<Void> query = new SQLQuery<Void>(this.databaseProvider.getConnection(), this.configuration);
            return query;
        }
        catch (SQLException e) {
            throw new RuntimeException("Could not create SQLQuery instance", e);
        }
    }

    private SQLDeleteClause getSQLDeleteClause(RelationalPath<?> relationalPath) {
        try {
            SQLDeleteClause deleteClause = new SQLDeleteClause(this.databaseProvider.getConnection(), this.configuration, relationalPath);
            return deleteClause;
        }
        catch (SQLException e) {
            throw new RuntimeException("Could not create SQLQuery instance", e);
        }
    }
}

