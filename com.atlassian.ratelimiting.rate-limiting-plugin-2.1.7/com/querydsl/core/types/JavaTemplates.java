/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Templates;

public class JavaTemplates
extends Templates {
    public static final JavaTemplates DEFAULT = new JavaTemplates();

    public JavaTemplates() {
        this.add(Ops.EQ, "{0} == {1}");
        this.add(Ops.IS_NULL, "{0} == null");
        this.add(Ops.IS_NOT_NULL, "{0} != null");
        this.add(Ops.INSTANCE_OF, "{0} instanceof {1}");
        this.add(Ops.ORDINAL, "{0}.ordinal()", 5);
        this.add(Ops.IN, "{1}.contains({0})");
        this.add(Ops.NOT_IN, "!{1}.contains({0})");
        this.add(Ops.COL_IS_EMPTY, "{0}.isEmpty()", 5);
        this.add(Ops.COL_SIZE, "{0}.size()", 5);
        this.add(Ops.ARRAY_SIZE, "{0}.length", 5);
        this.add(Ops.MAP_IS_EMPTY, "{0}.isEmpty()", 5);
        this.add(Ops.MAP_SIZE, "{0}.size()", 5);
        this.add(Ops.CONTAINS_KEY, "{0}.containsKey({1})");
        this.add(Ops.CONTAINS_VALUE, "{0}.containsValue({1})");
        this.add(Ops.BETWEEN, "{1} <= {0} && {0} <= {2}");
        this.add(Ops.CHAR_AT, "{0}.charAt({1})");
        this.add(Ops.LOWER, "{0}.toLowerCase()", 5);
        this.add(Ops.SUBSTR_1ARG, "{0}.substring({1})");
        this.add(Ops.SUBSTR_2ARGS, "{0}.substring({1},{2})");
        this.add(Ops.TRIM, "{0}.trim()", 5);
        this.add(Ops.UPPER, "{0}.toUpperCase()", 5);
        this.add(Ops.MATCHES, "{0}.matches({1})");
        this.add(Ops.MATCHES_IC, "{0l}.matches({1l})");
        this.add(Ops.STRING_LENGTH, "{0}.length()", 5);
        this.add(Ops.STRING_IS_EMPTY, "{0}.isEmpty()", 5);
        this.add(Ops.STRING_CONTAINS, "{0}.contains({1})");
        this.add(Ops.STRING_CONTAINS_IC, "{0l}.contains({1l})");
        this.add(Ops.STARTS_WITH, "{0}.startsWith({1})");
        this.add(Ops.STARTS_WITH_IC, "{0l}.startsWith({1l})");
        this.add(Ops.INDEX_OF, "{0}.indexOf({1})");
        this.add(Ops.INDEX_OF_2ARGS, "{0}.indexOf({1},{2})");
        this.add(Ops.EQ_IGNORE_CASE, "{0}.equalsIgnoreCase({1})");
        this.add(Ops.ENDS_WITH, "{0}.endsWith({1})");
        this.add(Ops.ENDS_WITH_IC, "{0l}.endsWith({1l})");
        this.add(Ops.StringOps.LOCATE, "({1}.indexOf({0})+1)");
        this.add(Ops.StringOps.LOCATE2, "({1}.indexOf({0},{2s}-1)+1)");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "{0}.getDayOfMonth()", 5);
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "{0}.getDayOfWeek()", 5);
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "{0}.getDayOfYear()", 5);
        this.add(Ops.DateTimeOps.HOUR, "{0}.getHour()", 5);
        this.add(Ops.DateTimeOps.MINUTE, "{0}.getMinute()", 5);
        this.add(Ops.DateTimeOps.MONTH, "{0}.getMonth()", 5);
        this.add(Ops.DateTimeOps.MILLISECOND, "{0}.getMilliSecond()", 5);
        this.add(Ops.DateTimeOps.SECOND, "{0}.getSecond()", 5);
        this.add(Ops.DateTimeOps.WEEK, "{0}.getWeek()", 5);
        this.add(Ops.DateTimeOps.YEAR, "{0}.getYear()", 5);
        this.add(Ops.DateTimeOps.YEAR_MONTH, "{0}.getYear() * 100 + {0}.getMonth()");
        this.add(Ops.CASE, "({0})");
        this.add(Ops.CASE_WHEN, "({0}) ? ({1}) : ({2})");
        this.add(Ops.CASE_ELSE, "{0}");
        this.add(Ops.CASE_EQ, "({0})");
        this.add(Ops.CASE_EQ_WHEN, "({0} == {1}) ? ({2}) : ({3})");
        this.add(Ops.CASE_EQ_ELSE, "{0}");
        for (Ops.MathOps op : Ops.MathOps.values()) {
            this.add(op, "Math." + this.getTemplate(op));
        }
    }
}

