/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.io.Serializable;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.type.Type;

public interface Projection
extends Serializable {
    public String toSqlString(Criteria var1, int var2, CriteriaQuery var3) throws HibernateException;

    public String toGroupSqlString(Criteria var1, CriteriaQuery var2) throws HibernateException;

    public Type[] getTypes(Criteria var1, CriteriaQuery var2) throws HibernateException;

    public Type[] getTypes(String var1, Criteria var2, CriteriaQuery var3) throws HibernateException;

    public String[] getColumnAliases(int var1);

    public String[] getColumnAliases(String var1, int var2);

    public String[] getAliases();

    public boolean isGrouped();
}

