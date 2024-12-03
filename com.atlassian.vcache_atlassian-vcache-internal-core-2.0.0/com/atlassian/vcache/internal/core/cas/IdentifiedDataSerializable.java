/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core.cas;

import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import java.io.Serializable;
import java.util.Objects;

public class IdentifiedDataSerializable
extends IdentifiedData {
    private final Serializable object;

    public IdentifiedDataSerializable(Serializable object) {
        this.object = Objects.requireNonNull(object);
    }

    public Serializable getObject() {
        return this.object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentifiedDataSerializable)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        IdentifiedDataSerializable that = (IdentifiedDataSerializable)o;
        return this.object.equals(that.object);
    }

    public int hashCode() {
        return this.object.hashCode();
    }
}

