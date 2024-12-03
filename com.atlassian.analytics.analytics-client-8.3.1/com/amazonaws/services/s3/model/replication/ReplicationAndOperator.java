/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.replication;

import com.amazonaws.services.s3.model.replication.ReplicationFilterPredicate;
import com.amazonaws.services.s3.model.replication.ReplicationNAryOperator;
import com.amazonaws.services.s3.model.replication.ReplicationPredicateVisitor;
import java.util.List;

public final class ReplicationAndOperator
extends ReplicationNAryOperator {
    public ReplicationAndOperator(List<ReplicationFilterPredicate> operands) {
        super(operands);
    }

    @Override
    public void accept(ReplicationPredicateVisitor replicationPredicateVisitor) {
        replicationPredicateVisitor.visit(this);
    }
}

