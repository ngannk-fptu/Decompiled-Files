/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import javax.naming.NamingException;
import org.springframework.ldap.core.DirContextAdapter;

public class USNChangedMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "uSNChanged";

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws NamingException {
        String usn = ctx.getStringAttribute(this.getKey());
        if (usn != null) {
            return ImmutableSet.of((Object)usn);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.getKey());
    }
}

