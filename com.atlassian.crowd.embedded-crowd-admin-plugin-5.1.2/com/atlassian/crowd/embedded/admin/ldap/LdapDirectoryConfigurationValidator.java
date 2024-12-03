/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.GenericLDAP
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.springframework.validation.Errors
 *  org.springframework.validation.ValidationUtils
 */
package com.atlassian.crowd.embedded.admin.ldap;

import com.atlassian.crowd.directory.GenericLDAP;
import com.atlassian.crowd.embedded.admin.ldap.LdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.ldap.SharedLdapDirectoryConfigurationValidator;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public final class LdapDirectoryConfigurationValidator
extends SharedLdapDirectoryConfigurationValidator<LdapDirectoryConfiguration> {
    public boolean supports(Class clazz) {
        return LdapDirectoryConfiguration.class.isAssignableFrom(clazz);
    }

    @Override
    public void validateConfiguration(LdapDirectoryConfiguration configuration, Errors errors) {
        String ldapAutoAddGroups;
        if (NumberUtils.toLong((String)configuration.getLdapCacheSynchroniseIntervalInMin()) < 1L) {
            errors.rejectValue("ldapCacheSynchroniseIntervalInMin", "invalid");
        }
        if ((ldapAutoAddGroups = configuration.getLdapAutoAddGroups()) != null && ldapAutoAddGroups.indexOf(124) != -1) {
            errors.rejectValue("ldapAutoAddGroups", "invalid");
        }
        if (!GenericLDAP.class.getName().equals(configuration.getType())) {
            ValidationUtils.rejectIfEmptyOrWhitespace((Errors)errors, (String)"ldapBasedn", (String)"required");
        }
    }
}

