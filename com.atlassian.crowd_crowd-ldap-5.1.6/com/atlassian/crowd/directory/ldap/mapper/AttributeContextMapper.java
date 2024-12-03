/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.mapper;

import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import java.util.Collections;
import java.util.Set;

public abstract class AttributeContextMapper<T>
implements ContextMapperWithRequiredAttributes<T> {
    protected final String propertyName;

    public AttributeContextMapper(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.propertyName);
    }
}

