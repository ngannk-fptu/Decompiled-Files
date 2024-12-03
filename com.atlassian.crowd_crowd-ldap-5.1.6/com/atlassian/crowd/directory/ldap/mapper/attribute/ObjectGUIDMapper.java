/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper.attribute;

import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.util.GuidHelper;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;

public class ObjectGUIDMapper
implements AttributeMapper {
    private static final Logger logger = LoggerFactory.getLogger(ObjectGUIDMapper.class);
    public static final String ATTRIBUTE_KEY = "objectGUID";

    @Override
    public String getKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public Set<String> getValues(DirContextAdapter ctx) throws NamingException {
        Attribute attr = ctx.getAttributes().get(this.getKey());
        if (attr == null || attr.size() != 1) {
            return Collections.emptySet();
        }
        Object attrValue = attr.get(0);
        if (attrValue instanceof byte[]) {
            return ImmutableSet.of((Object)GuidHelper.getGUIDAsString((byte[])attrValue));
        }
        logger.debug("Skipped value <{}> for attribute {} because it is not a byte array", attrValue, (Object)ATTRIBUTE_KEY);
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return Collections.singleton(this.getKey());
    }
}

