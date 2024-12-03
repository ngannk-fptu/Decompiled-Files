/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.UncategorizedLdapException
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper;

import com.atlassian.crowd.directory.ldap.mapper.AttributeContextMapper;
import com.atlassian.crowd.directory.ldap.util.DirectoryAttributeRetriever;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.DirContextAdapter;

public class ExternalIdContextMapper
extends AttributeContextMapper<String> {
    public ExternalIdContextMapper(String propertyName) {
        super(propertyName);
    }

    @Override
    public String mapFromContext(Object ctx) {
        try {
            DirContextAdapter context = (DirContextAdapter)ctx;
            return DirectoryAttributeRetriever.getValueFromExternalIdAttribute(this.propertyName, context.getAttributes());
        }
        catch (UncategorizedLdapException e) {
            throw new RuntimeException("Could not retrieve externalId from object: " + ctx, e);
        }
    }
}

