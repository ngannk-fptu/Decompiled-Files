/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Collection;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.FilterDefinition;

public interface Filter {
    public String getName();

    public FilterDefinition getFilterDefinition();

    public Filter setParameter(String var1, Object var2);

    public Filter setParameterList(String var1, Collection var2);

    public Filter setParameterList(String var1, Object[] var2);

    public void validate() throws HibernateException;
}

