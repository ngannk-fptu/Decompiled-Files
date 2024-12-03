/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.io.Serializable;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.engine.spi.TypedValue;

public interface Criterion
extends Serializable {
    public String toSqlString(Criteria var1, CriteriaQuery var2) throws HibernateException;

    public TypedValue[] getTypedValues(Criteria var1, CriteriaQuery var2) throws HibernateException;
}

