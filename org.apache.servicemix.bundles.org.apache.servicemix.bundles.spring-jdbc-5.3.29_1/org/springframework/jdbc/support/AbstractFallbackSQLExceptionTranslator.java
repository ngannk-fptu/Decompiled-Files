/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.support;

import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractFallbackSQLExceptionTranslator
implements SQLExceptionTranslator {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private SQLExceptionTranslator fallbackTranslator;

    public void setFallbackTranslator(@Nullable SQLExceptionTranslator fallback) {
        this.fallbackTranslator = fallback;
    }

    @Nullable
    public SQLExceptionTranslator getFallbackTranslator() {
        return this.fallbackTranslator;
    }

    @Override
    @Nullable
    public DataAccessException translate(String task, @Nullable String sql, SQLException ex) {
        Assert.notNull((Object)ex, (String)"Cannot translate a null SQLException");
        DataAccessException dae = this.doTranslate(task, sql, ex);
        if (dae != null) {
            return dae;
        }
        SQLExceptionTranslator fallback = this.getFallbackTranslator();
        if (fallback != null) {
            return fallback.translate(task, sql, ex);
        }
        return null;
    }

    @Nullable
    protected abstract DataAccessException doTranslate(String var1, @Nullable String var2, SQLException var3);

    protected String buildMessage(String task, @Nullable String sql, SQLException ex) {
        return task + "; " + (sql != null ? "SQL [" + sql + "]; " : "") + ex.getMessage();
    }
}

