/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.util.impl.CommandLineParserImpl;
import com.mchange.v1.db.sql.Schema;
import com.mchange.v1.util.CleanupUtils;
import java.sql.Connection;
import java.sql.DriverManager;

public class SchemaManager {
    static final String[] VALID = new String[]{"create", "drop"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] stringArray) {
        Connection connection = null;
        try {
            String[] stringArray2;
            CommandLineParserImpl commandLineParserImpl = new CommandLineParserImpl(stringArray, VALID, null, null);
            boolean bl = commandLineParserImpl.checkSwitch("create");
            if (!commandLineParserImpl.checkArgv()) {
                SchemaManager.usage();
            }
            if (!(bl ^ commandLineParserImpl.checkSwitch("drop"))) {
                SchemaManager.usage();
            }
            if ((stringArray2 = commandLineParserImpl.findUnswitchedArgs()).length == 2) {
                connection = DriverManager.getConnection(stringArray2[0]);
            } else if (stringArray2.length == 4) {
                connection = DriverManager.getConnection(stringArray2[0], stringArray2[1], stringArray2[2]);
            } else {
                SchemaManager.usage();
            }
            connection.setAutoCommit(false);
            Schema schema = (Schema)Class.forName(stringArray2[stringArray2.length - 1]).newInstance();
            if (bl) {
                schema.createSchema(connection);
                System.out.println("Schema created.");
            } else {
                schema.dropSchema(connection);
                System.out.println("Schema dropped.");
            }
            CleanupUtils.attemptClose(connection);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            CleanupUtils.attemptClose(connection);
        }
    }

    static void usage() {
        System.err.println("java -Djdbc.drivers=<driverclass> com.mchange.v1.db.sql.SchemaManager [-create | -drop] <jdbc_url> [<user> <password>] <schemaclass>");
        System.exit(-1);
    }
}

