/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.replication;

import com.amazonaws.services.s3.model.replication.ReplicationAndOperator;
import com.amazonaws.services.s3.model.replication.ReplicationPrefixPredicate;
import com.amazonaws.services.s3.model.replication.ReplicationTagPredicate;

public interface ReplicationPredicateVisitor {
    public void visit(ReplicationPrefixPredicate var1);

    public void visit(ReplicationTagPredicate var1);

    public void visit(ReplicationAndOperator var1);
}

