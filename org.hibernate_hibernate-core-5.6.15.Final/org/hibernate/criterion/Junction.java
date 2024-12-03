/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;

public class Junction
implements Criterion {
    private final Nature nature;
    private final List<Criterion> conditions = new ArrayList<Criterion>();

    protected Junction(Nature nature) {
        this.nature = nature;
    }

    protected Junction(Nature nature, Criterion ... criterion) {
        this(nature);
        Collections.addAll(this.conditions, criterion);
    }

    public Junction add(Criterion criterion) {
        this.conditions.add(criterion);
        return this;
    }

    public Nature getNature() {
        return this.nature;
    }

    public Iterable<Criterion> conditions() {
        return this.conditions;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria crit, CriteriaQuery criteriaQuery) throws HibernateException {
        ArrayList typedValues = new ArrayList();
        for (Criterion condition : this.conditions) {
            TypedValue[] subValues = condition.getTypedValues(crit, criteriaQuery);
            Collections.addAll(typedValues, subValues);
        }
        return typedValues.toArray(new TypedValue[typedValues.size()]);
    }

    @Override
    public String toSqlString(Criteria crit, CriteriaQuery criteriaQuery) throws HibernateException {
        if (this.conditions.size() == 0) {
            return "1=1";
        }
        StringBuilder buffer = new StringBuilder().append('(');
        Iterator<Criterion> itr = this.conditions.iterator();
        while (itr.hasNext()) {
            buffer.append(itr.next().toSqlString(crit, criteriaQuery));
            if (!itr.hasNext()) continue;
            buffer.append(' ').append(this.nature.getOperator()).append(' ');
        }
        return buffer.append(')').toString();
    }

    public String toString() {
        return '(' + StringHelper.join(' ' + this.nature.getOperator() + ' ', this.conditions.iterator()) + ')';
    }

    public static enum Nature {
        AND,
        OR;


        public String getOperator() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}

