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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.springframework.ldap.core.DirContextAdapter;

public class RFC4519MemberDnRangedMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "memberDNs";
    private final String groupMemberAttribute;
    private final boolean relaxedDnStandardisation;

    public RFC4519MemberDnRangedMapper(String groupMemberAttribute, boolean relaxedDnStandardisation) {
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
            Set<Object> members = Sets.newHashSet((Object[])memberArray);
            if (members.isEmpty()) {
                members = this.getInitialRangedMembers(ctx);
            }
            HashSet<String> standardDNs = new HashSet<String>(members.size());
            for (String string : members) {
                String dn = DNStandardiser.standardise(string, !this.relaxedDnStandardisation);
                standardDNs.add(dn);
            }
            return standardDNs;
        }
        return Collections.emptySet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<String> getInitialRangedMembers(DirContextAdapter ctx) throws NamingException {
        HashSet rangedMembers = new HashSet();
        try (NamingEnumeration<String> attrEnum = ctx.getAttributes().getIDs();){
            while (attrEnum.hasMore()) {
                String attrId = attrEnum.next();
                if (!attrId.startsWith(this.groupMemberAttribute + ";")) continue;
                rangedMembers = Sets.newHashSet((Object[])ctx.getStringAttributes(attrId));
                break;
            }
        }
        return rangedMembers;
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.groupMemberAttribute);
    }
}

