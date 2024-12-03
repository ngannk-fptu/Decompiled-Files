/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.loader.plan.spi.QuerySpaces;

public abstract class AbstractQuerySpace
implements QuerySpace {
    private final String uid;
    private final QuerySpace.Disposition disposition;
    private final ExpandingQuerySpaces querySpaces;
    private final boolean canJoinsBeRequired;
    private List<Join> joins;

    public AbstractQuerySpace(String uid, QuerySpace.Disposition disposition, ExpandingQuerySpaces querySpaces, boolean canJoinsBeRequired) {
        this.uid = uid;
        this.disposition = disposition;
        this.querySpaces = querySpaces;
        this.canJoinsBeRequired = canJoinsBeRequired;
    }

    protected SessionFactoryImplementor sessionFactory() {
        return this.querySpaces.getSessionFactory();
    }

    public boolean canJoinsBeRequired() {
        return this.canJoinsBeRequired;
    }

    @Override
    public QuerySpaces getQuerySpaces() {
        return this.querySpaces;
    }

    protected ExpandingQuerySpaces getExpandingQuerySpaces() {
        return this.querySpaces;
    }

    @Override
    public String getUid() {
        return this.uid;
    }

    @Override
    public QuerySpace.Disposition getDisposition() {
        return this.disposition;
    }

    @Override
    public Iterable<Join> getJoins() {
        return this.joins == null ? Collections.emptyList() : this.joins;
    }

    protected List<Join> internalGetJoins() {
        if (this.joins == null) {
            this.joins = new ArrayList<Join>();
        }
        return this.joins;
    }
}

