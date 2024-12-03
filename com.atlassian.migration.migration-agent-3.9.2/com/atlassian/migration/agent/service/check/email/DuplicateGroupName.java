/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.email;

import java.io.Serializable;
import java.util.Objects;

public class DuplicateGroupName
implements Serializable {
    private static final long serialVersionUID = 6484461483079406961L;
    public final String name;

    public DuplicateGroupName(String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DuplicateGroupName other = (DuplicateGroupName)o;
        return Objects.equals(this.name, other.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

