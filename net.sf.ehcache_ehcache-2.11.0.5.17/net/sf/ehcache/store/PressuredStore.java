/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.concurrent.Callable;

public interface PressuredStore {
    public void registerEmergencyValve(Callable<Void> var1);
}

