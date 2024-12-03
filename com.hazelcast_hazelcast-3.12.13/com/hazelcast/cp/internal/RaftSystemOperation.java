/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;

public interface RaftSystemOperation
extends AllowedDuringPassiveState,
ReadonlyOperation {
}

