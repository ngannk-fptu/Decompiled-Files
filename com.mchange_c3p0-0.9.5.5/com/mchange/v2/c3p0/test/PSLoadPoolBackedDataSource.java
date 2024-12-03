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

public final class PSLoadPoolBackedDataSource {
    static final String INSERT_STMT = "INSERT INTO testpbds VALUES ( ? , ? )";
    static final String SELECT_STMT = "SELECT count(*) FROM testpbds";
    static final String DELETE_STMT = "DELETE FROM testpbds";
    static DataSource ds;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] argv) {
        if (argv.length > 0) {
            System.err.println(PSLoadPoolBackedDataSource.class.getName() + " now requires no args. Please set everything in standard c3p0 config files.");
            return;
        }
        Object jdbc_url = null;
        Object username = null;
        Object password = null;
        try {
            DataSource ds_unpooled = DataSources.unpooledDataSource();
            ds = DataSources.pooledDataSource(ds_unpooled);
            Connection con = null;
            Statement stmt = null;
            try {
                con = ds_unpooled.getConnection();
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
            }
            StatementUtils.attemptClose((Statement)stmt);
            ConnectionUtils.attemptClose((Connection)con);
            for (int i = 0; i < 100; ++i) {
                ChurnThread t = new ChurnThread();
                t.start();
                System.out.println("THREAD MADE [" + i + "]");
                Thread.sleep(500L);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void executeInsert(Connection con, Random random) throws SQLException {
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
    static void executeSelect(Connection con, Random random) throws SQLException {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void executeDelete(Connection con, Random random) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(DELETE_STMT);
            int deleted = pstmt.executeUpdate();
            System.out.println("DELETE [" + deleted + " rows]");
        }
        finally {
            ResultSetUtils.attemptClose(rs);
            StatementUtils.attemptClose((Statement)pstmt);
        }
    }

    static class ChurnThread
    extends Thread {
        Random random = new Random();

        ChurnThread() {
        }

        /*
         * Unable to fully structure code
         */
        @Override
        public void run() {
            try {
                while (true) {
                    con = null;
                    try {
                        con = PSLoadPoolBackedDataSource.ds.getConnection();
                        select = this.random.nextInt(3);
                        switch (select) {
                            case 0: {
                                PSLoadPoolBackedDataSource.executeSelect(con, this.random);
                                ** break;
lbl11:
                                // 1 sources

                                break;
                            }
                            case 1: {
                                PSLoadPoolBackedDataSource.executeInsert(con, this.random);
                                ** break;
lbl15:
                                // 1 sources

                                break;
                            }
                            case 2: {
                                PSLoadPoolBackedDataSource.executeDelete(con, this.random);
                                break;
                            }
                            ** default:
lbl20:
                            // 1 sources

                            break;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        ConnectionUtils.attemptClose((Connection)con);
                    }
                    Thread.sleep(1L);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}

