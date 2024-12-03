/*
 * Decompiled with CFR 0.152.
 */
package org.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public interface Processor<T, R>
extends Subscriber<T>,
Publisher<R> {
}

