/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;

public final class Groups {
    @Deprecated
    public static final Function<Group, String> NAME_FUNCTION = DirectoryEntity::getName;
    @Deprecated
    public static final Function<Group, String> LOWER_NAME_FUNCTION = Functions.compose((Function)IdentifierUtils.TO_LOWER_CASE, NAME_FUNCTION);

    private Groups() {
    }

    @Deprecated
    public static Iterable<String> namesOf(Iterable<? extends Group> groups) {
        return Iterables.transform(groups, NAME_FUNCTION);
    }
}

