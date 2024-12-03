/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.seraph;

import com.atlassian.confluence.impl.seraph.TimingAccumulator;
import com.atlassian.event.api.EventPublisher;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AuthenticatorMetrics {
    private static final String GET_USER_EVENT_NAME = "confluence.seraph.getuser";
    private static final ThreadLocal<TimingAccumulator> threadLocalAccumulator = ThreadLocal.withInitial(TimingAccumulator::new);

    static boolean hasEvents() {
        return AuthenticatorMetrics.getAccumulator().hasData();
    }

    static void publishEvents(EventPublisher eventPublisher) {
        AuthenticatorMetrics.getAccumulator().publishEvent(eventPublisher, GET_USER_EVENT_NAME);
    }

    static void resetThreadLocal() {
        threadLocalAccumulator.remove();
    }

    public static <T> T measureGetUser(Supplier<T> impl) {
        return AuthenticatorMetrics.getAccumulator().accumulateOperation(impl);
    }

    private static @NonNull TimingAccumulator getAccumulator() {
        return threadLocalAccumulator.get();
    }
}

