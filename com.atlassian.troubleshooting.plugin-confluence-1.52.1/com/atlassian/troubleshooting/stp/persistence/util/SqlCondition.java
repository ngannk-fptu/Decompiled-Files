/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.persistence.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@VisibleForTesting
public class SqlCondition {
    private final String expression;
    private final String operator;
    private final Object value;

    public SqlCondition(String expression, String operator, Object value) {
        this.expression = Objects.requireNonNull(expression);
        this.operator = Objects.requireNonNull(operator);
        this.value = Objects.requireNonNull(value);
    }

    public static SqlCondition isEqual(String expression, Object value) {
        return new SqlCondition(expression, "=", value);
    }

    @Nonnull
    public String getSql() {
        return String.format("%s %s ?", this.expression, this.operator);
    }

    @Nonnull
    public Object getBindValue() {
        return this.value;
    }
}

