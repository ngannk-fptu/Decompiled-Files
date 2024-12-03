/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.core;

import javax.naming.Name;
import org.springframework.LdapDataEntry;
import org.springframework.ldap.filter.Filter;

public interface ObjectDirectoryMapper {
    public void mapToLdapDataEntry(Object var1, LdapDataEntry var2);

    public <T> T mapFromLdapDataEntry(LdapDataEntry var1, Class<T> var2);

    public Name getId(Object var1);

    public void setId(Object var1, Name var2);

    public Name getCalculatedId(Object var1);

    public Filter filterFor(Class<?> var1, Filter var2);

    public String attributeFor(Class<?> var1, String var2);

    public String[] manageClass(Class<?> var1);
}

