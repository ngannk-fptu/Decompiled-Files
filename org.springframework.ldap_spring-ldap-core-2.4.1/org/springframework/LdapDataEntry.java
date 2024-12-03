/*
 * Decompiled with CFR 0.152.
 */
package org.springframework;

import java.util.SortedSet;
import javax.naming.Name;
import javax.naming.directory.Attributes;

public interface LdapDataEntry {
    public String getStringAttribute(String var1);

    public Object getObjectAttribute(String var1);

    public boolean attributeExists(String var1);

    public void setAttributeValue(String var1, Object var2);

    public void setAttributeValues(String var1, Object[] var2);

    public void setAttributeValues(String var1, Object[] var2, boolean var3);

    public void addAttributeValue(String var1, Object var2);

    public void addAttributeValue(String var1, Object var2, boolean var3);

    public void removeAttributeValue(String var1, Object var2);

    public String[] getStringAttributes(String var1);

    public Object[] getObjectAttributes(String var1);

    public SortedSet<String> getAttributeSortedStringSet(String var1);

    public Name getDn();

    public Attributes getAttributes();
}

