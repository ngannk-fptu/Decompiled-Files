/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import java.util.EventListener;
import org.springframework.context.ApplicationEvent;

@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent>
extends EventListener {
    public void onApplicationEvent(E var1);
}

