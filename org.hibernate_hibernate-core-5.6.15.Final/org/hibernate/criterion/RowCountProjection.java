/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.Collections;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.type.Type;

public class RowCountProjection
extends SimpleProjection {
    private static final List ARGS = Collections.singletonList("*");

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        Type countFunctionReturnType = this.getFunction(criteriaQuery).getReturnType(null, criteriaQuery.getFactory());
        return new Type[]{countFunctionReturnType};
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
        return this.getFunction(criteriaQuery).render(null, ARGS, criteriaQuery.getFactory()) + " as y" + position + '_';
    }

    protected SQLFunction getFunction(CriteriaQuery criteriaQuery) {
        SQLFunctionRegistry sqlFunctionRegistry = criteriaQuery.getFactory().getSqlFunctionRegistry();
        SQLFunction function = sqlFunctionRegistry.findSQLFunction("count");
        if (function == null) {
            throw new HibernateException("Unable to locate count function mapping");
        }
        return function;
    }

    public String toString() {
        return "count(*)";
    }
}

