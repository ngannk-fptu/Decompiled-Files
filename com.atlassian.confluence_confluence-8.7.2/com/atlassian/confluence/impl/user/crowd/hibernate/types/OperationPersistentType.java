/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.OperationType
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.types;

import com.atlassian.confluence.impl.user.crowd.hibernate.types.EnumPersistentType;
import com.atlassian.crowd.embedded.api.OperationType;

@Deprecated(forRemoval=true)
public final class OperationPersistentType
extends EnumPersistentType<OperationType> {
    @Override
    public Class<OperationType> returnedClass() {
        return OperationType.class;
    }
}

