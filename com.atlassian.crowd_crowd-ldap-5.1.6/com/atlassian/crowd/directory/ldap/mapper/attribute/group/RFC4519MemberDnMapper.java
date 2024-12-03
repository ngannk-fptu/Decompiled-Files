/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute.group;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.util.DNStandardiser;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;

public class RFC4519MemberDnMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "memberDNs";
    private final String groupMemberAttribute;
    private final boolean relaxedDnStandardisation;

    public RFC4519MemberDnMapper(String groupMemberAttribute, boolean relaxedDnStandardisation) {
        this.groupMemberAttribute = groupMemberAttribute;
        this.relaxedDnStandardisation = relaxedDnStandardisation;
    }

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws Exception {
        Object[] memberArray = ctx.getStringAttributes(this.groupMemberAttribute);
        if (memberArray != null) {
            HashSet members = Sets.newHashSet((Object[])memberArray);
            HashSet<String> standardDNs = new HashSet<String>(members.size());
            for (String memberDN : members) {
                String dn = DNStandardiser.standardise(memberDN, !this.relaxedDnStandardisation);
                standardDNs.add(dn);
            }
            return standardDNs;
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.groupMemberAttribute);
    }
}

