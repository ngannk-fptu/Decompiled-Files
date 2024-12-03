/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class BooleanQueryBuilder<T> {
    public static final float DEFAULT_BOOST_FACTOR = 1.0f;
    protected final Set<T> must = new LinkedHashSet<T>();
    protected final Set<T> should = new LinkedHashSet<T>();
    protected final Set<T> mustNot = new LinkedHashSet<T>();
    protected float boost = 1.0f;

    public <U extends T> BooleanQueryBuilder<T> addMust(U e) {
        this.must.add(e);
        return this;
    }

    public <U extends T> BooleanQueryBuilder<T> addMust(U ... e) {
        return this.addMust((Collection<? extends T>)Arrays.asList(e));
    }

    public BooleanQueryBuilder<T> addMust(Collection<? extends T> must) {
        this.must.addAll(must);
        return this;
    }

    public <U extends T> BooleanQueryBuilder<T> addShould(U e) {
        this.should.add(e);
        return this;
    }

    public <U extends T> BooleanQueryBuilder<T> addShould(U ... e) {
        return this.addShould((Collection<? extends T>)Arrays.asList(e));
    }

    public BooleanQueryBuilder<T> addShould(Collection<? extends T> should) {
        this.should.addAll(should);
        return this;
    }

    public <U extends T> BooleanQueryBuilder<T> addMustNot(U e) {
        this.mustNot.add(e);
        return this;
    }

    public <U extends T> BooleanQueryBuilder<T> addMustNot(U ... e) {
        return this.addMustNot((Collection<? extends T>)Arrays.asList(e));
    }

    public BooleanQueryBuilder<T> addMustNot(Collection<? extends T> mustNot) {
        this.mustNot.addAll(mustNot);
        return this;
    }

    public BooleanQueryBuilder<T> boost(float boost) {
        this.boost = boost;
        return this;
    }

    public abstract T build();
}

