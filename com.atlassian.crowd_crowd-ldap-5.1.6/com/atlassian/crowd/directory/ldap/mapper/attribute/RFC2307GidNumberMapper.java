/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import java.util.Collections;
import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;

public class RFC2307GidNumberMapper
implements AttributeMapper {
    public static final String ATTRIBUTE_KEY = "gidNumber";

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws Exception {
        return Collections.singleton((String)ctx.getAttributes().get(this.getKey()).get());
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.getKey());
    }
}

