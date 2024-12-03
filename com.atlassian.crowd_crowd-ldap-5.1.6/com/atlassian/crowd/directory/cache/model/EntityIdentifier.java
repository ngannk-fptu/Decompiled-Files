/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.cache.model;

import com.atlassian.crowd.directory.cache.model.EntityType;
import java.io.Serializable;

public class EntityIdentifier
implements Serializable {
    private final EntityType type;
    private final String name;

    public EntityIdentifier(EntityType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public EntityType getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityIdentifier that = (EntityIdentifier)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return this.type == that.type;
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }
}

