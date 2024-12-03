/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.membership.MembershipType
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.types;

import com.atlassian.confluence.impl.user.crowd.hibernate.types.DirectoryPersistentType;
import com.atlassian.confluence.impl.user.crowd.hibernate.types.EnumPersistentType;
import com.atlassian.confluence.impl.user.crowd.hibernate.types.MembershipPersistentType;
import com.atlassian.confluence.impl.user.crowd.hibernate.types.OperationPersistentType;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.hibernate2.types.GroupPersistentType;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.membership.MembershipType;
import java.util.HashMap;

public final class TypeMapper {
    private static final HashMap<Class<? extends Enum>, Class<? extends EnumPersistentType>> mappings = new HashMap();

    public static Class enumToCustomTypeClass(Class<? extends Enum> enumType) {
        return mappings.get(enumType);
    }

    static {
        mappings.put(DirectoryType.class, DirectoryPersistentType.class);
        mappings.put(GroupType.class, GroupPersistentType.class);
        mappings.put(MembershipType.class, MembershipPersistentType.class);
        mappings.put(OperationType.class, OperationPersistentType.class);
    }
}

