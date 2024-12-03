/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.lang.reflect.Method;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

public interface CompositeType
extends Type {
    public Type[] getSubtypes();

    public String[] getPropertyNames();

    public boolean[] getPropertyNullability();

    public Object[] getPropertyValues(Object var1, SharedSessionContractImplementor var2) throws HibernateException;

    public Object[] getPropertyValues(Object var1, EntityMode var2) throws HibernateException;

    public Object getPropertyValue(Object var1, int var2, SharedSessionContractImplementor var3) throws HibernateException;

    public void setPropertyValues(Object var1, Object[] var2, EntityMode var3) throws HibernateException;

    public CascadeStyle getCascadeStyle(int var1);

    public FetchMode getFetchMode(int var1);

    public boolean isMethodOf(Method var1);

    public boolean isEmbedded();

    public boolean hasNotNullProperty();

    public int getPropertyIndex(String var1);
}

