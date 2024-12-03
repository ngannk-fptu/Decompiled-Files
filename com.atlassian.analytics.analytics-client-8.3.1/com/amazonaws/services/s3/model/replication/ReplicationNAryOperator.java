/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.replication;

import com.amazonaws.services.s3.model.replication.ReplicationFilterPredicate;
import java.util.List;

abstract class ReplicationNAryOperator
extends ReplicationFilterPredicate {
    private final List<ReplicationFilterPredicate> operands;

    public ReplicationNAryOperator(List<ReplicationFilterPredicate> operands) {
        this.operands = operands;
    }

    public List<ReplicationFilterPredicate> getOperands() {
        return this.operands;
    }
}

