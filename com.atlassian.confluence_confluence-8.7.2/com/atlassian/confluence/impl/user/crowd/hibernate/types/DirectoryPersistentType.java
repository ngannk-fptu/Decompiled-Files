/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DirectoryType
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.types;

import com.atlassian.confluence.impl.user.crowd.hibernate.types.EnumPersistentType;
import com.atlassian.crowd.embedded.api.DirectoryType;

@Deprecated(forRemoval=true)
public final class DirectoryPersistentType
extends EnumPersistentType<DirectoryType> {
    @Override
    public Class<DirectoryType> returnedClass() {
        return DirectoryType.class;
    }
}

