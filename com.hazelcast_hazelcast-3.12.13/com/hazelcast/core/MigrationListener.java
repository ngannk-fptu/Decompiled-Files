/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.MigrationEvent;
import java.util.EventListener;

public interface MigrationListener
extends EventListener {
    public void migrationStarted(MigrationEvent var1);

    public void migrationCompleted(MigrationEvent var1);

    public void migrationFailed(MigrationEvent var1);
}

