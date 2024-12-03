/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.init;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;
import org.springframework.util.Assert;

public abstract class DatabasePopulatorUtils {
    public static void execute(DatabasePopulator populator, DataSource dataSource) throws DataAccessException {
        Assert.notNull((Object)populator, (String)"DatabasePopulator must not be null");
        Assert.notNull((Object)dataSource, (String)"DataSource must not be null");
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            try {
                populator.populate(connection);
                if (!connection.getAutoCommit() && !DataSourceUtils.isConnectionTransactional(connection, dataSource)) {
                    connection.commit();
                }
            }
            finally {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
        }
        catch (ScriptException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new UncategorizedScriptException("Failed to execute database script", ex);
        }
    }
}

