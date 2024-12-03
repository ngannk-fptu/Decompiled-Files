/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.ContextMapper
 */
package com.atlassian.crowd.directory.ldap.mapper;

import java.util.Set;
import org.springframework.ldap.core.ContextMapper;

public interface ContextMapperWithRequiredAttributes<T>
extends ContextMapper {
    public T mapFromContext(Object var1);

    public Set<String> getRequiredLdapAttributes();
}

