/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.mapper.UserContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.mapper.entity.ActiveDirectoryUserAttributesMapper;
import com.atlassian.crowd.directory.ldap.mapper.entity.LDAPUserAttributesMapper;
import java.util.List;

public class ActiveDirectoryUserContextMapper
extends UserContextMapper {
    public ActiveDirectoryUserContextMapper(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper, List<AttributeMapper> customAttributeMappers) {
        super(directoryId, ldapPropertiesMapper, customAttributeMappers);
    }

    @Override
    protected LDAPUserAttributesMapper getAttributesMapper() {
        return new ActiveDirectoryUserAttributesMapper(this.directoryId, this.ldapPropertiesMapper);
    }
}

