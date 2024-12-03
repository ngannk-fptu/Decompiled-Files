/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

@Immutable
public class GadgetId
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;

    private GadgetId(String id) {
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
        GadgetId otherId = (GadgetId)o;
        return this.id.equals(otherId.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public static GadgetId valueOf(String id) {
        return new GadgetId(id);
    }
}

