/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.dashboard;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

@Immutable
public class DashboardId
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;

    private DashboardId(String id) {
        this.id = id;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (this.id == null) {
            throw new InvalidObjectException("id cannot be null");
        }
    }

    public String value() {
        return this.id;
    }

    public String toString() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DashboardId otherId = (DashboardId)o;
        return this.id.equals(otherId.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public static DashboardId valueOf(String id) {
        return new DashboardId(id);
    }
}

