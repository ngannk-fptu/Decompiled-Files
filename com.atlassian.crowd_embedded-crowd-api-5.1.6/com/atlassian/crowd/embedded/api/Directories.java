/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.Directory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public final class Directories {
    @Deprecated
    public static final Function<Directory, String> NAME_FUNCTION = new Function<Directory, String>(){

        public String apply(Directory directory) {
            return directory.getName();
        }
    };
    @Deprecated
    public static final Predicate<Directory> ACTIVE_FILTER = new Predicate<Directory>(){

        public boolean apply(Directory directory) {
            return directory.isActive();
        }
    };

    private Directories() {
    }

    @Deprecated
    public static Iterable<String> namesOf(Iterable<? extends Directory> directories) {
        return Iterables.transform(directories, NAME_FUNCTION);
    }

    public static Predicate<Directory> directoryWithIdPredicate(final long directoryId) {
        return new Predicate<Directory>(){

            public boolean apply(Directory directory) {
                return directory.getId() == directoryId;
            }
        };
    }
}

