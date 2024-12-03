/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  io.atlassian.fugue.Suppliers
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db;

import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.confluence.plugins.collaborative.content.feedback.exception.DataFetchException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import io.atlassian.fugue.Suppliers;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Utils {
    private static final String ESCAPE_CHAR_LEFT = System.getProperty("confluence.db.escape.identifier.char.left");
    private static final String ESCAPE_CHAR_RIGHT = System.getProperty("confluence.db.escape.identifier.char.right");
    private final Supplier<DatabaseType> dbDialect = Suppliers.memoize(() -> ((DataSourceProvider)dataSourceProvider).getDatabaseType());
    private final TransactionalExecutorFactory transactionalExecutorFactory;

    @Autowired
    public Utils(@ComponentImport DataSourceProvider dataSourceProvider, @ComponentImport(value="salTransactionalExecutorFactory") TransactionalExecutorFactory transactionalExecutorFactory) {
        this.transactionalExecutorFactory = transactionalExecutorFactory;
    }

    public String escapeIdentifier(String identifier) {
        return this.escapeCharLeft() + identifier + this.escapeCharRight();
    }

    public <T> T executeInTransaction(boolean isReadOnly, long contentId, Callable<T> action) {
        return (T)this.transactionalExecutorFactory.createExecutor(isReadOnly, false).execute(connection -> {
            try {
                return action.call();
            }
            catch (Exception e) {
                throw DataFetchException.queryError("Error executing reconciliation history query", contentId, e);
            }
        });
    }

    private String escapeCharLeft() {
        if (ESCAPE_CHAR_LEFT != null) {
            return ESCAPE_CHAR_LEFT;
        }
        if (this.dbDialect.get() == DatabaseType.MS_SQL) {
            return "[";
        }
        if (this.dbDialect.get() == DatabaseType.MYSQL) {
            return "`";
        }
        return "\"";
    }

    private String escapeCharRight() {
        if (ESCAPE_CHAR_RIGHT != null) {
            return ESCAPE_CHAR_RIGHT;
        }
        if (this.dbDialect.get() == DatabaseType.MS_SQL) {
            return "]";
        }
        if (this.dbDialect.get() == DatabaseType.MYSQL) {
            return "`";
        }
        return "\"";
    }
}

