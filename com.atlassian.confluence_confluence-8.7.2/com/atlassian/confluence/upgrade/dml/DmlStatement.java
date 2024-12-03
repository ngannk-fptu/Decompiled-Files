/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.dml;

import java.util.Arrays;

public class DmlStatement {
    private final String statementWithPlaceholders;
    private final Object[] arguments;

    public DmlStatement(String statementWithPlaceholders, Object[] arguments) {
        this.statementWithPlaceholders = statementWithPlaceholders;
        this.arguments = arguments;
    }

    public String getStatementWithPlaceholders() {
        return this.statementWithPlaceholders;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DmlStatement that = (DmlStatement)o;
        if (!Arrays.equals(this.arguments, that.arguments)) {
            return false;
        }
        return this.statementWithPlaceholders.equals(that.statementWithPlaceholders);
    }

    public int hashCode() {
        int result = this.statementWithPlaceholders.hashCode();
        result = 31 * result + Arrays.hashCode(this.arguments);
        return result;
    }

    public String toString() {
        return "DmlStatement{statementWithPlaceholders='" + this.statementWithPlaceholders + "', arguments=" + Arrays.toString(this.arguments) + "}";
    }
}

