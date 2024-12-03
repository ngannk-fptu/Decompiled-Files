/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.transaction.TransactionContext;
import javax.transaction.xa.XAResource;

public interface HazelcastXAResource
extends XAResource,
DistributedObject {
    public TransactionContext getTransactionContext();
}

