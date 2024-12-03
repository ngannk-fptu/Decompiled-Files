/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ConnectionUtils
 *  com.mchange.v1.db.sql.StatementUtils
 *  com.mchange.v2.naming.ReferenceableUtils
 *  com.mchange.v2.ser.SerializableUtils
 */
package com.mchange.v2.c3p0.test;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v1.db.sql.StatementUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;
import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;
import com.mchange.v2.naming.ReferenceableUtils;
import com.mchange.v2.ser.SerializableUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.DataSource;

public final class TestRefSerStuff {
    static String toString(DataSource ds) {
        if (ds instanceof AbstractPoolBackedDataSource) {
            return ((AbstractPoolBackedDataSource)ds).toString(true);
        }
        return ds.toString();
    }

    static void create(DataSource ds) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("CREATE TABLE TRSS_TABLE ( a_col VARCHAR(16) )");
        }
        catch (Throwable throwable) {
            StatementUtils.attemptClose(stmt);
            ConnectionUtils.attemptClose((Connection)con);
            throw throwable;
        }
        StatementUtils.attemptClose((Statement)stmt);
        ConnectionUtils.attemptClose((Connection)con);
    }

    static void drop(DataSource ds) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("DROP TABLE TRSS_TABLE");
        }
        catch (Throwable throwable) {
            StatementUtils.attemptClose(stmt);
            ConnectionUtils.attemptClose((Connection)con);
            throw throwable;
        }
        StatementUtils.attemptClose((Statement)stmt);
        ConnectionUtils.attemptClose((Connection)con);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void doSomething(DataSource ds) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            int i = stmt.executeUpdate("INSERT INTO TRSS_TABLE VALUES ('" + System.currentTimeMillis() + "')");
            if (i != 1) {
                throw new SQLException("Insert failed somehow strange!");
            }
        }
        catch (Throwable throwable) {
            StatementUtils.attemptClose(stmt);
            ConnectionUtils.attemptClose((Connection)con);
            throw throwable;
        }
        StatementUtils.attemptClose((Statement)stmt);
        ConnectionUtils.attemptClose((Connection)con);
    }

    static void doTest(DataSource checkMe) throws Exception {
        TestRefSerStuff.doSomething(checkMe);
        System.err.println("\tcreated:   " + TestRefSerStuff.toString(checkMe));
        DataSource afterSer = (DataSource)SerializableUtils.testSerializeDeserialize((Object)checkMe);
        TestRefSerStuff.doSomething(afterSer);
        System.err.println("\tafter ser: " + TestRefSerStuff.toString(afterSer));
        Reference ref = ((Referenceable)((Object)checkMe)).getReference();
        DataSource afterRef = (DataSource)ReferenceableUtils.referenceToObject((Reference)ref, null, null, null);
        TestRefSerStuff.doSomething(afterRef);
        System.err.println("\tafter ref: " + TestRefSerStuff.toString(afterRef));
    }

    public static void main(String[] argv) {
        if (argv.length > 0) {
            System.err.println(TestRefSerStuff.class.getName() + " now requires no args. Please set everything in standard c3p0 config files.");
            return;
        }
        try {
            DriverManagerDataSource dmds = new DriverManagerDataSource();
            try {
                TestRefSerStuff.drop(dmds);
            }
            catch (Exception exception) {
                // empty catch block
            }
            TestRefSerStuff.create(dmds);
            System.err.println("DriverManagerDataSource:");
            TestRefSerStuff.doTest(dmds);
            WrapperConnectionPoolDataSource wcpds = new WrapperConnectionPoolDataSource();
            wcpds.setNestedDataSource(dmds);
            PoolBackedDataSource pbds = new PoolBackedDataSource();
            pbds.setConnectionPoolDataSource(wcpds);
            System.err.println("PoolBackedDataSource:");
            TestRefSerStuff.doTest(pbds);
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            TestRefSerStuff.doTest(cpds);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

