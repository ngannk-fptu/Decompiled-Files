/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch;

import com.hazelcast.spi.AbstractWaitNotifyKey;

public class LatchKey
extends AbstractWaitNotifyKey {
    public LatchKey(String name) {
        super("hz:impl:countDownLatchService", name);
    }
}

