/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.object;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.object.RdbmsOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class SqlOperation
extends RdbmsOperation {
    @Nullable
    private PreparedStatementCreatorFactory preparedStatementFactory;
    @Nullable
    private ParsedSql cachedSql;
    private final Object parsedSqlMonitor = new Object();

    @Override
    protected final void compileInternal() {
        this.preparedStatementFactory = new PreparedStatementCreatorFactory(this.resolveSql(), this.getDeclaredParameters());
        this.preparedStatementFactory.setResultSetType(this.getResultSetType());
        this.preparedStatementFactory.setUpdatableResults(this.isUpdatableResults());
        this.preparedStatementFactory.setReturnGeneratedKeys(this.isReturnGeneratedKeys());
        if (this.getGeneratedKeysColumnNames() != null) {
            this.preparedStatementFactory.setGeneratedKeysColumnNames(this.getGeneratedKeysColumnNames());
        }
        this.onCompileInternal();
    }

    protected void onCompileInternal() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ParsedSql getParsedSql() {
        Object object = this.parsedSqlMonitor;
        synchronized (object) {
            if (this.cachedSql == null) {
                this.cachedSql = NamedParameterUtils.parseSqlStatement(this.resolveSql());
            }
            return this.cachedSql;
        }
    }

    protected final PreparedStatementSetter newPreparedStatementSetter(@Nullable Object[] params) {
        Assert.state((this.preparedStatementFactory != null ? 1 : 0) != 0, (String)"No PreparedStatementFactory available");
        return this.preparedStatementFactory.newPreparedStatementSetter(params);
    }

    protected final PreparedStatementCreator newPreparedStatementCreator(@Nullable Object[] params) {
        Assert.state((this.preparedStatementFactory != null ? 1 : 0) != 0, (String)"No PreparedStatementFactory available");
        return this.preparedStatementFactory.newPreparedStatementCreator(params);
    }

    protected final PreparedStatementCreator newPreparedStatementCreator(String sqlToUse, @Nullable Object[] params) {
        Assert.state((this.preparedStatementFactory != null ? 1 : 0) != 0, (String)"No PreparedStatementFactory available");
        return this.preparedStatementFactory.newPreparedStatementCreator(sqlToUse, params);
    }
}

