/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum ExpressionType {
    SQL("SQL");

    private final String expressionType;

    private ExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public String toString() {
        return this.expressionType;
    }

    public static ExpressionType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (ExpressionType enumEntry : ExpressionType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

