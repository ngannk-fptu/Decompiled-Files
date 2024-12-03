/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;

public interface AttributeMapper {
    public String getKey();

    public Set<String> getValues(DirContextAdapter var1) throws Exception;

    public Set<String> getRequiredLdapAttributes();
}

