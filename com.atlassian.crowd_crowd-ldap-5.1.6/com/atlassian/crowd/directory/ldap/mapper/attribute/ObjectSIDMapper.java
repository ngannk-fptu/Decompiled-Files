/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.ldap.core.DirContextAdapter
 *  org.springframework.ldap.support.LdapUtils
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.support.LdapUtils;

public class ObjectSIDMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "objectSid";

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws Exception {
        byte[] objectSidAsByteArray = (byte[])ctx.getObjectAttribute(this.getKey());
        if (objectSidAsByteArray != null) {
            String objectSid = LdapUtils.convertBinarySidToString((byte[])objectSidAsByteArray);
            return ImmutableSet.of((Object)objectSid);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.getKey());
    }
}

