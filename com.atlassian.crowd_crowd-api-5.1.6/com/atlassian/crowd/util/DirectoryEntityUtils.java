/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.model.DirectoryEntity;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class DirectoryEntityUtils {
    public static <T extends DirectoryEntity> Predicate<T> whereNameEquals(final String name) {
        Preconditions.checkNotNull((Object)name, (Object)"name to match must not be null");
        return new Predicate<T>(){

            public boolean apply(T entity) {
                return name.equals(entity.getName());
            }
        };
    }
}

