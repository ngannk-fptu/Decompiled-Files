/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import java.util.EventListener;
import java.util.function.Consumer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;

@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent>
extends EventListener {
    public void onApplicationEvent(E var1);

    public static <T> ApplicationListener<PayloadApplicationEvent<T>> forPayload(Consumer<T> consumer) {
        return event -> consumer.accept(event.getPayload());
    }
}

