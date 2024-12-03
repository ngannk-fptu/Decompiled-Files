/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.StatelessSession
 *  org.hibernate.query.Query
 */
package com.atlassian.migration.agent.store.jpa.impl;

import java.util.stream.Stream;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;

public class StatelessResults<T>
implements AutoCloseable {
    private final StatelessSession statelessSession;
    private final Query<T> query;

    public StatelessResults(StatelessSession statelessSession, Query<T> query) {
        this.statelessSession = statelessSession;
        this.query = query;
    }

    public Stream<T> stream() {
        return this.query.stream();
    }

    @Override
    public void close() {
        this.statelessSession.close();
    }
}

