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
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;

public final class C3P0BenchmarkApp {
    static final String EMPTY_TABLE_CREATE = "CREATE TABLE emptyyukyuk (a varchar(8), b varchar(8))";
    static final String EMPTY_TABLE_SELECT = "SELECT * FROM emptyyukyuk";
    static final String EMPTY_TABLE_DROP = "DROP TABLE emptyyukyuk";
    static final String EMPTY_TABLE_CONDITIONAL_SELECT = "SELECT * FROM emptyyukyuk where a = ?";
    static final String N_ENTRY_TABLE_CREATE = "CREATE TABLE n_entryyukyuk (a INTEGER)";
    static final String N_ENTRY_TABLE_INSERT = "INSERT INTO n_entryyukyuk VALUES ( ? )";
    static final String N_ENTRY_TABLE_SELECT = "SELECT * FROM n_entryyukyuk";
    static final String N_ENTRY_TABLE_DROP = "DROP TABLE n_entryyukyuk";
    static final int NUM_ITERATIONS = 2000;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static void main(String[] argv) {
        block25: {
            ComboPooledDataSource cpds;
            if (argv.length > 0) {
                System.err.println(C3P0BenchmarkApp.class.getName() + " now requires no args. Please set everything in standard c3p0 config files.");
                return;
            }
            DriverManagerDataSource ds_unpooled = null;
            ComboPooledDataSource ds_pooled = null;
            ds_unpooled = new DriverManagerDataSource();
            ds_pooled = cpds = new ComboPooledDataSource();
            ds_pooled.getParentLogger().info("Pooled DataSource created.");
            C3P0BenchmarkApp.create(ds_pooled);
            System.out.println("Please wait. Tests can be very slow.");
            ArrayList<Test> l = new ArrayList<Test>();
            l.add(new ConnectionAcquisitionTest());
            l.add(new StatementCreateTest());
            l.add(new StatementEmptyTableSelectTest());
            l.add(new PreparedStatementEmptyTableSelectTest());
            l.add(new PreparedStatementAcquireTest());
            l.add(new ResultSetReadTest());
            l.add(new FiveThreadPSQueryTestTest());
            int len = l.size();
            for (int i = 0; i < len; ++i) {
                ((Test)l.get(i)).perform(ds_unpooled, ds_pooled, 2000);
            }
            try {
                C3P0BenchmarkApp.drop(ds_pooled);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
            break block25;
            catch (Throwable t) {
                block24: {
                    try {
                        System.err.print("Aborting tests on Throwable -- ");
                        t.printStackTrace();
                        if (!(t instanceof Error)) break block24;
                        throw (Error)t;
                    }
                    catch (Throwable throwable) {
                        try {
                            C3P0BenchmarkApp.drop(ds_pooled);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
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
                        throw throwable;
                    }
                }
                try {
                    C3P0BenchmarkApp.drop(ds_pooled);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void create(DataSource ds) throws SQLException {
        System.err.println("Creating test schema.");
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        try {
            con = ds.getConnection();
            ps1 = con.prepareStatement(EMPTY_TABLE_CREATE);
            ps2 = con.prepareStatement(N_ENTRY_TABLE_CREATE);
            ps3 = con.prepareStatement(N_ENTRY_TABLE_INSERT);
            ps1.executeUpdate();
            ps2.executeUpdate();
            for (int i = 0; i < 2000; ++i) {
                ps3.setInt(1, i);
                ps3.executeUpdate();
                System.err.print('.');
            }
            System.err.println();
            System.err.println("Test schema created.");
        }
        catch (Throwable throwable) {
            StatementUtils.attemptClose(ps1);
            StatementUtils.attemptClose(ps2);
            StatementUtils.attemptClose(ps3);
            ConnectionUtils.attemptClose((Connection)con);
            throw throwable;
        }
        StatementUtils.attemptClose((Statement)ps1);
        StatementUtils.attemptClose((Statement)ps2);
        StatementUtils.attemptClose((Statement)ps3);
        ConnectionUtils.attemptClose((Connection)con);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void drop(DataSource ds) throws SQLException {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        try {
            con = ds.getConnection();
            ps1 = con.prepareStatement(EMPTY_TABLE_DROP);
            ps2 = con.prepareStatement(N_ENTRY_TABLE_DROP);
            ps1.executeUpdate();
            ps2.executeUpdate();
            System.err.println("Test schema dropped.");
        }
        catch (Throwable throwable) {
            StatementUtils.attemptClose(ps1);
            StatementUtils.attemptClose(ps2);
            ConnectionUtils.attemptClose((Connection)con);
            throw throwable;
        }
        StatementUtils.attemptClose((Statement)ps1);
        StatementUtils.attemptClose((Statement)ps2);
        ConnectionUtils.attemptClose((Connection)con);
    }

    static class FiveThreadPSQueryTestTest
    extends Test {
        FiveThreadPSQueryTestTest() {
            super("Five threads getting a connection, executing a query, " + System.getProperty("line.separator") + "and retrieving results concurrently via a prepared statement (in a transaction).");
        }

        @Override
        protected long test(final DataSource ds, final int n) throws Exception {
            int i;
            long start = System.currentTimeMillis();
            Thread[] ts = new Thread[5];
            for (i = 0; i < 5; ++i) {
                class QueryThread
                extends Thread {
                    QueryThread(int num) {
                        super("QueryThread-" + num);
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void run() {
                        Connection con = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        for (int i = 0; i < n / 5; ++i) {
                            try {
                                con = ds.getConnection();
                                con.setAutoCommit(false);
                                pstmt = con.prepareStatement(C3P0BenchmarkApp.EMPTY_TABLE_CONDITIONAL_SELECT);
                                pstmt.setString(1, "boo");
                                rs = pstmt.executeQuery();
                                while (rs.next()) {
                                    System.err.println("Huh?? Empty table has values?");
                                }
                                con.commit();
                            }
                            catch (Exception e) {
                                try {
                                    System.err.print("FiveThreadPSQueryTestTest exception -- ");
                                    e.printStackTrace();
                                    try {
                                        if (con != null) {
                                            con.rollback();
                                        }
                                    }
                                    catch (SQLException e2) {
                                        System.err.print("Rollback on exception failed! -- ");
                                        e2.printStackTrace();
                                    }
                                }
                                catch (Throwable throwable) {
                                    ResultSetUtils.attemptClose(rs);
                                    StatementUtils.attemptClose(pstmt);
                                    ConnectionUtils.attemptClose((Connection)con);
                                    con = null;
                                    throw throwable;
                                }
                                ResultSetUtils.attemptClose(rs);
                                StatementUtils.attemptClose((Statement)pstmt);
                                ConnectionUtils.attemptClose((Connection)con);
                                con = null;
                                continue;
                            }
                            ResultSetUtils.attemptClose((ResultSet)rs);
                            StatementUtils.attemptClose((Statement)pstmt);
                            ConnectionUtils.attemptClose((Connection)con);
                            con = null;
                            continue;
                        }
                    }
                }
                ts[i] = new QueryThread(i);
                ts[i].start();
            }
            for (i = 0; i < 5; ++i) {
                ts[i].join();
            }
            return System.currentTimeMillis() - start;
        }
    }

    static class ResultSetReadTest
    extends Test {
        ResultSetReadTest() {
            super("Reading one row / one entry from a result set");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected long test(DataSource ds, int n) throws SQLException {
            long l;
            if (n > 10000) {
                throw new IllegalArgumentException("10K max.");
            }
            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                con = ds.getConnection();
                pstmt = con.prepareStatement(C3P0BenchmarkApp.N_ENTRY_TABLE_SELECT);
                rs = pstmt.executeQuery();
                long start = System.currentTimeMillis();
                for (int i = 0; i < n; ++i) {
                    if (!rs.next()) {
                        System.err.println("huh?");
                    }
                    rs.getInt(1);
                }
                long end = System.currentTimeMillis();
                l = end - start;
            }
            catch (Throwable throwable) {
                ResultSetUtils.attemptClose(rs);
                StatementUtils.attemptClose((Statement)pstmt);
                ConnectionUtils.attemptClose((Connection)con);
                throw throwable;
            }
            ResultSetUtils.attemptClose((ResultSet)rs);
            StatementUtils.attemptClose((Statement)pstmt);
            ConnectionUtils.attemptClose((Connection)con);
            return l;
        }
    }

    static class PreparedStatementEmptyTableSelectTest
    extends Test {
        PreparedStatementEmptyTableSelectTest() {
            super("Empty Table PreparedStatement Select (on a single PreparedStatement)");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected long test(DataSource ds, int n) throws SQLException {
            long l;
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ds.getConnection();
                pstmt = con.prepareStatement(C3P0BenchmarkApp.EMPTY_TABLE_SELECT);
                l = this.test(pstmt, n);
            }
            catch (Throwable throwable) {
                StatementUtils.attemptClose(pstmt);
                ConnectionUtils.attemptClose((Connection)con);
                throw throwable;
            }
            StatementUtils.attemptClose((Statement)pstmt);
            ConnectionUtils.attemptClose((Connection)con);
            return l;
        }

        long test(PreparedStatement pstmt, int n) throws SQLException {
            long start = System.currentTimeMillis();
            for (int i = 0; i < n; ++i) {
                pstmt.executeQuery().close();
            }
            long end = System.currentTimeMillis();
            return end - start;
        }
    }

    static class PreparedStatementAcquireTest
    extends Test {
        PreparedStatementAcquireTest() {
            super("Acquire and Cleanup a PreparedStatement (same statement, many times)");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected long test(DataSource ds, int n) throws SQLException {
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ds.getConnection();
                long start = System.currentTimeMillis();
                for (int i = 0; i < n; ++i) {
                    try {
                        pstmt = con.prepareStatement(C3P0BenchmarkApp.EMPTY_TABLE_CONDITIONAL_SELECT);
                        continue;
                    }
                    finally {
                        StatementUtils.attemptClose((Statement)pstmt);
                    }
                }
                long end = System.currentTimeMillis();
                long l = end - start;
                return l;
            }
            finally {
                ConnectionUtils.attemptClose((Connection)con);
            }
        }
    }

    static class DataBaseMetaDataListNonexistentTablesTest
    extends Test {
        DataBaseMetaDataListNonexistentTablesTest() {
            super("DataBaseMetaDataListNonexistentTablesTest");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected long test(DataSource ds, int n) throws SQLException {
            Connection con = null;
            Statement stmt = null;
            try {
                con = ds.getConnection();
                long l = this.test(con, n);
                return l;
            }
            finally {
                StatementUtils.attemptClose(stmt);
                ConnectionUtils.attemptClose((Connection)con);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        long test(Connection con, int n) throws SQLException {
            ResultSet rs = null;
            try {
                long start = System.currentTimeMillis();
                for (int i = 0; i < n; ++i) {
                    rs = con.getMetaData().getTables(null, null, "PROBABLYNOT", new String[]{"TABLE"});
                }
                long end = System.currentTimeMillis();
                long l = end - start;
                return l;
            }
            finally {
                ResultSetUtils.attemptClose(rs);
            }
        }
    }

    static class StatementEmptyTableSelectTest
    extends Test {
        StatementEmptyTableSelectTest() {
            super("Empty Table Statement Select (on a single Statement)");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected long test(DataSource ds, int n) throws SQLException {
            long l;
            Connection con = null;
            Statement stmt = null;
            try {
                con = ds.getConnection();
                stmt = con.createStatement();
                l = this.test(stmt, n);
            }
            catch (Throwable throwable) {
                StatementUtils.attemptClose(stmt);
                ConnectionUtils.attemptClose((Connection)con);
                throw throwable;
            }
            StatementUtils.attemptClose((Statement)stmt);
            ConnectionUtils.attemptClose((Connection)con);
            return l;
        }

        long test(Statement stmt, int n) throws SQLException {
            long start = System.currentTimeMillis();
            for (int i = 0; i < n; ++i) {
                stmt.executeQuery(C3P0BenchmarkApp.EMPTY_TABLE_SELECT).close();
            }
            long end = System.currentTimeMillis();
            return end - start;
        }
    }

    static class StatementCreateTest
    extends Test {
        StatementCreateTest() {
            super("Statement Creation and Cleanup");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected long test(DataSource ds, int n) throws SQLException {
            Connection con = null;
            try {
                con = ds.getConnection();
                long l = this.test(con, n);
                return l;
            }
            finally {
                ConnectionUtils.attemptClose((Connection)con);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        long test(Connection con, int n) throws SQLException {
            Statement stmt = null;
            long start = System.currentTimeMillis();
            for (int i = 0; i < n; ++i) {
                try {
                    stmt = con.createStatement();
                    continue;
                }
                finally {
                    StatementUtils.attemptClose((Statement)stmt);
                }
            }
            long end = System.currentTimeMillis();
            return end - start;
        }
    }

    static class ConnectionAcquisitionTest
    extends Test {
        ConnectionAcquisitionTest() {
            super("Connection Acquisition and Cleanup");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected long test(DataSource ds, int n) throws Exception {
            long start = System.currentTimeMillis();
            for (int i = 0; i < n; ++i) {
                Connection con = null;
                try {
                    con = ds.getConnection();
                    continue;
                }
                finally {
                    ConnectionUtils.attemptClose((Connection)con);
                }
            }
            long end = System.currentTimeMillis();
            return end - start;
        }
    }

    static abstract class Test {
        String name;

        Test(String name) {
            this.name = name;
        }

        public void perform(DataSource unpooled, DataSource pooled, int iterations) throws Exception {
            double msecs_unpooled = (double)this.test(unpooled, iterations) / (double)iterations;
            double msecs_pooled = (double)this.test(pooled, iterations) / (double)iterations;
            System.out.println(this.name + " [ " + iterations + " iterations ]:");
            System.out.println("\tunpooled: " + msecs_unpooled + " msecs");
            System.out.println("\t  pooled: " + msecs_pooled + " msecs");
            System.out.println("\tspeed-up factor: " + msecs_unpooled / msecs_pooled + " times");
            System.out.println("\tspeed-up absolute: " + (msecs_unpooled - msecs_pooled) + " msecs");
            System.out.println();
        }

        protected abstract long test(DataSource var1, int var2) throws Exception;
    }
}

