/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.example;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.sql.Connection;
import java.sql.Statement;

public class InitSqlConnectionCustomizer
extends AbstractConnectionCustomizer {
    static final MLogger logger = MLog.getLogger(InitSqlConnectionCustomizer.class);

    private String getInitSql(String parentDataSourceIdentityToken) {
        return (String)this.extensionsForToken(parentDataSourceIdentityToken).get("initSql");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCheckOut(Connection c, String parentDataSourceIdentityToken) throws Exception {
        String initSql = this.getInitSql(parentDataSourceIdentityToken);
        if (initSql != null) {
            Statement stmt = null;
            try {
                stmt = c.createStatement();
                int num = stmt.executeUpdate(initSql);
                if (logger.isLoggable(MLevel.FINEST)) {
                    logger.log(MLevel.FINEST, "Initialized checked-out Connection '" + c + "' with initSql '" + initSql + "'. Return value: " + num);
                }
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }
}

