/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;

public interface JoinOperation
extends UrgentSystemOperation,
AllowedDuringPassiveState,
IdentifiedDataSerializable {
}

