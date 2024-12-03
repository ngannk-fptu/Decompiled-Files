/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.test.C3P0BenchmarkApp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import javax.sql.DataSource;

public final class InterruptedBatchTest {
    static DataSource ds_unpooled = null;
    static DataSource ds_pooled = null;

    public static void main(String[] argv) {
        if (argv.length > 0) {
            System.err.println(C3P0BenchmarkApp.class.getName() + " now requires no args. Please set everything in standard c3p0 config files.");
            return;
        }
        try {
            ds_unpooled = new DriverManagerDataSource();
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            ds_pooled = cpds;
            InterruptedBatchTest.attemptSetupTable();
            InterruptedBatchTest.performTransaction(true);
            InterruptedBatchTest.performTransaction(false);
            InterruptedBatchTest.checkCount();
        }
        catch (Throwable t) {
            System.err.print("Aborting tests on Throwable -- ");
            t.printStackTrace();
            if (t instanceof Error) {
                throw (Error)t;
            }
        }
        finally {
            try {
                DataSources.destroy(ds_pooled);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                DataSources.destroy(ds_unpooled);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void performTransaction(boolean throwAnException) throws SQLException {
        Connection con = null;
        Statement prepStat = null;
        try {
            con = ds_pooled.getConnection();
            con.setAutoCommit(false);
            prepStat = con.prepareStatement("INSERT INTO CG_TAROPT_LOG(CO_ID, ENTDATE, CS_SEQNO, DESCRIPTION) VALUES (?,?,?,?)");
            prepStat.setLong(1, -665L);
            prepStat.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            prepStat.setInt(3, 1);
            prepStat.setString(4, "time: " + System.currentTimeMillis());
            prepStat.addBatch();
            if (throwAnException) {
                throw new NullPointerException("my exception");
            }
            prepStat.executeBatch();
            con.commit();
        }
        catch (Exception e) {
            System.out.println("exception caught (NPE expected): ");
            e.printStackTrace();
        }
        finally {
            try {
                if (prepStat != null) {
                    prepStat.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                con.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void attemptSetupTable() throws Exception {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ds_pooled.getConnection();
            stmt = con.createStatement();
            try {
                stmt.executeUpdate("CREATE TABLE CG_TAROPT_LOG ( CO_ID INTEGER, ENTDATE TIMESTAMP, CS_SEQNO INTEGER, DESCRIPTION VARCHAR(32) )");
            }
            catch (SQLException e) {
                System.err.println("Table already constructed?");
                e.printStackTrace();
            }
            stmt.executeUpdate("DELETE FROM CG_TAROPT_LOG");
        }
        finally {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                con.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void checkCount() throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ds_pooled.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM CG_TAROPT_LOG");
            rs.next();
            System.out.println(rs.getInt(1) + " rows found. (one row expected.)");
        }
        finally {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                con.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

