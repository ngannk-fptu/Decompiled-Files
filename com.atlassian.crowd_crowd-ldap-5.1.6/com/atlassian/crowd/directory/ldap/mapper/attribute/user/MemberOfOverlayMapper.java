/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute.user;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.util.DNStandardiser;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;

public class MemberOfOverlayMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "memberOf";
    private final String userMemberOfAttribute;
    private final boolean relaxedDnStandardisation;

    public MemberOfOverlayMapper(String userMemberOfAttribute, boolean relaxedDnStandardisation) {
        this.userMemberOfAttribute = userMemberOfAttribute;
        this.relaxedDnStandardisation = relaxedDnStandardisation;
    }

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws Exception {
        String[] memberships = ctx.getStringAttributes(this.userMemberOfAttribute);
        if (memberships != null) {
            HashSet<String> standardDNs = new HashSet<String>(memberships.length);
            for (String memberDN : memberships) {
                String dn = DNStandardiser.standardise(memberDN, !this.relaxedDnStandardisation);
                standardDNs.add(dn);
            }
            return standardDNs;
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.userMemberOfAttribute);
    }
}

