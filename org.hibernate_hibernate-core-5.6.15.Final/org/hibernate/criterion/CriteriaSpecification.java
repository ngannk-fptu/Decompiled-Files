/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.hibernate.transform.PassThroughResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.RootEntityResultTransformer;

public interface CriteriaSpecification {
    public static final String ROOT_ALIAS = "this";
    public static final ResultTransformer ALIAS_TO_ENTITY_MAP = AliasToEntityMapResultTransformer.INSTANCE;
    public static final ResultTransformer ROOT_ENTITY = RootEntityResultTransformer.INSTANCE;
    public static final ResultTransformer DISTINCT_ROOT_ENTITY = DistinctRootEntityResultTransformer.INSTANCE;
    public static final ResultTransformer PROJECTION = PassThroughResultTransformer.INSTANCE;
    @Deprecated
    public static final int INNER_JOIN = JoinType.INNER_JOIN.getJoinTypeValue();
    @Deprecated
    public static final int FULL_JOIN = JoinType.FULL_JOIN.getJoinTypeValue();
    @Deprecated
    public static final int LEFT_JOIN = JoinType.LEFT_OUTER_JOIN.getJoinTypeValue();
}

