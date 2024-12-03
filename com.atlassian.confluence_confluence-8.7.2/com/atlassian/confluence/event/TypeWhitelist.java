/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TypeWhitelist
implements Predicate<Object> {
    private final Set<Class> types;

    public TypeWhitelist(Set<Class> types) {
        this.types = types;
    }

    public boolean apply(@NonNull Object candidate) {
        Preconditions.checkNotNull((Object)candidate);
        for (Class type : this.types) {
            if (!type.isAssignableFrom(candidate.getClass())) continue;
            return true;
        }
        return false;
    }
}

