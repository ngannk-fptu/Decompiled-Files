/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.spi.NodeAware;
import java.io.Serializable;
import java.util.concurrent.Callable;

public interface SecureCallable<V>
extends Callable<V>,
Serializable,
HazelcastInstanceAware,
NodeAware {
}

