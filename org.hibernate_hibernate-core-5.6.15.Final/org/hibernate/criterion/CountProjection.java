/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.AggregateProjection;
import org.hibernate.criterion.CriteriaQuery;

public class CountProjection
extends AggregateProjection {
    private boolean distinct;

    protected CountProjection(String prop) {
        super("count", prop);
    }

    @Override
    protected List buildFunctionParameterList(Criteria criteria, CriteriaQuery criteriaQuery) {
        String[] cols = criteriaQuery.getColumns(this.propertyName, criteria);
        return this.distinct ? this.buildCountDistinctParameterList(cols) : Arrays.asList(cols);
    }

    private List buildCountDistinctParameterList(String[] cols) {
        ArrayList<String> params = new ArrayList<String>(cols.length + 1);
        params.add("distinct");
        params.addAll(Arrays.asList(cols));
        return params;
    }

    public CountProjection setDistinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public String toString() {
        if (this.distinct) {
            return "distinct " + super.toString();
        }
        return super.toString();
    }
}

