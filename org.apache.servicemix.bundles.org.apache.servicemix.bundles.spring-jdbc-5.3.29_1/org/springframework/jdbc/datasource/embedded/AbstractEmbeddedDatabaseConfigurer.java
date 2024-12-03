/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.jdbc.datasource.embedded;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;

abstract class AbstractEmbeddedDatabaseConfigurer
implements EmbeddedDatabaseConfigurer {
    protected final Log logger = LogFactory.getLog(this.getClass());

    AbstractEmbeddedDatabaseConfigurer() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutdown(DataSource dataSource, String databaseName) {
        block27: {
            Connection con = null;
            try {
                con = dataSource.getConnection();
                if (con == null) break block27;
                try (Statement stmt = con.createStatement();){
                    stmt.execute("SHUTDOWN");
                }
            }
            catch (SQLException ex) {
                this.logger.info((Object)"Could not shut down embedded database", (Throwable)ex);
            }
            finally {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (SQLException ex) {
                        this.logger.debug((Object)"Could not close JDBC Connection on shutdown", (Throwable)ex);
                    }
                    catch (Throwable ex) {
                        this.logger.debug((Object)"Unexpected exception on closing JDBC Connection", ex);
                    }
                }
            }
        }
    }
}

