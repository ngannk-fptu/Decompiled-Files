/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ConnectionUtils
 *  com.mchange.v1.db.sql.StatementUtils
 */
package com.mchange.v2.c3p0.stmt;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v1.db.sql.StatementUtils;
import com.mchange.v2.c3p0.DriverManagerDataSourceFactory;
import com.mchange.v2.c3p0.PoolBackedDataSourceFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public final class StatementCacheBenchmark {
    static final String EMPTY_TABLE_CREATE = "CREATE TABLE emptyyukyuk (a varchar(8), b varchar(8))";
    static final String EMPTY_TABLE_SELECT = "SELECT * FROM emptyyukyuk";
    static final String EMPTY_TABLE_DROP = "DROP TABLE emptyyukyuk";
    static final String EMPTY_TABLE_CONDITIONAL_SELECT = "SELECT * FROM emptyyukyuk where a = ?";
    static final int NUM_ITERATIONS = 2000;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public static void main(String[] argv) {
        block16: {
            ds_unpooled = null;
            ds_pooled = null;
            try {
                jdbc_url = null;
                username = null;
                password = null;
                if (argv.length == 3) {
                    jdbc_url = argv[0];
                    username = argv[1];
                    password = argv[2];
                } else if (argv.length == 1) {
                    jdbc_url = argv[0];
                    username = null;
                    password = null;
                } else {
                    StatementCacheBenchmark.usage();
                }
                if (!jdbc_url.startsWith("jdbc:")) {
                    StatementCacheBenchmark.usage();
                }
                ds_unpooled = DriverManagerDataSourceFactory.create(jdbc_url, username, password);
                ds_pooled = PoolBackedDataSourceFactory.create(jdbc_url, username, password, 5, 20, 5, 0, 100);
                StatementCacheBenchmark.create(ds_pooled);
                StatementCacheBenchmark.perform(ds_pooled, "pooled");
                StatementCacheBenchmark.perform(ds_unpooled, "unpooled");
                ** try [egrp 1[TRYBLOCK] [0 : 110->117)] { 
            }
            catch (Exception e) {
                e.printStackTrace();
                break block16;
            }
            finally {
                try {
                    StatementCacheBenchmark.drop(ds_pooled);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
lbl-1000:
            // 1 sources

            {
                StatementCacheBenchmark.drop(ds_pooled);
            }
lbl28:
            // 1 sources

            catch (Exception e) {
                e.printStackTrace();
            }
            {
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void perform(DataSource ds, String name) throws SQLException {
        Connection c = null;
        Statement ps = null;
        try {
            c = ds.getConnection();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 2000; ++i) {
                PreparedStatement test = c.prepareStatement(EMPTY_TABLE_CONDITIONAL_SELECT);
                test.close();
            }
            long end = System.currentTimeMillis();
            System.err.println(name + " --> " + (float)(end - start) / 2000.0f + " [" + 2000 + " iterations]");
        }
        finally {
            StatementUtils.attemptClose(ps);
            ConnectionUtils.attemptClose((Connection)c);
        }
    }

    private static void usage() {
        System.err.println("java -Djdbc.drivers=<comma_sep_list_of_drivers> " + StatementCacheBenchmark.class.getName() + " <jdbc_url> [<username> <password>]");
        System.exit(-1);
    }

    static void create(DataSource ds) throws SQLException {
        System.err.println("Creating test schema.");
        Connection con = null;
        PreparedStatement ps1 = null;
        try {
            con = ds.getConnection();
            ps1 = con.prepareStatement(EMPTY_TABLE_CREATE);
            ps1.executeUpdate();
            System.err.println("Test schema created.");
        }
        catch (Throwable throwable) {
            StatementUtils.attemptClose(ps1);
            ConnectionUtils.attemptClose((Connection)con);
            throw throwable;
        }
        StatementUtils.attemptClose((Statement)ps1);
        ConnectionUtils.attemptClose((Connection)con);
    }

    static void drop(DataSource ds) throws SQLException {
        Connection con = null;
        PreparedStatement ps1 = null;
        try {
            con = ds.getConnection();
            ps1 = con.prepareStatement(EMPTY_TABLE_DROP);
            ps1.executeUpdate();
        }
        catch (Throwable throwable) {
            StatementUtils.attemptClose(ps1);
            ConnectionUtils.attemptClose((Connection)con);
            throw throwable;
        }
        StatementUtils.attemptClose((Statement)ps1);
        ConnectionUtils.attemptClose((Connection)con);
        System.err.println("Test schema dropped.");
    }
}

