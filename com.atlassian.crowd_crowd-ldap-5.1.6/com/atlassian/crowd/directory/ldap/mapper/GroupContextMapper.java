/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.group.GroupType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.ldap.NamingException
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithCustomAttributes;
import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.mapper.entity.LDAPGroupAttributesMapper;
import com.atlassian.crowd.directory.ldap.util.DNStandardiser;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import java.util.List;
import java.util.Set;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextAdapter;

public class GroupContextMapper
extends ContextMapperWithCustomAttributes<LDAPGroupWithAttributes> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final long directoryId;
    protected final GroupType groupType;
    protected final LDAPPropertiesMapper ldapPropertiesMapper;

    public GroupContextMapper(long directoryId, GroupType groupType, LDAPPropertiesMapper ldapPropertiesMapper, List<AttributeMapper> customAttributeMappers) {
        super(customAttributeMappers);
        this.directoryId = directoryId;
        this.groupType = groupType;
        this.ldapPropertiesMapper = ldapPropertiesMapper;
    }

    @Override
    protected Set<String> getCoreRequiredLdapAttributes() {
        return this.getAttributesMapper().getRequiredLdapAttributes();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public LDAPGroupWithAttributes mapFromContext(DirContextAdapter context) throws NamingException {
        GroupTemplateWithAttributes groupTemplate;
        Attributes attributes = context.getAttributes();
        LDAPGroupAttributesMapper mapper = this.getAttributesMapper();
        MDC.put((String)"crowd.ldap.context", (String)context.getDn().toString());
        try {
            groupTemplate = mapper.mapGroupFromAttributes(attributes);
        }
        finally {
            MDC.remove((String)"crowd.ldap.context");
        }
        for (AttributeMapper attributeMapper : this.customAttributeMappers) {
            try {
                groupTemplate.setAttribute(attributeMapper.getKey(), attributeMapper.getValues(context));
            }
            catch (Exception e) {
                this.logger.error("Failed to map attribute <" + attributeMapper.getKey() + "> from context with DN <" + context.getDn().toString() + ">", (Throwable)e);
            }
        }
        String dn = DNStandardiser.standardise((LdapName)context.getDn(), !this.ldapPropertiesMapper.isRelaxedDnStandardisation());
        LDAPGroupWithAttributes group = new LDAPGroupWithAttributes(dn, groupTemplate);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Created group <" + group + "> from DN <" + context.getDn() + ">");
        }
        return group;
    }

    protected LDAPGroupAttributesMapper getAttributesMapper() {
        return new LDAPGroupAttributesMapper(this.directoryId, this.groupType, this.ldapPropertiesMapper);
    }
}

