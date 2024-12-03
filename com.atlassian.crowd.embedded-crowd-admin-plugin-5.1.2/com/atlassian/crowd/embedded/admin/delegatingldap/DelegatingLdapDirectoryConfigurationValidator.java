/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.GenericLDAP
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.validation.Errors
 *  org.springframework.validation.ValidationUtils
 */
package com.atlassian.crowd.embedded.admin.delegatingldap;

import com.atlassian.crowd.directory.GenericLDAP;
import com.atlassian.crowd.embedded.admin.delegatingldap.DelegatingLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.ldap.SharedLdapDirectoryConfigurationValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public final class DelegatingLdapDirectoryConfigurationValidator
extends SharedLdapDirectoryConfigurationValidator<DelegatingLdapDirectoryConfiguration> {
    public boolean supports(Class clazz) {
        return DelegatingLdapDirectoryConfiguration.class.isAssignableFrom(clazz);
    }

    @Override
    public void validateConfiguration(DelegatingLdapDirectoryConfiguration configuration, Errors errors) {
        String ldapAutoAddGroups = configuration.getLdapAutoAddGroups();
        if (ldapAutoAddGroups != null && ldapAutoAddGroups.indexOf(124) != -1) {
            errors.rejectValue("ldapAutoAddGroups", "invalid");
        }
        if (configuration.isCreateUserOnAuth()) {
            this.validateCreateUserOnAuthFields(configuration, errors);
            if (configuration.isSynchroniseGroupMemberships()) {
                this.validateSynchroniseGroupMembershipsFields(configuration, errors);
            }
        }
        if (!GenericLDAP.class.getName().equals(configuration.getType())) {
            ValidationUtils.rejectIfEmptyOrWhitespace((Errors)errors, (String)"ldapBasedn", (String)"required");
        }
    }

    private void validateSynchroniseGroupMembershipsFields(DelegatingLdapDirectoryConfiguration configuration, Errors errors) {
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapGroupObjectclass())) {
            errors.rejectValue("ldapGroupObjectclass", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapGroupFilter())) {
            errors.rejectValue("ldapGroupFilter", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapGroupName())) {
            errors.rejectValue("ldapGroupName", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapGroupDescription())) {
            errors.rejectValue("ldapGroupDescription", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapGroupUsernames())) {
            errors.rejectValue("ldapGroupUsernames", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserGroup())) {
            errors.rejectValue("ldapUserGroup", "required");
        }
    }

    private void validateCreateUserOnAuthFields(DelegatingLdapDirectoryConfiguration configuration, Errors errors) {
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserObjectclass())) {
            errors.rejectValue("ldapUserObjectclass", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserFilter())) {
            errors.rejectValue("ldapUserFilter", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserUsernameRdn())) {
            errors.rejectValue("ldapUserUsernameRdn", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserFirstname())) {
            errors.rejectValue("ldapUserFirstname", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserLastname())) {
            errors.rejectValue("ldapUserLastname", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserDisplayname())) {
            errors.rejectValue("ldapUserDisplayname", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getLdapUserEmail())) {
            errors.rejectValue("ldapUserEmail", "required");
        }
    }
}

