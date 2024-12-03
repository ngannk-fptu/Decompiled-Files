/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.ognl;

public class ErrorMessageBuilder {
    private final StringBuilder message = new StringBuilder();

    public static ErrorMessageBuilder create() {
        return new ErrorMessageBuilder();
    }

    private ErrorMessageBuilder() {
    }

    public ErrorMessageBuilder errorSettingExpressionWithValue(String expr, Object value) {
        this.appenExpression(expr);
        if (value instanceof Object[]) {
            this.appendValueAsArray((Object[])value, this.message);
        } else {
            this.appendValue(value);
        }
        return this;
    }

    private void appenExpression(String expr) {
        this.message.append("Error setting expression '");
        this.message.append(expr);
        this.message.append("' with value ");
    }

    private void appendValue(Object value) {
        this.message.append("'");
        this.message.append(value);
        this.message.append("'");
    }

    private void appendValueAsArray(Object[] valueArray, StringBuilder msg) {
        msg.append("[");
        for (int index = 0; index < valueArray.length; ++index) {
            this.appendValue(valueArray[index]);
            if (!this.hasMoreElements(valueArray, index)) continue;
            msg.append(", ");
        }
        msg.append("]");
    }

    private boolean hasMoreElements(Object[] valueArray, int index) {
        return index < valueArray.length + 1;
    }

    public String build() {
        return this.message.toString();
    }
}

