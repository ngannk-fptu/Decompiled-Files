/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import java.util.List;
import java.util.Set;
import org.hibernate.loader.custom.Return;
import org.hibernate.param.ParameterBinder;

public interface CustomQuery {
    public String getSQL();

    public Set<String> getQuerySpaces();

    public List<ParameterBinder> getParameterValueBinders();

    public List<Return> getCustomQueryReturns();
}

