/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import java.sql.SQLException;
import java.util.List;

public interface SQLExceptionTranslator {
    public RuntimeException translate(String var1, List<Object> var2, SQLException var3);

    public RuntimeException translate(SQLException var1);
}

