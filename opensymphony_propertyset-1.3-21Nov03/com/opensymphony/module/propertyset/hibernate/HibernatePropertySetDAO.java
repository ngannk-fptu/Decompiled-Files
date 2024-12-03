/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.hibernate;

import com.opensymphony.module.propertyset.hibernate.PropertySetItem;
import java.util.Collection;

public interface HibernatePropertySetDAO {
    public void setImpl(PropertySetItem var1, boolean var2);

    public Collection getKeys(String var1, Long var2, String var3, int var4);

    public PropertySetItem findByKey(String var1, Long var2, String var3);

    public void remove(String var1, Long var2, String var3);
}

