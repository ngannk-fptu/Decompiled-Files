/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.util.UserUtils
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.NamingException
 *  org.springframework.ldap.UncategorizedLdapException
 */
package com.atlassian.crowd.directory.ldap.mapper.entity;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.util.DirectoryAttributeRetriever;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.util.UserUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.UncategorizedLdapException;

public class LDAPUserAttributesMapper {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final LDAPPropertiesMapper ldapPropertiesMapper;
    protected final long directoryId;

    public LDAPUserAttributesMapper(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper) {
        this.directoryId = directoryId;
        this.ldapPropertiesMapper = ldapPropertiesMapper;
    }

    public Attributes mapAttributesFromUser(User user) throws NamingException {
        if (user == null) {
            throw new UncategorizedLdapException("Cannot map attributes from a null User");
        }
        User populatedUser = UserUtils.populateNames((User)user);
        BasicAttributes directoryAttributes = new BasicAttributes(true);
        directoryAttributes.put(new BasicAttribute(this.ldapPropertiesMapper.getObjectClassAttribute(), this.ldapPropertiesMapper.getUserObjectClass()));
        this.putValueInAttributes(populatedUser.getName(), this.ldapPropertiesMapper.getUserNameAttribute(), directoryAttributes);
        this.putValueInAttributes(populatedUser.getEmailAddress(), this.ldapPropertiesMapper.getUserEmailAttribute(), directoryAttributes);
        this.putValueInAttributes(populatedUser.getFirstName(), this.ldapPropertiesMapper.getUserFirstNameAttribute(), directoryAttributes);
        this.putValueInAttributes(populatedUser.getLastName(), this.ldapPropertiesMapper.getUserLastNameAttribute(), directoryAttributes);
        this.putValueInAttributes(populatedUser.getDisplayName(), this.ldapPropertiesMapper.getUserDisplayNameAttribute(), directoryAttributes);
        return directoryAttributes;
    }

    public UserTemplateWithAttributes mapUserFromAttributes(Attributes directoryAttributes) throws NamingException {
        if (directoryAttributes == null) {
            throw new UncategorizedLdapException("Cannot map from null attributes");
        }
        String username = this.getUsernameFromAttributes(directoryAttributes);
        UserTemplate user = new UserTemplate(username, this.directoryId);
        user.setActive(this.getUserActiveFromAttribute(directoryAttributes));
        user.setEmailAddress(StringUtils.defaultString((String)this.getUserEmailFromAttribute(directoryAttributes)));
        user.setFirstName(this.getUserFirstNameFromAttribute(directoryAttributes));
        user.setLastName(this.getUserLastNameFromAttribute(directoryAttributes));
        user.setDisplayName(this.getUserDisplayNameFromAttribute(directoryAttributes));
        user.setExternalId(this.getExternalIdFromAttribute(directoryAttributes));
        User prepopulatedUser = UserUtils.populateNames((User)user);
        return UserTemplateWithAttributes.toUserWithNoAttributes((User)prepopulatedUser);
    }

    protected String getUserDisplayNameFromAttribute(Attributes directoryAttributes) {
        return DirectoryAttributeRetriever.getValueFromAttributes(this.ldapPropertiesMapper.getUserDisplayNameAttribute(), directoryAttributes);
    }

    protected String getUserLastNameFromAttribute(Attributes directoryAttributes) {
        return DirectoryAttributeRetriever.getValueFromAttributes(this.ldapPropertiesMapper.getUserLastNameAttribute(), directoryAttributes);
    }

    protected String getUserFirstNameFromAttribute(Attributes directoryAttributes) {
        return DirectoryAttributeRetriever.getValueFromAttributes(this.ldapPropertiesMapper.getUserFirstNameAttribute(), directoryAttributes);
    }

    protected String getUserEmailFromAttribute(Attributes directoryAttributes) {
        return DirectoryAttributeRetriever.getValueFromAttributes(this.ldapPropertiesMapper.getUserEmailAttribute(), directoryAttributes);
    }

    protected boolean getUserActiveFromAttribute(Attributes directoryAttributes) {
        return true;
    }

    protected String getExternalIdFromAttribute(Attributes directoryAttributes) {
        return DirectoryAttributeRetriever.getValueFromExternalIdAttribute(this.ldapPropertiesMapper.getExternalIdAttribute(), directoryAttributes);
    }

    private void putValueInAttributes(String userAttributeValue, String directoryAttributeName, Attributes directoryAttributes) {
        if (StringUtils.isNotEmpty((CharSequence)userAttributeValue)) {
            directoryAttributes.put(new BasicAttribute(directoryAttributeName, userAttributeValue));
        }
    }

    protected String getUsernameFromAttributes(Attributes directoryAttributes) throws NamingException {
        String username = DirectoryAttributeRetriever.getValueFromAttributes(this.ldapPropertiesMapper.getUserNameAttribute(), directoryAttributes);
        if (username == null) {
            this.logger.error("The following record does not have a username: " + directoryAttributes.toString());
            throw new UncategorizedLdapException("Unable to find the username of the principal.");
        }
        return username;
    }

    public Set<String> getRequiredLdapAttributes() {
        HashSet attributes = Sets.newHashSet((Object[])new String[]{this.ldapPropertiesMapper.getUserNameAttribute(), this.ldapPropertiesMapper.getUserEmailAttribute(), this.ldapPropertiesMapper.getUserFirstNameAttribute(), this.ldapPropertiesMapper.getUserLastNameAttribute(), this.ldapPropertiesMapper.getUserDisplayNameAttribute()});
        if (StringUtils.isNotBlank((CharSequence)this.ldapPropertiesMapper.getExternalIdAttribute())) {
            attributes.add(this.ldapPropertiesMapper.getExternalIdAttribute());
        }
        return ImmutableSet.copyOf((Collection)attributes);
    }
}

