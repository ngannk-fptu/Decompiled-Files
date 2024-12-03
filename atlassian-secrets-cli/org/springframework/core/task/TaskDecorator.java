/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.task;

@FunctionalInterface
public interface TaskDecorator {
    public Runnable decorate(Runnable var1);
}

