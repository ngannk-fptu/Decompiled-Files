/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping;

import org.springframework.lang.Nullable;

public interface IdentifierAccessor {
    @Nullable
    public Object getIdentifier();

    default public Object getRequiredIdentifier() {
        Object identifier = this.getIdentifier();
        if (identifier != null) {
            return identifier;
        }
        throw new IllegalStateException("Could not obtain identifier!");
    }
}

