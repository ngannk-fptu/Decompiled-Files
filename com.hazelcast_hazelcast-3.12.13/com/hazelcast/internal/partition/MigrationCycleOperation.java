/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;

public interface MigrationCycleOperation
extends UrgentSystemOperation,
AllowedDuringPassiveState {
}

