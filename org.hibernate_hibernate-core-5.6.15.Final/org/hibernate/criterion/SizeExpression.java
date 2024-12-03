/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.Locale;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.sql.ConditionFragment;
import org.hibernate.type.StandardBasicTypes;

public class SizeExpression
implements Criterion {
    private final String propertyName;
    private final int size;
    private final String op;

    protected SizeExpression(String propertyName, int size, String op) {
        this.propertyName = propertyName;
        this.size = size;
        this.op = op;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String entityName = criteriaQuery.getEntityName(criteria, this.propertyName);
        String role = entityName + '.' + criteriaQuery.getPropertyName(this.propertyName);
        QueryableCollection cp = (QueryableCollection)criteriaQuery.getFactory().getCollectionPersister(role);
        String[] fk = cp.getKeyColumnNames();
        String[] pk = ((Loadable)cp.getOwnerEntityPersister()).getIdentifierColumnNames();
        ConditionFragment subQueryRestriction = new ConditionFragment().setTableAlias(criteriaQuery.getSQLAlias(criteria, this.propertyName)).setCondition(pk, fk);
        return String.format(Locale.ROOT, "? %s (select count(*) from %s where %s)", this.op, cp.getTableName(), subQueryRestriction.toFragmentString());
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[]{new TypedValue(StandardBasicTypes.INTEGER, this.size)};
    }

    public String toString() {
        return Integer.toString(this.size) + this.op + this.propertyName + ".size";
    }
}

