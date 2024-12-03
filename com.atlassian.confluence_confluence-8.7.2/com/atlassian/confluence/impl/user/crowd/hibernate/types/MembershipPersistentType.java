/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.membership.MembershipType
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.types;

import com.atlassian.confluence.impl.user.crowd.hibernate.types.EnumPersistentType;
import com.atlassian.crowd.model.membership.MembershipType;

@Deprecated(forRemoval=true)
public final class MembershipPersistentType
extends EnumPersistentType<MembershipType> {
    @Override
    public Class<MembershipType> returnedClass() {
        return MembershipType.class;
    }
}

