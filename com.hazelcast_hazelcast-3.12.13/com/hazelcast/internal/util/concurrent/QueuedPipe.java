/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.Pipe;
import java.util.Queue;

public interface QueuedPipe<E>
extends Queue<E>,
Pipe<E> {
}

