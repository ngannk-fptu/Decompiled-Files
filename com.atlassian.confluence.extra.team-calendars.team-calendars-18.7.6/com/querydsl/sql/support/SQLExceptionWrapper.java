/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.StandardSystemProperty
 */
package com.querydsl.sql.support;

import com.google.common.base.StandardSystemProperty;
import com.querydsl.sql.support.JavaSE6SQLExceptionWrapper;
import com.querydsl.sql.support.JavaSE7SQLExceptionWrapper;
import java.sql.SQLException;

public abstract class SQLExceptionWrapper {
    public static final SQLExceptionWrapper INSTANCE;

    public abstract RuntimeException wrap(SQLException var1);

    public abstract RuntimeException wrap(String var1, SQLException var2);

    static {
        double javaVersion = Double.parseDouble(StandardSystemProperty.JAVA_SPECIFICATION_VERSION.value());
        INSTANCE = javaVersion > 1.6 ? new JavaSE7SQLExceptionWrapper() : new JavaSE6SQLExceptionWrapper();
    }
}

