/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.tuple.Pair
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper;

import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.ExternalIdContextMapper;
import com.atlassian.crowd.directory.ldap.util.DirectoryAttributeRetriever;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.ldap.core.DirContextAdapter;

public class NameWithExternalIdContextMapper
implements ContextMapperWithRequiredAttributes<Pair<String, String>> {
    private final ExternalIdContextMapper externalIdMapper;
    private final String nameAttribute;
    private final Set<String> requiredAttributes;

    public NameWithExternalIdContextMapper(String nameAttribute, String externalIdAttribute) {
        this.externalIdMapper = new ExternalIdContextMapper(externalIdAttribute);
        this.nameAttribute = nameAttribute;
        this.requiredAttributes = ImmutableSet.of((Object)nameAttribute, (Object)externalIdAttribute);
    }

    @Override
    public Pair<String, String> mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter)ctx;
        return Pair.of((Object)DirectoryAttributeRetriever.getValueFromAttributes(this.nameAttribute, context.getAttributes()), (Object)this.externalIdMapper.mapFromContext(ctx));
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return this.requiredAttributes;
    }
}

