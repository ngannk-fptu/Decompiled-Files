/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.wan;

public interface Watchable {
    public void goLive();

    public void die();

    public boolean probeLiveness();

    public String name();
}

