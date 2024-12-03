/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.engine.spi.TypedValue;

public class NaturalIdentifier
implements Criterion {
    private final Conjunction conjunction = new Conjunction();

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return this.conjunction.getTypedValues(criteria, criteriaQuery);
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return this.conjunction.toSqlString(criteria, criteriaQuery);
    }

    public Map<String, Object> getNaturalIdValues() {
        ConcurrentHashMap<String, Object> naturalIdValueMap = new ConcurrentHashMap<String, Object>();
        for (Criterion condition : this.conjunction.conditions()) {
            SimpleExpression equalsCondition;
            if (!SimpleExpression.class.isInstance(condition) || !"=".equals((equalsCondition = (SimpleExpression)SimpleExpression.class.cast(condition)).getOp())) continue;
            naturalIdValueMap.put(equalsCondition.getPropertyName(), equalsCondition.getValue());
        }
        return naturalIdValueMap;
    }

    public NaturalIdentifier set(String property, Object value) {
        this.conjunction.add(Restrictions.eq(property, value));
        return this;
    }
}

