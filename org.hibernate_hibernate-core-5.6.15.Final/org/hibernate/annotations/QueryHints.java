/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import org.hibernate.graph.GraphSemantic;

public class QueryHints {
    public static final String CACHE_MODE = "org.hibernate.cacheMode";
    public static final String CACHE_REGION = "org.hibernate.cacheRegion";
    public static final String CACHEABLE = "org.hibernate.cacheable";
    public static final String CALLABLE = "org.hibernate.callable";
    public static final String COMMENT = "org.hibernate.comment";
    public static final String FETCH_SIZE = "org.hibernate.fetchSize";
    public static final String FLUSH_MODE = "org.hibernate.flushMode";
    public static final String READ_ONLY = "org.hibernate.readOnly";
    public static final String TIMEOUT_HIBERNATE = "org.hibernate.timeout";
    public static final String TIMEOUT_JPA = "javax.persistence.query.timeout";
    public static final String TIMEOUT_JAKARTA_JPA = "jakarta.persistence.query.timeout";
    public static final String NATIVE_LOCKMODE = "org.hibernate.lockMode";
    @Deprecated
    public static final String FETCHGRAPH = GraphSemantic.FETCH.getJpaHintName();
    @Deprecated
    public static final String LOADGRAPH = GraphSemantic.LOAD.getJpaHintName();
    public static final String FOLLOW_ON_LOCKING = "hibernate.query.followOnLocking";
    public static final String PASS_DISTINCT_THROUGH = "hibernate.query.passDistinctThrough";
    public static final String NATIVE_SPACES = "org.hibernate.query.native.spaces";

    private QueryHints() {
    }
}

