/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.replication;

import com.amazonaws.services.s3.model.replication.ReplicationPredicateVisitor;
import java.io.Serializable;

public abstract class ReplicationFilterPredicate
implements Serializable {
    public abstract void accept(ReplicationPredicateVisitor var1);
}

