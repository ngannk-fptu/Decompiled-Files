/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.DirContextAdapter;

public class DeduplicatingDnMapperDecorator
implements ContextMapperWithRequiredAttributes<LdapName> {
    private final ContextMapperWithRequiredAttributes<LdapName> delegate;
    private final Map<String, LdapName> mappedLdapNames = new HashMap<String, LdapName>();

    public DeduplicatingDnMapperDecorator(ContextMapperWithRequiredAttributes<LdapName> delegate) {
        this.delegate = delegate;
    }

    @Override
    public LdapName mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter)ctx;
        String name = context.getDn().toString();
        return this.mappedLdapNames.computeIfAbsent(name, k -> this.delegate.mapFromContext(ctx));
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return this.delegate.getRequiredLdapAttributes();
    }
}

