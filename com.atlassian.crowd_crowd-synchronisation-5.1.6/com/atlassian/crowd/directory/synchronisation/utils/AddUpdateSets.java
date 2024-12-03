/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.synchronisation.utils;

import java.util.Collections;
import java.util.Set;

public class AddUpdateSets<A, U> {
    private final Set<A> toAddSet;
    private final Set<U> toUpdateSet;

    public AddUpdateSets(Set<A> addSet, Set<U> updateSet) {
        this.toAddSet = Collections.unmodifiableSet(addSet);
        this.toUpdateSet = Collections.unmodifiableSet(updateSet);
    }

    public Set<A> getToAddSet() {
        return this.toAddSet;
    }

    public Set<U> getToUpdateSet() {
        return this.toUpdateSet;
    }
}

