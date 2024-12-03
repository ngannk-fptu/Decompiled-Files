/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.util.Set;

public interface DistributedManager {
    public int getActiveSessionsFull();

    public Set<String> getSessionIdsFull();
}

