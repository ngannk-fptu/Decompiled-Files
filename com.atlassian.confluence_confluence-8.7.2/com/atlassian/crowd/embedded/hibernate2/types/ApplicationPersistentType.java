/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 */
package com.atlassian.crowd.embedded.hibernate2.types;

import com.atlassian.confluence.impl.user.crowd.hibernate.types.EnumPersistentType;
import com.atlassian.crowd.model.application.ApplicationType;

@Deprecated
public final class ApplicationPersistentType
extends EnumPersistentType<ApplicationType> {
    @Override
    public Class<ApplicationType> returnedClass() {
        return ApplicationType.class;
    }
}

