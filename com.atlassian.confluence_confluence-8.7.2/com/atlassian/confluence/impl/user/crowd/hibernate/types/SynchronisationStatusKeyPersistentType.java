/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.types;

import com.atlassian.confluence.impl.user.crowd.hibernate.types.EnumPersistentType;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;

@Deprecated(forRemoval=true)
public final class SynchronisationStatusKeyPersistentType
extends EnumPersistentType<SynchronisationStatusKey> {
    @Override
    public Class<SynchronisationStatusKey> returnedClass() {
        return SynchronisationStatusKey.class;
    }
}

