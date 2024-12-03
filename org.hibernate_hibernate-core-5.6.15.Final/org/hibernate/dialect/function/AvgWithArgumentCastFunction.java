/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;

public class AvgWithArgumentCastFunction
extends StandardAnsiSqlAggregationFunctions.AvgFunction {
    private final String castType;

    public AvgWithArgumentCastFunction(String castType) {
        this.castType = castType;
    }

    @Override
    protected String renderArgument(String argument, int firstArgumentJdbcType) {
        if (firstArgumentJdbcType == 8 || firstArgumentJdbcType == 6) {
            return argument;
        }
        return "cast(" + argument + " as " + this.castType + ")";
    }
}

