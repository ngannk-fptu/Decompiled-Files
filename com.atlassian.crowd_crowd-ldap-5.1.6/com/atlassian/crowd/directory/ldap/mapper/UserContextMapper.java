/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
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
import com.atlassian.crowd.directory.ldap.mapper.entity.LDAPUserAttributesMapper;
import com.atlassian.crowd.directory.ldap.util.DNStandardiser;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import java.util.List;
import java.util.Set;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextAdapter;

public class UserContextMapper
extends ContextMapperWithCustomAttributes<LDAPUserWithAttributes> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final long directoryId;
    protected final LDAPPropertiesMapper ldapPropertiesMapper;

    public UserContextMapper(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper, List<AttributeMapper> customAttributeMappers) {
        super(customAttributeMappers);
        this.directoryId = directoryId;
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
    public LDAPUserWithAttributes mapFromContext(DirContextAdapter context) throws NamingException {
        UserTemplateWithAttributes userTemplate;
        Attributes attributes = context.getAttributes();
        LDAPUserAttributesMapper mapper = this.getAttributesMapper();
        MDC.put((String)"crowd.ldap.context", (String)context.getDn().toString());
        try {
            userTemplate = mapper.mapUserFromAttributes(attributes);
        }
        finally {
            MDC.remove((String)"crowd.ldap.context");
        }
        for (AttributeMapper attributeMapper : this.customAttributeMappers) {
            try {
                userTemplate.setAttribute(attributeMapper.getKey(), attributeMapper.getValues(context));
            }
            catch (Exception e) {
                this.logger.warn("Failed to map attribute <" + attributeMapper.getKey() + "> from context with DN <" + context.getDn().toString() + ">", (Throwable)e);
            }
        }
        String dn = DNStandardiser.standardise((LdapName)context.getDn(), !this.ldapPropertiesMapper.isRelaxedDnStandardisation());
        LDAPUserWithAttributes user = new LDAPUserWithAttributes(dn, userTemplate);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Created user <" + user + "> from DN <" + context.getDn() + ">");
        }
        return user;
    }

    protected LDAPUserAttributesMapper getAttributesMapper() {
        return new LDAPUserAttributesMapper(this.directoryId, this.ldapPropertiesMapper);
    }
}

