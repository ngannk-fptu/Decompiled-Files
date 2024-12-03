/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute.group;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;

public class RFC2307MemberUidMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "memberUIDs";
    private final String groupMemberAttribute;

    public RFC2307MemberUidMapper(String groupMemberAttribute) {
        this.groupMemberAttribute = groupMemberAttribute;
    }

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws Exception {
        Object[] members = ctx.getStringAttributes(this.groupMemberAttribute);
        return members == null ? Collections.emptySet() : Sets.newHashSet((Object[])members);
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.groupMemberAttribute);
    }
}

