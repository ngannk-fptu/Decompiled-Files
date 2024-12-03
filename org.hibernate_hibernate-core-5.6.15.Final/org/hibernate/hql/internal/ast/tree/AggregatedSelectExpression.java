/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import java.util.List;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.transform.ResultTransformer;

public interface AggregatedSelectExpression
extends SelectExpression {
    public List getAggregatedSelectionTypeList();

    public String[] getAggregatedAliases();

    public ResultTransformer getResultTransformer();

    public Class getAggregationResultType();
}

