/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.group.GroupType
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.NamingException
 *  org.springframework.ldap.UncategorizedLdapException
 */
package com.atlassian.crowd.directory.ldap.mapper.entity;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.util.DirectoryAttributeRetriever;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupType;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.UncategorizedLdapException;

public class LDAPGroupAttributesMapper {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final long directoryId;
    protected final GroupType groupType;
    protected final String objectClassAttribute;
    protected final String objectClassValue;
    protected final String nameAttribute;
    protected final String descriptionAttribute;
    protected final String externalIdAttribute;
    private final Set<String> requiredAttributes;

    public LDAPGroupAttributesMapper(long directoryId, GroupType groupType, LDAPPropertiesMapper ldapPropertiesMapper) {
        this.directoryId = directoryId;
        this.groupType = groupType;
        this.objectClassAttribute = ldapPropertiesMapper.getObjectClassAttribute();
        switch (this.groupType) {
            case GROUP: {
                this.objectClassValue = ldapPropertiesMapper.getGroupObjectClass();
                this.nameAttribute = ldapPropertiesMapper.getGroupNameAttribute();
                this.descriptionAttribute = ldapPropertiesMapper.getGroupDescriptionAttribute();
                this.externalIdAttribute = ldapPropertiesMapper.getGroupExternalIdAttribute();
                break;
            }
            default: {
                throw new IllegalArgumentException("Cannot create LDAPGroupAttributesMapper for groupType: " + groupType);
            }
        }
        ImmutableSet.Builder requiredAttributesBuilder = ImmutableSet.builder();
        requiredAttributesBuilder.add((Object)this.nameAttribute);
        requiredAttributesBuilder.add((Object)this.descriptionAttribute);
        if (this.externalIdAttribute != null) {
            requiredAttributesBuilder.add((Object)this.externalIdAttribute);
        }
        this.requiredAttributes = requiredAttributesBuilder.build();
    }

    public Attributes mapAttributesFromGroup(Group group) throws NamingException {
        if (group == null) {
            throw new UncategorizedLdapException("Cannot map attributes from a null Group");
        }
        BasicAttributes directoryAttributes = new BasicAttributes(true);
        directoryAttributes.put(new BasicAttribute(this.objectClassAttribute, this.objectClassValue));
        this.putValueInAttributes(group.getName(), this.nameAttribute, directoryAttributes);
        if (StringUtils.isNotBlank((CharSequence)group.getDescription()) && StringUtils.isNotBlank((CharSequence)this.descriptionAttribute)) {
            this.putValueInAttributes(group.getDescription(), this.descriptionAttribute, directoryAttributes);
        }
        if (StringUtils.isNotBlank((CharSequence)group.getExternalId()) && StringUtils.isNotBlank((CharSequence)this.externalIdAttribute)) {
            this.putValueInAttributes(group.getExternalId(), this.externalIdAttribute, directoryAttributes);
        }
        return directoryAttributes;
    }

    public GroupTemplateWithAttributes mapGroupFromAttributes(Attributes directoryAttributes) throws NamingException {
        if (directoryAttributes == null) {
            throw new UncategorizedLdapException("Cannot map from null attributes");
        }
        String groupname = this.getGroupNameFromAttributes(directoryAttributes);
        GroupTemplateWithAttributes group = new GroupTemplateWithAttributes(groupname, this.directoryId, this.groupType);
        group.setActive(this.getGroupActiveFromAttribute(directoryAttributes));
        group.setDescription(this.getGroupDescriptionFromAttribute(directoryAttributes));
        group.setExternalId(this.getExternalIdFromAttribute(directoryAttributes));
        return group;
    }

    protected String getGroupDescriptionFromAttribute(Attributes directoryAttributes) {
        return DirectoryAttributeRetriever.getValueFromAttributes(this.descriptionAttribute, directoryAttributes);
    }

    protected boolean getGroupActiveFromAttribute(Attributes directoryAttributes) {
        return true;
    }

    protected String getExternalIdFromAttribute(Attributes directoryAttributes) {
        return DirectoryAttributeRetriever.getValueFromExternalIdAttribute(this.externalIdAttribute, directoryAttributes);
    }

    private void putValueInAttributes(String groupAttributeValue, String directoryAttributeName, Attributes directoryAttributes) {
        if (groupAttributeValue != null) {
            directoryAttributes.put(new BasicAttribute(directoryAttributeName, groupAttributeValue));
        }
    }

    protected String getGroupNameFromAttributes(Attributes directoryAttributes) throws NamingException {
        String groupname = DirectoryAttributeRetriever.getValueFromAttributes(this.nameAttribute, directoryAttributes);
        if (groupname == null) {
            this.logger.error("The following record does not have a groupname: " + directoryAttributes.toString());
            throw new UncategorizedLdapException("Unable to find the groupname of the principal.");
        }
        return groupname;
    }

    public Set<String> getRequiredLdapAttributes() {
        return this.requiredAttributes;
    }
}

