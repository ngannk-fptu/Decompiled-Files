/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.directory.spi;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

@Immutable
public final class ExternalGadgetSpecId
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;

    protected ExternalGadgetSpecId(String id) {
        this.id = id;
    }

    public String value() {
        return this.id;
    }

    public static ExternalGadgetSpecId valueOf(String id) {
        return new ExternalGadgetSpecId(id);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (this.id == null) {
            throw new InvalidObjectException("id cannot be null");
        }
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (other.getClass() != ExternalGadgetSpecId.class) {
            return false;
        }
        ExternalGadgetSpecId that = (ExternalGadgetSpecId)other;
        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id;
    }
}

