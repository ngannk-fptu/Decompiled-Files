/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum;

import com.hazelcast.quorum.QuorumEvent;
import java.util.EventListener;

public interface QuorumListener
extends EventListener {
    public void onChange(QuorumEvent var1);
}

