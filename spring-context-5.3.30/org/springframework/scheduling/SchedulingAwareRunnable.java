/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling;

public interface SchedulingAwareRunnable
extends Runnable {
    public boolean isLongLived();
}

