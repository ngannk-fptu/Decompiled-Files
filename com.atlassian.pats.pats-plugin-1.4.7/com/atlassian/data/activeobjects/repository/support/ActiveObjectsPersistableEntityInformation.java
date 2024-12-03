/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.NotImplementedException
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityInformation;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.NotImplementedException;

public class ActiveObjectsPersistableEntityInformation<T extends RawEntity<ID>, ID>
implements ActiveObjectsEntityInformation<T, ID> {
    private final Class<T> domainClass;

    public ActiveObjectsPersistableEntityInformation(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    @Override
    public boolean isNew(T entity) {
        return false;
    }

    @Override
    public ID getId(T entity) {
        throw new NotImplementedException("getId");
    }

    @Override
    public Class<ID> getIdType() {
        throw new NotImplementedException("getIdType");
    }

    @Override
    public Class<T> getJavaType() {
        return this.domainClass;
    }

    @Override
    public String getEntityName() {
        return this.domainClass.getSimpleName();
    }
}

