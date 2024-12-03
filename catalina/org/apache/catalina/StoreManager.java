/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import org.apache.catalina.DistributedManager;
import org.apache.catalina.Session;
import org.apache.catalina.Store;

public interface StoreManager
extends DistributedManager {
    public Store getStore();

    public void removeSuper(Session var1);
}

