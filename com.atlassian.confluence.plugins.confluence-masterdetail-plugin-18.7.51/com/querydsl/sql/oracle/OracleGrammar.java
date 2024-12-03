/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.oracle;

import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import java.util.Date;

public final class OracleGrammar {
    public static final NumberExpression<Integer> level = Expressions.numberTemplate(Integer.class, "level", new Object[0]);
    public static final NumberExpression<Integer> rownum = Expressions.numberTemplate(Integer.class, "rownum", new Object[0]);
    public static final NumberExpression<Integer> rowid = Expressions.numberTemplate(Integer.class, "rowid", new Object[0]);
    public static final DateExpression<Date> sysdate = Expressions.dateTemplate(Date.class, "sysdate", new Object[0]);

    private OracleGrammar() {
    }
}

