/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.replication;

import com.amazonaws.services.s3.model.replication.ReplicationFilterPredicate;
import com.amazonaws.services.s3.model.replication.ReplicationPredicateVisitor;

public final class ReplicationPrefixPredicate
extends ReplicationFilterPredicate {
    private final String prefix;

    public ReplicationPrefixPredicate(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void accept(ReplicationPredicateVisitor replicationPredicateVisitor) {
        replicationPredicateVisitor.visit(this);
    }
}

