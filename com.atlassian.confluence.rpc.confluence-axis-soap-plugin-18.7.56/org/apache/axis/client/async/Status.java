/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client.async;

import org.apache.axis.constants.Enum;

public class Status
extends Enum {
    private static final Type type = new Type();
    public static final String NONE_STR = "none";
    public static final String INTERRUPTED_STR = "interrupted";
    public static final String COMPLETED_STR = "completed";
    public static final String EXCEPTION_STR = "exception";
    public static final Status NONE = type.getStatus("none");
    public static final Status INTERRUPTED = type.getStatus("interrupted");
    public static final Status COMPLETED = type.getStatus("completed");
    public static final Status EXCEPTION = type.getStatus("exception");
    public static final Status DEFAULT = NONE;

    public static Status getDefault() {
        return (Status)type.getDefault();
    }

    public static final Status getStatus(int style) {
        return type.getStatus(style);
    }

    public static final Status getStatus(String style) {
        return type.getStatus(style);
    }

    public static final Status getStatus(String style, Status dephault) {
        return type.getStatus(style, dephault);
    }

    public static final boolean isValid(String style) {
        return type.isValid(style);
    }

    public static final int size() {
        return type.size();
    }

    public static final String[] getUses() {
        return type.getEnumNames();
    }

    private Status(int value, String name) {
        super(type, value, name);
    }

    static {
        type.setDefault(DEFAULT);
    }

    public static class Type
    extends Enum.Type {
        private Type() {
            super("status", new Enum[]{new Status(0, Status.NONE_STR), new Status(1, Status.INTERRUPTED_STR), new Status(2, Status.COMPLETED_STR), new Status(3, Status.EXCEPTION_STR)});
        }

        public final Status getStatus(int status) {
            return (Status)this.getEnum(status);
        }

        public final Status getStatus(String status) {
            return (Status)this.getEnum(status);
        }

        public final Status getStatus(String status, Status dephault) {
            return (Status)this.getEnum(status, dephault);
        }
    }
}

