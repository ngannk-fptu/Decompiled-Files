/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import java.io.Serializable;

public final class Status
implements Serializable {
    public static final Status STATUS_UNINITIALISED = new Status(0, "STATUS_UNINITIALISED");
    public static final Status STATUS_ALIVE = new Status(1, "STATUS_ALIVE");
    public static final Status STATUS_SHUTDOWN = new Status(2, "STATUS_SHUTDOWN");
    private static final long serialVersionUID = 2732730630423367732L;
    private static final Status[] STATUSES = new Status[]{STATUS_UNINITIALISED, STATUS_ALIVE, STATUS_SHUTDOWN};
    private final String name;
    private final int intValue;

    private Status(int intValue, String name) {
        this.intValue = intValue;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static Status convertIntToStatus(int statusAsInt) throws IllegalArgumentException {
        if (statusAsInt < Status.STATUS_UNINITIALISED.intValue || statusAsInt > Status.STATUS_SHUTDOWN.intValue) {
            throw new IllegalArgumentException("int value of statuses must be between 1 and three");
        }
        return STATUSES[statusAsInt];
    }

    public int intValue() {
        return this.intValue;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Status)) {
            return false;
        }
        return ((Status)object).intValue == this.intValue;
    }

    public boolean equals(Status status) {
        if (status == null) {
            return false;
        }
        return this.intValue == status.intValue;
    }

    public int hashCode() {
        return this.intValue;
    }
}

