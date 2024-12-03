/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.Closeable;
import org.apache.http.impl.client.cache.AsynchronousValidationRequest;

public interface SchedulingStrategy
extends Closeable {
    public void schedule(AsynchronousValidationRequest var1);
}

