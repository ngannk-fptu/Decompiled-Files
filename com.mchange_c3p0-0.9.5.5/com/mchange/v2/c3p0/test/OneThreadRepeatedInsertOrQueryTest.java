/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ConnectionUtils
 *  com.mchange.v1.db.sql.ResultSetUtils
 *  com.mchange.v1.db.sql.StatementUtils
 */
package com.mchange.v2.c3p0.test;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v1.db.sql.ResultSetUtils;
import com.mchange.v1.db.sql.StatementUtils;
import com.mchange.v2.c3p0.DataSources;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import javax.sql.DataSource;

public final class OneThreadRepeatedInsertOrQueryTest {
    static final String INSERT_STMT = "INSERT INTO testpbds VALUES ( ? , ? )";
    static final String SELECT_STMT = "SELECT count(*) FROM testpbds";
    static Random random = new Random();
    static DataSource ds;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void main(String[] argv) {
        Connection con;
        block18: {
            String jdbc_url = null;
            String username = null;
            String password = null;
            if (argv.length == 3) {
                jdbc_url = argv[0];
                username = argv[1];
                password = argv[2];
            } else if (argv.length == 1) {
                jdbc_url = argv[0];
                username = null;
                password = null;
            } else {
                OneThreadRepeatedInsertOrQueryTest.usage();
            }
            if (!jdbc_url.startsWith("jdbc:")) {
                OneThreadRepeatedInsertOrQueryTest.usage();
            }
            try {
                DataSource ds_unpooled = DataSources.unpooledDataSource(jdbc_url, username, password);
                ds = DataSources.pooledDataSource(ds_unpooled);
                con = null;
                Statement stmt = null;
                try {
                    con = ds.getConnection();
                    stmt = con.createStatement();
                    stmt.executeUpdate("CREATE TABLE testpbds ( a varchar(16), b varchar(16) )");
                }
                catch (SQLException e) {
                    try {
                        e.printStackTrace();
                        System.err.println("relation testpbds already exists, or something bad happened.");
                    }
                    catch (Throwable throwable) {
                        StatementUtils.attemptClose(stmt);
                        ConnectionUtils.attemptClose((Connection)con);
                        throw throwable;
                    }
                    StatementUtils.attemptClose((Statement)stmt);
                    ConnectionUtils.attemptClose((Connection)con);
                    break block18;
                }
                StatementUtils.attemptClose((Statement)stmt);
                ConnectionUtils.attemptClose((Connection)con);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        while (true) {
            con = null;
            try {
                con = ds.getConnection();
                boolean select = random.nextBoolean();
                if (select) {
                    OneThreadRepeatedInsertOrQueryTest.executeSelect(con);
                    continue;
                }
                OneThreadRepeatedInsertOrQueryTest.executeInsert(con);
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            finally {
                ConnectionUtils.attemptClose((Connection)con);
                continue;
            }
            break;
        }
    }

    static void executeInsert(Connection con) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(INSERT_STMT);
            pstmt.setInt(1, random.nextInt());
            pstmt.setInt(2, random.nextInt());
            pstmt.executeUpdate();
            System.out.println("INSERTION");
        }
        finally {
            StatementUtils.attemptClose((Statement)pstmt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void executeSelect(Connection con) throws SQLException {
        long l = System.currentTimeMillis();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(SELECT_STMT);
            rs = pstmt.executeQuery();
            rs.next();
            System.out.println("SELECT [count=" + rs.getInt(1) + ", time=" + (System.currentTimeMillis() - l) + " msecs]");
        }
        catch (Throwable throwable) {
            ResultSetUtils.attemptClose(rs);
            StatementUtils.attemptClose((Statement)pstmt);
            throw throwable;
        }
        ResultSetUtils.attemptClose((ResultSet)rs);
        StatementUtils.attemptClose((Statement)pstmt);
    }

    private static void usage() {
        System.err.println("java -Djdbc.drivers=<comma_sep_list_of_drivers> " + OneThreadRepeatedInsertOrQueryTest.class.getName() + " <jdbc_url> [<username> <password>]");
        System.exit(-1);
    }
}

