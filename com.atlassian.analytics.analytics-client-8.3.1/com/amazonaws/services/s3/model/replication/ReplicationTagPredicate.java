/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.replication;

import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.replication.ReplicationFilterPredicate;
import com.amazonaws.services.s3.model.replication.ReplicationPredicateVisitor;

public final class ReplicationTagPredicate
extends ReplicationFilterPredicate {
    private final Tag tag;

    public ReplicationTagPredicate(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return this.tag;
    }

    @Override
    public void accept(ReplicationPredicateVisitor replicationPredicateVisitor) {
        replicationPredicateVisitor.visit(this);
    }
}

