/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.config;

import java.util.List;
import org.bedework.util.config.ConfInfo;

public interface HibernateConfigI {
    public void setHibernateProperties(List<String> var1);

    @ConfInfo(collectionElementName="hibernateProperty", elementType="java.lang.String")
    public List<String> getHibernateProperties();

    public void setHibernateDialect(String var1);

    @ConfInfo(dontSave=true)
    public String getHibernateDialect();

    public void addHibernateProperty(String var1, String var2);

    @ConfInfo(dontSave=true)
    public String getHibernateProperty(String var1);

    public void removeHibernateProperty(String var1);

    @ConfInfo(dontSave=true)
    public void setHibernateProperty(String var1, String var2);
}

