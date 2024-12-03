/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.logging;

public class ErrorLoggedEvent {
    private final String level;
    private final String category;
    private final String methodName;
    private final String exceptionType;

    public ErrorLoggedEvent(String level, String category, String methodName, String exceptionType) {
        this.level = level;
        this.category = category;
        this.methodName = methodName;
        this.exceptionType = exceptionType;
    }

    public String getLevel() {
        return this.level;
    }

    public String getCategory() {
        return this.category;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getExceptionType() {
        return this.exceptionType;
    }
}

