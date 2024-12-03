/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.view;

import com.atlassian.plugin.util.Assertions;
import net.jcip.annotations.Immutable;

@Immutable
public final class ModuleId {
    private final long id;

    private ModuleId(long id) {
        this.id = id;
    }

    public long value() {
        return this.id;
    }

    public String toString() {
        return String.valueOf(this.id);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ModuleId otherId = (ModuleId)obj;
        return this.id == otherId.id;
    }

    public int hashCode() {
        return (int)(this.id ^ this.id >>> 32);
    }

    public static ModuleId valueOf(long id) {
        return new ModuleId(id);
    }

    public static ModuleId valueOf(String id) throws NumberFormatException {
        return new ModuleId(Long.parseLong((String)Assertions.notNull((String)"id", (Object)id)));
    }
}

