/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.target.dynamic;

public interface Refreshable {
    public void refresh();

    public long getRefreshCount();

    public long getLastRefreshTime();
}

