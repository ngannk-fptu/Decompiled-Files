/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute.group;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.util.RangeOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.naming.NamingEnumeration;
import org.springframework.ldap.core.DirContextAdapter;

public class RFC4519MemberDnRangeOffsetMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "memberRangeStart";
    private final String groupMemberAttribute;

    public RFC4519MemberDnRangeOffsetMapper(String groupMemberAttribute) {
        this.groupMemberAttribute = groupMemberAttribute;
    }

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws Exception {
        HashSet<String> attributes = new HashSet<String>();
        try (NamingEnumeration<String> attrEnum = ctx.getAttributes().getIDs();){
            while (attrEnum.hasMore()) {
                String attrId = attrEnum.next();
                if (!attrId.startsWith(this.groupMemberAttribute + ";")) continue;
                RangeOption range = RangeOption.parse(attrId.split(";")[1]);
                int newStart = range.getTerminal() + 1;
                attributes.add(String.valueOf(newStart));
                break;
            }
        }
        return attributes;
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.groupMemberAttribute);
    }
}

