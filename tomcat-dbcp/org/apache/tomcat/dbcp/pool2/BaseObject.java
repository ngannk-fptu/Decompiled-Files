/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

public abstract class BaseObject {
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(" [");
        this.toStringAppendFields(builder);
        builder.append("]");
        return builder.toString();
    }

    protected void toStringAppendFields(StringBuilder builder) {
    }
}

