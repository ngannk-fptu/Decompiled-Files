/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.graph.GraphSemantic;

public class QueryHints {
    @Deprecated
    public static final String HINT_TIMEOUT = "org.hibernate.timeout";
    public static final String SPEC_HINT_TIMEOUT = "javax.persistence.query.timeout";
    public static final String JAKARTA_SPEC_HINT_TIMEOUT = "jakarta.persistence.query.timeout";
    public static final String HINT_COMMENT = "org.hibernate.comment";
    public static final String HINT_FETCH_SIZE = "org.hibernate.fetchSize";
    public static final String HINT_CACHEABLE = "org.hibernate.cacheable";
    public static final String HINT_CACHE_REGION = "org.hibernate.cacheRegion";
    public static final String HINT_READONLY = "org.hibernate.readOnly";
    public static final String HINT_CACHE_MODE = "org.hibernate.cacheMode";
    public static final String HINT_FLUSH_MODE = "org.hibernate.flushMode";
    public static final String HINT_NATIVE_LOCKMODE = "org.hibernate.lockMode";
    public static final String HINT_FETCHGRAPH = GraphSemantic.FETCH.getJpaHintName();
    public static final String HINT_LOADGRAPH = GraphSemantic.LOAD.getJpaHintName();
    public static final String JAKARTA_HINT_FETCHGRAPH = GraphSemantic.FETCH.getJakartaJpaHintName();
    public static final String JAKARTA_HINT_LOADGRAPH = GraphSemantic.LOAD.getJakartaJpaHintName();
    public static final String HINT_FOLLOW_ON_LOCKING = "hibernate.query.followOnLocking";
    public static final String HINT_PASS_DISTINCT_THROUGH = "hibernate.query.passDistinctThrough";
    public static final String HINT_NATIVE_SPACES = "org.hibernate.query.native.spaces";
    private static final Set<String> HINTS = QueryHints.buildHintsSet();

    private static Set<String> buildHintsSet() {
        HashSet<String> hints = new HashSet<String>();
        hints.add(HINT_TIMEOUT);
        hints.add(SPEC_HINT_TIMEOUT);
        hints.add(JAKARTA_SPEC_HINT_TIMEOUT);
        hints.add(HINT_COMMENT);
        hints.add(HINT_FETCH_SIZE);
        hints.add(HINT_CACHE_REGION);
        hints.add(HINT_CACHEABLE);
        hints.add(HINT_READONLY);
        hints.add(HINT_CACHE_MODE);
        hints.add(HINT_FLUSH_MODE);
        hints.add(HINT_NATIVE_LOCKMODE);
        hints.add(HINT_FETCHGRAPH);
        hints.add(HINT_LOADGRAPH);
        hints.add(JAKARTA_HINT_FETCHGRAPH);
        hints.add(JAKARTA_HINT_LOADGRAPH);
        hints.add(HINT_NATIVE_SPACES);
        return Collections.unmodifiableSet(hints);
    }

    public static Set<String> getDefinedHints() {
        return HINTS;
    }

    protected QueryHints() {
    }
}

