/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

import com.mchange.v1.lang.ClassUtils;
import com.mchange.v2.codegen.intfc.DelegatorGenerator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;

public final class RecreatePackage {
    static final Class[] intfcs = new Class[]{Connection.class, ResultSet.class, DatabaseMetaData.class, Statement.class, PreparedStatement.class, CallableStatement.class, DataSource.class};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] stringArray) {
        try {
            DelegatorGenerator delegatorGenerator = new DelegatorGenerator();
            String string = RecreatePackage.class.getName();
            String string2 = string.substring(0, string.lastIndexOf(46));
            for (int i = 0; i < intfcs.length; ++i) {
                Class clazz = intfcs[i];
                String string3 = ClassUtils.simpleClassName(clazz);
                String string4 = "Filter" + string3;
                String string5 = "SynchronizedFilter" + string3;
                Writer writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(string4 + ".java"));
                    delegatorGenerator.setMethodModifiers(1);
                    delegatorGenerator.writeDelegator(clazz, string2 + '.' + string4, writer);
                    System.err.println(string4);
                }
                finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                try {
                    writer = new BufferedWriter(new FileWriter(string5 + ".java"));
                    delegatorGenerator.setMethodModifiers(33);
                    delegatorGenerator.writeDelegator(clazz, string2 + '.' + string5, writer);
                    System.err.println(string5);
                    continue;
                }
                finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private RecreatePackage() {
    }
}

