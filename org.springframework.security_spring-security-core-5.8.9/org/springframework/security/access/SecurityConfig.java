/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.access;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SecurityConfig
implements ConfigAttribute {
    private final String attrib;

    public SecurityConfig(String config) {
        Assert.hasText((String)config, (String)"You must provide a configuration attribute");
        this.attrib = config;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ConfigAttribute) {
            ConfigAttribute attr = (ConfigAttribute)obj;
            return this.attrib.equals(attr.getAttribute());
        }
        return false;
    }

    @Override
    public String getAttribute() {
        return this.attrib;
    }

    public int hashCode() {
        return this.attrib.hashCode();
    }

    public String toString() {
        return this.attrib;
    }

    public static List<ConfigAttribute> createListFromCommaDelimitedString(String access) {
        return SecurityConfig.createList(StringUtils.commaDelimitedListToStringArray((String)access));
    }

    public static List<ConfigAttribute> createList(String ... attributeNames) {
        Assert.notNull((Object)attributeNames, (String)"You must supply an array of attribute names");
        ArrayList<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>(attributeNames.length);
        for (String attribute : attributeNames) {
            attributes.add(new SecurityConfig(attribute.trim()));
        }
        return attributes;
    }
}

