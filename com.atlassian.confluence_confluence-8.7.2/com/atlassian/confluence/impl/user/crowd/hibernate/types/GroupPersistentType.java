/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.GroupType
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.types;

import com.atlassian.confluence.impl.user.crowd.hibernate.types.EnumPersistentType;
import com.atlassian.crowd.model.group.GroupType;

@Deprecated(forRemoval=true)
public final class GroupPersistentType
extends EnumPersistentType<GroupType> {
    @Override
    public Class<GroupType> returnedClass() {
        return GroupType.class;
    }
}

