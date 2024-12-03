/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ConnectionUtils
 *  com.mchange.v1.db.sql.ResultSetUtils
 */
package com.mchange.v2.c3p0.test;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v1.db.sql.ResultSetUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public final class ListTablesTest {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] argv) {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup(argv[0]);
            System.err.println(ds.getClass());
            Connection con = null;
            ResultSet rs = null;
            try {
                con = ds.getConnection();
                DatabaseMetaData md = con.getMetaData();
                rs = md.getTables(null, null, "%", null);
                while (rs.next()) {
                    System.out.println(rs.getString(3));
                }
            }
            catch (Throwable throwable) {
                ResultSetUtils.attemptClose(rs);
                ConnectionUtils.attemptClose((Connection)con);
                throw throwable;
            }
            ResultSetUtils.attemptClose((ResultSet)rs);
            ConnectionUtils.attemptClose((Connection)con);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

