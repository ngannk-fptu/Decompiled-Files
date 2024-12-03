/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.crowd.directory.ldap.mapper.entity;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.UserAccountControlUtil;
import com.atlassian.crowd.directory.ldap.mapper.entity.LDAPUserAttributesMapper;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class ActiveDirectoryUserAttributesMapper
extends LDAPUserAttributesMapper {
    public ActiveDirectoryUserAttributesMapper(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper) {
        super(directoryId, ldapPropertiesMapper);
    }

    @Override
    protected boolean getUserActiveFromAttribute(Attributes directoryAttributes) {
        try {
            Attribute attribute = directoryAttributes.get("userAccountControl");
            if (attribute != null) {
                String userAccountControlValue = attribute.get().toString();
                return UserAccountControlUtil.isUserEnabled(userAccountControlValue);
            }
            this.logger.debug("LDAP attribute userAccountControl is not present, user is enabled by default");
            return true;
        }
        catch (NamingException e) {
            return true;
        }
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Sets.union(super.getRequiredLdapAttributes(), Collections.singleton("userAccountControl"));
    }
}

