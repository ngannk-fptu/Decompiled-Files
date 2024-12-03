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
import com.mchange.v2.c3p0.PooledDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import javax.sql.DataSource;

public final class LoadPoolBackedDataSource {
    static final int NUM_THREADS = 100;
    static final int ITERATIONS_PER_THREAD = 1000;
    static DataSource ds;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] argv) {
        if (argv.length > 0) {
            System.err.println(LoadPoolBackedDataSource.class.getName() + " now requires no args. Please set everything in standard c3p0 config files.");
            return;
        }
        Object jdbc_url = null;
        Object username = null;
        Object password = null;
        try {
            int i;
            DataSource ds_unpooled = DataSources.unpooledDataSource();
            ds = DataSources.pooledDataSource(ds_unpooled);
            Connection con = null;
            Statement stmt = null;
            try {
                con = ds.getConnection();
                stmt = con.createStatement();
                stmt.executeUpdate("CREATE TABLE testpbds ( a varchar(16), b varchar(16) )");
                System.err.println("LoadPoolBackedDataSource -- TEST SCHEMA CREATED");
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
            }
            StatementUtils.attemptClose((Statement)stmt);
            ConnectionUtils.attemptClose((Connection)con);
            Thread[] threads = new Thread[100];
            for (i = 0; i < 100; ++i) {
                ChurnThread t = new ChurnThread(i);
                threads[i] = t;
                t.start();
                System.out.println("THREAD MADE [" + i + "]");
                Thread.sleep(500L);
            }
            for (i = 0; i < 100; ++i) {
                threads[i].join();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Connection con = null;
            Statement stmt = null;
            try {
                con = ds.getConnection();
                stmt = con.createStatement();
                stmt.executeUpdate("DROP TABLE testpbds");
                System.err.println("LoadPoolBackedDataSource -- TEST SCHEMA DROPPED");
            }
            catch (Exception e) {
                try {
                    e.printStackTrace();
                }
                catch (Throwable throwable) {
                    StatementUtils.attemptClose(stmt);
                    ConnectionUtils.attemptClose((Connection)con);
                    throw throwable;
                }
                StatementUtils.attemptClose((Statement)stmt);
                ConnectionUtils.attemptClose((Connection)con);
            }
            StatementUtils.attemptClose((Statement)stmt);
            ConnectionUtils.attemptClose((Connection)con);
        }
    }

    static void executeInsert(Connection con, Random random) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO testpbds VALUES ('" + random.nextInt() + "', '" + random.nextInt() + "')");
            System.out.println("INSERTION");
        }
        finally {
            StatementUtils.attemptClose((Statement)stmt);
        }
    }

    static void executeDelete(Connection con) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM testpbds;");
            System.out.println("DELETION");
        }
        finally {
            StatementUtils.attemptClose((Statement)stmt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void executeSelect(Connection con) throws SQLException {
        long l = System.currentTimeMillis();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT count(*) FROM testpbds");
            rs.next();
            System.out.println("SELECT [count=" + rs.getInt(1) + ", time=" + (System.currentTimeMillis() - l) + " msecs]");
        }
        catch (Throwable throwable) {
            ResultSetUtils.attemptClose(rs);
            StatementUtils.attemptClose((Statement)stmt);
            throw throwable;
        }
        ResultSetUtils.attemptClose((ResultSet)rs);
        StatementUtils.attemptClose((Statement)stmt);
    }

    private static void usage() {
        System.err.println("java -Djdbc.drivers=<comma_sep_list_of_drivers> " + LoadPoolBackedDataSource.class.getName() + " <jdbc_url> [<username> <password>]");
        System.exit(-1);
    }

    static class ChurnThread
    extends Thread {
        Random random = new Random();
        int num;

        public ChurnThread(int num) {
            this.num = num;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                for (int i = 0; i < 1000; ++i) {
                    Connection con = null;
                    try {
                        con = ds.getConnection();
                        int select = this.random.nextInt(3);
                        switch (select) {
                            case 0: {
                                LoadPoolBackedDataSource.executeSelect(con);
                                break;
                            }
                            case 1: {
                                LoadPoolBackedDataSource.executeInsert(con, this.random);
                                break;
                            }
                            case 2: {
                                LoadPoolBackedDataSource.executeDelete(con);
                            }
                        }
                        PooledDataSource pds = (PooledDataSource)ds;
                        System.out.println("iteration: (" + this.num + ", " + i + ')');
                        System.out.println(pds.getNumConnectionsDefaultUser());
                        System.out.println(pds.getNumIdleConnectionsDefaultUser());
                        System.out.println(pds.getNumBusyConnectionsDefaultUser());
                        System.out.println(pds.getNumConnectionsAllUsers());
                        Thread.sleep(1L);
                        continue;
                    }
                    finally {
                        ConnectionUtils.attemptClose((Connection)con);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

