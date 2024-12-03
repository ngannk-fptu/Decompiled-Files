/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper;

import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;

public abstract class ContextMapperWithCustomAttributes<T>
implements ContextMapperWithRequiredAttributes<T> {
    protected final List<AttributeMapper> customAttributeMappers;

    public ContextMapperWithCustomAttributes(List<AttributeMapper> customAttributeMappers) {
        Preconditions.checkNotNull(customAttributeMappers);
        this.customAttributeMappers = customAttributeMappers;
    }

    @Override
    public final T mapFromContext(Object ctx) {
        return this.mapFromContext((DirContextAdapter)ctx);
    }

    public abstract T mapFromContext(DirContextAdapter var1);

    protected abstract Set<String> getCoreRequiredLdapAttributes();

    @Override
    public Set<String> getRequiredLdapAttributes() {
        HashSet<String> combined = new HashSet<String>();
        Set<String> core = this.getCoreRequiredLdapAttributes();
        if (core == null) {
            return null;
        }
        combined.addAll(this.getCoreRequiredLdapAttributes());
        for (AttributeMapper m : this.customAttributeMappers) {
            Set<String> attrs = m.getRequiredLdapAttributes();
            if (attrs == null) {
                return null;
            }
            combined.addAll(attrs);
        }
        return combined;
    }

    public static Set<String> aggregate(AttributeMapper ... mappers) {
        HashSet<String> combined = new HashSet<String>();
        for (AttributeMapper m : mappers) {
            Set<String> attrs = m.getRequiredLdapAttributes();
            if (attrs == null) {
                return null;
            }
            combined.addAll(attrs);
        }
        return combined;
    }
}

