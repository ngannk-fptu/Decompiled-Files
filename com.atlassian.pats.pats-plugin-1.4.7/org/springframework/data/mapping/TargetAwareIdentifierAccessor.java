/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping;

import org.springframework.data.mapping.IdentifierAccessor;

public abstract class TargetAwareIdentifierAccessor
implements IdentifierAccessor {
    private final Object target;

    public TargetAwareIdentifierAccessor(Object target) {
        this.target = target;
    }

    @Override
    public Object getRequiredIdentifier() {
        Object identifier = this.getIdentifier();
        if (identifier != null) {
            return identifier;
        }
        throw new IllegalStateException(String.format("Could not obtain identifier from %s!", this.target));
    }
}

