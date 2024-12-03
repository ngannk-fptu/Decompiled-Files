/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.EventListener;

public interface StoreListener
extends EventListener {
    public void clusterCoherent(boolean var1);
}

