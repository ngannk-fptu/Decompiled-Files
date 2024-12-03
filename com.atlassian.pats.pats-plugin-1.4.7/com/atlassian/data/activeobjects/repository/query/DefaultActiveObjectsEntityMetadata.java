/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityMetadata;
import org.springframework.util.Assert;

public class DefaultActiveObjectsEntityMetadata<T>
implements ActiveObjectsEntityMetadata<T> {
    private final Class<T> domainType;

    public DefaultActiveObjectsEntityMetadata(Class<T> domainType) {
        Assert.notNull(domainType, (String)"Domain type must not be null!");
        this.domainType = domainType;
    }

    @Override
    public Class<T> getJavaType() {
        return this.domainType;
    }

    @Override
    public String getEntityName() {
        return this.domainType.getName();
    }
}

